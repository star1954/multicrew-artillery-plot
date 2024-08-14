package com.verellum.multicrew.arty;


import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.bytedeco.javacpp.*;

import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class TemplateMatch {

    public static Rectangle cropToMatch(BufferedImage bi, String templatePath){
        //read in image default colors
        Mat sourceColor = ScreenCapture.bufferedImageToMat(bi);
        Mat sourceGrey = new Mat(sourceColor.size(), CV_8UC1);
        cvtColor(sourceColor, sourceGrey, COLOR_BGR2GRAY);
        //load template
        Mat template = imread(templatePath ,IMREAD_COLOR);
        //Size for the result image
        Size size = new Size(sourceGrey.cols()-template.cols()+1, sourceGrey.rows()-template.rows()+1);
        Mat result = new Mat(size, CV_32FC1);
        matchTemplate(sourceColor, template, result, TM_CCORR_NORMED);
        
        DoublePointer minVal= new DoublePointer();
        DoublePointer maxVal= new DoublePointer();
        Point min = new Point();
        Point max = new Point();
        minMaxLoc(result, minVal, maxVal, min, max, null);
        rectangle(sourceColor,new Rect(max.x(),max.y(),template.cols(),template.rows()), MathU.randColor(), 2, 0, 0);
        return new Rectangle(max.x(), max.y(), template.cols(), template.rows());
    }
}