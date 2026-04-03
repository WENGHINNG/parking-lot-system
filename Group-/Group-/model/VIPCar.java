package model;

public class VIPCar extends Vehicle {
    private static final long serialVersionUID = 1L;

    public VIPCar(String plateNo) {
        super(plateNo, VehicleType.VIP);
    }
}