package admin;

import controller.ParkingLot;
import model.ParkingSpot;
import strategy.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;
import java.time.format.DateTimeFormatter;

public class AdminPanel extends JPanel {

    private ParkingLot parkingLot;
    
    // 需要动态更新的 UI 组件
    private JLabel revenueLabel;      // 显示总收入
    private JLabel occupancyLabel;    // 显示占用率
    private JTable finesTable;        // 欠款表格
    private DefaultTableModel finesModel;
    private JTable spotsTable;        // 车位状态表格
    private DefaultTableModel spotsModel;
    private ButtonGroup strategyGroup; // 单选按钮组

    public AdminPanel() {
        this.parkingLot = ParkingLot.getInstance();
        setLayout(new BorderLayout());

        // 创建标签页容器 (Tabbed Pane)
        JTabbedPane tabbedPane = new JTabbedPane();

        // 标签页 1: 仪表盘 & 设置 (对应需求 6.1 & 6.3 - 收入/罚款/策略)
        tabbedPane.addTab("Dashboard & Settings", createDashboardTab());

        // 标签页 2: 实时车位状态 (对应需求 6.3 - 车辆列表)
        tabbedPane.addTab("Live Lot Status", createStatusTab());

        add(tabbedPane, BorderLayout.CENTER);

        // 底部全局刷新按钮 (Refresh Button)
        JButton refreshBtn = new JButton("Refresh All Data");
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 14));
        refreshBtn.setBackground(new Color(70, 130, 180)); // 钢蓝色
        refreshBtn.setForeground(Color.WHITE);
        
        // 点击刷新时，重新从 ParkingLot 获取数据
        refreshBtn.addActionListener(e -> refreshData());
        add(refreshBtn, BorderLayout.SOUTH);
        
        // 初始化时先加载一次数据
        refreshData();
    }

    // === 创建第一个标签页 (仪表盘) ===
    private JPanel createDashboardTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- 顶部: 统计卡片 (Total Revenue & Occupancy) ---
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        
        // 卡片 1: 总收入
        JPanel revPanel = new JPanel(new BorderLayout());
        revPanel.setBorder(BorderFactory.createTitledBorder("Total Revenue"));
        revenueLabel = new JLabel("RM 0.00", SwingConstants.CENTER);
        revenueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        revenueLabel.setForeground(new Color(0, 100, 0)); // 深绿色代表钱
        revPanel.add(revenueLabel);
        
        // 卡片 2: 占用率
        JPanel occPanel = new JPanel(new BorderLayout());
        occPanel.setBorder(BorderFactory.createTitledBorder("Occupancy Rate"));
        occupancyLabel = new JLabel("0%", SwingConstants.CENTER);
        occupancyLabel.setFont(new Font("Arial", Font.BOLD, 24));
        occupancyLabel.setForeground(Color.BLUE);
        occPanel.add(occupancyLabel);

        statsPanel.add(revPanel);
        statsPanel.add(occPanel);
        
        panel.add(statsPanel, BorderLayout.NORTH);

        // --- 中间: 罚款策略选择器 (对应需求: Choose Fine Scheme) ---
        JPanel strategyPanel = new JPanel(new GridLayout(3, 1));
        strategyPanel.setBorder(BorderFactory.createTitledBorder("Active Fine Strategy (Select to Change)"));
        
        JRadioButton fixedBtn = new JRadioButton("Option A: Fixed Fine (RM 50 flat)");
        JRadioButton progBtn = new JRadioButton("Option B: Progressive Fine (Increases every 24h)");
        JRadioButton hourlyBtn = new JRadioButton("Option C: Hourly Fine (RM 20/hour)");
        
        strategyGroup = new ButtonGroup();
        strategyGroup.add(fixedBtn);
        strategyGroup.add(progBtn);
        strategyGroup.add(hourlyBtn);
        
        // 默认选中 Fixed
        fixedBtn.setSelected(true);

        // 添加监听器：一旦点击，立即修改后台的计费逻辑 (Strategy Pattern 的威力!)
        fixedBtn.addActionListener(e -> {
            parkingLot.setFineStrategy(new FixedFineStrategy());
            JOptionPane.showMessageDialog(this, "Strategy Changed to: Fixed Fine (RM 50)");
        });
        
        progBtn.addActionListener(e -> {
            parkingLot.setFineStrategy(new ProgressiveFineStrategy());
            JOptionPane.showMessageDialog(this, "Strategy Changed to: Progressive Fine");
        });
        
        hourlyBtn.addActionListener(e -> {
            parkingLot.setFineStrategy(new HourlyFineStrategy());
            JOptionPane.showMessageDialog(this, "Strategy Changed to: Hourly Fine (RM 20/hr)");
        });

        strategyPanel.add(fixedBtn);
        strategyPanel.add(progBtn);
        strategyPanel.add(hourlyBtn);
        
        // --- 底部: 未付罚款表格 (对应需求: Unpaid Fines) ---
        JPanel finesPanel = new JPanel(new BorderLayout());
        finesPanel.setBorder(BorderFactory.createTitledBorder("Outstanding Unpaid Fines"));
        
        String[] columns = {"Plate No", "Amount Due (RM)"};
        finesModel = new DefaultTableModel(columns, 0);
        finesTable = new JTable(finesModel);
        finesPanel.add(new JScrollPane(finesTable));
        
        // 使用分割面板 (Split Pane) 上下布局
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, strategyPanel, finesPanel);
        split.setDividerLocation(150); // 设置分割线位置
        
        panel.add(split, BorderLayout.CENTER);

        return panel;
    }

    // === 创建第二个标签页 (实时状态列表) ===
    private JPanel createStatusTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 表格显示所有车位 (对应需求: View vehicles currently parked)
        String[] columns = {"Spot ID", "Type", "Rate (RM/hr)", "Status", "Vehicle Plate", "Entry Time"};
        spotsModel = new DefaultTableModel(columns, 0);
        spotsTable = new JTable(spotsModel);
        
        // 设置一点表格样式
        spotsTable.setRowHeight(25);
        spotsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scroll = new JScrollPane(spotsTable);
        scroll.setBorder(BorderFactory.createTitledBorder("Full Parking Lot Listing"));
        
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // === 数据刷新逻辑 (核心) ===
    public void refreshData() {
        // 1. 更新顶部统计数字
        double revenue = parkingLot.getTotalRevenue();
        revenueLabel.setText(String.format("RM %.2f", revenue));
        
        double occupancy = parkingLot.getOccupancyRate() * 100;
        occupancyLabel.setText(String.format("%.1f%%", occupancy));

        // 2. 更新未付罚款表格
        finesModel.setRowCount(0); // 清空旧数据
        Map<String, Double> fines = parkingLot.getOutstandingFines();
        for (Map.Entry<String, Double> entry : fines.entrySet()) {
            finesModel.addRow(new Object[]{
                entry.getKey(),
                String.format("%.2f", entry.getValue())
            });
        }

        // 3. 更新所有车位状态表格
        spotsModel.setRowCount(0); // 清空旧数据
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (ParkingSpot spot : parkingLot.getAllSpots()) {
            String status = spot.isAvailable() ? "Available" : "Occupied";
            String plate = "-";
            String time = "-";
            
            // 如果车位被占，取出车辆信息
            if (!spot.isAvailable()) {
                plate = spot.getCurrentVehicle().getPlateNo();
                time = spot.getCurrentVehicle().getEntryTime().format(dtf);
            }
            
            spotsModel.addRow(new Object[]{
                spot.getSpotId(),
                spot.getType(),
                String.format("%.2f", spot.getHourlyRate()),
                status,
                plate,
                time
            });
        }
    }
}