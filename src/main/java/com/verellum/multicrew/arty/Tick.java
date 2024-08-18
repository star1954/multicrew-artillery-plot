package com.verellum.multicrew.arty;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javafx.application.Platform;

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
                g2d.drawOval((int)ping[0] - (int)(ping[2]*pingScale/2 + 3),(int)ping[1] - (int)(ping[2]*pingScale/2 + 3),(int)(ping[2]*pingScale + 3),(int)(ping[2]*pingScale + 3));
            //for hovered pings
        }

        //Render animations
        Main.getMainController().animatePreview(System.currentTimeMillis()*0.01);
        
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
