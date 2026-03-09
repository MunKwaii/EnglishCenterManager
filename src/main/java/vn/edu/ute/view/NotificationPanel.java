package vn.edu.ute.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import vn.edu.ute.model.Notification;
import vn.edu.ute.model.enums.NotificationTargetRole;
import vn.edu.ute.service.NotificationService;
import vn.edu.ute.service.impl.NotificationServiceImpl;

public class NotificationPanel extends JPanel {

    // Services
    private final NotificationService notificationService = new NotificationServiceImpl();

    // UI Components
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTitle, txtSearch, txtId; // Thêm txtId để quản lý cập nhật/xóa
    private JTextArea taContent;
    private JComboBox<NotificationTargetRole> cbTargetRole;
    private JButton btnSend, btnUpdate, btnDelete, btnSearch, btnRefresh;

    public NotificationPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tổ chức theo 3 phần chính giống ClassPanel
        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadDataToTable(notificationService.getAllNotifications());
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

        taContent = new JTextArea(4, 20);
        taContent.setLineWrap(true);
        taContent.setWrapStyleWord(true);
        JScrollPane spContent = new JScrollPane(taContent);

        // Bố trí các thành phần
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; formPanel.add(txtId, gbc);

        gbc.gridx = 2; gbc.weightx = 0; formPanel.add(new JLabel("Đối tượng:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0; formPanel.add(cbTargetRole, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; formPanel.add(new JLabel("Tiêu đề:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0; formPanel.add(txtTitle, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.weightx = 0; formPanel.add(new JLabel("Nội dung:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH; formPanel.add(spContent, gbc);

        return formPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Tiêu đề", "Đối tượng", "Ngày tạo"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
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
        txtSearch = new JTextField(15);
        btnSearch = new JButton("Tìm kiếm");
        searchPanel.add(new JLabel("Tìm tiêu đề:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        // Các nút chức năng bên phải
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSend = new JButton("Gửi mới");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới");

        buttonPanel.add(btnSend);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
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
                tableModel.addRow(new Object[]{
                        n.getNotificationId(), n.getTitle(), n.getTargetRole(), n.getCreatedAt()
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
                        .build();
                notificationService.createNotification(n); // Save xử lý cả update
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                refreshUI();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật!");
            }
        });

        // Tìm kiếm sử dụng Lambda từ Service
        btnSearch.addActionListener(e -> {
            loadDataToTable(notificationService.searchByKeyword(txtSearch.getText()));
        });

        // Làm mới
        btnRefresh.addActionListener(e -> refreshUI());

        // Xóa
        btnDelete.addActionListener(e -> {
            if (txtId.getText().isEmpty()) return;
            int confirm = JOptionPane.showConfirmDialog(this, "Xóa thông báo này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
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
        loadDataToTable(notificationService.getAllNotifications());
    }
}