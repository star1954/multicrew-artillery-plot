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
    public static String[] paths = { null, null };
    public static String path1, path2, path3, path4;
    public static Mat template = imread(path1,0);

    /**
     * @param args
     * @throws URISyntaxException
     * @throws FileNotFoundException
     * @throws InterruptedException
     */
    public static void main(String[] args) {
        path1 = "src/main/resources/com/verellum/multicrew/arty/maps/chernobyl.png";
        path2 = "src/main/resources/com/verellum/multicrew/arty/maps/test4.png";
        path3 = "src/main/resources/com/verellum/multicrew/arty/icons/circle_test2.png";
        path4 = "src/main/resources/com/verellum/multicrew/arty/maps/default_homography.xml";
        //template goes second
        paths[1] = path1;
        paths[0] = path2;
        sc = new ScreenCapture();
        Init.launch();
    }

}
