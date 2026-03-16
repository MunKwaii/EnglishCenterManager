package vn.edu.ute.view;

import vn.edu.ute.model.Student;
import vn.edu.ute.model.enums.Gender;
import vn.edu.ute.model.enums.Status;
import vn.edu.ute.service.StudentService;
import vn.edu.ute.service.impl.StudentServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class StudentPanel extends JPanel {

    private final StudentService studentService = new StudentServiceImpl();

    // UI Components
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtFullName, txtPhone, txtEmail, txtAddress, txtSearch;
    private JSpinner spDob;
    private JComboBox<Gender> cbGender;
    private JComboBox<Status> cbStatus;
    private JComboBox<String> cbFilterGender, cbSearchType;
    private JButton btnAdd, btnUpdate, btnDelete, btnSearch, btnRefresh, btnSort;
    private JLabel lblStats;

    public StudentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Chia 3 phần đúng phong cách ClassPanel
        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadDataToTable(studentService.getAllStudents());
        updateStats();
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin học viên"));

        txtId = new JTextField(); txtId.setEditable(false);
        txtFullName = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        txtAddress = new JTextField();

        // Xử lý Ngày sinh bằng JSpinner
        spDob = new JSpinner(new SpinnerDateModel());
        spDob.setEditor(new JSpinner.DateEditor(spDob, "dd/MM/yyyy"));

        cbGender = new JComboBox<>(Gender.values());
        cbStatus = new JComboBox<>(Status.values());

        lblStats = new JLabel("Tổng học viên đang hoạt động: 0");
        lblStats.setFont(new Font("Arial", Font.BOLD, 12));

        formPanel.add(new JLabel("ID:")); formPanel.add(txtId);
        formPanel.add(new JLabel("Họ tên:")); formPanel.add(txtFullName);
        formPanel.add(new JLabel("Ngày sinh:")); formPanel.add(spDob);
        formPanel.add(new JLabel("Giới tính:")); formPanel.add(cbGender);
        formPanel.add(new JLabel("Số điện thoại:")); formPanel.add(txtPhone);
        formPanel.add(new JLabel("Email:")); formPanel.add(txtEmail);
        formPanel.add(new JLabel("Địa chỉ:")); formPanel.add(txtAddress);
        formPanel.add(new JLabel("Trạng thái:")); formPanel.add(cbStatus);

        return formPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Họ Tên", "Ngày Sinh", "Giới Tính", "SĐT", "Email", "Trạng Thái"};

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

                studentService.getAllStudents().stream()
                        .filter(s -> s.getStudentId().equals(id))
                        .findFirst()
                        .ifPresent(s -> {
                            txtId.setText(String.valueOf(s.getStudentId()));
                            txtFullName.setText(s.getFullName());
                            txtPhone.setText(s.getPhone());
                            txtEmail.setText(s.getEmail());
                            txtAddress.setText(s.getAddress());
                            cbGender.setSelectedItem(s.getGender());
                            cbStatus.setSelectedItem(s.getStatus());
                            if (s.getDateOfBirth() != null) {
                                spDob.setValue(Date.from(s.getDateOfBirth().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                            }
                        });
            }
        });
        return tablePanel;
    }

    private JPanel createButtonPanel() {
        JPanel actionPanel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(12);
        btnSearch = new JButton("Tìm kiếm");
        cbSearchType = new JComboBox<>(new String[]{"Tên học viên", "Số điện thoại"});

        cbFilterGender = new JComboBox<>(new String[]{"Tất cả giới tính", "Nam", "Nữ"});

        searchPanel.add(new JLabel("Lọc giới tính:"));
        searchPanel.add(cbFilterGender);
        searchPanel.add(new JLabel("Tìm theo:"));
        searchPanel.add(cbSearchType);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdd = new JButton("Thêm mới");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa");
        btnSort = new JButton("Sắp xếp A-Z");
        btnRefresh = new JButton("Làm mới");

        buttonPanel.add(btnAdd); buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete); buttonPanel.add(btnSort); buttonPanel.add(btnRefresh);

        actionPanel.add(searchPanel, BorderLayout.WEST);
        actionPanel.add(buttonPanel, BorderLayout.EAST);

        setupListeners();
        return actionPanel;
    }

    private void setupListeners() {
        btnAdd.addActionListener(e -> handleAddUpdate(null));

        btnUpdate.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Chọn một học viên để sửa!");
                return;
            }
            handleAddUpdate(Long.parseLong(txtId.getText()));
        });

        btnDelete.addActionListener(e -> {
            if (txtId.getText().isEmpty()) return;
            int confirm = JOptionPane.showConfirmDialog(this, "Xóa học viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    studentService.deleteStudent(Long.parseLong(txtId.getText()));
                    refreshUI();
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi khi xóa!"); }
            }
        });

        // Filter theo giới tính
        cbFilterGender.addActionListener(e -> {
            txtSearch.setText("");
            loadDataToTable(fetchFilteredStudents());
            updateStats();
        });

        // Tìm kiếm theo loại (tên hoặc SĐT)
        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nhập thông tin tìm kiếm!");
                return;
            }

            String searchType = (String) cbSearchType.getSelectedItem();
            List<Student> results;

            if ("Tên học viên".equals(searchType)) {
                results = studentService.getAllStudents().stream()
                        .filter(s -> s.getFullName().toLowerCase().contains(keyword.toLowerCase()))
                        .toList();
            } else { // Số điện thoại
                Student found = studentService.findByPhone(keyword);
                results = found != null ? List.of(found) : List.of();
            }

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả!");
            }
            loadDataToTable(results);
        });

        // Sắp xếp từ A-Z
        btnSort.addActionListener(e -> {
            List<Student> sorted = studentService.getSortedStudentsByName();
            loadDataToTable(sorted);
            JOptionPane.showMessageDialog(this, "Đã sắp xếp từ A-Z");
        });

        btnRefresh.addActionListener(e -> refreshUI());
    }

    private List<Student> fetchFilteredStudents() {
        String filterType = (String) cbFilterGender.getSelectedItem();

        if ("Nam".equals(filterType)) {
            return studentService.filterByGender(Gender.Male);
        } else if ("Nữ".equals(filterType)) {
            return studentService.filterByGender(Gender.Female);
        } else {
            return studentService.getAllStudents();
        }
    }

    private void handleAddUpdate(Long id) {
        try {
            Date dobDate = (Date) spDob.getValue();
            Student s = Student.builder()
                    .studentId(id)
                    .fullName(txtFullName.getText())
                    .phone(txtPhone.getText())
                    .email(txtEmail.getText())
                    .address(txtAddress.getText())
                    .gender((Gender) cbGender.getSelectedItem())
                    .status((Status) cbStatus.getSelectedItem())
                    .dateOfBirth(dobDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                    .registrationDate(LocalDate.now())
                    .build();

            if (id == null) {
                studentService.addStudent(s);
            } else {
                studentService.updateStudent(s);
            }
            JOptionPane.showMessageDialog(this, "Thành công!");
            refreshUI();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void loadDataToTable(List<Student> list) {
        tableModel.setRowCount(0);
        for (Student s : list) {
            tableModel.addRow(new Object[]{
                    s.getStudentId(), s.getFullName(), s.getDateOfBirth(),
                    s.getGender(), s.getPhone(), s.getEmail(), s.getStatus()
            });
        }
    }

    private void refreshUI() {
        txtId.setText(""); txtFullName.setText(""); txtPhone.setText("");
        txtEmail.setText(""); txtAddress.setText(""); txtSearch.setText("");
        spDob.setValue(new Date());
        cbFilterGender.setSelectedIndex(0);
        loadDataToTable(studentService.getAllStudents());
        updateStats();
    }

    private void updateStats() {
        long activeCount = studentService.countActiveStudents();
        long totalCount = studentService.getAllStudents().size();
        lblStats.setText(String.format("Tổng học viên: %d | Đang hoạt động: %d", totalCount, activeCount));
    }
}