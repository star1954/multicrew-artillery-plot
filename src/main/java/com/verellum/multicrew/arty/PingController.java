package com.verellum.multicrew.arty;

import java.awt.Point;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class PingController extends Controller {

    private double[] location;
    private MainController mc;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button deleteButton;

    @FXML
    private Label grid;

    @FXML
    private Label xy;

    public void bindings(double[] location, MainController mc) {
        this.mc = mc;
        setLocation(location);
    }

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }


    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
        Point metersPoint = MathUtils.pxPointToMeters(new Point((int)location[0], (int)location[1]), 9, 330, mc.getMapScaleMeters());
        xy.setText("(" + metersPoint.x + "m, " + metersPoint.y + "m)");
        grid.setText(MathUtils.metersPointToGrid(metersPoint, 9, mc.getMapScaleMeters()));
    }

    @FXML
    void delete(ActionEvent event) {
        PingDetect.prune(mc.getPingList().indexOf(this));
        mc.removePing(mc.getPingList().indexOf(this));
    }

    //TODO make pingcontroller look pressed like button when moiseclick is held
    //TODO maybe switch to eventhandler instead of 2 
    @FXML
    void select(MouseEvent event) {
        mc.setTargetToPing(location);
    }

    @FXML
    void press(MouseEvent event) {
        System.out.println("press");
        anchorPane.getStyleClass().setAll("pingpressed");
    }

    @FXML
    void release(MouseEvent event) {
        System.out.println("relase");
        anchorPane.getStyleClass().setAll("ping");
    }

    @FXML
    void mouseEnter(MouseEvent event){
        mc.setPreviewToPing(location);
    }
    @FXML
    void mouseExit(MouseEvent event){
        mc.clearPreview();
    }
    
}
