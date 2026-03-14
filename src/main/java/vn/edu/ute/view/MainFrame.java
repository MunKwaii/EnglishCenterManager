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
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. SIDEBAR ---
        JPanel pnlSidebar = new JPanel();
        pnlSidebar.setLayout(new BoxLayout(pnlSidebar, BoxLayout.Y_AXIS));
        pnlSidebar.setBackground(new Color(44, 62, 80));
        pnlSidebar.setPreferredSize(new Dimension(250, 800));

        JLabel lblBrand = new JLabel("MIS MANAGEMENT");
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblBrand.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        lblBrand.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlSidebar.add(lblBrand);
        pnlSidebar.add(Box.createVerticalStrut(20));

        // --- 2. CONTENT AREA (CardLayout) ---
        cardLayout = new CardLayout();
        pnlContent = new JPanel(cardLayout);
        pnlContent.setBackground(Color.WHITE);

        // Card Mặc định (Home)
        JPanel pnlHome = new JPanel(new BorderLayout());
        pnlHome.setBackground(Color.WHITE);
        JLabel lblWelcome = new JLabel("CHÀO MỪNG TRỞ LẠI, " + currentUser.getUsername().toUpperCase(), SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
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
        scrollSidebar.setPreferredSize(new Dimension(250, 800));
        
        add(scrollSidebar, BorderLayout.WEST);

        // --- 3. STATUS BAR ---
        JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlStatus.setBackground(new Color(236, 240, 241));
        lblStatus = new JLabel("Đang đăng nhập: " + currentUser.getUsername() + " | Quyền hạn: " + currentUser.getRole());
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 13));
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
                MenuConstants.MODULE_ENROLLMENT
        };

        for (String module : allModules) {
            if (MenuConstants.isModuleAllowed(module, currentUser.getRole())) {
                addMenuButton(pnlSidebar, module, e -> handleMenuClick(module));
            }
        }
    }

    private JButton addMenuButton(JPanel sidebar, String text, java.awt.event.ActionListener action) {
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
            default: return null;
        }
    }

    private void handleLogout() {
        UserSession.logout();
        this.dispose();
        new LoginFrame().setVisible(true);
    }
}