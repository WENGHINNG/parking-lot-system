import controller.ParkingLot;
import model.*;
import strategy.ProgressiveFineStrategy;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Parking System Test Start ===");

        // 1. 获取管理器
        ParkingLot lot = ParkingLot.getInstance();

        // 2. 模拟停车 (Entry)
        Vehicle myCar = new Car("ABC1234");
        Ticket ticket = lot.parkVehicle(myCar);

        if (ticket != null) {
            System.out.println("Parked successfully!");
            System.out.println(ticket);
        } else {
            System.out.println("Parking failed: No spots.");
        }

        // 3. 模拟管理员切换罚款策略
        lot.setFineStrategy(new ProgressiveFineStrategy());

        // 4. 模拟取车 (Exit)
        // 注意：因为是刚才才停进去的，时间很短，费用可能是 1小时的钱，没罚款
        Payment receipt = lot.exitVehicle("ABC1234", true, PaymentMethod.CASH);

        if (receipt != null) {
            System.out.println("Exit successfully!");
            System.out.println(receipt);
        } else {
            System.out.println("Exit failed: Car not found.");
        }
    }
}