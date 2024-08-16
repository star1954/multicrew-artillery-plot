package com.verellum.multicrew.arty;

public class PingController extends Controller {

    private final int id;
    private double[] location;

    public PingController(int id, double[] location) {
        this.id = id;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

}
