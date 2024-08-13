package com.verellum.multicrew.arty;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Scanner;

import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import nu.pattern.OpenCV;

/**
 * Hello world!
 *
 */
public class App 
{

    public static void main( String[] args ) throws URISyntaxException, FileNotFoundException, InterruptedException
    {
        OpenCV.loadLocally();
        Mat img1 = Imgcodecs.imread("src/main/resources/com/verellum/multicrew/arty/maps/chernobyl.png");

        String path1 = "src/main/resources/com/verellum/multicrew/arty/icons/circle.png";
        String path2 = "src/main/resources/com/verellum/multicrew/arty/maps/test6.png";
        String path3 = "src/main/resources/com/verellum/multicrew/arty/icons/circle.png";
        String path4 = "src/main/resources/com/verellum/multicrew/arty/maps/default_homography.xml";
        String[] paths = {path2, path1, path3};
        new TemplateMatch().run(paths);
        /*paths[0] = path1;
        paths[1] = path2;
        paths[2] = path4;
        new AKAZEMatch().run(paths);*/
    }

    
    
}
