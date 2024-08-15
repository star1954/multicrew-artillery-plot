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
    //public static String[] paths = { null, null };
    //public static String path1, path2, path3, path4;
    //public static Mat template = imread(path1,0);

    
    

    /**
     * @param args
     * @throws URISyntaxException
     * @throws FileNotFoundException
     * @throws InterruptedException
     */
    public static void main(String[] args) {
        sc = new ScreenCapture();
        Init.initApp();
    }

    /**
     * The method to be called every update tick
     */
    private static void tick(){

    }
    

}
