package com.verellum.multicrew.arty;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Tick implements Runnable{

    private boolean isRunning = false;

    private final double PINGSCALE = 10;

    public Tick() {}

    @Override
    public void run() {
        if (Main.mapRegion == null)
            return;
        while (isRunning) {
            Main.mapImage = Main.sc.capture(Main.mapRegion);
            
            BufferedImage noGreen = ScreenCapture.removeGreenChannel(Main.mapImage);

            //draw for debug
            Graphics2D g2d = Main.mapImage.createGraphics();

            //TODO maaaaybe multithread the 3 template matches :))))))))

            //Detect Pings
            Rectangle region1 = TemplateMatch.matchRect(noGreen, Main.pingTemplate[0]);
            Rectangle region2 = TemplateMatch.matchRect(noGreen, Main.pingTemplate[1]);
            Rectangle region3 = TemplateMatch.matchRect(noGreen, Main.pingTemplate[2]);
            
            if (Init.debug){
                g2d.setColor(Color.RED);
                g2d.drawRect(region1.x,region1.y,region1.width,region1.height);
                g2d.setColor(Color.CYAN);
                g2d.drawRect(region2.x,region2.y,region2.width,region2.height);
                g2d.setColor(Color.GREEN);
                g2d.drawRect(region3.x,region3.y,region3.width,region3.height);
            }
            
            PingDetect.pushRects(region1, region2, region3);
            
            PingDetect.calcCorrelations(new Main.Callback<Double[], Object>() {
                @Override
                public void call(Double[] output) {
                    Main.getMainController().appendList(Arrays.stream(output).mapToDouble(Double::doubleValue).toArray());
                }
            }, new Main.Callback<Integer, Double[]>() {
                @Override
                public void callTwo(Integer i, Double[] output) {
                    Main.getMainController().updatePing(i, Arrays.stream(output).mapToDouble(Double::doubleValue).toArray());
                }
            });
            
            for (double[] ping : PingDetect.filteredPings) {
                //andro certifiedTM one-liner
                if (Init.debug)
                    g2d.drawOval((int)ping[0] - (int)(ping[2]*PINGSCALE/2 + 3),(int)ping[1] - (int)(ping[2]*PINGSCALE/2 + 3),(int)(ping[2]*PINGSCALE + 3),(int)(ping[2]*PINGSCALE + 3));
                //for hovered pings
            }

            //Render animations
            Main.getMainController().animatePreview(System.currentTimeMillis()*0.01);
            
            Main.getMainController().setImageView(Main.mapImage);
            g2d.dispose();
        }
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public boolean getRunning() {
        return isRunning;
    }

    public void stop() {
        setRunning(false);
    }

}
