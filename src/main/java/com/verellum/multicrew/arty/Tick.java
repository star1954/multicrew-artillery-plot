package com.verellum.multicrew.arty;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class Tick implements Runnable {

    private boolean isRunning = false;

    private final double PINGSCALE = 10;

    private LinkedList<Rectangle> regions = new LinkedList<Rectangle>();

    private LinkedList<Color> debugColors = new LinkedList<Color>();

    public Tick() {
        debugColors.addLast(Color.RED);
        debugColors.addLast(Color.CYAN);
        debugColors.addLast(Color.GREEN);
    }

    @Override
    public void run() {
        if (Main.mapRegion == null)
            return;
        if (isRunning) {
            // Catch exceptions and print them out to console
            try {
                step();
            } catch (Exception e) {
                Thread t = Thread.currentThread();
                t.getUncaughtExceptionHandler().uncaughtException(t, e);
            }
        }

    }

    private void step() {
        // Grab map and remove green channel
        Main.mapImage = Main.sc.capture(Main.mapRegion);
        BufferedImage noGreen = ScreenCapture.removeGreenChannel(Main.mapImage);
        // Green channel removed for filtering

        // draw for debug
        Graphics2D g2d = Main.mapImage.createGraphics();

        // TODO maaaaybe multithread the 3 template matches :))))))))

        // Detect Pings
        for (int i = 0; i < Main.pingTemplate.length; i++) {
            regions.addFirst(TemplateMatch.matchRect(noGreen, Main.pingTemplate[i]));
        }

        Iterator<Rectangle> regionit = regions.iterator();
        PingDetect.pushRects(regionit.next(), regionit.next(), regionit.next());


        // Draw debug squares
        if (Init.debug) {
            regionit = regions.iterator();
            Iterator<Color> colorit = debugColors.iterator();
            while(regionit.hasNext() && colorit.hasNext()){
                g2d.setColor(colorit.next());
                Rectangle rect = regionit.next();
                g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
            }
        }

        //Run the correlator to detect valid pings
        PingDetect.calcCorrelations(new Main.Callback<Double[], Object>() {
            @Override
            public void call(Double[] output) {
                Main.getMainController().appendList(Arrays.stream(output).mapToDouble(Double::doubleValue).toArray());
            }
        }, new Main.Callback<Integer, Double[]>() {
            @Override
            public void callTwo(Integer i, Double[] output) {
                Main.getMainController().updatePing(i,
                        Arrays.stream(output).mapToDouble(Double::doubleValue).toArray());
            }
        });

        for (double[] ping : PingDetect.filteredPings) {
            // andro certifiedTM one-liner
            if (Init.debug)
                g2d.drawOval((int) ping[0] - (int) (ping[2] * PINGSCALE / 2 + 3),
                        (int) ping[1] - (int) (ping[2] * PINGSCALE / 2 + 3), (int) (ping[2] * PINGSCALE + 3),
                        (int) (ping[2] * PINGSCALE + 3));
            // for hovered pings
        }

        // Render animations and map
        Main.getMainController().animate(System.currentTimeMillis() * 0.01);
        Main.getMainController().setImageView(Main.mapImage);
        g2d.dispose();
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
