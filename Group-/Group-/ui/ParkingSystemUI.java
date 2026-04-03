package ui;

import admin.AdminPanel;
import javax.swing.*;

public class ParkingSystemUI extends JFrame {

    public ParkingSystemUI() {
        setTitle("Parking Lot Management System (Group Assignment)");
        setSize(1000, 700); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        JTabbedPane tabbedPane = new JTabbedPane();

        EntryPanel entryPanel = new EntryPanel();
        ExitPanel exitPanel = new ExitPanel();
        AdminPanel adminPanel = new AdminPanel(); // 创建 Admin 实例

        tabbedPane.addTab("Entry (Park Vehicle)", entryPanel);
        tabbedPane.addTab("Exit (Payment)", exitPanel);
        tabbedPane.addTab("Admin Dashboard", adminPanel);

        // === Fix: 添加监听器，切换到 Admin 页面时自动刷新数据 ===
        tabbedPane.addChangeListener(e -> {
            // 如果选中的是第3个标签页 (Index 2，因为从0开始算)
            if (tabbedPane.getSelectedIndex() == 2) {
                adminPanel.refreshData(); // 自动调用刷新
            }
        });

        add(tabbedPane);
        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new ParkingSystemUI());
    }
}