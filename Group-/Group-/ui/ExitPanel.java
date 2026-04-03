package ui;

import controller.ParkingLot;
import java.awt.*;
import javax.swing.*;
import model.*;

public class ExitPanel extends JPanel {

    private JTextField txtPlate;
    private JCheckBox chkPayFine;
    private JRadioButton radioCash;
    private JRadioButton radioCard;
    private JTextArea textArea;

    public ExitPanel() {
        setLayout(new BorderLayout(15,15));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // Title
        JLabel title = new JLabel("Vehicle Exit & Payment", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Exit Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtPlate = new JTextField(15);
        chkPayFine = new JCheckBox("Pay Outstanding Fine");
        chkPayFine.setSelected(true);

        radioCash = new JRadioButton("Cash");
        radioCard = new JRadioButton("Card");

        ButtonGroup group = new ButtonGroup();
        group.add(radioCash);
        group.add(radioCard);
        radioCash.setSelected(true);

        // === Fix: 移除自定义颜色 ===
        JButton btnExit = new JButton("Exit & Pay");

        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Plate Number:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPlate, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Fine Payment:"), gbc);
        gbc.gridx = 1;
        formPanel.add(chkPayFine, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 1;
        formPanel.add(radioCash, gbc);

        gbc.gridy = 3;
        formPanel.add(radioCard, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(btnExit, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Receipt
        textArea = new JTextArea(10,30);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBorder(BorderFactory.createTitledBorder("Receipt"));
        add(new JScrollPane(textArea), BorderLayout.SOUTH);

        btnExit.addActionListener(e -> handleExit());
    }

    private void handleExit() {
        try {
            String plate = txtPlate.getText().trim().toUpperCase();

            if(plate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Plate number required!");
                return;
            }

            boolean payFine = chkPayFine.isSelected();
            PaymentMethod method = radioCash.isSelected() ? PaymentMethod.CASH : PaymentMethod.CARD;

            Payment payment = ParkingLot.getInstance().exitVehicle(plate, payFine, method);

            // === Fix: 增加非空判断 ===
            if (payment != null) {
                textArea.setText(payment.toString());
                JOptionPane.showMessageDialog(this, "Payment Successful!");
                txtPlate.setText("");
                chkPayFine.setSelected(true);
            } else {
                textArea.setText("");
                JOptionPane.showMessageDialog(this, 
                    "Vehicle Not Found! Please check the plate number.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}