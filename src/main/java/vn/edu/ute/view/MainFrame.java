package vn.edu.ute.view;

import javax.swing.*;
import java.awt.*;
import vn.edu.ute.util.UserSession;
import vn.edu.ute.model.UserAccount;

public class MainFrame extends JFrame {

    private JPanel pnlContent; // Nơi chứa các Panel con (Teacher, Student...)
    private JLabel lblStatus;

    public MainFrame() {
        // Lấy thông tin người dùng từ Session đã lưu lúc Login
        UserAccount currentUser = UserSession.getCurrentUser();

        setTitle("Hệ thống Quản lý MIS - Center");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. SIDEBAR (Bên trái) ---
        JPanel pnlSidebar = new JPanel();
        pnlSidebar.setLayout(new BoxLayout(pnlSidebar, BoxLayout.Y_AXIS));
        pnlSidebar.setBackground(new Color(44, 62, 80));
        pnlSidebar.setPreferredSize(new Dimension(250, 800));

        // Logo hoặc Tên hệ thống trên Sidebar
        JLabel lblBrand = new JLabel("MIS MANAGEMENT");
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblBrand.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        pnlSidebar.add(lblBrand);

        // Các nút chức năng (Tùy biến theo Role ở mục 3 bên dưới)
        addMenuButton(pnlSidebar, "Quản lý Giáo viên", e -> showPanel(new TeacherPanel()));
        addMenuButton(pnlSidebar, "Quản lý Học viên", e -> showPanel(new StudentPanel()));
        addMenuButton(pnlSidebar, "Quản lý Lớp học", e -> showPanel(new ClassPanel()));
        addMenuButton(pnlSidebar, "Thông báo hệ thống", e -> showPanel(new NotificationPanel()));

        pnlSidebar.add(Box.createVerticalGlue()); // Đẩy nút Đăng xuất xuống cuối
        addMenuButton(pnlSidebar, "Đăng xuất", e -> handleLogout());

        add(pnlSidebar, BorderLayout.WEST);

        // --- 2. CONTENT AREA (Chính giữa) ---
        pnlContent = new JPanel(new BorderLayout());
        pnlContent.setBackground(Color.WHITE);

        // Màn hình chào mừng khi mới vào
        JLabel lblWelcome = new JLabel("CHÀO MỪNG TRỞ LẠI, " + currentUser.getUsername().toUpperCase(), SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        pnlContent.add(lblWelcome, BorderLayout.CENTER);

        add(pnlContent, BorderLayout.CENTER);

        // --- 3. STATUS BAR (Dưới cùng - Hiển thị ADMIN/ROLE tại đây) ---
        JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlStatus.setBackground(new Color(236, 240, 241));

        // ĐÂY LÀ CHỖ HIỂN THỊ QUYỀN HẠN MÀ ÔNG CẦN
        lblStatus = new JLabel("Đang đăng nhập: " + currentUser.getUsername() +
                " | Quyền hạn: " + currentUser.getRole());
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        pnlStatus.add(lblStatus);

        add(pnlStatus, BorderLayout.SOUTH);
    }

    private void addMenuButton(JPanel sidebar, String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(250, 50));
        btn.setBackground(new Color(44, 62, 80));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(action);
        sidebar.add(btn);
        sidebar.add(Box.createVerticalStrut(5));
    }

    private void showPanel(JPanel panel) {
        pnlContent.removeAll();
        pnlContent.add(panel, BorderLayout.CENTER);
        pnlContent.revalidate();
        pnlContent.repaint();
    }

    private void handleLogout() {
        UserSession.logout();
        this.dispose();
        new LoginFrame().setVisible(true);
    }
}