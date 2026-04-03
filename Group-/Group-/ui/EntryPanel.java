package ui;

import controller.ParkingLot;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.*;

public class EntryPanel extends JPanel {

    private JTextField txtPlate;
    private JComboBox<String> comboVehicleType;
    private JComboBox<String> comboSpot;
    private JTextArea textArea;

    public EntryPanel() {
        setLayout(new BorderLayout(15,15));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // Title
        JLabel title = new JLabel("Vehicle Entry System", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Entry Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtPlate = new JTextField(15);
        
        comboVehicleType = new JComboBox<>(new String[]{
                "Car", "Motorcycle", "SUV", "VIP", "Handicapped"
        });

        comboSpot = new JComboBox<>();
        loadSpots(); // 动态加载

        JButton btnPark = new JButton("Park Vehicle");
        JButton btnRefresh = new JButton("Refresh Spots");

        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Plate Number:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPlate, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Vehicle Type:"), gbc);
        gbc.gridx = 1;
        formPanel.add(comboVehicleType, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Select Spot:"), gbc);
        gbc.gridx = 1;
        formPanel.add(comboSpot, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnRefresh);
        btnPanel.add(btnPark);

        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Receipt Area
        textArea = new JTextArea(10,30);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBorder(BorderFactory.createTitledBorder("Ticket Output"));
        add(new JScrollPane(textArea), BorderLayout.SOUTH);

        // Actions
        btnPark.addActionListener(e -> handleEntry());
        btnRefresh.addActionListener(e -> loadSpots());
    }

    private void loadSpots() {
        comboSpot.removeAllItems();
        List<ParkingSpot> spots = ParkingLot.getInstance().getAllSpots();
        for (ParkingSpot s : spots) {
            String status = s.isAvailable() ? "(Empty)" : "(Full)";
            // === 改动：显示类型，方便用户选择 ===
            // 显示格式: F1-R1-S1 [REGULAR] (Empty)
            String display = String.format("%s [%s] %s", s.getSpotId(), s.getType(), status);
            comboSpot.addItem(display);
        }
    }

    private void handleEntry() {
        try {
            String plate = txtPlate.getText().trim().toUpperCase();
            String typeStr = comboVehicleType.getSelectedItem().toString();
            
            if (comboSpot.getItemCount() == 0) return;
            
            // === 改动：解析 ID，只取空格前的第一部分 ===
            // 因为现在选中的是 "F1-R1-S1 [REGULAR] (Empty)"，我们只要 "F1-R1-S1"
            String selectedItem = comboSpot.getSelectedItem().toString();
            String spotId = selectedItem.split(" ")[0]; 

            if(plate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Plate number required!");
                return;
            }

            Vehicle vehicleObj;
            switch (typeStr) {
                case "Car": vehicleObj = new Car(plate); break;
                case "Motorcycle": vehicleObj = new Motorcycle(plate); break;
                case "SUV": vehicleObj = new SUV(plate); break;
                case "VIP": vehicleObj = new VIPCar(plate); break;
                case "Handicapped": vehicleObj = new HandicappedVehicle(plate); break;
                default: throw new IllegalArgumentException("Unknown Type");
            }

            Ticket ticket = ParkingLot.getInstance().parkVehicleAt(spotId, vehicleObj);

            if (ticket != null) {
                textArea.setText(ticket.toString());
                JOptionPane.showMessageDialog(this, "Ticket Created Successfully!");
                txtPlate.setText("");
                loadSpots(); // 刷新状态
            } else {
                textArea.setText("");
                // 提示更明确
                JOptionPane.showMessageDialog(this, 
                    "Entry Denied!\nPossible reasons:\n1. Spot is occupied\n2. Vehicle type mismatches spot type\n(e.g., Don't park a Car in a Motorcycle spot!)", 
                    "Parking Failed", JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}