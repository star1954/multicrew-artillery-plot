package com.verellum.multicrew.arty;

import java.util.Arrays;
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
    public static int requiredCorrelations = 3;

    public static List<double[]> filteredPings;

    public static void initialize() {
        smallPingList = new LinkedList<Rectangle>();
        mediumPingList = new LinkedList<Rectangle>();
        largePingList = new LinkedList<Rectangle>();
        filteredPings = new LinkedList<double[]>();
        pingList = new LinkedList<LinkedList<Rectangle>>();
    }

    public static void pushRects(Rectangle smallPing, Rectangle mediumPing, Rectangle largePing) {
        smallPingList.addFirst(smallPing);
        mediumPingList.addFirst(mediumPing);
        largePingList.addFirst(largePing);
        if (smallPingList.size() > listSize) {
            smallPingList.removeLast();
            mediumPingList.removeLast();
            largePingList.removeLast();
        }
    }

    public static void calcCorrelations(Main.Callback<Double[], ?> callback,
            Main.Callback<Integer, Double[]> callback2) {
        Rectangle smallPing = null;
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

        for (int i = 0; i < filteredPings.size(); i++) {
            double[] ds = filteredPings.get(i);
            if (MathUtils.distanceSquared(ds[0] - output[0], ds[1] - output[1]) < maxMergeSq) {
                if (ds[2] >= output[2]) {
                    ds[0] = output[0];
                    ds[1] = output[1];
                    ds[2] = output[2];
                    callback2.callTwo(i, Arrays.stream(output).boxed().toArray(Double[]::new));
                    // Main.getMainController().updatePing(i, output);
                }
                return;
            }
        }

        filteredPings.add(output);
        callback.call(Arrays.stream(output).boxed().toArray(Double[]::new));
        // Main.getMainController().appendList(output);

    }

    public static void calcCorrelations(Main.Callback<Double[], ?> callback, Main.Callback<Integer, Double[]> callback2,
            Rectangle basePing) {
        // Rectangle basePing = mediumPingList.get(0);
        LinkedList<Double> pingDistSq = new LinkedList<Double>();
        LinkedList<Rectangle> correlations = new LinkedList<Rectangle>();

        for (LinkedList<Rectangle> pings : pingList) {
            for (Rectangle ping : pings) {
                double distSq = MathUtils.distanceSquared(ping.getCenterX() - basePing.getCenterX(),
                        ping.getCenterY() - basePing.getCenterY());
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

        for (int i = 0; i < filteredPings.size(); i++) {
            double[] ds = filteredPings.get(i);
            if (MathUtils.distanceSquared(ds[0] - output[0], ds[1] - output[1]) < maxMergeSq) {
                if (ds[2] >= output[2]) {
                    ds[0] = output[0];
                    ds[1] = output[1];
                    ds[2] = output[2];
                    callback2.callTwo(i, Arrays.stream(output).boxed().toArray(Double[]::new));
                    // Main.getMainController().updatePing(i, output);
                }
                return;
            }
        }

        filteredPings.add(output);
        callback.call(Arrays.stream(output).boxed().toArray(Double[]::new));
        // Main.getMainController().appendList(output);

    }

    public static void flush() {
        filteredPings.clear();
    }

    public static void prune(int index) {
        filteredPings.remove(index);
    }
}
