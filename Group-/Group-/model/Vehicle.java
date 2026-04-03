package model;

import java.time.LocalDateTime;
import java.io.Serializable; 

// 2. 加上 implements Serializable
public abstract class Vehicle implements Serializable {
    // 3. 加个版本号 (防止警告)
    private static final long serialVersionUID = 1L;
    
    protected String plateNo;
    protected VehicleType type;
    protected LocalDateTime entryTime;

    public Vehicle(String plateNo, VehicleType type) {
        this.plateNo = plateNo;
        this.type = type;
        this.entryTime = LocalDateTime.now();
    }

    public String getPlateNo() { return plateNo; }
    public VehicleType getType() { return type; }
    public LocalDateTime getEntryTime() { return entryTime; }
    
    public void setEntryTime(LocalDateTime time) { this.entryTime = time; }
}