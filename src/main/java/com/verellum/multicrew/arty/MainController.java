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
    void initialize() {
        
    }
    
    @FXML
    void screencap(ActionEvent event) {
        System.out.println("attempting capture!!!");
        BufferedImage bi = sc.capture();
        imageView.setImage(SwingFXUtils.toFXImage(ScreenCapture.cropImage(bi, TemplateMatch.cropToMatch(bi, App.paths[1])), null));
    }

    public ScreenCapture getsScreenCapture() {
        return sc;
    }

    public void setScreenCapture(ScreenCapture sc) {
        this.sc = sc;
    }

}
