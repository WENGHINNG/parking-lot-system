package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.Serializable;

public class Ticket implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String ticketId;
    private String plateNo;
    private String spotId;
    private LocalDateTime entryTime;

    public Ticket(String plateNo, String spotId, LocalDateTime entryTime) {
        this.plateNo = plateNo;
        this.spotId = spotId;
        this.entryTime = entryTime;
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        this.ticketId = "T-" + plateNo + "-" + entryTime.format(dtf);
    }

    public String getTicketId() { return ticketId; }
    public String getPlateNo() { return plateNo; }
    public String getSpotId() { return spotId; }
    public LocalDateTime getEntryTime() { return entryTime; }

    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "=== PARKING TICKET ===\n" +
               "ID:    " + ticketId + "\n" +
               "Spot:  " + spotId + "\n" +
               "Plate: " + plateNo + "\n" +
               "Time:  " + entryTime.format(dtf) + "\n" +
               "======================";
    }
}