package com.verellum.multicrew.arty;

import java.awt.Rectangle;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class MainController extends Controller {

    private Init init;

    private ScreenCapture sc;
    private Point player;
    private Point playerMeters;

    private Point target;
    private Point targetMeters;

    private Circle playerCircle;
    private Circle targetCircle;
    private Circle previewCircle;

    private double mapScaleMeters;
    private double shellVelocity;

    private LinkedList<PingController> pingCards;

    private double[] calculations = {0,0,0,0,0,0,0};

    private AnchorPane ap;

    @FXML
    private Label azimuth;

    @FXML
    private Button conversionButton;

    @FXML
    private Label directElevation;

    @FXML
    private Label directTTI;

    @FXML
    private GridPane gridPane;

    @FXML
    private Pane imgParent;

    @FXML
    private Label indirectElevation;

    @FXML
    private Label indirectTTI;

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
    private TextField studsBox;

    @FXML
    private ToggleButton toggleCaptureButton;

    @FXML
    private TextField velocityBox;

    @FXML
    void initialize() {
        target = new Point();
        player = new Point();
        targetCircle = new Circle(0, Color.RED);
        playerCircle = new Circle(0, Color.BLUE);
        previewCircle = new Circle(0, Color.CORAL);

        targetCircle.setMouseTransparent(true);
        playerCircle.setMouseTransparent(true);
        previewCircle.setMouseTransparent(true);
        
        previewCircle.getStyleClass().add(".circlenofill");

        imgParent.getChildren().add(targetCircle);
        imgParent.getChildren().add(playerCircle);
        imgParent.getChildren().add(previewCircle);

        pingCards = new LinkedList<PingController>();

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

        toggleCaptureButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                startCapture(null);
            else
                stopCapture(null);
        });


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
        loadFXML("ping.fxml", new double[] {110, 230, 1}, this);
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

    @FXML
    void clearBoth(ActionEvent event) {
        clearPlayer();
        clearTarget();
    }

    private void clearPlayer() {
        setPlayer(0, 0);
        playerCircle.setRadius(0);
    }

    private void clearTarget() {
        setTarget(0, 0);
        targetCircle.setRadius(0);
    }

    private void clearPreview() {
        setPreview(0, 0, 0);
    }

    //TODO un-hardcode radius
    public void setTarget(double x, double y) {
        target.setLocation(x, y);
        targetCircle.relocate(x-5, y-5);
        targetCircle.setRadius(5);
        updateCalc();
    }

    private void setPlayer(double x, double y) {
        player.setLocation(x, y);
        playerCircle.relocate(x-5, y-5);
        playerCircle.setRadius(5);
        updateCalc();
    }

    private void setPreview(double x, double y, double radius) {
        previewCircle.relocate(x-radius, y-radius);
        previewCircle.setRadius(radius);
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

    //TODO remove hardcode of 1.3 scaling modifier
    public void setTargetToPing(double[] ping) {
        setTarget(ping[0]*1.3, ping[1]*1.3);
    }

    //TODO remove hardcode of 1.3 scaling modifier and ping display sizes
    public void setPreviewToPing(double[] ping) {
        setPreview(ping[0]*1.3, ping[1]*1.3, ping[2]*5 + 2);
    }

    /**
     * @param px pixel position on larger map in GUI (442x442)
     * @return position on real map image (330x330)
     */
    private static int scaleConv(double px) {
        return (int)((px - 13) / 1.3);
    }

    public void appendList(double[] ping) {
        loadFXML("ping.fxml", ping, this);
    }

    public void updatePing(int id, double[] ping) {
        Platform.runLater(() -> {
            pingCards.get(id).setLocation(ping);
        });
    }

    public void removePing(int id) {
        pingList.getChildren().remove(pingCards.get(id).getAnchorPane());
        pingCards.remove(pingCards.get(id));
    }

    public LinkedList<PingController> getPingList() {
        return pingCards;
    }

    private void loadFXML(String fxml, double[] location, MainController mc) {
        System.out.println(location[0]);
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml));
                AnchorPane par;
                par = fxmlLoader.load();
                PingController pc = (PingController) fxmlLoader.getController();
                pingCards.add(pc);
                pingList.getChildren().add(par);
                pc.bindings(location, mc);
            } catch (IOException e) {
                e.printStackTrace();
            }
                
        });
        
    }

    //TODO update labels with calculations
    private void updateCalc() {
        if (targetCircle.getRadius() == 0 || playerCircle.getRadius() == 0)
            return;
        
        DecimalFormat df = new DecimalFormat("#.##");
        targetMeters = MathUtils.pxPointToMeters(new Point(scaleConv(target.getX()), scaleConv(target.getY())), 9, 330, mapScaleMeters);
        playerMeters = MathUtils.pxPointToMeters(new Point(scaleConv(player.getX()), scaleConv(player.getY())), 9, 330, mapScaleMeters);

        //TODO distance to target label in FXML
        //distance
        calculations[0] = MathUtils.distance(playerMeters, targetMeters);
        //direct
        calculations[1] = MathUtils.directAngle(calculations[0], shellVelocity);
        //indirect
        calculations[2] = MathUtils.indirectAngle(calculations[0], shellVelocity);
        //TTI direct
        calculations[3] = MathUtils.flightTime(calculations[1], shellVelocity, calculations[0]);
        //TII indirect
        calculations[4] = MathUtils.flightTime(calculations[2], shellVelocity, calculations[0]);
        //max range
        calculations[5] = MathUtils.maxRange(shellVelocity);
        //azimuth
        calculations[6] = MathUtils.azimuth(playerMeters, targetMeters);

        //TODO add units
        directElevation.setText(df.format(calculations[1]));
        indirectElevation.setText(df.format(calculations[2]));
        directTTI.setText(df.format(calculations[3]));
        indirectTTI.setText(df.format(calculations[4]));
        azimuth.setText(df.format(calculations[6]));
    }

    public double getMapScaleMeters() {
        return mapScaleMeters;
    }

}