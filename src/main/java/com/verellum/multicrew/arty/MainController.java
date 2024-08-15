package com.verellum.multicrew.arty;

import java.awt.Rectangle;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class MainController extends Controller {

    private ScreenCapture sc;
    private Point player;
    private Point playerConv;
    private Point target;
    private Point targetConv;
    private Circle playerCircle;
    private Circle targetCircle;

    @FXML
    private GridPane gridPane;

    @FXML
    private Pane imgParent;

    @FXML
    private VBox pingList;

    @FXML
    private Button screencapButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button startCaptureButton;

    @FXML
    private Button stopCaptureButton;

    @FXML
    void initialize() {
        
        target = new Point();
        targetConv = new Point();
        player = new Point();
        playerConv = new Point();
        targetCircle = new Circle();
        playerCircle = new Circle();
        imgParent.getChildren().add(targetCircle);
        imgParent.getChildren().add(playerCircle);

        gridPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                double x = event.getX();
                double y = event.getY();
                target.setLocation(x, y);
                targetConv.setLocation(scaleConv(x), scaleConv(y));

                targetCircle.relocate(x-5, y-5);
                targetCircle.setRadius(5);
                targetCircle.setFill(Color.RED);
            }
        });
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
            Main.tick = new Tick(1000/30);
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

    @FXML
    void clearPlayer(ActionEvent event) {

    }

    @FXML
    void clearTarget(ActionEvent event) {

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

    /**
     * @param px pixel position on larger map in GUI
     * @return position on real map image (330x330)
     */
    public static int scaleConv(double px) {
        return (int)((px - 13) / 1.3);
    }



}