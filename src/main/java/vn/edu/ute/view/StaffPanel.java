package vn.edu.ute.view;

import vn.edu.ute.model.Staff;
import vn.edu.ute.model.enums.StaffRole;
import vn.edu.ute.model.enums.Status;
import vn.edu.ute.service.StaffService;
import vn.edu.ute.service.impl.StaffServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StaffPanel extends JPanel {
    private final StaffService staffService = new StaffServiceImpl();

    private DefaultTableModel tableModel;
    private JTable staffTable;

    private JTextField txtId, txtName, txtPhone, txtEmail;
    private JComboBox<StaffRole> cbRole;

    private Long selectedStaffId = null;

    public StaffPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- FORM ---
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Nhân sự"));

        txtId = new JTextField();
        txtId.setEditable(false);
        txtName = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        cbRole = new JComboBox<>(StaffRole.values());

        formPanel.add(new JLabel("ID:")); formPanel.add(txtId);
        formPanel.add(new JLabel("Vai trò:")); formPanel.add(cbRole);
        formPanel.add(new JLabel("Họ và tên:")); formPanel.add(txtName);
        formPanel.add(new JLabel("Số điện thoại:")); formPanel.add(txtPhone);
        formPanel.add(new JLabel("Email:")); formPanel.add(txtEmail);
        formPanel.add(new JLabel("")); formPanel.add(new JLabel(""));

        // --- ACTIONS ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Cập nhật");
        JButton btnDelete = new JButton("Xóa");
        JButton btnRefresh = new JButton("Làm mới form");

        actionPanel.add(btnAdd);
        actionPanel.add(btnUpdate);
        actionPanel.add(btnDelete);
        actionPanel.add(btnRefresh);

        // --- TABLE ---
        String[] columns = {"ID", "Họ tên", "Vai trò", "SĐT", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        staffTable = new JTable(tableModel);

        // Row selection -> fill form
        staffTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && staffTable.getSelectedRow() != -1) {
                int row = staffTable.getSelectedRow();
                selectedStaffId = (Long) tableModel.getValueAt(row, 0);

                txtId.setText(String.valueOf(selectedStaffId));
                txtName.setText(String.valueOf(tableModel.getValueAt(row, 1)));
                cbRole.setSelectedItem(tableModel.getValueAt(row, 2));
                txtPhone.setText(String.valueOf(tableModel.getValueAt(row, 3)));
                txtEmail.setText(String.valueOf(tableModel.getValueAt(row, 4)));
            }
        });

        // Buttons
        btnAdd.addActionListener(e -> addStaff());
        btnUpdate.addActionListener(e -> updateStaff());
        btnDelete.addActionListener(e -> deleteStaff());
        btnRefresh.addActionListener(e -> clearForm());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(actionPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(staffTable), BorderLayout.CENTER);

        loadDataToTable();
    }

    private Staff buildStaffFromForm(Long staffId) {
        return Staff.builder()
                .staffId(staffId)
                .fullName(txtName.getText().trim())
                .role((StaffRole) cbRole.getSelectedItem())
                .phone(txtPhone.getText().trim())
                .email(txtEmail.getText().trim())
                .status(Status.Active)
                .build();
    }

    private void addStaff() {
        try {
            Staff staff = buildStaffFromForm(null);
            staffService.addStaff(staff);
            JOptionPane.showMessageDialog(this, "Thêm mới thành công!");
            clearForm();
            loadDataToTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void updateStaff() {
        if (selectedStaffId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân sự cần cập nhật!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Staff staff = buildStaffFromForm(selectedStaffId);
            staffService.updateStaff(staff);
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            clearForm();
            loadDataToTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteStaff() {
        if (selectedStaffId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân sự cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xóa nhân sự này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            staffService.deleteStaff(selectedStaffId);
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            clearForm();
            loadDataToTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void clearForm() {
        selectedStaffId = null;
        txtId.setText("");
        txtName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        cbRole.setSelectedIndex(0);
        staffTable.clearSelection();
    }

    private void loadDataToTable() {
        try {
            tableModel.setRowCount(0);
            List<Staff> staffs = staffService.getActiveStaffs();
            if (staffs == null) return;

            staffs.forEach(s -> tableModel.addRow(new Object[]{
                    s.getStaffId(), s.getFullName(), s.getRole(), s.getPhone(), s.getEmail()
            }));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}