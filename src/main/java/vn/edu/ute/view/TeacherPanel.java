package vn.edu.ute.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import vn.edu.ute.model.Teacher;
import vn.edu.ute.model.enums.Status;
import vn.edu.ute.service.TeacherService;
import vn.edu.ute.service.impl.TeacherServiceImpl;

public class TeacherPanel extends JPanel {

    private final TeacherService teacherService = new TeacherServiceImpl();

    // UI Components
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtName, txtPhone, txtEmail, txtSpecialty, txtSearch;
    private JComboBox<Status> cbStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnSearch, btnRefresh;

    public TeacherPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Chia 3 phần đúng phong cách Pro
        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadDataToTable(teacherService.getAllTeachers());
    }

    private JPanel createFormPanel() {
        // Dùng GridLayout cho các ô nhập liệu thẳng hàng như StudentPanel
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin giáo viên"));

        txtId = new JTextField(); txtId.setEditable(false);
        txtName = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        txtSpecialty = new JTextField();
        cbStatus = new JComboBox<>(Status.values());

        formPanel.add(new JLabel("ID:")); formPanel.add(txtId);
        formPanel.add(new JLabel("Họ Tên:")); formPanel.add(txtName);
        formPanel.add(new JLabel("Số điện thoại:")); formPanel.add(txtPhone);
        formPanel.add(new JLabel("Email:")); formPanel.add(txtEmail);
        formPanel.add(new JLabel("Chuyên môn:")); formPanel.add(txtSpecialty);
        formPanel.add(new JLabel("Trạng thái:")); formPanel.add(cbStatus);

        return formPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Họ Tên", "SĐT", "Email", "Chuyên môn", "Trạng Thái"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Sự kiện click bảng đổ ngược lên Form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                Long id = (Long) tableModel.getValueAt(row, 0);

                // Tìm giáo viên từ Service để đổ dữ liệu chính xác
                teacherService.getAllTeachers().stream()
                        .filter(t -> t.getTeacherId().equals(id))
                        .findFirst()
                        .ifPresent(t -> {
                            txtId.setText(String.valueOf(t.getTeacherId()));
                            txtName.setText(t.getFullName());
                            txtPhone.setText(t.getPhone());
                            txtEmail.setText(t.getEmail());
                            txtSpecialty.setText(t.getSpecialty());
                            cbStatus.setSelectedItem(t.getStatus());
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
        btnSearch = new JButton("Tìm chuyên môn");
        searchPanel.add(txtSearch); searchPanel.add(btnSearch);

        // Các nút chức năng bên phải
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdd = new JButton("Thêm mới");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới");

        buttonPanel.add(btnAdd); buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete); buttonPanel.add(btnRefresh);

        actionPanel.add(searchPanel, BorderLayout.WEST);
        actionPanel.add(buttonPanel, BorderLayout.EAST);

        setupListeners();
        return actionPanel;
    }

    private void setupListeners() {
        // Thêm mới
        btnAdd.addActionListener(e -> handleAddUpdate(null));

        // Cập nhật
        btnUpdate.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Chọn một giáo viên để sửa!");
                return;
            }
            handleAddUpdate(Long.parseLong(txtId.getText()));
        });

        // Xóa
        btnDelete.addActionListener(e -> {
            if (txtId.getText().isEmpty()) return;
            int confirm = JOptionPane.showConfirmDialog(this, "Xóa giáo viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    teacherService.deleteTeacher(Long.parseLong(txtId.getText()));
                    refreshUI();
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi xóa!"); }
            }
        });

        // Tìm kiếm sử dụng Lambda
        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText();
            loadDataToTable(teacherService.findBySpecialty(keyword));
        });

        btnRefresh.addActionListener(e -> refreshUI());
    }

    private void handleAddUpdate(Long id) {
        try {
            Teacher t = Teacher.builder()
                    .teacherId(id)
                    .fullName(txtName.getText())
                    .phone(txtPhone.getText())
                    .email(txtEmail.getText())
                    .specialty(txtSpecialty.getText())
                    .status((Status) cbStatus.getSelectedItem())
                    .build();

            if (id == null) {
                teacherService.addTeacher(t);
            } else {
                teacherService.updateTeacher(t);
            }
            JOptionPane.showMessageDialog(this, "Thao tác thành công!");
            refreshUI();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void loadDataToTable(List<Teacher> list) {
        tableModel.setRowCount(0);
        for (Teacher t : list) {
            tableModel.addRow(new Object[]{
                    t.getTeacherId(), t.getFullName(), t.getPhone(),
                    t.getEmail(), t.getSpecialty(), t.getStatus()
            });
        }
    }

    private void refreshUI() {
        txtId.setText(""); txtName.setText(""); txtPhone.setText("");
        txtEmail.setText(""); txtSpecialty.setText(""); txtSearch.setText("");
        cbStatus.setSelectedIndex(0);
        loadDataToTable(teacherService.getAllTeachers());
    }
}