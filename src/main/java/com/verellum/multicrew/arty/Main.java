package com.verellum.multicrew.arty;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bytedeco.opencv.opencv_core.Mat;

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
