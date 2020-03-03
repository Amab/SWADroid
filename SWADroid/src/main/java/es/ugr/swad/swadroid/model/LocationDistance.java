package es.ugr.swad.swadroid.model;

public class LocationDistance {

    private double distance;
    private String location;

    public LocationDistance(String location, double distance) {
        this.distance = distance;
        this.location = location;
    }

    public double getDistance() { return distance;  }

    public void setMac(double mac) {
        this.distance = distance;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
