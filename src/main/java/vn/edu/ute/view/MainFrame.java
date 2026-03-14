package vn.edu.ute.view;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import vn.edu.ute.util.UserSession;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.util.MenuConstants;
import vn.edu.ute.view.TeacherPanel;
import vn.edu.ute.view.StudentPanel;
import vn.edu.ute.view.TeacherDashboardPanel;
import vn.edu.ute.view.StudentDashboardPanel;
import vn.edu.ute.view.ClassPanel;
import vn.edu.ute.view.CoursePanel;
import vn.edu.ute.view.FinancePanel;
import vn.edu.ute.view.NotificationPanel;
import vn.edu.ute.view.SchedulePanel;
import vn.edu.ute.view.AccountPanel;
import vn.edu.ute.view.BranchPanel;
import vn.edu.ute.view.RoomPanel;
import vn.edu.ute.view.PromotionPanel;
import vn.edu.ute.view.PlacementTestPanel;
import vn.edu.ute.view.EnrollmentPanel;
import vn.edu.ute.view.ResultPanel;
import vn.edu.ute.view.CertificatePanel;
import vn.edu.ute.view.LoginFrame;
import javax.swing.ScrollPaneConstants;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;

public class MainFrame extends JFrame {

    private JPanel pnlContent;
    private CardLayout cardLayout;
    private JLabel lblStatus;
    private UserAccount currentUser;

    // Map để lưu trữ các Panel đã được khởi tạo (Lazy Load)
    private Map<String, JPanel> initializedPanels = new HashMap<>();

    public MainFrame() {
        currentUser = UserSession.getCurrentUser();

        setTitle("Hệ thống Quản lý MIS - Center");
        setMinimumSize(new Dimension(900, 600));  // kích thước tối thiểu
        setSize(1100, 720);                       // kích thước mặc định nhỏ hơn
        setExtendedState(JFrame.MAXIMIZED_BOTH);  // mở toàn màn hình khi khởi động
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. SIDEBAR ---
        JPanel pnlSidebar = new JPanel();
        pnlSidebar.setLayout(new BoxLayout(pnlSidebar, BoxLayout.Y_AXIS));
        pnlSidebar.setBackground(new Color(33, 47, 60));
        pnlSidebar.setPreferredSize(new Dimension(200, 600));

        JLabel lblBrand = new JLabel("MIS CENTER");
        lblBrand.setForeground(new Color(93, 173, 226));
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblBrand.setBorder(BorderFactory.createEmptyBorder(18, 15, 18, 15));
        lblBrand.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlSidebar.add(lblBrand);

        // Đường phan cách
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(52, 73, 94));
        sep.setMaximumSize(new Dimension(200, 1));
        pnlSidebar.add(sep);
        pnlSidebar.add(Box.createVerticalStrut(5));

        // --- 2. CONTENT AREA (CardLayout) ---
        cardLayout = new CardLayout();
        pnlContent = new JPanel(cardLayout);
        pnlContent.setBackground(Color.WHITE);

        // Card Mặc định (Home)
        JPanel pnlHome = new JPanel(new BorderLayout());
        pnlHome.setBackground(new Color(245, 248, 250));
        JLabel lblWelcome = new JLabel("✨ Chào mừng trở lại, " + currentUser.getUsername().toUpperCase() + "!", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblWelcome.setForeground(new Color(44, 62, 80));
        pnlHome.add(lblWelcome, BorderLayout.CENTER);
        pnlContent.add(pnlHome, "Home");
        add(pnlContent, BorderLayout.CENTER);

        // --- TẠO MENU ĐỘNG DỰA TRÊN QUYỀN ---
        buildDynamicSidebar(pnlSidebar);

        pnlSidebar.add(Box.createVerticalGlue());
        addMenuButton(pnlSidebar, "Đăng xuất", e -> handleLogout());
        
        JScrollPane scrollSidebar = new JScrollPane(pnlSidebar);
        scrollSidebar.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollSidebar.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollSidebar.setBorder(BorderFactory.createEmptyBorder());
        scrollSidebar.setPreferredSize(new Dimension(200, 600));
        scrollSidebar.getVerticalScrollBar().setUnitIncrement(12);
        
        add(scrollSidebar, BorderLayout.WEST);

        // --- 3. STATUS BAR ---
        JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        pnlStatus.setBackground(new Color(44, 62, 80));
        lblStatus = new JLabel("●  " + currentUser.getUsername() + "  |  " + currentUser.getRole());
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(189, 195, 199));
        pnlStatus.add(lblStatus);
        add(pnlStatus, BorderLayout.SOUTH);
    }

    /**
     * Sinh các nút Sidebar dựa trên MenuConstants
     */
    private void buildDynamicSidebar(JPanel pnlSidebar) {
        String[] allModules = {
                MenuConstants.MODULE_TEACHER,
                MenuConstants.MODULE_STUDENT,
                MenuConstants.MODULE_CLASS,
                MenuConstants.MODULE_COURSE,
                MenuConstants.MODULE_FINANCE,
                MenuConstants.MODULE_SCHEDULE,
                MenuConstants.MODULE_NOTIFICATION,
                MenuConstants.MODULE_ACCOUNT,
                MenuConstants.MODULE_BRANCH,
                MenuConstants.MODULE_ROOM,
                MenuConstants.MODULE_PROMOTION,
                MenuConstants.MODULE_PLACEMENT_TEST,
                MenuConstants.MODULE_ENROLLMENT,
                MenuConstants.MODULE_RESULT,
                MenuConstants.MODULE_CERTIFICATE
        };

        for (String module : allModules) {
            if (MenuConstants.isModuleAllowed(module, currentUser.getRole())) {
                addMenuButton(pnlSidebar, module, e -> handleMenuClick(module));
            }
        }
    }

    private JButton addMenuButton(JPanel sidebar, String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setPreferredSize(new Dimension(200, 40));
        Color bgNormal  = new Color(33, 47, 60);
        Color bgHover   = new Color(52, 152, 219);
        Color bgLogout  = new Color(169, 50, 38);
        boolean isLogout = text.equals("Đăng xuất");
        btn.setBackground(isLogout ? bgLogout : bgNormal);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Hover effects
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(isLogout ? new Color(203, 67, 53) : bgHover);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(isLogout ? bgLogout : bgNormal);
            }
        });
        btn.addActionListener(action);
        sidebar.add(btn);
        sidebar.add(Box.createVerticalStrut(2));
        return btn;
    }

    /**
     * Logic Lazy Load: Chỉ khởi tạo Panel khi người dùng click vào
     */
    private void handleMenuClick(String moduleName) {
        if (!initializedPanels.containsKey(moduleName)) {
            JPanel newPanel = createPanelForModule(moduleName);
            if (newPanel != null) {
                pnlContent.add(newPanel, moduleName);
                initializedPanels.put(moduleName, newPanel);
            } else {
                JOptionPane.showMessageDialog(this, "Module " + moduleName + " đang được phát triển!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        cardLayout.show(pnlContent, moduleName);
    }

    private JPanel createPanelForModule(String moduleName) {
        switch (moduleName) {
            case MenuConstants.MODULE_TEACHER: return new TeacherPanel();
            case MenuConstants.MODULE_STUDENT: return new StudentPanel();
            case MenuConstants.MODULE_CLASS: 
                if (currentUser.getRole() == vn.edu.ute.model.enums.UserRole.Teacher) {
                    return new TeacherDashboardPanel();
                } else if (currentUser.getRole() == vn.edu.ute.model.enums.UserRole.Student) {
                    return new StudentDashboardPanel();
                }
                return new ClassPanel();
            case MenuConstants.MODULE_COURSE: return new CoursePanel();
            case MenuConstants.MODULE_FINANCE: return new FinancePanel();
            case MenuConstants.MODULE_NOTIFICATION: return new NotificationPanel(currentUser);
            case MenuConstants.MODULE_SCHEDULE: return new SchedulePanel();
            case MenuConstants.MODULE_ACCOUNT: return new AccountPanel();
            case MenuConstants.MODULE_BRANCH: return new BranchPanel();
            case MenuConstants.MODULE_ROOM: return new RoomPanel();
            case MenuConstants.MODULE_PROMOTION: return new PromotionPanel();
            case MenuConstants.MODULE_PLACEMENT_TEST: return new PlacementTestPanel();
            case MenuConstants.MODULE_ENROLLMENT: return new EnrollmentPanel();
            case MenuConstants.MODULE_RESULT: return new ResultPanel();
            case MenuConstants.MODULE_CERTIFICATE: return new CertificatePanel();
            default: return null;
        }
    }

    private void handleLogout() {
        UserSession.logout();
        this.dispose();
        new LoginFrame().setVisible(true);
    }
}