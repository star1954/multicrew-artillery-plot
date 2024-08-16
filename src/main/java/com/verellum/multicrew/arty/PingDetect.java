package com.verellum.multicrew.arty;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.awt.Rectangle;

public class PingDetect {
    private static LinkedList<Rectangle> smallPingList;
    private static LinkedList<Rectangle> mediumPingList;
    private static LinkedList<Rectangle> largePingList;

    public static int listSize = 15;
    public static int maxErrSq = 9;
    public static int maxMergeSq = 9;

    public static List<double[]> filteredPings;

    public static void initialize(){
        smallPingList = new LinkedList<Rectangle>();
        mediumPingList = new LinkedList<Rectangle>();
        largePingList = new LinkedList<Rectangle>();
        filteredPings = new ArrayList<double[]>();
    }

    public static void pushRects(Rectangle smallPing, Rectangle mediumPing, Rectangle largePing){
        smallPingList.addFirst(smallPing);
        mediumPingList.addFirst(mediumPing);
        largePingList.addFirst(largePing);
        if(smallPingList.size() > listSize){
            smallPingList.removeLast();
            mediumPingList.removeLast();
            largePingList.removeLast();
        }
    }

    public static void calcCorrelations(){
        // System.out.println(filteredPings.size());
        Rectangle smallPing = null;
        Rectangle mediumPing = mediumPingList.get(0);
        Rectangle largePing = null;
        double sPingDistSq = 0;
        double lPingDistSq = 0;
        double avgDist = 0;

        for (Rectangle ping : smallPingList) {
            sPingDistSq = MathUtils.distanceSquared(ping.getCenterX()-mediumPing.getCenterX(), ping.getCenterY()-mediumPing.getCenterY());
            if (sPingDistSq <= maxErrSq){
                smallPing = ping;
                break;
            }
        }

        for (Rectangle ping : largePingList) {
            lPingDistSq = MathUtils.distanceSquared(ping.getCenterX()-mediumPing.getCenterX(), ping.getCenterY()-mediumPing.getCenterY());
            if (lPingDistSq <= maxErrSq){
                largePing = ping;
                break;
            }
        }


        if(smallPing!=null && largePing!=null){
            int count = 1;
            double totalDist = 0;
            if (smallPing!=null){
                count++;
                totalDist += Math.sqrt(sPingDistSq);
            }
            if (largePing!=null){
                count++;
                totalDist += Math.sqrt(lPingDistSq);
            }
            avgDist = totalDist/count;
        }else{
           return;
        }

        double[] output = {mediumPing.getCenterX(), mediumPing.getCenterY(), avgDist};

        for (double[] ds : filteredPings) {
            if(MathUtils.distanceSquared(ds[0]-output[0], ds[1]-output[1]) < maxMergeSq){
                if(ds[2] >= output[2]){
                    ds[0]=output[0];
                    ds[1]=output[1];
                    ds[2]=output[2];
                }
                return;
            }
        }

        
        filteredPings.add(output);
    }

    public static void flush() {
        filteredPings.clear();
    }
}
