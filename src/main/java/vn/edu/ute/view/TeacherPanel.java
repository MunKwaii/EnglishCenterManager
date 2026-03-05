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
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtName, txtPhone, txtEmail, txtSpecialty;
    private JComboBox<String> cbStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    public TeacherPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- PHẦN 1: FORM NHẬP LIỆU (BÊN TRÁI) ---
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createTitledBorder("Thông tin giáo viên"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Các Label và TextField
        addControl(pnlForm, "ID:", txtId = new JTextField(15), 0, gbc);
        txtId.setEditable(false); // ID tự tăng, không cho sửa
        addControl(pnlForm, "Họ Tên:", txtName = new JTextField(15), 1, gbc);
        addControl(pnlForm, "Số điện thoại:", txtPhone = new JTextField(15), 2, gbc);
        addControl(pnlForm, "Email:", txtEmail = new JTextField(15), 3, gbc);
        addControl(pnlForm, "Chuyên môn:", txtSpecialty = new JTextField(15), 4, gbc);

        String[] statuses = {"Active", "Inactive"};
        addControl(pnlForm, "Trạng thái:", cbStatus = new JComboBox<>(statuses), 5, gbc);

        // --- PHẦN 2: BẢNG HIỂN THỊ (Ở GIỮA) ---
        String[] columns = {"ID", "Họ Tên", "SĐT", "Email", "Chuyên môn", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // --- PHẦN 3: CỤM NÚT BẤM (PHÍA DƯỚI) ---
        JPanel pnlButtons = new JPanel();
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnClear = new JButton("Làm mới");
        pnlButtons.add(btnAdd); pnlButtons.add(btnUpdate);
        pnlButtons.add(btnDelete); pnlButtons.add(btnClear);

        // Đưa tất cả vào Panel chính
        add(pnlForm, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);

        // --- GẮN SỰ KIỆN ---
        setupEvents();
        loadData();
    }

    private void addControl(JPanel p, String label, JComponent c, int row, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = row; p.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.gridy = row; p.add(c, gbc);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        // Dùng cái Lambda sắp xếp ông đã viết ở Service
        List<Teacher> list = teacherService.getSortedTeachers();
        for (Teacher t : list) {
            tableModel.addRow(new Object[]{
                    t.getTeacherId(), t.getFullName(), t.getPhone(), t.getEmail(), t.getSpecialty(), t.getStatus()
            });
        }
    }

    private void setupEvents() {
        // 1. Khi Click vào dòng trên bảng -> Đổ dữ liệu lên Form
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                txtId.setText(table.getValueAt(row, 0).toString());
                txtName.setText(table.getValueAt(row, 1).toString());
                txtPhone.setText(table.getValueAt(row, 2).toString());
                txtEmail.setText(table.getValueAt(row, 3).toString());
                txtSpecialty.setText(table.getValueAt(row, 4).toString());
                cbStatus.setSelectedItem(table.getValueAt(row, 5).toString());
            }
        });

        // 2. Nút Thêm
        btnAdd.addActionListener(e -> {
            try {
                Teacher t = new Teacher();
                t.setFullName(txtName.getText());
                t.setPhone(txtPhone.getText());
                t.setEmail(txtEmail.getText());
                t.setSpecialty(txtSpecialty.getText());
                t.setStatus(Status.valueOf(cbStatus.getSelectedItem().toString()));

                teacherService.addTeacher(t); // Gọi hàm add ở Service
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
                loadData();
                clearForm();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        });

        // 3. Nút Sửa
        btnUpdate.addActionListener(e -> {
            try {
                if(txtId.getText().isEmpty()) return;
                Teacher t = new Teacher();
                t.setTeacherId(Long.parseLong(txtId.getText()));
                t.setFullName(txtName.getText());
                t.setPhone(txtPhone.getText());
                t.setEmail(txtEmail.getText());
                t.setSpecialty(txtSpecialty.getText());
                t.setStatus(Status.valueOf(cbStatus.getSelectedItem().toString()));

                teacherService.addTeacher(t); // Save xử lý cả Update
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật!");
            }
        });

        // 4. Nút Làm mới
        btnClear.addActionListener(e -> clearForm());
    }

    private void clearForm() {
        txtId.setText(""); txtName.setText(""); txtPhone.setText("");
        txtEmail.setText(""); txtSpecialty.setText("");
    }
}