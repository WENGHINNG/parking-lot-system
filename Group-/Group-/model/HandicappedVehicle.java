package model;

public class HandicappedVehicle extends Vehicle {
    private static final long serialVersionUID = 1L;

    public HandicappedVehicle(String plateNo) {
        super(plateNo, VehicleType.HANDICAPPED);
    }
}