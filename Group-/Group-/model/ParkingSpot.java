package model;

import java.io.Serializable;

public class ParkingSpot implements Serializable {
    private static final long serialVersionUID = 1L;

    private String spotId;
    private SpotType type;
    private double hourlyRate;
    private Vehicle currentVehicle;

    public ParkingSpot(String spotId, SpotType type, double hourlyRate) {
        this.spotId = spotId; 
        this.type = type;
        this.hourlyRate = hourlyRate;
        this.currentVehicle = null;
    }

    public boolean isAvailable() {
        return currentVehicle == null;
    }

    public void park(Vehicle v) {
        this.currentVehicle = v;
    }

    public Vehicle removeVehicle() {
        Vehicle v = this.currentVehicle;
        this.currentVehicle = null;
        return v;
    }

    public String getSpotId() { return spotId; }
    public SpotType getType() { return type; }
    public double getHourlyRate() { return hourlyRate; }
    public Vehicle getCurrentVehicle() { return currentVehicle; }
}