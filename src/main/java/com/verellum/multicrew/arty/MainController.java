package com.verellum.multicrew.arty;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
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
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
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
import javafx.scene.shape.Line;

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

    private Line line;

    private double mapScaleMeters;
    private double shellVelocity;

    private LinkedList<PingController> pingCards;

    private double[] calculations = {0,0,0,0,0,0,0};

    /**
     * its still kinda hardcoded in initialize cause the offset of 13 is the 10px margin in the map template multiplied by 1.3
     * but it would be slightly less annoying to do window scaling now if we really wanted (i dont but if you two want it super bad i could)
     */
    private static double guiScaleModifier;

    @FXML
    private Label azimuth;

    @FXML
    private Button clipboardButton;

    @FXML
    private Button conversionButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Label directElevation;

    @FXML
    private Label directTTI;

    @FXML
    private Label distance;

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

    /**
     * 
     */
    @FXML
    void initialize() {
        //Initialize variables
        pingCards = new LinkedList<PingController>();

        //Initialize tooltips
        conversionButton.setTooltip(new Tooltip("Convert studs (right) to meters (left)"));
        clipboardButton.setTooltip(new Tooltip("Copy calculations to clipboard"));
        deleteButton.setTooltip(new Tooltip("Clear pings list"));
        guiScaleModifier = (imgParent.getPrefWidth() - 13) / 330;

        //Initialize location display circles and helpers
        target = new Point();
        player = new Point();
        targetCircle = new Circle(0, Color.RED);
        playerCircle = new Circle(0, Color.BLUE);
        previewCircle = new Circle(0);
        line = new Line();

        //Disable interaction on non-GUI elements, allow passthrough of inputs to the map
        targetCircle.setMouseTransparent(true);
        playerCircle.setMouseTransparent(true);
        previewCircle.setMouseTransparent(true);
        line.setMouseTransparent(true);

        //Preview circle style
        previewCircle.setFill(new Color(0,0,0,0));
        previewCircle.setStroke(Color.MAGENTA);
        previewCircle.setStrokeWidth(2);
        previewCircle.getStrokeDashArray().add(Math.PI*5*(2d / 5d));
        previewCircle.getStrokeDashArray().add(Math.PI*5*(3d / 5d));

        //Line style
        line.setStrokeWidth(2);
        line.setStroke(Color.BLACK);
        line.getStrokeDashArray().add(0,10d);
        line.getStrokeDashArray().add(0,5d);

        //Add the elements
        imgParent.getChildren().add(line);
        imgParent.getChildren().add(targetCircle);
        imgParent.getChildren().add(playerCircle);
        imgParent.getChildren().add(previewCircle);

        //Initialize the player and target set functionality
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

        //Initialize the text formatter for the studs input box
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

        //Initialize the text formatter for the meters input box
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

        //Initialize the text formatter for the projectile velociy box
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

        //Functionality for the meters input box
        metersBox.textProperty().addListener((observable, oldValue, newValue) -> {
            if (metersBox.getText().isEmpty())
                return;
            mapScaleMeters = Double.parseDouble(newValue);
            updateCalc();
            metersBox.setText(newValue);
        });

        //Functionality for the velocity input box
        velocityBox.textProperty().addListener((observable, oldValue, newValue) -> {
            if (velocityBox.getText().isEmpty())
                return;
            shellVelocity = Double.parseDouble(newValue);
            updateCalc();
            velocityBox.setText(newValue);
        });

        //Toggle capture button functionality
        toggleCaptureButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                startCapture(null);
            } else {
                stopCapture(null);
            }
        });
    }

    
    /** 
     * @param event
     */
    @FXML
    void clearBoth(ActionEvent event) {
        clearPlayer();
        clearTarget();
    }

    @FXML
    void clearMap(ActionEvent event) {
        imgParent.setBackground(Background.EMPTY);
    }

    
    /** 
     * @param event
     */
    @FXML
    void close(ActionEvent event) {
        init.stop();
        Platform.exit();
    }

    
    /** 
     * @param event
     */
    @FXML
    void convert(ActionEvent event) {
        if (studsBox.getText().isEmpty()) 
            return;
        metersBox.setText(Integer.toString((int)MathUtils.studsToMeters(Integer.parseInt(studsBox.getText()))));
    }

    
    /** 
     * @param event
     */
    @FXML
    void copyClipboard(ActionEvent event) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
            new StringSelection(
                "Distance: " + distance.getText() +
                " Elevation: " + directElevation.getText() + 
                " / " + indirectElevation.getText() + 
                " Azimuth: " + azimuth.getText()
            ), null
        );
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
     * calls appropriate methods to clear list of saved pings
     * @param event
     */
    @FXML
    void flushPings(ActionEvent event) {
        pingList.getChildren().clear();
        pingCards.clear();
        PingDetect.flush();
    }

    
    /** 
     * opens about window
     * @param event
     */
    @FXML
    void openAbout(ActionEvent event) {

    }

    
    /** 
     * opens help guide window
     * @param event
     */
    @FXML
    void openHelp(ActionEvent event) {

    }

    
    /** 
     * toggles debug setting
     * @param event
     */
    @FXML
    void setDebug(ActionEvent event) {
        Init.debug = menuSettingsDebug.isSelected();
    }

    @FXML
    void setScale1(ActionEvent event) {

    }

    @FXML
    void setScale125(ActionEvent event) {

    }

    @FXML
    void setScale15(ActionEvent event) {

    }

    @FXML
    void setScale175(ActionEvent event) {

    }

    @FXML
    void setScale2(ActionEvent event) {

    }
    
    /**
     * starts main loop
     * 
     * @param event event of button being clicked, not important
     */
    void startCapture(ActionEvent event) {
        if (TickFactory.getTickNum() < 1) {
            Main.tick = TickFactory.createTick(Init.TICKRATE);
            screencapButton.setDisable(true);
        } else {
            TickFactory.reviveTicks();
            screencapButton.setDisable(true);
        }
    }

    /**
     * IMMEDIATELY KILLS ALL TICKS
     * because if a tick thread hangs, now it can be killed rather than be inaccesible
     * 
     * @param event event of button being clicked, not important
     */
    @FXML
    void stopCapture(ActionEvent event) {
        //featuring fun double colon lambda shortcut cuz i wanted to learn that
        TickFactory.stopAll();
        screencapButton.setDisable(false);
    }

    private void clearPlayer() {
        setPlayer(0, 0);
        playerCircle.setRadius(0);
    }

    private void clearTarget() {
        setTarget(0, 0);
        targetCircle.setRadius(0);
    }

    public void clearPreview() {
        setPreview(0, 0, 0);
    }

    public BufferedImage getBackgroundImage() {
        return SwingFXUtils.fromFXImage(imgParent.getBackground().getImages().get(0).getImage(), null);
    }

    
    /** 
     * @param x
     * @param y
     */
    //TODO un-hardcode radius
    public void setTarget(double x, double y) {
        target.setLocation(x, y);
        targetCircle.setCenterX(x);
        targetCircle.setCenterY(y);
        targetCircle.setRadius(5);
        drawLine();
        updateCalc();
    }

    private void drawLine() {
        if (targetCircle.getRadius() == 0 || playerCircle.getRadius() == 0) {
            line.setStartX(0);
            line.setStartY(0);
            line.setEndX(0);
            line.setEndY(0);
            return;
        }
        line.setEndX(target.getX());
        line.setEndY(target.getY());
        line.setStartX(player.getX());
        line.setStartY(player.getY());
    }

    
    /** 
     * @param x
     * @param y
     */
    private void setPlayer(double x, double y) {
        player.setLocation(x, y);
        playerCircle.setCenterX(x);
        playerCircle.setCenterY(y);
        playerCircle.setRadius(5);
        drawLine();
        updateCalc();
    }

    
    /** 
     * @param x
     * @param y
     * @param radius
     */
    private void setPreview(double x, double y, double radius) {
        previewCircle.relocate(x, y);
        previewCircle.setRadius(radius);
    }

    
    /** 
     * @param init
     */
    public void setInit(Init init) {
        this.init = init;
    }

    
    /** 
     * @param sc
     */
    public void setScreenCapture(ScreenCapture sc) {
        this.sc = sc;
    }

    
    /** 
     * @param bi
     */
    //i had to kill the imageview, otherwise it causes refresh issues, FUCK KNOWS WHY
    public void setImageView(BufferedImage bi) {
        imgParent.setBackground(new Background(
            new BackgroundImage(SwingFXUtils.toFXImage(bi, null), 
            null, 
            null, 
            null, 
            new BackgroundSize(420, 420, false, false, true, true))
        ));
        // if (!SwingFXUtils.fromFXImage(imgParent.getBackground().getImages().get(0).getImage(), null).equals(bi)) {
        //     try {
        //         throw new Exception();
        //     } catch (Exception e) {
        //         e.printStackTrace();
        //     }
        // }
        // imageView.setImage(SwingFXUtils.toFXImage(bi, null));
    }

    
    /** 
     * sets the current target to a specific ping location
     * @param ping
     */
    public void setTargetToPing(double[] ping) {
        setTarget(ping[0]*guiScaleModifier, ping[1]*guiScaleModifier);
    }

    
    /** 
     * sets the preview circle to be on the location of a specific ping
     * @param ping
     */
    public void setPreviewToPing(double[] ping) {
        setPreview(ping[0]*guiScaleModifier, ping[1]*guiScaleModifier, 10);
    }

    
    /** 
     * @param time
     */
    public void animate(double time){
        previewCircle.setStrokeDashOffset(time % 15);
        line.setStrokeDashOffset(-time % 15);
    }

    /**
     * @param px pixel position on larger map in GUI (442x442)
     * @return position on real map image (330x330)
     */
    private static int scaleConv(double px) {
        return (int)((px - (10*guiScaleModifier)) / guiScaleModifier);
    }

    
    /** 
     * calls the method to load the ping fxml and add it to the list
     * @param ping
     */
    public void appendList(double[] ping) {
        loadFXML("ping.fxml", ping, this);
    }

    
    /** 
     * given a ping id and new location it updates the location stored in that ping
     * @param id
     * @param ping
     */
    public void updatePing(int id, double[] ping) {
        Platform.runLater(() -> {
            pingCards.get(id).setLocation(ping);
        });
    }

    
    /** 
     * removes a specific ping from the list by its ID
     * @param id
     */
    public void removePing(int id) {
        pingList.getChildren().remove(pingCards.get(id).getAnchorPane());
        pingCards.remove(pingCards.get(id));
    }

    
    /** 
     * @return LinkedList<PingController>
     */
    public LinkedList<PingController> getPingList() {
        return pingCards;
    }

    
    /** 
     * loads ping.fxml, adds it to the linkedlist and binds it to the maincontroller
     * @param fxml
     * @param location
     * @param mc
     */
    private void loadFXML(String fxml, double[] location, MainController mc) {
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml));
                AnchorPane par;
                par = fxmlLoader.load();
                PingController pc = (PingController) fxmlLoader.getController();
                pingCards.add(pc);
                pingList.getChildren().add(par);
                par.toBack();
                pc.bindings(location, mc);
            } catch (IOException e) {
                e.printStackTrace();
            }
                
        });
        
    }

    private void updateCalc() {
        if (targetCircle.getRadius() == 0 || playerCircle.getRadius() == 0)
            return;
        
        DecimalFormat df = new DecimalFormat("#.##");
        targetMeters = MathUtils.pxPointToMeters(new Point(scaleConv(target.getX()), scaleConv(target.getY())), 9, 330, mapScaleMeters);
        playerMeters = MathUtils.pxPointToMeters(new Point(scaleConv(player.getX()), scaleConv(player.getY())), 9, 330, mapScaleMeters);

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

        distance.setText(df.format(calculations[0])+"m");
        directElevation.setText(df.format(calculations[1])+"°");
        indirectElevation.setText(df.format(calculations[2])+"°");
        directTTI.setText(df.format(calculations[3])+"s"); 
        indirectTTI.setText(df.format(calculations[4])+"s");
        azimuth.setText(df.format(calculations[6])+"°");
    }

    
    /** 
     * @return double
     */
    public double getMapScaleMeters() {
        return mapScaleMeters;
    }

}