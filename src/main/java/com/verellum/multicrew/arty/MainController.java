package com.verellum.multicrew.arty;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class MainController extends Controller {

    private ScreenCapture sc;

    @FXML
    private Pane imgParent;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button screencapButton;

    // @FXML
    // private ImageView imageView;

    @FXML
    private Button startCaptureButton;

    @FXML
    private Button stopCaptureButton;

    @FXML
    void initialize() {

    }

    /**
     * finds map onscreen, crops screen capture to that portion, saves it to
     * variable
     * and then displays that on the GUI
     * 
     * @param event event of button being clicked, not important
     */
    @FXML
    void findMap(ActionEvent event) throws IOException {
        System.out.println("attempting capture!!!");
        BufferedImage bi = sc.capture();
        Rectangle region = TemplateMatch.matchRect(bi, Main.mapTemplate, Main.mapTemplate);
        Main.mapRegion = region;
        BufferedImage result = ScreenCapture.cropImage(bi, region);
        setImageView(result);
    }
    
    /**
     * starts main loop
     * 
     * @param event event of button being clicked, not important
     */
    @FXML
    void startCapture(ActionEvent event) {
        if (Tick.getNumThreads() < 1)
            Main.tick = new Tick(1000/15);
    }

    /**
     * IMMEDIATELY KILLS MAIN LOOP
     * 
     * @param event event of button being clicked, not important
     */
    @FXML
    void stopCapture(ActionEvent event) {
        if (Tick.getNumThreads() == 1) 
            Main.tick.stop();
    }

    public ScreenCapture getsScreenCapture() {
        return sc;
    }

    public void setScreenCapture(ScreenCapture sc) {
        this.sc = sc;
    }

    //i had to kill the imageview, otherwise it causes refresh issues, FUCK KNOWS WHY
    public void setImageView(BufferedImage bi) {
        imgParent.setBackground(new Background(
            new BackgroundImage(SwingFXUtils.toFXImage(bi, null), 
            null, 
            null, 
            null, 
            new BackgroundSize(420, 420, false, false, true, true))
        ));
        // imageView.setImage(SwingFXUtils.toFXImage(bi, null));
    }

}
