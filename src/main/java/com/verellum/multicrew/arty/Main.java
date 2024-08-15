package com.verellum.multicrew.arty;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import org.bytedeco.opencv.opencv_core.Mat;

import javafx.scene.Scene;

/**
 * Hello world!
 *
 */
public class Main {

    static ScreenCapture sc;
    static Scene scene;
    public static int runStatus;

    private static void init(){
        runStatus = 0;
        Init.initApp();
    }
    

    /**
     * @param args
     * @throws URISyntaxException
     * @throws FileNotFoundException
     * @throws InterruptedException
     */
    public static void main(String[] args) {
        sc = new ScreenCapture();
        init();//run the initialization steps
        
    }

    public static void beginCapture(){
        runStatus = 1;
    }

    private static void captureLoop(){

    }

    /**
     * The method to be called every update tick
     */
    private static void tick(){

    }

    
    

}
