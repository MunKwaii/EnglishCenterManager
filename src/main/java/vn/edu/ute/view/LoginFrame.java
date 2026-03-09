package vn.edu.ute.view;

import javax.swing.*;
import java.awt.*;
import vn.edu.ute.service.UserAccountService;
import vn.edu.ute.service.impl.UserAccountServiceImpl;
import vn.edu.ute.util.UserSession;

public class LoginFrame extends JFrame {

    private final UserAccountService authService = new UserAccountServiceImpl();

    // UI Components
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPass;
    private JButton btnLogin, btnExit;

    public LoginFrame() {
        setTitle("Hệ thống Quản lý MIS Center - Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
        setupEvents();

        setSize(450, 300);
        setLocationRelativeTo(null); // Hiện ra giữa màn hình
    }

    private void initComponents() {
        // Dùng BorderLayout làm gốc, chia thành Header và Form
        setLayout(new BorderLayout(20, 20));

        // 1. Header: Tiêu đề lớn
        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(new Color(41, 128, 185)); // Màu xanh dương chuyên nghiệp
        JLabel lblTitle = new JLabel("MIS CENTER LOGIN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // 2. Center: Form nhập liệu
        JPanel pnlForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        pnlForm.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1;
        txtUsername = new JTextField(15);
        pnlForm.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        pnlForm.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        pnlForm.add(txtPassword, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        chkShowPass = new JCheckBox("Hiện mật khẩu");
        pnlForm.add(chkShowPass, gbc);

        add(pnlForm, BorderLayout.CENTER);

        // 3. South: Nút bấm
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setPreferredSize(new Dimension(120, 35));
        btnLogin.setBackground(new Color(46, 204, 113));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);

        btnExit = new JButton("Thoát");
        btnExit.setPreferredSize(new Dimension(100, 35));

        pnlButtons.add(btnLogin);
        pnlButtons.add(btnExit);
        add(pnlButtons, BorderLayout.SOUTH);
    }

    private void setupEvents() {
        // Hiện/Ẩn mật khẩu
        chkShowPass.addActionListener(e -> {
            if (chkShowPass.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('•');
            }
        });

        // Xử lý đăng nhập
        btnLogin.addActionListener(e -> handleLogin());

        // Nhấn Enter để đăng nhập luôn cho tiện
        txtPassword.addActionListener(e -> handleLogin());

        btnExit.addActionListener(e -> System.exit(0));
    }

    private void handleLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        // Gọi Service check SHA-256 trong DB
        if (authService.login(user, pass)) {
            JOptionPane.showMessageDialog(this, "Chào mừng " + UserSession.getCurrentUser().getUsername() + " trở lại!");

            // Đóng cửa sổ Login và mở Dashboard chính
            this.dispose();
            openMainDashboard();
        } else {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openMainDashboard() {
        // Chỗ này sau này ông sẽ gọi cái Frame chính của nhóm ông (Ví dụ MainFrame)
        JFrame main = new JFrame("Hệ thống Quản lý MIS - Dashboard");
        main.setSize(1200, 800);
        main.setLocationRelativeTo(null);
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Demo hiển thị Role hiện tại
        JLabel lblWelcome = new JLabel("Quyền hạn hiện tại: " + UserSession.getCurrentUser().getRole(), SwingConstants.CENTER);
        main.add(lblWelcome);

        main.setVisible(true);
    }

    public static void main(String[] args) {
        // Set giao diện hệ thống cho đẹp
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}