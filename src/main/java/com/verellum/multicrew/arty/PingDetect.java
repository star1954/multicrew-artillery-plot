package com.verellum.multicrew.arty;

import java.util.ArrayList;
import java.util.List;
import java.awt.Rectangle;

public class PingDetect {
    private static List<Rectangle> smallPingList;
    private static List<Rectangle> mediumPingList;
    private static List<Rectangle> largePingList;

    public static int listSize = 20;
    public static int maxErrSq = 9;
    public static int maxMergeSq = 9;

    public static List<double[]> filteredPings;
            

    public static void initialize(){
        smallPingList = new ArrayList<Rectangle>();
        mediumPingList = new ArrayList<Rectangle>();
        largePingList = new ArrayList<Rectangle>();
        filteredPings = new ArrayList<>();
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
        System.out.println(filteredPings.size());
        Rectangle smallPing = null;
        Rectangle mediumPing = mediumPingList.get(0);
        Rectangle largePing = null;
        double sPingDistSq = 0;
        double lPingDistSq = 0;
        double avgDist = 0;

        for (Rectangle ping : smallPingList) {
            sPingDistSq = MathU.distanceSquared(ping.getCenterX()-mediumPing.getCenterX(), ping.getCenterY()-mediumPing.getCenterY());
            if (sPingDistSq <= maxErrSq){
                smallPing = ping;
                break;
            }
        }

        for (Rectangle ping : largePingList) {
            lPingDistSq = MathU.distanceSquared(ping.getCenterX()-mediumPing.getCenterX(), ping.getCenterY()-mediumPing.getCenterY());
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
            if(MathU.distanceSquared(ds[0]-output[0], ds[1]-output[1]) < maxMergeSq){
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
}
