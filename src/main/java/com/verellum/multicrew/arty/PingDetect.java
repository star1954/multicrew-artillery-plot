package com.verellum.multicrew.arty;

import java.util.List;
import java.awt.Rectangle;

public class PingDetect {
    private static List<Rectangle> smallPingList;
    private static List<Rectangle> mediumPingList;
    private static List<Rectangle> largePingList;

    public static int listSize = 30;

    public static List<double[]> filteredPings;

    public static void initialize(){

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
        Rectangle smallPing = null;
        Rectangle mediumPing = mediumPingList.get(0);
        Rectangle largePing = null;
        double sPingDistSq = 0;
        double lPingDistSq = 0;
        double avgDistSq = 0;
        for (Rectangle ping : smallPingList) {
            sPingDistSq = MathU.distanceSquared(ping.getCenterX()-mediumPing.getCenterX(), ping.getCenterY()-mediumPing.getCenterY());
            if (sPingDistSq <= 25){
                smallPing = ping;
                break;
            }
        }
        for (Rectangle ping : largePingList) {
            lPingDistSq = MathU.distanceSquared(ping.getCenterX()-mediumPing.getCenterX(), ping.getCenterY()-mediumPing.getCenterY());
            if (lPingDistSq <= 25){
                largePing = ping;
                break;
            }
        }

        if(smallPing!=null || largePing!=null){
            int count = 1;
            double totalDistSq = 0;
            if (smallPing==null){
                count++;
                totalDistSq += sPingDistSq;
            }
            if (largePing==null){
                count++;
                totalDistSq += lPingDistSq;
            }
            avgDistSq = totalDistSq/count;
        }else{
           return;
        }

        double[] output = {mediumPing.getCenterX(), mediumPing.getCenterY(), 1.0d/avgDistSq};
        filteredPings.add(output);
    }
}
