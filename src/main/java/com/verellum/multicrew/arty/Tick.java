package com.verellum.multicrew.arty;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Tick {

    private static int numThreads;

    private final long timeBetweenTicks;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private boolean isRunning = false;

    public double pingScale = 10;

    public Tick(long tickTime){
        numThreads++;
        timeBetweenTicks = tickTime;
        //Return early in case the map has not been detected yet
        if (Main.mapRegion == null){
            System.out.println("no map");
            return;
        }

        if(!isRunning){
            isRunning = true;
            executor.scheduleWithFixedDelay(()-> {
                step();
            },0,timeBetweenTicks,TimeUnit.MILLISECONDS);
        }
    }

    /**
     * The method to be called every update tick
     */
    private void step(){
        long b = System.currentTimeMillis();
        //TODO: draw the rest of the owl
        Main.mapImage = Main.sc.capture(Main.mapRegion);
        
        BufferedImage noGreen = ScreenCapture.removeGreenChannel(Main.mapImage);
        //Detect pings
        Rectangle region1 = TemplateMatch.matchRect(noGreen, Main.pingTemplate[0]);
        //draw for debug
        Graphics2D g2d = Main.mapImage.createGraphics();
        g2d.setColor(Color.RED);
        if (Init.debug)
            g2d.drawRect(region1.x,region1.y,region1.width,region1.height);

        //Detect pings
        Rectangle region2 = TemplateMatch.matchRect(noGreen, Main.pingTemplate[1]);
        //draw for debug
        g2d.setColor(Color.CYAN);
        if (Init.debug)
            g2d.drawRect(region2.x,region2.y,region2.width,region2.height);

        Rectangle region3 = TemplateMatch.matchRect(noGreen, Main.pingTemplate[2]);
        //draw for debug
        g2d.setColor(Color.GREEN);
        if (Init.debug)
            g2d.drawRect(region3.x,region3.y,region3.width,region3.height);

        PingDetect.pushRects(region1, region2, region3);
        PingDetect.calcCorrelations();

        
        for (double[] ping : PingDetect.filteredPings) {
            //andro certifiedTM one-liner
            g2d.drawOval((int)ping[0] - (int)(ping[2]*pingScale/2 + 3),(int)ping[1] - (int)(ping[2]*pingScale/2 + 3),(int)(ping[2]*pingScale + 3),(int)(ping[2]*pingScale + 3));
        }
        
        Main.getMainController().setImageView(Main.mapImage);
        g2d.dispose();
    }

    /**
     * stops execution as soon as possible*
     * <p> *(all scheduled tasks will be completed, but no new ones can be made)
     */
    public void stop() {
        numThreads--;
        executor.close();
    }

    public boolean getRunning() {
        return isRunning;
    }

    public static int getNumThreads() {
        return numThreads;
    }

}
