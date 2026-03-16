package vn.edu.ute.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import vn.edu.ute.service.UserAccountService;
import vn.edu.ute.service.impl.UserAccountServiceImpl;
import vn.edu.ute.util.UserSession;

public class LoginFrame extends JFrame {

    private final UserAccountService authService = new UserAccountServiceImpl();

    private final Color COLOR_PRIMARY = new Color(52, 152, 219);
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPass;
    private JButton btnLogin, btnExit;

    public LoginFrame() {
        setTitle("MIS Center - Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initComponents();
        setupEvents();

        pack();
        setSize(480, 550);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);

        // Header
        JPanel pnlHeader = new JPanel(new GridBagLayout());
        pnlHeader.setBackground(COLOR_PRIMARY);
        pnlHeader.setPreferredSize(new Dimension(480, 120));
        JLabel lblTitle = new JLabel("MIS CENTER LOGIN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle);
        mainPanel.add(pnlHeader, BorderLayout.NORTH);

        // Form
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(new EmptyBorder(30, 50, 30, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        gbc.gridy = 0; pnlForm.add(new JLabel("Tên đăng nhập"), gbc);
        gbc.gridy = 1; txtUsername = new JTextField(); customizeField(txtUsername); pnlForm.add(txtUsername, gbc);
        gbc.gridy = 2; pnlForm.add(new JLabel("Mật khẩu"), gbc);
        gbc.gridy = 3; txtPassword = new JPasswordField(); customizeField(txtPassword); pnlForm.add(txtPassword, gbc);
        gbc.gridy = 4; chkShowPass = new JCheckBox("Hiện mật khẩu"); chkShowPass.setBackground(Color.WHITE); pnlForm.add(chkShowPass, gbc);

        mainPanel.add(pnlForm, BorderLayout.CENTER);

        // Footer
        JPanel pnlFooter = new JPanel(new GridBagLayout());
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setBorder(new EmptyBorder(0, 50, 40, 50));
        GridBagConstraints gbcBtn = new GridBagConstraints();
        gbcBtn.fill = GridBagConstraints.HORIZONTAL; gbcBtn.weightx = 1.0; gbcBtn.insets = new Insets(0, 5, 0, 5);

        btnLogin = new JButton("ĐĂNG NHẬP");
        customizeButton(btnLogin, COLOR_PRIMARY, Color.WHITE);
        pnlFooter.add(btnLogin, gbcBtn);

        btnExit = new JButton("THOÁT");
        customizeButton(btnExit, new Color(231, 76, 60), Color.WHITE);
        gbcBtn.gridx = 1;
        pnlFooter.add(btnExit, gbcBtn);

        mainPanel.add(pnlFooter, BorderLayout.SOUTH);
    }

    private void customizeField(JTextField field) {
        field.setPreferredSize(new Dimension(300, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
    }

    private void customizeButton(JButton btn, Color bg, Color fg) {
        btn.setPreferredSize(new Dimension(140, 45));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
    }

    private void setupEvents() {
        chkShowPass.addActionListener(e -> txtPassword.setEchoChar(chkShowPass.isSelected() ? (char) 0 : '•'));
        btnLogin.addActionListener(e -> handleLogin());
        btnExit.addActionListener(e -> System.exit(0));
    }

    // --- HÀM XỬ LÝ ĐĂNG NHẬP ---
    private void handleLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (authService.login(user, pass)) {
            JOptionPane.showMessageDialog(this, "Thành công! Chào mừng " + UserSession.getCurrentUser().getUsername());
            openMainDashboard(); // Gọi hàm mở Dashboard ở đây
        } else {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- HÀM MỞ DASHBOARD (TÁCH RIÊNG RA NGOÀI) ---
    private void openMainDashboard() {
        this.dispose(); // Đóng cửa sổ Login
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true); // Mở MainFrame
        });
    }
}