package vn.edu.ute.view;

import vn.edu.ute.model.UserAccount;
import vn.edu.ute.model.enums.UserRole;
import vn.edu.ute.service.UserAccountService;
import vn.edu.ute.service.impl.UserAccountServiceImpl;
import vn.edu.ute.model.Teacher;
import vn.edu.ute.model.Student;
import vn.edu.ute.model.Staff;
import vn.edu.ute.service.TeacherService;
import vn.edu.ute.service.StudentService;
import vn.edu.ute.service.StaffService;
import vn.edu.ute.service.impl.TeacherServiceImpl;
import vn.edu.ute.service.impl.StudentServiceImpl;
import vn.edu.ute.service.impl.StaffServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;

public class AccountPanel extends JPanel {

    private final UserAccountService accountService = new UserAccountServiceImpl();
    private final TeacherService teacherService = new TeacherServiceImpl();
    private final StudentService studentService = new StudentServiceImpl();
    private final StaffService staffService = new StaffServiceImpl();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtUsername, txtPassword, txtSearch;
    private JComboBox<UserRole> cbRole;
    private JComboBox<Boolean> cbIsActive;
    
    // Combo box for mapping to actual Person (Teacher/Student/Staff)
    private JLabel lblPersonMapping;
    private JComboBox<PersonItem> cbPersonMapping;

    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnSearch;

    public AccountPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        try {
            loadDataToTable(accountService.getAllAccounts());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu tài khoản: " + e.getMessage());
        }
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Tài khoản"));

        txtUsername = new JTextField();
        txtPassword = new JTextField();
        
        cbRole = new JComboBox<>(UserRole.values());
        
        cbIsActive = new JComboBox<>(new Boolean[]{true, false});
        cbIsActive.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Boolean) {
                    setText((Boolean) value ? "Hoạt động" : "Bị khóa");
                }
                return this;
            }
        });

        lblPersonMapping = new JLabel("Liên kết NV/HV:");
        cbPersonMapping = new JComboBox<>();
        
        // Cập nhật cbPersonMapping dựa trên Role được chọn
        cbRole.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updatePersonMappingCombo((UserRole) e.getItem());
            }
        });

        // Initialize mapping combo
        updatePersonMappingCombo((UserRole) cbRole.getSelectedItem());

        formPanel.add(new JLabel("Tên đăng nhập:"));
        formPanel.add(txtUsername);
        formPanel.add(new JLabel("Mật khẩu:"));
        formPanel.add(txtPassword);

        formPanel.add(new JLabel("Quyền han:"));
        formPanel.add(cbRole);
        formPanel.add(new JLabel("Trạng thái:"));
        formPanel.add(cbIsActive);
        
        formPanel.add(lblPersonMapping);
        formPanel.add(cbPersonMapping);
        formPanel.add(new JLabel("")); // Dummy cho chẵn grid
        formPanel.add(new JLabel(""));

        return formPanel;
    }

    private void updatePersonMappingCombo(UserRole role) {
        cbPersonMapping.removeAllItems();
        cbPersonMapping.addItem(new PersonItem(null, null, null, "[ Không liên kết ]"));
        
        try {
            if (role == UserRole.Teacher) {
                List<Teacher> teachers = teacherService.getAllTeachers();
                if (teachers != null) {
                    teachers.forEach(t -> cbPersonMapping.addItem(new PersonItem(t, null, null, t.getFullName() + " (ID:" + t.getTeacherId() + ")")));
                }
            } else if (role == UserRole.Student) {
                List<Student> students = studentService.getAllStudents();
                if (students != null) {
                    students.forEach(s -> cbPersonMapping.addItem(new PersonItem(null, s, null, s.getFullName() + " (ID:" + s.getStudentId() + ")")));
                }
            } else if (role == UserRole.Staff) {
                List<Staff> staffs = staffService.getAllStaffs();
                if (staffs != null) {
                    staffs.forEach(s -> cbPersonMapping.addItem(new PersonItem(null, null, s, s.getFullName() + " (ID:" + s.getStaffId() + ")")));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách liên kết: " + ex.getMessage());
        }
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Tên đăng nhập", "Quyền", "Trạng thái", "Liên kết Person"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int selectedRow = table.getSelectedRow();
                Long userId = (Long) tableModel.getValueAt(selectedRow, 0);

                // Get full object
                try {
                    UserAccount acc = accountService.getAllAccounts().stream()
                            .filter(a -> a.getUserId().equals(userId))
                            .findFirst()
                            .orElse(null);

                    if (acc != null) {
                        txtUsername.setText(acc.getUsername());
                        // KHÔNG load password vào textfield (bảo mật/hash)
                        txtPassword.setText("");
                        
                        cbRole.setSelectedItem(acc.getRole());
                        cbIsActive.setSelectedItem(acc.getIsActive());
                        
                        // Cập nhật người liên kết
                        updatePersonMappingCombo(acc.getRole());
                        setPersonMappingCombo(acc);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(tablePanel, "Lỗi khi lấy thông tin chi tiết tài khoản.");
                }
            }
        });

        return tablePanel;
    }
    
    private void setPersonMappingCombo(UserAccount acc) {
        Long targetId = null;
        if (acc.getRole() == UserRole.Teacher && acc.getTeacher() != null) targetId = acc.getTeacher().getTeacherId();
        else if (acc.getRole() == UserRole.Student && acc.getStudent() != null) targetId = acc.getStudent().getStudentId();
        else if (acc.getRole() == UserRole.Staff && acc.getStaff() != null) targetId = acc.getStaff().getStaffId();
        
        if (targetId == null) {
            cbPersonMapping.setSelectedIndex(0);
            return;
        }

        for (int i = 0; i < cbPersonMapping.getItemCount(); i++) {
            PersonItem item = cbPersonMapping.getItemAt(i);
            if (acc.getRole() == UserRole.Teacher && item.teacher != null && item.teacher.getTeacherId().equals(targetId)) {
                cbPersonMapping.setSelectedIndex(i); return;
            }
            if (acc.getRole() == UserRole.Student && item.student != null && item.student.getStudentId().equals(targetId)) {
                cbPersonMapping.setSelectedIndex(i); return;
            }
            if (acc.getRole() == UserRole.Staff && item.staff != null && item.staff.getStaffId().equals(targetId)) {
                cbPersonMapping.setSelectedIndex(i); return;
            }
        }
    }

    private JPanel createButtonPanel() {
        JPanel actionPanel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(15);
        btnSearch = new JButton("Tìm kiếm");
        searchPanel.add(new JLabel("Tìm username/role:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdd = new JButton("Thêm mới");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        actionPanel.add(searchPanel, BorderLayout.WEST);
        actionPanel.add(buttonPanel, BorderLayout.EAST);

        setupButtonListeners();

        return actionPanel;
    }

    private void loadDataToTable(List<UserAccount> accounts) {
        tableModel.setRowCount(0);
        if (accounts != null) {
            for (UserAccount acc : accounts) {
                String personInfo = "N/A";
                if (acc.getRole() == UserRole.Teacher && acc.getTeacher() != null) personInfo = acc.getTeacher().getFullName();
                else if (acc.getRole() == UserRole.Student && acc.getStudent() != null) personInfo = acc.getStudent().getFullName();
                else if (acc.getRole() == UserRole.Staff && acc.getStaff() != null) personInfo = acc.getStaff().getFullName();
                else if (acc.getRole() == UserRole.Admin) personInfo = "System Admin";

                tableModel.addRow(new Object[]{
                        acc.getUserId(),
                        acc.getUsername(),
                        acc.getRole().name(),
                        acc.getIsActive() ? "Hoạt động" : "Bị khóa",
                        personInfo
                });
            }
        }
    }

    private void setupButtonListeners() {
        btnAdd.addActionListener(e -> {
            try {
                if (txtPassword.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Mật khẩu không được để trống khi tạo mới!");
                    return;
                }
                
                UserAccount account = buildAccountFromForm(null);
                accountService.addAccount(account);
                
                JOptionPane.showMessageDialog(this, "Thêm tài khoản thành công!");
                loadDataToTable(accountService.getAllAccounts());
                clearForm();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm: " + ex.getMessage());
            }
        });

        btnUpdate.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần cập nhật!");
                return;
            }
            try {
                Long userId = (Long) tableModel.getValueAt(selectedRow, 0);
                UserAccount account = buildAccountFromForm(userId);

                accountService.updateAccount(account);
                JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thành công!");
                loadDataToTable(accountService.getAllAccounts());
                clearForm();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage());
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần xóa!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa tài khoản này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Long userId = (Long) tableModel.getValueAt(selectedRow, 0);
                    accountService.deleteAccount(userId);
                    JOptionPane.showMessageDialog(this, "Xóa tài khoản thành công!");
                    loadDataToTable(accountService.getAllAccounts());
                    clearForm();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi xóa dữ liệu: " + ex.getMessage());
                }
            }
        });

        btnRefresh.addActionListener(e -> {
            clearForm();
            try {
                loadDataToTable(accountService.getAllAccounts());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText();
            try {
                List<UserAccount> result = accountService.searchByUsername(keyword);
                loadDataToTable(result);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + ex.getMessage());
            }
        });
    }

    private UserAccount buildAccountFromForm(Long userId) {
        PersonItem selectedPerson = (PersonItem) cbPersonMapping.getSelectedItem();
        Teacher teacher = selectedPerson != null ? selectedPerson.teacher : null;
        Student student = selectedPerson != null ? selectedPerson.student : null;
        Staff staff = selectedPerson != null ? selectedPerson.staff : null;

        UserAccount account = new UserAccount();
        account.setUserId(userId);
        account.setUsername(txtUsername.getText());
        account.setRole((UserRole) cbRole.getSelectedItem());
        account.setIsActive((Boolean) cbIsActive.getSelectedItem());
        account.setTeacher(teacher);
        account.setStudent(student);
        account.setStaff(staff);
                
        if (!txtPassword.getText().isEmpty()) {
            account.setPasswordHash(txtPassword.getText());
        }

        return account;
    }

    private void clearForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        cbRole.setSelectedIndex(0);
        cbIsActive.setSelectedIndex(0);
        cbPersonMapping.setSelectedIndex(0);
        txtSearch.setText("");
        table.clearSelection();
    }
    
    // Wrapper class for JComboBox to hold Teacher, Student, or Staff
    private static class PersonItem {
        Teacher teacher;
        Student student;
        Staff staff;
        String display;

        public PersonItem(Teacher t, Student s, Staff st, String display) {
            this.teacher = t;
            this.student = s;
            this.staff = st;
            this.display = display;
        }

        @Override
        public String toString() {
            return display;
        }
    }
}
