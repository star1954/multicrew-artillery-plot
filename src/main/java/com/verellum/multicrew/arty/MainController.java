package com.verellum.multicrew.arty;

import java.awt.Rectangle;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
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

    private Init init;

    private ScreenCapture sc;
    private Point player;
    private Point playerMeters;

    private Point target;
    private Point targetMeters;

    private Circle playerCircle;
    private Circle targetCircle;

    private double mapScaleMeters;
    private double shellVelocity;

    private LinkedList<PingController> pingCards;

    @FXML
    private Button conversionButton;

    @FXML
    private GridPane gridPane;

    @FXML
    private Pane imgParent;

    @FXML
    private CheckMenuItem menuSettingsDebug;

    @FXML
    private TextField metersBox;

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
    private TextField studsBox;

    @FXML
    private TextField velocityBox;

    @FXML
    void initialize() {
        target = new Point();
        player = new Point();
        targetCircle = new Circle(0, Color.RED);
        playerCircle = new Circle(0, Color.BLUE);

        targetCircle.setMouseTransparent(true);
        playerCircle.setMouseTransparent(true);

        imgParent.getChildren().add(targetCircle);
        imgParent.getChildren().add(playerCircle);

        gridPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                double x = event.getX();
                double y = event.getY();
                setTarget(x, y);
            }
            if (event.getButton() == MouseButton.SECONDARY) {
                double x = event.getX();
                double y = event.getY();
                setPlayer(x, y);
            }
        });

        studsBox.setTextFormatter(new TextFormatter<Integer>(change -> {
            if (change.isDeleted())
                return change;
            String str = change.getControlNewText();
            if (str.matches("0\\d+"))
                return null;
            try {
                double n = Integer.parseInt(str);
                return 0 <= n && n <= 9999 ? change : null;
            } catch (NumberFormatException e) {
                return null;
            }
        }));

        velocityBox.setTextFormatter(new TextFormatter<Integer>(change -> {
            if (change.isDeleted())
                return change;
            String str = change.getControlNewText();
            if (str.matches("0\\d+"))
                return null;
            try {
                double n = Integer.parseInt(str);
                return 0 <= n && n <= 9999 ? change : null;
            } catch (NumberFormatException e) {
                return null;
            }
        }));

        metersBox.setTextFormatter(new TextFormatter<Integer>(change -> {
            if (change.isDeleted())
                return change;
            String str = change.getControlNewText();
            if (str.matches("0\\d+"))
                return null;
            try {
                double n = Integer.parseInt(str);
                return 0 <= n && n <= 9999 ? change : null;
            } catch (NumberFormatException e) {
                return null;
            }
        }));

        metersBox.textProperty().addListener((observable, oldValue, newValue) -> {
            if (metersBox.getText().isEmpty())
                return;
            mapScaleMeters = Double.parseDouble(newValue);
            updateCalc();
            metersBox.setText(newValue);
        });

        velocityBox.textProperty().addListener((observable, oldValue, newValue) -> {
            if (velocityBox.getText().isEmpty())
                return;
            shellVelocity = Double.parseDouble(newValue);
            updateCalc();
            velocityBox.setText(newValue);
        });
    }

    @FXML
    void clearBoth(ActionEvent event) {
        clearPlayer();
        clearTarget();
    }

    @FXML
    void close(ActionEvent event) {
        init.stop();
    }

    @FXML
    void convert(ActionEvent event) {
        if (studsBox.getText().isEmpty()) 
            return;
        metersBox.setText(Integer.toString((int)MathUtils.studsToMeters(Integer.parseInt(studsBox.getText()))));
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

    @FXML
    void openAbout(ActionEvent event) {

    }

    @FXML
    void openHelp(ActionEvent event) {

    }

    @FXML
    void setDebug(ActionEvent event) {
        Init.debug = menuSettingsDebug.isSelected();
    }
    
    /**
     * starts main loop
     * 
     * @param event event of button being clicked, not important
     */
    @FXML
    void startCapture(ActionEvent event) {
        if (Tick.getNumThreads() < 1)
            Main.tick = new Tick(Init.TICKRATE);
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

    void clearPlayer() {
        setPlayer(0, 0);
        playerCircle.setRadius(0);
    }

    void clearTarget() {
        setTarget(0, 0);
        targetCircle.setRadius(0);
    }

    void setTarget(double x, double y) {
        target.setLocation(x, y);
        targetCircle.relocate(x-5, y-5);
        targetCircle.setRadius(5);
        updateCalc();
    }

    void setPlayer(double x, double y) {
        player.setLocation(x, y);
        playerCircle.relocate(x-5, y-5);
        playerCircle.setRadius(5);
        updateCalc();
    }

    public void setInit(Init init) {
        this.init = init;
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

    public void setTargetToPing(double[] ping) {
        setTarget(ping[0]*1.3, ping[1]*1.3);
    }

    /**
     * @param px pixel position on larger map in GUI (442x442)
     * @return position on real map image (330x330)
     */
    public static int scaleConv(double px) {
        return (int)((px - 13) / 1.3);
    }

    //TODO use callbacks for both of these
    //TODO have appendList load cards and add to vbox with ping info
    public void appendList(double[] ping) {

    }

    //TODO have updatePing (called when refining a pings confidence) update the ping object in our list
    public void updatePing(int id, double[] ping) {

    }

    //TODO update labels with calculations
    private void updateCalc() {
        if (targetCircle.getRadius() == 0 || playerCircle.getRadius() == 0)
            return;
        
        targetMeters = MathUtils.pxPointToMeters(new Point(scaleConv(target.getX()), scaleConv(target.getY())), 9, 330, mapScaleMeters);
        playerMeters = MathUtils.pxPointToMeters(new Point(scaleConv(player.getX()), scaleConv(player.getY())), 9, 330, mapScaleMeters);

        double distance = MathUtils.distance(playerMeters, targetMeters);
        double directAngle = MathUtils.directAngle(distance, shellVelocity);
        double indirectAngle = MathUtils.indirectAngle(distance, shellVelocity);
        double directFlightTime = MathUtils.flightTime(directAngle, shellVelocity, distance);
        double indirectFlightTime = MathUtils.flightTime(indirectAngle, shellVelocity, distance);
        double maxRange = MathUtils.maxRange(shellVelocity);
        System.out.println("---------------------------------");
        System.out.println("distance to target: " + distance + "m");
        System.out.println("direct angle " + directAngle + " degrees");
        System.out.println("indirect angle " + indirectAngle + " degrees");
        System.out.println("direct flight time " + directFlightTime + " seconds");
        System.out.println("indirect flight time " + indirectFlightTime + " seconds");
        System.out.println("max range " + maxRange + "m");
    }

    //TODO consider if having a button for this makes sense even
    public void clearCalc() {

    }

}