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
    public static byte runStatus;
    public static BufferedImage mapTemplate;
    public static BufferedImage mapImage;
    public static Rectangle mapRegion;
    
    private static final long timeBetweenTicks = 1000/1;
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

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

    public static void beginCapture(){
        //Return early in case the map has not been detected yet
        if (mapRegion == null){
            return;
        }

        if(runStatus == 0){
            runStatus = 1;
            executor.scheduleWithFixedDelay(()-> {
                tick();
            },0,timeBetweenTicks,TimeUnit.MILLISECONDS);
        }
    }

    /**
     * The method to be called every update tick
     */
    private static void tick(){
        //TODO: draw the rest of the owl
    }

    
    

}
