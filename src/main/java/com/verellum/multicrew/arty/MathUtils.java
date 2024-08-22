package com.verellum.multicrew.arty;

import java.awt.Point;

/**
 * <p>
 * Utility class to contain calculations required for effective gunnery
 * <p>
 * generously <i>borrowed</i> from the people at mtc-artillery
 * (https://github.com/ari-party/mtc-artillery)
 */
public class MathUtils {

    // constants generously taken mtc-artillery :))
    private static final double g = 9.8 * 1.8;
    // private static final double P = 1.2;

    public static double distanceSquared(double x, double y){
        return Math.pow(x, 2) + Math.pow(y, 2);
    }
    
    public static double distanceSquared(Point p1, Point p2) {
        return Point.distanceSq(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public static double distance(double x, double y){
        return Math.sqrt(distanceSquared(x, y));
    }

    public static double distance(Point p1, Point p2) {
        return Point.distance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public static double azimuth(double dx, double dy){
        double azi = 180 + Math.atan2(dy, dx) * 180 / Math.PI;
        azi = (azi+270) % 360;
        return azi;
    }
    public static double azimuth(Point p1, Point p2){
        return azimuth(p2.getX()-p1.getX(), p2.getY()-p1.getY());
    }

    /**
     * @param studs
     * @return double Distance in meters
     *         Converts studs to meters
     */
    public static double studsToMeters(double studs) {
        return studs / (5 / 1.8);
    }

    /**
     * @param meters
     * @return double Distance in studs
     *         Converts meters to studs
     */
    public static double metersToStuds(double meters) {
        return meters * (5 / 1.8);
    }

    /**
     * @param distance Distance to target
     * @param velocity Velocity of projectile
     * @return double Elevation
     *         Calculates a ballistic trajectory for high-angle artillery, such as a
     *         mortar
     *         Does not account for wind, air resistance, parent velocity, etc.
     */
    public static double indirectAngle(double distance, double velocity) {
        double sqrt = Math
                .sqrt(Math.pow(velocity, 4) - g * (g * Math.pow(distance, 2) + 2 * 0 * Math.pow(velocity, 2)));
        double rad = Math.atan(((Math.pow(velocity, 2)) + sqrt) / (g * distance));
        return rad * (180 / Math.PI);
    }

    /**
     * @param distance Distance to target
     * @param velocity Velocity of projectile
     * @return double Elevation
     *         Calculates a ballistic trajectory for low-angle artillery, such as a
     *         cannon
     *         Does not account for wind, air resistance, parent velocity, etc.
     */
    public static double directAngle(double distance, double velocity) {
        double sqrt = Math
                .sqrt(Math.pow(velocity, 4) - g * (g * Math.pow(distance, 2) + 2 * 0 * Math.pow(velocity, 2)));
        double rad = Math.atan(((Math.pow(velocity, 2)) - sqrt) / (g * distance));
        return rad * (180 / Math.PI);
    }

    /**
     * @param elevation Elevation in degrees
     * @param velocity  Velocity of projectile
     * @param distance  Distance to target
     * @return double Time of flight
     *         Calculates the time of flight of the projectile
     */
    public static double flightTime(double elevation, double velocity, double distance) {
        elevation = Math.toRadians(elevation);
        return distance / (velocity * Math.cos(elevation));
    }

    /**
     * @param velocity Velocity of the projectile
     * @return double Maximum range of the projectile
     *         Calculates the maximum theoretical range of a projectile given
     *         optimal firing angle
     */
    public static double maxRange(double velocity) {
        double theta = Math.PI / 4;
        return Math.pow(velocity, 2) * (2 * Math.sin(theta) * Math.cos(theta)) / g;
    }

    /**
     * @param velocity     Velocity of the projectile
     * @param maxElevation Maximum elevation of the barrel
     * @return double Maximum range of the projectile
     *         Calculates the maximum theoretical range of a projectile given
     *         Use the minimum elevation in case of high-angle trajectories
     */
    public static double maxRange(double velocity, double maxElevation) {
        double theta = Math.min(45, Math.toRadians(maxElevation));
        return Math.pow(velocity, 2) * (2 * Math.sin(theta) * Math.cos(theta)) / g;
    }

    /**
     * @param pxPoint point in map measured in pixels
     * @param gridCount number of grid cells on the map
     * @param px pixel width of the map (it better be square)
     * @param gridSize width of 1 grid square in meters
     * @return same point but converted to meters as the x and y
     */
    public static Point pxPointToMeters(Point pxPoint, int gridCount, double px, double gridSize) {
        double mapSize = gridSize*gridCount;
        double metersPerPixel = mapSize/px;
        return new Point((int)(pxPoint.getX()*metersPerPixel), (int)(pxPoint.getY()*metersPerPixel));
    }

    public static String metersPointToGrid(Point metersPoint, int gridCount, double gridSize) {
        String map = "ABCDEFGHJ";
        int col = (int)(metersPoint.getX()/gridSize);
        int row = (int)(metersPoint.getY()/gridSize);
        return (map.charAt(col) + Integer.toString(row+1));
    }

}
