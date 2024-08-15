package com.verellum.multicrew.arty;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.bytedeco.javacpp.*;

import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class TemplateMatch {

    /**
     * @param bi BufferedImage to run template matching on
     * @param template template to be matched
     * @return Rectangle with coordinates onscreen where best fit match is found
     */
    public static Rectangle matchRect(BufferedImage bi, BufferedImage template) {
        // read in image default colors

        Mat sourceColor = ScreenCapture.bufferedImageToMat(bi);
        Mat sourceGrey = new Mat(sourceColor.size(), CV_8UC1);
        cvtColor(sourceColor, sourceGrey, COLOR_BGR2GRAY);
        // load template
        Mat templateColor = ScreenCapture.bufferedImageToMat(template);
        Mat templateGray = new Mat(templateColor.size(), CV_8UC1);
        cvtColor(templateColor, templateGray, COLOR_BGR2GRAY);
        // Size for the result image
        Size size = new Size(sourceGrey.cols() - templateGray.cols() + 1, sourceGrey.rows() - templateGray.rows() + 1);
        Mat result = new Mat(size, CV_32FC1);
        //was TM_CCORR_NORMED before
        matchTemplate(sourceGrey, templateGray, result, TM_CCOEFF_NORMED);
        DoublePointer minVal = new DoublePointer();
        DoublePointer maxVal = new DoublePointer();
        Point min = new Point();
        Point max = new Point();
        minMaxLoc(result, minVal, maxVal, min, max, null);
        rectangle(sourceColor, new Rect(max.x(), max.y(), templateGray.cols(), templateGray.rows()), MathU.randColor(),
                2, 0, 0);
        return new Rectangle(max.x(), max.y(), templateGray.cols(), templateGray.rows());
    }

    /**
     * Overload
     * @param bi BufferedImage to run template matching on
     * @param template template to be matched
     * @param mask mask for template
     * @return Rectangle with coordinates onscreen where best fit match is found
     */
    public static Rectangle matchRect(BufferedImage bi, BufferedImage template, BufferedImage mask) {
        // read in image default colors

        Mat sourceColor = ScreenCapture.bufferedImageToMat(bi);
        Mat sourceGrey = new Mat(sourceColor.size(), CV_8UC1);
        cvtColor(sourceColor, sourceGrey, COLOR_BGR2GRAY);
        // load template
        Mat templateColor = ScreenCapture.bufferedImageToMat(template);
        Mat templateGray = new Mat(templateColor.size(), CV_8UC1);
        cvtColor(templateColor, templateGray, COLOR_BGR2GRAY);
        // load mask
        Mat maskColour = ScreenCapture.bufferedImageToMat(mask);
        Mat maskGrey = new Mat(templateColor.size(), CV_8UC1);
        cvtColor(maskColour, maskGrey, COLOR_BGR2GRAY);
        // Size for the result image
        Size size = new Size(sourceGrey.cols() - templateGray.cols() + 1, sourceGrey.rows() - templateGray.rows() + 1);
        Mat result = new Mat(size, CV_32FC1);
        //was TM_CCORR_NORMED before
        matchTemplate(sourceGrey, templateGray, result, TM_CCOEFF_NORMED, maskGrey);
        DoublePointer minVal = new DoublePointer();
        DoublePointer maxVal = new DoublePointer();
        Point min = new Point();
        Point max = new Point();
        minMaxLoc(result, minVal, maxVal, min, max, null);
        rectangle(sourceColor, new Rect(max.x(), max.y(), templateGray.cols(), templateGray.rows()), MathU.randColor(),
                2, 0, 0);
        return new Rectangle(max.x(), max.y(), templateGray.cols(), templateGray.rows());
    }

}