package com.verellum.multicrew.arty;

import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public class MainController extends Controller {

    private ScreenCapture sc;

    @FXML
    private Button screencapButton;

    @FXML
    private ImageView imageView;

    @FXML
    private Button startCaptureButton;

    @FXML
    private Button stopCaptureButton;

    @FXML
    void initialize() {
        
    }
    
    /**
     * finds map onscreen, crops screen capture to that portion, saves it to variable
     * and then displays that on the GUI
     * @param event event of button being clicked, not important
     */
    @FXML
    void findMap(ActionEvent event) {
        System.out.println("attempting capture!!!");
        BufferedImage bi = sc.capture();
        imageView.setImage(SwingFXUtils.toFXImage(ScreenCapture.cropImage(bi, TemplateMatch.matchRect(bi, Main.paths[1])), null));
    }

    /**
     * starts main loop
     * @param event event of button being clicked, not important
     */
    @FXML
    void startCapture(ActionEvent event) {
        //TODO begin main loop, have it look for pings and player on set FPS, maybe on seperate thread?
    }

    /**
     * IMMEDIATELY KILLS MAIN LOOP
     * @param event event of button being clicked, not important
     */
    @FXML
    void stopCapture(ActionEvent event) {
        //TODO halt main loop
    }

    public ScreenCapture getsScreenCapture() {
        return sc;
    }

    public void setScreenCapture(ScreenCapture sc) {
        this.sc = sc;
    }

}
