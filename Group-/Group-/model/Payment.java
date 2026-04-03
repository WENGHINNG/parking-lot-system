package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.Serializable;

public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String plateNo;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double durationHours;
    private double parkingFee;
    private double fineAmount;
    private double totalPaid;
    private double remainingBalance;
    private PaymentMethod method;

    public Payment(String plateNo, LocalDateTime entryTime, LocalDateTime exitTime, 
                   double durationHours, double parkingFee, double fineAmount, 
                   double totalPaid, PaymentMethod method) {
        this.plateNo = plateNo;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.durationHours = durationHours;
        this.parkingFee = parkingFee;
        this.fineAmount = fineAmount;
        this.totalPaid = totalPaid;
        this.method = method;
        
        double totalDue = parkingFee + fineAmount;
        this.remainingBalance = totalDue - totalPaid;
        if (this.remainingBalance < 0) this.remainingBalance = 0.0;
    }

    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "=== OFFICIAL RECEIPT ===\n" +
               "Plate No:    " + plateNo + "\n" +
               "Entry:       " + entryTime.format(dtf) + "\n" +
               "Exit:        " + exitTime.format(dtf) + "\n" +
               "Duration:    " + String.format("%.0f", durationHours) + " hours\n" +
               "------------------------\n" +
               "Parking Fee: RM " + String.format("%.2f", parkingFee) + "\n" +
               "Fines Due:   RM " + String.format("%.2f", fineAmount) + "\n" +
               "------------------------\n" +
               "Total Due:   RM " + String.format("%.2f", parkingFee + fineAmount) + "\n" +
               "PAID:        RM " + String.format("%.2f", totalPaid) + " (" + method + ")\n" +
               "------------------------\n" +
               "BALANCE:     RM " + String.format("%.2f", remainingBalance) + "\n" +
               "========================";
    }
    
    public double getRemainingBalance() { return remainingBalance; }
}