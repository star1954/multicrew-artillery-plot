package com.verellum.multicrew.arty;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Tick {

    private static int numThreads;

    private final long timeBetweenTicks;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private boolean isRunning = false;

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
        //TODO: draw the rest of the owl
        Main.mapImage = Main.sc.capture(Main.mapRegion);
        

        // //Detect pings
        // Rectangle region = TemplateMatch.matchRect(Main.mapImage, Main.pingTemplate[0]);
        // //draw for debug
        // Graphics2D g2d = Main.mapImage.createGraphics();
        // g2d.setColor(Color.PINK);
        // g2d.drawRect(region.x,region.y,region.width,region.height);
        Main.getMainController().setImageView(Main.mapImage);
        
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
