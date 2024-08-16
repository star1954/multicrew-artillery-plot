package com.verellum.multicrew.arty;

public class PingController extends Controller {

    private final int id;
    private double[] location;
    private MainController mc;

    public PingController(int id, double[] location, MainController mc) {
        this.id = id;
        this.location = location;
        this.mc = mc;
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

    //TODO create ping.fxml
    //TODO have click to select, targetting ping location on main gui
    //TODO be able to delete ping, removing from list as well as filteredPings
}
