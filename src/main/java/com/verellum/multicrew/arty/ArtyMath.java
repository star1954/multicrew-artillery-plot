package com.verellum.multicrew.arty;

/**
 * <p>Utility class to contain calculations required for effective gunnery
 * <p>generously <i>borrowed</i> from the people at mtc-artillery (https://github.com/ari-party/mtc-artillery)
 */
public class ArtyMath {

    //constants generously taken mtc-artillery :))
    private static final double G = 9.8 * 1.8;
    // private static final double P = 1.2;

    public static double studsToMeters(double studs) {
        return studs / (5 / 1.8);
    }

    public static double metersToStuds(double meters) {
        return meters * (5 / 1.8);
    }

    public static double indirectAngle(double distance, double velocity) {
        double sqrt = Math.sqrt(Math.pow(velocity, 4) - G * (G * Math.pow(distance, 2) + 2 * 0 * Math.pow(velocity, 2)));
        double rad = Math.atan(((Math.pow(velocity, 2)) + sqrt) / (G * distance));
        return rad * (180 / Math.PI);
    }

    public static double directAngle(double distance, double velocity) {
        double sqrt = Math.sqrt(Math.pow(velocity, 4) - G * (G * Math.pow(distance, 2) + 2 * 0 * Math.pow(velocity, 2)));
        double rad = Math.atan(((Math.pow(velocity, 2)) - sqrt) / (G * distance));
        return rad * (180 / Math.PI);
    }

    public static double flightTime(double elevation, double velocity, double distance) {
        double rad = (elevation * Math.PI) / 180;
        return distance / (velocity * Math.cos(rad));
    }

    public static double maxRange(double velocity) {
        double theta = 45 * (Math.PI / 180);
        return Math.pow(velocity, 2) * (2 * Math.sin(theta) * Math.cos(theta)) / G;
    }

}
