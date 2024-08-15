package com.verellum.multicrew.arty;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javafx.scene.Scene;

/**
 * Hello world!
 *
 */
public class Main {

    static ScreenCapture sc;
    static Scene scene;
    public static BufferedImage mapTemplate;
    public static BufferedImage mapImage;
    public static BufferedImage[] pingTemplate;
    public static Rectangle mapRegion;
    public static Tick tick;

    private static MainController mc;
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        sc = new ScreenCapture();
        Init.initApp();
    }

    public static void setMainController(MainController mc) {
        Main.mc = mc;
    }

    public static MainController getMainController() {
        return mc;
    }

}
