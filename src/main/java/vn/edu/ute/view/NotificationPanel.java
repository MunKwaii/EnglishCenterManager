package vn.edu.ute.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import vn.edu.ute.model.Notification;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.model.enums.NotificationTargetRole;
import vn.edu.ute.service.NotificationService;
import vn.edu.ute.service.impl.NotificationServiceImpl;
import vn.edu.ute.util.PermissionUtils;

public class NotificationPanel extends JPanel {

    // Services
    private final NotificationService notificationService = new NotificationServiceImpl();
    private UserAccount currentUser;
    // UI Components
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTitle, txtSearch, txtId; // Thêm txtId để quản lý cập nhật/xóa
    private JTextArea taContent;
    private JComboBox<NotificationTargetRole> cbTargetRole;
    private JComboBox<String> cbFilterRole;
    private JButton btnSend, btnUpdate, btnDelete, btnSearch, btnRefresh, btnTop5, btnUrgent;
    private JLabel lblStats, lblUrgentWarning;

    public NotificationPanel(UserAccount currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tổ chức theo 3 phần chính giống ClassPanel
        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        // PHÂN QUYỀN: Ẩn/vô hiệu hóa nút nếu không có quyền
        applyPermissions();

        loadDataToTable(loadNotificationsForCurrentUser());
        updateStats();
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Soạn thảo thông báo"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField();
        txtId.setEditable(false);
        txtTitle = new JTextField();
        cbTargetRole = new JComboBox<>(NotificationTargetRole.values());

        lblStats = new JLabel("Tổng thông báo: 0 | Khẩn cấp: Không");
        lblStats.setFont(new Font("Arial", Font.BOLD, 12));

        taContent = new JTextArea(4, 20);
        taContent.setLineWrap(true);
        taContent.setWrapStyleWord(true);
        JScrollPane spContent = new JScrollPane(taContent);

        // ...existing code...

        // Bố trí các thành phần
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(txtId, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Đối tượng:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1.0;
        formPanel.add(cbTargetRole, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Tiêu đề:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        formPanel.add(txtTitle, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Nội dung:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(spContent, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        formPanel.add(lblStats, gbc);

        return formPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        String[] columns = { "ID", "Tiêu đề", "Người tạo", "Đối tượng", "Ngày tạo" };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Sự kiện chọn dòng để đổ dữ liệu ngược lại form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                Long id = (Long) tableModel.getValueAt(row, 0);

                // Tìm object thông báo từ Service để đổ dữ liệu chính xác
                notificationService.getAllNotifications().stream()
                        .filter(n -> n.getNotificationId().equals(id))
                        .findFirst()
                        .ifPresent(n -> {
                            txtId.setText(String.valueOf(n.getNotificationId()));
                            txtTitle.setText(n.getTitle());
                            taContent.setText(n.getContent());
                            cbTargetRole.setSelectedItem(n.getTargetRole());
                        });
            }
        });

        return tablePanel;
    }

    private JPanel createButtonPanel() {
        JPanel actionPanel = new JPanel(new BorderLayout());

        // Phần tìm kiếm bên trái
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(12);
        btnSearch = new JButton("Tìm kiếm");

        cbFilterRole = new JComboBox<>(new String[] { "Staff", "Student", "Teacher", "All" });

        searchPanel.add(new JLabel("Lọc đối tượng:"));
        searchPanel.add(cbFilterRole);
        searchPanel.add(new JLabel("Tìm tiêu đề:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        // Các nút chức năng bên phải
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSend = new JButton("Gửi mới");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa");
        btnTop5 = new JButton("Top 5 mới nhất");
        btnUrgent = new JButton("Thông báo khẩn cấp");
        btnRefresh = new JButton("Làm mới");

        buttonPanel.add(btnSend);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnTop5);
        buttonPanel.add(btnUrgent);
        buttonPanel.add(btnRefresh);

        actionPanel.add(searchPanel, BorderLayout.WEST);
        actionPanel.add(buttonPanel, BorderLayout.EAST);

        setupButtonListeners();
        return actionPanel;
    }

    private void loadDataToTable(List<Notification> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (Notification n : list) {
                String createdBy = (n.getCreatedByUser() != null)
                        ? n.getCreatedByUser().getUsername()
                        : "N/A";

                tableModel.addRow(new Object[] {
                        n.getNotificationId(), n.getTitle(), createdBy, n.getTargetRole(), n.getCreatedAt()
                });
            }
        }
    }

    private void setupButtonListeners() {
        // Gửi mới
        btnSend.addActionListener(e -> {
            try {
                Notification n = Notification.builder()
                        .title(txtTitle.getText())
                        .content(taContent.getText())
                        .targetRole((NotificationTargetRole) cbTargetRole.getSelectedItem())
                        .createdByUser(currentUser)
                        .build();
                notificationService.createNotification(n);
                JOptionPane.showMessageDialog(this, "Gửi thông báo thành công!");
                refreshUI();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        });

        // Cập nhật
        btnUpdate.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn thông báo cần sửa!");
                return;
            }
            try {
                Notification n = Notification.builder()
                        .notificationId(Long.parseLong(txtId.getText()))
                        .title(txtTitle.getText())
                        .content(taContent.getText())
                        .targetRole((NotificationTargetRole) cbTargetRole.getSelectedItem())
                        .createdByUser(currentUser)
                        .build();
                notificationService.updateNotification(n);
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                refreshUI();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + ex.getMessage());
            }
        });

        // Tìm kiếm theo từ khóa
        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText().trim();
            if (keyword.isEmpty()) {
                loadDataToTable(loadNotificationsForCurrentUser());
            } else {
                loadDataToTable(notificationService.searchByKeyword(keyword));
            }
        });

        // Filter theo đối tượng (Role)
        cbFilterRole.addActionListener(e -> {
            String filterType = (String) cbFilterRole.getSelectedItem();
            List<Notification> results;

            if ("Staff".equals(filterType)) {
                results = notificationService.getNotificationsForUser(NotificationTargetRole.Staff);
            } else if ("Student".equals(filterType)) {
                results = notificationService.getNotificationsForUser(NotificationTargetRole.Student);
            } else if ("Teacher".equals(filterType)) {
                results = notificationService.getNotificationsForUser(NotificationTargetRole.Teacher);
            } else { // All
                results = notificationService.getNotificationsForUser(NotificationTargetRole.All);
            }

            loadDataToTable(results);
        });

        // Top 5 thông báo mới nhất (lọc theo role của user hiện tại)
        btnTop5.addActionListener(e -> {
            List<Notification> top5 = getTop5ForCurrentUser();
            loadDataToTable(top5);
            JOptionPane.showMessageDialog(this, "Đã tải 5 thông báo mới nhất");
        });

        // Kiểm tra thông báo khẩn cấp
        btnUrgent.addActionListener(e -> {
            boolean hasUrgent = notificationService.hasUrgentNotifications();
            if (hasUrgent) {
                JOptionPane.showMessageDialog(this, "CÓ thông báo KHẨN CẤP trong hệ thống!", "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Không có thông báo khẩn cấp", "Thông tin",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Làm mới
        btnRefresh.addActionListener(e -> refreshUI());

        // Xóa
        btnDelete.addActionListener(e -> {
            if (txtId.getText().isEmpty())
                return;
            int confirm = JOptionPane.showConfirmDialog(this, "Xóa thông báo này?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    notificationService.deleteNotification(Long.parseLong(txtId.getText()));
                    refreshUI();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa!");
                }
            }
        });
    }

    private void refreshUI() {
        txtId.setText("");
        txtTitle.setText("");
        taContent.setText("");
        txtSearch.setText("");
        cbTargetRole.setSelectedIndex(0);
        if (cbFilterRole.isVisible()) {
            cbFilterRole.setSelectedIndex(0);
        }
        loadDataToTable(loadNotificationsForCurrentUser());
        updateStats();
    }

    /**
     * Load thông báo phù hợp với role của user hiện tại:
     * - Admin/Staff: xem tất cả thông báo (dùng combobox lọc)
     * - Student: chỉ xem thông báo Student và All
     * - Teacher: chỉ xem thông báo Teacher và All
     */
    private List<Notification> loadNotificationsForCurrentUser() {
        if (PermissionUtils.canViewAllNotifications(currentUser)) {
            return notificationService.getAllNotifications();
        }
        // Lấy thông báo theo role + All, gộp lại
        String roleName = currentUser.getRole().name(); // "Student" hoặc "Teacher"
        NotificationTargetRole targetRole;
        try {
            targetRole = NotificationTargetRole.valueOf(roleName);
        } catch (IllegalArgumentException ex) {
            return notificationService.getAllNotifications();
        }
        List<Notification> result = new ArrayList<>();
        List<Notification> forRole = notificationService.getNotificationsForUser(targetRole);
        List<Notification> forAll = notificationService.getNotificationsForUser(NotificationTargetRole.All);
        if (forRole != null)
            result.addAll(forRole);
        if (forAll != null)
            result.addAll(forAll);
        return result;
    }

    /**
     * Lấy top 5 thông báo mới nhất theo role của user hiện tại:
     * - Admin/Staff: top 5 toàn bộ thông báo
     * - Student/Teacher: top 5 trong các thông báo dành cho role của họ và All
     */
    private List<Notification> getTop5ForCurrentUser() {
        List<Notification> pool = loadNotificationsForCurrentUser();
        return pool.stream()
                .filter(n -> n.getCreatedAt() != null)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .collect(java.util.stream.Collectors.toList());
    }

    private void updateStats() {
        long totalCount = notificationService.getAllNotifications().size();
        boolean hasUrgent = notificationService.hasUrgentNotifications();
        String urgentStatus = hasUrgent ? "Có" : "Không";
        lblStats.setText(String.format("Tổng thông báo: %d | Khẩn cấp: %s", totalCount, urgentStatus));

        if (hasUrgent) {
            lblStats.setForeground(new Color(231, 76, 60)); // Red color
        } else {
            lblStats.setForeground(new Color(39, 174, 96)); // Green color
        }
    }

    /**
     * Phân quyền: Chỉ Admin và Staff (Admin/Manager) mới được gửi/sửa/xóa
     * Student và Teacher chỉ được XEM thông báo của mình
     */
    private void applyPermissions() {
        boolean canManage = PermissionUtils.canManageNotifications(currentUser);
        boolean canViewAll = PermissionUtils.canViewAllNotifications(currentUser);

        // Ẩn combobox lọc đối tượng với Student và Teacher
        if (!canViewAll) {
            cbFilterRole.setVisible(false);
            // Ẩn luôn label "Lọc đối tượng:" đi kèm
            Component[] components = cbFilterRole.getParent().getComponents();
            for (int i = 0; i < components.length; i++) {
                if (components[i] == cbFilterRole && i > 0) {
                    components[i - 1].setVisible(false);
                    break;
                }
            }
        }

        if (!canManage) {
            // Vô hiệu hóa các nút quản lý
            btnSend.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);

            // Vô hiệu hóa form soạn thảo
            txtTitle.setEditable(false);
            taContent.setEditable(false);
            cbTargetRole.setEnabled(false);

            // Hiển thị thông báo chế độ chỉ đọc
            JLabel lblReadOnly = new JLabel("Chế độ chỉ đọc - Bạn không có quyền gửi thông báo", SwingConstants.CENTER);
            lblReadOnly.setForeground(new Color(231, 76, 60));
            lblReadOnly.setFont(new Font("Segoe UI", Font.BOLD, 14));
            add(lblReadOnly, BorderLayout.NORTH);

            // Di chuyển form xuống dưới label cảnh báo
            Component formPanel = getComponent(0);
            remove(formPanel);

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(lblReadOnly, BorderLayout.NORTH);
            topPanel.add(formPanel, BorderLayout.CENTER);
            add(topPanel, BorderLayout.NORTH);
        }
    }
}