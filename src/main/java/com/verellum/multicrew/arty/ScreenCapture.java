package com.verellum.multicrew.arty;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

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
     *         This captures the main monitor
     */
    public BufferedImage capture() {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return robot.createScreenCapture(screenRect);
    }

    /**
     * @param bi The BufferedImage to write to storage
     *           Writes the image to the storage as output.jpg
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
     *                 Writes the image to the storage as a .jpg file
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

}
