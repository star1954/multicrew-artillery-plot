package com.verellum.multicrew.arty;

import org.bytedeco.opencv.opencv_core.Scalar;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>Utility class to contain calculations required for effective gunnery
 * <p>generously <i>borrowed</i> from the people at mtc-artillery (https://github.com/ari-party/mtc-artillery)
 */
public class MathU {

    //constants generously taken mtc-artillery :))
    private static final double g = 9.8 * 1.8;
    // private static final double P = 1.2;

    /** 
     * @param studs
     * @return double Distance in meters
     * Converts studs to meters
     */
    public static double studsToMeters(double studs) {
        return studs / (5 / 1.8);
    }

    
    /** 
     * @param meters
     * @return double Distance in studs
     * Converts meters to studs
     */
    public static double metersToStuds(double meters) {
        return meters * (5 / 1.8);
    }

    
    /** 
     * @param distance Distance to target
     * @param velocity Velocity of projectile
     * @return double Elevation
     * Calculates a ballistic trajectory for high-angle artillery, such as a mortar
     * Does not account for wind, air resistance, parent velocity, etc.
     */
    public static double indirectAngle(double distance, double velocity) {
        double sqrt = Math.sqrt(Math.pow(velocity, 4) - g * (g * Math.pow(distance, 2) + 2 * 0 * Math.pow(velocity, 2)));
        double rad = Math.atan(((Math.pow(velocity, 2)) + sqrt) / (g * distance));
        return rad * (180 / Math.PI);
    }
    
    
    /** 
     * @param distance Distance to target
     * @param velocity Velocity of projectile
     * @return double Elevation
     * Calculates a ballistic trajectory for low-angle artillery, such as a cannon
     * Does not account for wind, air resistance, parent velocity, etc.
     */
    public static double directAngle(double distance, double velocity) {
        double sqrt = Math.sqrt(Math.pow(velocity, 4) - g * (g * Math.pow(distance, 2) + 2 * 0 * Math.pow(velocity, 2)));
        double rad = Math.atan(((Math.pow(velocity, 2)) - sqrt) / (g * distance));
        return rad * (180 / Math.PI);
    }

    
    
    /** 
     * @param elevation Elevation in degrees
     * @param velocity Velocity of projectile
     * @param distance Distance to target
     * @return double Time of flight
     * Calculates the time of flight of the projectile
     */
    public static double flightTime(double elevation, double velocity, double distance) {
        elevation = Math.toRadians(elevation); 
        return distance / (velocity * Math.cos(elevation));
    }

    
    
    /** 
     * @param velocity Velocity of the projectile
     * @return double Maximum range of the projectile
     * Calculates the maximum theoretical range of a projectile given optimal firing angle
     */
    public static double maxRange(double velocity) {
        double theta = Math.PI/4;
        return Math.pow(velocity, 2) * (2 * Math.sin(theta) * Math.cos(theta)) / g;
    }

    
    
    /** 
     * @param velocity Velocity of the projectile
     * @param maxElevation Maximum elevation of the barrel
     * @return double Maximum range of the projectile
     * Calculates the maximum theoretical range of a projectile given
     * Use the minimum elevation in case of high-angle trajectories
     */
    public static double maxRange(double velocity, double maxElevation) {
        double theta = Math.min(45, Math.toRadians(maxElevation));
        return Math.pow(velocity, 2) * (2 * Math.sin(theta) * Math.cos(theta)) / g;
    }

    //Generates a random color in RGB
    public static Scalar randColor(){
        int b,g,r;
        b= ThreadLocalRandom.current().nextInt(0, 255 + 1);
        g= ThreadLocalRandom.current().nextInt(0, 255 + 1);
        r= ThreadLocalRandom.current().nextInt(0, 255 + 1);
        return new Scalar (b,g,r,0);
     }

}
