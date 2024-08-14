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

    public BufferedImage capture() {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return robot.createScreenCapture(screenRect);
    }

    public void output(BufferedImage bi) {
        File output = new File("output.jpg");
        try {
			ImageIO.write(bi, "jpg", output);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

}
