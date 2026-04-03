package controller;

import model.*;
import strategy.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;

// 必须实现 Serializable 才能被整个存进文件
public class ParkingLot implements Serializable {
    private static final long serialVersionUID = 1L;
    private static ParkingLot instance;

    private List<ParkingSpot> spots;
    private FineStrategy fineStrategy;
    private double totalRevenue;
    private Map<String, Double> outstandingFines;

    private static final String DB_FILE = "parking_db.ser";

    private ParkingLot() {
        if (!loadData()) {
            System.out.println("No database found. Initializing new data.");
            spots = new ArrayList<>();
            totalRevenue = 0.0;
            outstandingFines = new HashMap<>();
            fineStrategy = new FixedFineStrategy();
            initializeMockData();
        } else {
            System.out.println("Database loaded successfully.");
        }
    }

    public static ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot();
        }
        return instance;
    }

    // === 你修改后的 5 层楼数据 ===
    private void initializeMockData() {
        // Floor 1
        spots.add(new ParkingSpot("F1-R1-S1", SpotType.COMPACT, 2.0));
        spots.add(new ParkingSpot("F1-R1-S2", SpotType.REGULAR, 5.0));
        spots.add(new ParkingSpot("F1-R2-S1", SpotType.HANDICAPPED, 2.0));
        // Floor 2
        spots.add(new ParkingSpot("F2-R1-S1", SpotType.REGULAR, 5.0));
        spots.add(new ParkingSpot("F2-R1-S2", SpotType.RESERVED, 10.0));
        spots.add(new ParkingSpot("F2-R1-S3", SpotType.RESERVED, 10.0));
        // Floor 3
        spots.add(new ParkingSpot("F3-R1-S1", SpotType.COMPACT, 2.0));
        spots.add(new ParkingSpot("F3-R1-S2", SpotType.REGULAR, 5.0));
        spots.add(new ParkingSpot("F3-R2-S1", SpotType.HANDICAPPED, 2.0));
        // Floor 4
        spots.add(new ParkingSpot("F4-R1-S1", SpotType.REGULAR, 5.0));
        spots.add(new ParkingSpot("F4-R1-S2", SpotType.RESERVED, 10.0));
        spots.add(new ParkingSpot("F4-R1-S3", SpotType.RESERVED, 10.0));
        // Floor 5
        spots.add(new ParkingSpot("F5-R1-S1", SpotType.COMPACT, 2.0));
        spots.add(new ParkingSpot("F5-R1-S2", SpotType.REGULAR, 5.0));
        spots.add(new ParkingSpot("F5-R2-S1", SpotType.HANDICAPPED, 2.0));
    }

    // === Database Save ===
    public void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DB_FILE))) {
            out.writeObject(spots);
            out.writeObject(outstandingFines);
            out.writeObject(totalRevenue);
            out.writeObject(fineStrategy);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // === Database Load ===
    @SuppressWarnings("unchecked")
    private boolean loadData() {
        File f = new File(DB_FILE);
        if (!f.exists())
            return false;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DB_FILE))) {
            this.spots = (List<ParkingSpot>) in.readObject();
            this.outstandingFines = (Map<String, Double>) in.readObject();
            this.totalRevenue = (double) in.readObject();
            this.fineStrategy = (FineStrategy) in.readObject();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === Entry System ===
    public Ticket parkVehicle(Vehicle v) {
        ParkingSpot spot = findAvailableSpot(v.getType());
        if (spot == null)
            return null;
        return parkToSpot(spot, v);
    }

    public Ticket parkVehicleAt(String spotId, Vehicle v) {
        ParkingSpot spot = null;
        for (ParkingSpot s : spots) {
            if (s.getSpotId().equals(spotId)) {
                spot = s;
                break;
            }
        }

        if (spot == null || !spot.isAvailable() || !isSpotSuitable(spot, v.getType()))
            return null;
        return parkToSpot(spot, v);
    }

    private Ticket parkToSpot(ParkingSpot spot, Vehicle v) {
        spot.park(v);
        saveData();
        return new Ticket(v.getPlateNo(), spot.getSpotId(), v.getEntryTime());
    }

    // === Exit System ===
    public Payment exitVehicle(String plateNo, boolean payFine, PaymentMethod method) {
        ParkingSpot targetSpot = null;
        for (ParkingSpot spot : spots) {
            if (!spot.isAvailable() && spot.getCurrentVehicle().getPlateNo().equals(plateNo)) {
                targetSpot = spot;
                break;
            }
        }

        if (targetSpot == null)
            return null;

        Vehicle v = targetSpot.getCurrentVehicle();
        LocalDateTime now = LocalDateTime.now();

       ///////////////////////////// v.setEntryTime(now.minusHours(25));

        long minutes = Duration.between(v.getEntryTime(), now).toMinutes();
        double hours = Math.ceil(minutes / 60.0);
        if (hours == 0)
            hours = 1;

        v.setEntryTime(now.minusHours(25)); // For testing fine calculation

        double parkingFee = 0.0;
        if (v.getType() == VehicleType.HANDICAPPED) {
            if (targetSpot.getType() == SpotType.HANDICAPPED)
                parkingFee = 0.0;
            else
                parkingFee = hours * 2.0;
        } else {
            parkingFee = hours * targetSpot.getHourlyRate();
        }

        double currentFine = 0.0;
        double hoursOverstayed = hours > 24 ? hours - 24 : 0;
        currentFine += fineStrategy.calculateFine(hoursOverstayed);

        if (targetSpot.getType() == SpotType.RESERVED && v.getType() != VehicleType.VIP) {
            currentFine += 50.0;
        }

        double previousFine = outstandingFines.getOrDefault(plateNo, 0.0);
        double totalFine = currentFine + previousFine;

        double amountPaid = 0.0;
        if (payFine) {
            amountPaid = parkingFee + totalFine;
            outstandingFines.remove(plateNo);
        } else {
            amountPaid = parkingFee;
            outstandingFines.put(plateNo, totalFine);
        }

        totalRevenue += amountPaid;
        targetSpot.removeVehicle();

        saveData();

        return new Payment(v.getPlateNo(), v.getEntryTime(), now, hours, parkingFee, totalFine, amountPaid, method);
    }

    public void addOutstandingFine(String plateNo, double amount) {
        double current = outstandingFines.getOrDefault(plateNo, 0.0);
        outstandingFines.put(plateNo, current + amount);
        saveData();
    }

    private ParkingSpot findAvailableSpot(VehicleType vType) {
        for (ParkingSpot spot : spots) {
            if (spot.isAvailable() && isSpotSuitable(spot, vType)) {
                return spot;
            }
        }
        return null;
    }

    private boolean isSpotSuitable(ParkingSpot spot, VehicleType vType) {
        SpotType sType = spot.getType();
        if (vType == VehicleType.MOTORCYCLE)
            return sType == SpotType.COMPACT;
        if (vType == VehicleType.SUV)
            return sType == SpotType.REGULAR;
        if (vType == VehicleType.VIP)
            return sType == SpotType.RESERVED || sType == SpotType.REGULAR || sType == SpotType.COMPACT;
        if (vType == VehicleType.CAR)
            return sType == SpotType.COMPACT || sType == SpotType.REGULAR || sType == SpotType.RESERVED;
        return true;
    }

    public void setFineStrategy(FineStrategy strategy) {
        this.fineStrategy = strategy;
        saveData();
    }

    public List<ParkingSpot> getAllSpots() {
        return spots;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public Map<String, Double> getOutstandingFines() {
        return outstandingFines;
    }

    public double getOccupancyRate() {
        if (spots.isEmpty())
            return 0.0;
        long occupiedCount = spots.stream().filter(s -> !s.isAvailable()).count();
        return (double) occupiedCount / spots.size();
    }

    public List<ParkingSpot> getSpotsByFloor(int floorNum) {
        String prefix = "F" + floorNum;
        List<ParkingSpot> floorSpots = new ArrayList<>();
        for (ParkingSpot s : spots) {
            if (s.getSpotId().startsWith(prefix))
                floorSpots.add(s);
        }
        return floorSpots;
    }
}