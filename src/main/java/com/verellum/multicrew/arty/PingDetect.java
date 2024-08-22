package com.verellum.multicrew.arty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.awt.Rectangle;

public class PingDetect {
    private static LinkedList<Rectangle> smallPingList;
    private static LinkedList<Rectangle> mediumPingList;
    private static LinkedList<Rectangle> largePingList;

    private static LinkedList<LinkedList<Rectangle>> pingList;

    public static int listSize = 55;
    public static int maxErrSq = 9;
    public static int maxMergeSq = 9;
    public static int requiredCorrelations = 2; //should be 1 less than the number of rectangles that match

    public static LinkedList<double[]> filteredPings;

    public static void initialize() {
        smallPingList = new LinkedList<Rectangle>();
        mediumPingList = new LinkedList<Rectangle>();
        largePingList = new LinkedList<Rectangle>();
        filteredPings = new LinkedList<double[]>();
        pingList = new LinkedList<LinkedList<Rectangle>>();

        //for compat, initialize the first 3 pinglists and set their references to small, medium, and large
        pingList.addLast(smallPingList);
        pingList.addLast(mediumPingList);
        pingList.addLast(largePingList);
    }

    
    /** 
     * @param smallPing The suspected ping found using the small sized circle
     * @param mediumPing The suspected ping found using the medium sized circle
     * @param largePing The suspected ping found using the large sized circle
     * Adds the given pings to the first 3 ping lists
     */
    public static void pushRects(Rectangle smallPing, Rectangle mediumPing, Rectangle largePing) {
        //for compat
        smallPingList.addFirst(smallPing);
        mediumPingList.addFirst(mediumPing);
        largePingList.addFirst(largePing);
        if (smallPingList.size() > listSize) {
            smallPingList.removeLast();
            mediumPingList.removeLast();
            largePingList.removeLast();
        }
        Iterator<LinkedList<Rectangle>> pingIt = pingList.iterator();
        pingIt.next().addFirst(smallPing);
        pingIt.next().addFirst(mediumPing);
        pingIt.next().addFirst(largePing);
        for (LinkedList<Rectangle> pings : pingList) {
            if (pings.size() > listSize){
                pings.removeLast();
            }
        }
    }

    
    /** 
     * @param rects The list of suspected pings found
     */
    public static void pushRects(LinkedList<Rectangle> rects){
     Iterator<LinkedList<Rectangle>> pingIt = pingList.iterator();
        for (Rectangle rectangle : rects) {
            LinkedList<Rectangle> pL = pingIt.next();
            pL.addFirst(rectangle);
        }
    }

    
    /** 
     * @param newPingCallback Callback when a new ping is found and added
     * @param mergedPingCallback Callback when a an existing ping is found and updated
     */
    public static void calcCorrelations(Main.Callback<Double[], ?> newPingCallback, Main.Callback<Integer, Double[]> mergedPingCallback) {
        if(pingList.size() >= 2){
        Rectangle mediumPing = pingList.get(1).getFirst();
        calcCorrelations(newPingCallback, mergedPingCallback, mediumPing);
        }else{
            System.out.println("a");
        }
        /*Rectangle smallPing = null;
        Rectangle mediumPing = mediumPingList.get(0);
        Rectangle largePing = null;
        double sPingDistSq = 0;
        double lPingDistSq = 0;
        double avgDist = 0;

        for (Rectangle ping : smallPingList) {
            sPingDistSq = MathUtils.distanceSquared(ping.getCenterX() - mediumPing.getCenterX(),
                    ping.getCenterY() - mediumPing.getCenterY());
            if (sPingDistSq <= maxErrSq) {
                smallPing = ping;
                break;
            }
        }

        for (Rectangle ping : largePingList) {
            lPingDistSq = MathUtils.distanceSquared(ping.getCenterX() - mediumPing.getCenterX(),
                    ping.getCenterY() - mediumPing.getCenterY());
            if (lPingDistSq <= maxErrSq) {
                largePing = ping;
                break;
            }
        }

        if (smallPing != null && largePing != null) {
            int count = 1;
            double totalDist = 0;
            if (smallPing != null) {
                count++;
                totalDist += Math.sqrt(sPingDistSq);
            }
            if (largePing != null) {
                count++;
                totalDist += Math.sqrt(lPingDistSq);
            }
            avgDist = totalDist / count;
        } else {
            return;
        }

        double[] output = { mediumPing.getCenterX(), mediumPing.getCenterY(), avgDist };

        int i = 0;
        for (double[] ds : filteredPings) {
            if (MathUtils.distanceSquared(ds[0] - output[0], ds[1] - output[1]) < maxMergeSq) {
            
                ds[0] = output[0];
                ds[1] = output[1];
                ds[2] = output[2];
                mergedPingCallback.callTwo(i, Arrays.stream(output).boxed().toArray(Double[]::new));
                // Main.getMainController().updatePing(i, output);
                return;
            }
            i++;
        }

        filteredPings.add(output);
        newPingCallback.call(Arrays.stream(output).boxed().toArray(Double[]::new));
        // Main.getMainController().appendList(output);*/

    }

    
    /**      
     * @param newPingCallback Callback when a new ping is found and added
     * @param mergedPingCallback Callback when a an existing ping is found and updated
     * @param basePing The suspected ping to detect groupings around. Use the most reliable circle size for this
     */
    public static void calcCorrelations(Main.Callback<Double[], ?> newPingCallback, Main.Callback<Integer, Double[]> mergedPingCallback,
            Rectangle basePing) {
        // Rectangle basePing = mediumPingList.get(0);
        LinkedList<Double> pingDistSq = new LinkedList<Double>();
        ArrayList<Rectangle> correlations = new ArrayList<Rectangle>();

        for (LinkedList<Rectangle> pings : pingList) {
            for (Rectangle ping : pings) {
                double distSq = MathUtils.distanceSquared(ping.getCenterX() - basePing.getCenterX(), ping.getCenterY() - basePing.getCenterY());
                if (distSq <= maxErrSq && distSq != 0) {
                    correlations.addLast(ping);
                    pingDistSq.addLast(distSq);
                    break;
                }
            }
        }

        if (correlations.size() <= requiredCorrelations) {
            return;
        }

        double avgDist = 0;
        int count = 0;

        for (double distSq : pingDistSq) {
            count++;
            avgDist += distSq;
        }

        avgDist = Math.sqrt(avgDist / count);

        double[] output = { basePing.getCenterX(), basePing.getCenterY(), avgDist };
        int i = 0;
        for (double[] ds : filteredPings) {
            if (MathUtils.distanceSquared(ds[0] - output[0], ds[1] - output[1]) < maxMergeSq) {
            
                ds[0] = output[0];
                ds[1] = output[1];
                ds[2] = output[2];
                mergedPingCallback.callTwo(i, Arrays.stream(output).boxed().toArray(Double[]::new));
                // Main.getMainController().updatePing(i, output);
                return;
            }
            i++;
        }

        filteredPings.add(output);
        newPingCallback.call(Arrays.stream(output).boxed().toArray(Double[]::new));
        // Main.getMainController().appendList(output);

    }
    /**
     * Cleares the list of correlated pings
     */
    public static void flush() {
        filteredPings.clear();
    }

    
    /** 
     * @param index The index of the correlated ping to remove
     */
    public static void prune(int index) {
        filteredPings.remove(index);
    }
}
