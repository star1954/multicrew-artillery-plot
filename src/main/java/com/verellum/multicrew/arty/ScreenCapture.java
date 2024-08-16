package com.verellum.multicrew.arty;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.opencv.opencv_core.Mat;

/**
 * Utility class to take screenshots
 */
public class ScreenCapture {

    private Robot robot;

    ScreenCapture() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return BufferedImage The screen captured
     * <p> (This captures the main monitor)
     */
    public BufferedImage capture() {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return robot.createScreenCapture(screenRect);
    }

    /**
     * overload of capture()
     * @param region rectangle of screen to be captured
     * @return BufferedImage capture of the specific portion of screen
     */
    public BufferedImage capture(Rectangle region) {
        return robot.createScreenCapture(region);
    }

    /**
     * @param bi The BufferedImage to write to storage
     * <p> Writes the image to the storage as output.jpg
     */
    public void output(BufferedImage bi) {
        File output = new File("temp/output.jpg");
        output.mkdirs(); //Create the temp directory if it isn't there already
        try {
            ImageIO.write(bi, "jpg", output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param bi       The BufferedImage to write to storage
     * @param fileName The name of the file to be written
     * <p> Writes the image to the storage as a .jpg file
     */
    public void output(BufferedImage bi, String fileName) {
        File output = new File("temp/".concat(fileName));
        output.mkdirs(); //Create the temp directory if it isn't there already
        try {
            ImageIO.write(bi, "jpg", output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param bi BufferedImage input to be converted
     * @return Mat from converted image
     */
    public static Mat bufferedImageToMat(BufferedImage bi) {
        return Java2DFrameUtils.toMat(bi);
    }

    /**
     * @param bi BufferedImage to be cropped
     * @param rect rectangle used to determine crop area
     * @return cropped image
     */
    public static BufferedImage cropImage(BufferedImage bi, Rectangle rect) {
        return bi.getSubimage(rect.x, rect.y, rect.width, rect.height);
    }

    /**
     * @param bi input image to be STRIPPED OF ITS DASTARDLY GREEN
     * @return purified green-free image
     */
    public static BufferedImage removeGreenChannel(BufferedImage bi) {
        BufferedImage result = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
        for (int w = 0; w < bi.getWidth(); w++) {
            for (int h = 0; h < bi.getHeight(); h++ ) {
                int rgb = bi.getRGB(w,h);
                //bitshift magic idk
                int r = rgb >> 16 & 0xff;
                // int g = rgb >> 8 & 0xff;
                int b = rgb & 0xff;
                result.setRGB(w, h, (r << 16 | b));
            }
        }
        return result;
    }

}
