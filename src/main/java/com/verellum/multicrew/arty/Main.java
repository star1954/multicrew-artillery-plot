package com.verellum.multicrew.arty;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
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
    public static BufferedImage mapImage;
    public static Rectangle mapRegion;

    private static final long timeBetweenTicks = 1000/15;

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
        init(); //run the initialization steps
        
    }

    public static Thread beginCapture(){
        runStatus = 1;
        //Return early in case the map has not been detected yet
        if (mapRegion == null){
            return null;
        }
        Thread loopThread = new Thread(() -> captureLoop());
        return loopThread;
    }

    private static void captureLoop(){
        long lts = System.currentTimeMillis();
        while (runStatus == 1){
            if(lts + timeBetweenTicks >= System.currentTimeMillis()){
                try {
                    Thread.sleep((lts+timeBetweenTicks) - System.currentTimeMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            tick();
        }
    }

    /**
     * The method to be called every update tick
     */
    private static void tick(){

    }

    
    

}
