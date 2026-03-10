package vn.edu.ute.view;

import javax.swing.*;

import vn.edu.ute.view.panel.AcademicOperationPanel;
import vn.edu.ute.view.panel.EnrollmentPanel;
import vn.edu.ute.view.panel.FinancePanel;
import vn.edu.ute.view.panel.StaffPanel;

import java.awt.*;

public class MainDashboard extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;

    public MainDashboard() {
        setTitle("Hệ thống Quản lý Trung tâm Anh ngữ");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. Tạo Sidebar (Menu điều hướng) ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel lblTitle = new JLabel("MENU CHỨC NĂNG");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblTitle);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        // Khởi tạo các nút chức năng
        JButton btnStaff = createMenuButton("Quản lý Nhân sự");
        JButton btnEnrollment = createMenuButton("Ghi danh");
        JButton btnFinance = createMenuButton("Tài chính & Hóa đơn");
        JButton btnAcademic = createMenuButton("Vận hành Lớp học");

        sidebar.add(btnStaff);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnEnrollment);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnFinance);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnAcademic);

        // --- 2. Tạo Main Content với CardLayout ---
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Thêm các Panel chức năng vào CardLayout
        cardPanel.add(new StaffPanel(), "Staff");
        cardPanel.add(new EnrollmentPanel(), "Enrollment");
        cardPanel.add(new FinancePanel(), "Finance");
        cardPanel.add(new AcademicOperationPanel(), "Academic");

        // --- 3. SỬ DỤNG LAMBDA CHO SỰ KIỆN CLICK ---
        btnStaff.addActionListener(e -> cardLayout.show(cardPanel, "Staff"));
        btnEnrollment.addActionListener(e -> cardLayout.show(cardPanel, "Enrollment"));
        btnFinance.addActionListener(e -> cardLayout.show(cardPanel, "Finance"));
        btnAcademic.addActionListener(e -> cardLayout.show(cardPanel, "Academic"));

        // Gắn vào JFrame
        add(sidebar, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(200, 45));
        button.setFont(new Font("Arial", Font.PLAIN, 15));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Hàm Main để chạy độc lập và test giao diện
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Có thể thêm Look & Feel ở đây để UI đẹp hơn
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            
            new MainDashboard().setVisible(true);
        });
    }
}