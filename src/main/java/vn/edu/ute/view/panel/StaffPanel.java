package vn.edu.ute.view.panel;

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
    private JTextField txtName, txtPhone, txtEmail;
    private JComboBox<StaffRole> cbRole;

    // Staff ID variable
    private Long selectedStaffId = null;

    public StaffPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- PHẦN FORM NHẬP LIỆU (NORTH) ---
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Nhân sự"));

        txtName = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        cbRole = new JComboBox<>(StaffRole.values());

        formPanel.add(new JLabel("Họ và tên:")); formPanel.add(txtName);
        formPanel.add(new JLabel("Vai trò:")); formPanel.add(cbRole);
        formPanel.add(new JLabel("Số điện thoại:")); formPanel.add(txtPhone);
        formPanel.add(new JLabel("Email:")); formPanel.add(txtEmail);

        // --- PHẦN NÚT CHỨC NĂNG (CENTER) ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu");
        JButton btnRefresh = new JButton("Làm mới form");
        
        // --- PHẦN BẢNG DỮ LIỆU (SOUTH) ---
        String[] columns = {"ID", "Họ tên", "Vai trò", "SĐT", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override // Khóa không cho người dùng edit trực tiếp vào ô trong JTable
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        staffTable = new JTable(tableModel);

        // --- SỬ DỤNG LAMBDA CHO CÁC SỰ KIỆN ---

        // 1. Sự kiện Click vào hàng của JTable để lấy dữ liệu fill lên Form
        staffTable.getSelectionModel().addListSelectionListener(e -> {
            // !e.getValueIsAdjusting() giúp sự kiện chỉ chạy 1 lần khi nhả chuột
            if (!e.getValueIsAdjusting() && staffTable.getSelectedRow() != -1) {
                int row = staffTable.getSelectedRow();
                
                // Lấy dữ liệu từ JTable dựa vào chỉ số cột (Column Index)
                selectedStaffId = (Long) tableModel.getValueAt(row, 0);
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                cbRole.setSelectedItem(tableModel.getValueAt(row, 2));
                txtPhone.setText(tableModel.getValueAt(row, 3).toString());
                txtEmail.setText(tableModel.getValueAt(row, 4).toString());
            }
        });

        // 2. Sự kiện bấm nút Lưu
        btnSave.addActionListener(e -> saveStaff());

        // 3. Sự kiện Làm mới form (Clear data)
        btnRefresh.addActionListener(e -> clearForm());

        actionPanel.add(btnSave);
        actionPanel.add(btnRefresh);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(actionPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(staffTable), BorderLayout.CENTER);

        // Tải dữ liệu lần đầu
        loadDataToTable();
    }

    private void saveStaff() {
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Nếu selectedStaffId có giá trị, Hibernate sẽ tự động UPDATE. Ngược lại là INSERT.
            Staff staff = Staff.builder()
                    .staffId(selectedStaffId) 
                    .fullName(txtName.getText().trim())
                    .role((StaffRole) cbRole.getSelectedItem())
                    .phone(txtPhone.getText().trim())
                    .email(txtEmail.getText().trim())
                    .status(Status.Active) // Mặc định là Active
                    .build();
            
            staffService.saveStaff(staff);
            
            String msg = (selectedStaffId == null) ? "Thêm mới thành công!" : "Cập nhật thành công!";
            JOptionPane.showMessageDialog(this, msg);
            
            clearForm(); // Xóa trắng form sau khi lưu
            loadDataToTable(); // Tải lại bảng

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void clearForm() {
        selectedStaffId = null; // Reset lại biến ID để chuyển về trạng thái Thêm mới
        txtName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        cbRole.setSelectedIndex(0);
        staffTable.clearSelection(); // Bỏ chọn highlight dưới JTable
    }

    private void loadDataToTable() {
        try {
            tableModel.setRowCount(0); // Xóa dữ liệu cũ trên giao diện
            List<Staff> staffs = staffService.getActiveStaffs();
            
            // Sử dụng LAMBDA Stream để đổ dữ liệu
            staffs.forEach(s -> tableModel.addRow(new Object[]{
                    s.getStaffId(), s.getFullName(), s.getRole(), s.getPhone(), s.getEmail()
            }));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}