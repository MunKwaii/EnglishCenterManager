package vn.edu.ute.view;

import vn.edu.ute.model.Course;
import vn.edu.ute.model.enums.CourseLevel;
import vn.edu.ute.model.enums.DurationUnit;
import vn.edu.ute.model.enums.Status;
import vn.edu.ute.service.CourseService;
import vn.edu.ute.service.impl.CourseServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class CoursePanel extends JPanel {

    // 1. Khai báo Service để gọi backend
    private final CourseService courseService = new CourseServiceImpl();

    // 2. Khai báo các thành phần giao diện (UI Components)
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtCourseName, txtFee, txtSearch;
    private JComboBox<CourseLevel> cbLevel;
    private JComboBox<DurationUnit> cbDurationUnit;
    private JComboBox<Status> cbStatus;

    private JButton btnAdd, btnUpdate, btnDelete, btnSearch, btnRefresh;

    public CoursePanel() {
        // Thiết lập layout chính cho Panel là BorderLayout
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Khởi tạo và gắn các khu vực vào Panel
        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        // Tải dữ liệu từ DB lên bảng ngay khi mở giao diện
        loadDataToTable(courseService.getAllCourses());
    }

    // --- KHU VỰC 1: FORM NHẬP LIỆU ---
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Khóa học"));

        txtCourseName = new JTextField();
        txtFee = new JTextField();
        cbLevel = new JComboBox<>(CourseLevel.values());
        cbDurationUnit = new JComboBox<>(DurationUnit.values());
        cbStatus = new JComboBox<>(Status.values());

        formPanel.add(new JLabel("Tên khóa học:"));
        formPanel.add(txtCourseName);
        formPanel.add(new JLabel("Cấp độ:"));
        formPanel.add(cbLevel);

        formPanel.add(new JLabel("Học phí (VNĐ):"));
        formPanel.add(txtFee);
        formPanel.add(new JLabel("Đơn vị thời gian:"));
        formPanel.add(cbDurationUnit);

        formPanel.add(new JLabel("Trạng thái:"));
        formPanel.add(cbStatus);

        return formPanel;
    }

    // --- KHU VỰC 2: BẢNG HIỂN THỊ (JTABLE) ---
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());

        // Khởi tạo Model cho bảng (định nghĩa các cột)
        String[] columns = { "ID", "Tên Khóa Học", "Cấp độ", "Học phí", "Đơn vị", "Trạng thái" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép sửa trực tiếp trên ô JTable
            }
        };

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Bắt sự kiện click chuột vào bảng để đưa dữ liệu ngược lên Form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int selectedRow = table.getSelectedRow();
                txtCourseName.setText(tableModel.getValueAt(selectedRow, 1).toString());
                cbLevel.setSelectedItem(CourseLevel.valueOf(tableModel.getValueAt(selectedRow, 2).toString()));
                txtFee.setText(tableModel.getValueAt(selectedRow, 3).toString());
                cbDurationUnit.setSelectedItem(DurationUnit.valueOf(tableModel.getValueAt(selectedRow, 4).toString()));
                cbStatus.setSelectedItem(Status.valueOf(tableModel.getValueAt(selectedRow, 5).toString()));
            }
        });

        return tablePanel;
    }

    // --- KHU VỰC 3: CÁC NÚT BẤM (BUTTONS) & XỬ LÝ SỰ KIỆN ---
    private JPanel createButtonPanel() {
        JPanel actionPanel = new JPanel(new BorderLayout());

        // Khung tìm kiếm bên trái
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(15);
        btnSearch = new JButton("Tìm kiếm bằng Lambda");
        searchPanel.add(new JLabel("Tìm tên:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        // Khung chức năng bên phải
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

        // Gắn sự kiện cho các nút
        setupButtonListeners();

        return actionPanel;
    }

    // --- CÁC HÀM XỬ LÝ LOGIC ---

    private void loadDataToTable(List<Course> courses) {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        if (courses != null) {
            for (Course c : courses) {
                tableModel.addRow(new Object[] {
                        c.getCourseId(),
                        c.getCourseName(),
                        c.getLevel().name(),
                        c.getFee(),
                        c.getDurationUnit().name(),
                        c.getStatus().name()
                });
            }
        }
    }

    private void setupButtonListeners() {
        // Sự kiện Thêm mới
        btnAdd.addActionListener(e -> {
            try {
                Course course = Course.builder()
                        .courseName(txtCourseName.getText())
                        .level((CourseLevel) cbLevel.getSelectedItem())
                        .fee(new BigDecimal(txtFee.getText()))
                        .durationUnit((DurationUnit) cbDurationUnit.getSelectedItem())
                        .status((Status) cbStatus.getSelectedItem())
                        .build();

                courseService.addCourse(course);
                JOptionPane.showMessageDialog(this, "Thêm khóa học thành công!");
                loadDataToTable(courseService.getAllCourses());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi nhập liệu. Vui lòng kiểm tra lại học phí!");
            }
        });

        // Sự kiện Tìm kiếm (Sử dụng hàm Lambda ở Service)
        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText();
            List<Course> result = courseService.searchCourseByName(keyword);
            loadDataToTable(result);
        });

        // Sự kiện Làm mới
        btnRefresh.addActionListener(e -> {
            txtCourseName.setText("");
            txtFee.setText("");
            txtSearch.setText("");
            loadDataToTable(courseService.getAllCourses());
        });

        // Sự kiện Cập nhật
        btnUpdate.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn khóa học cần cập nhật bảng (hoặc chọn dòng trên bảng)!");
                return;
            }
            try {
                Long courseId = Long.parseLong(tableModel.getValueAt(selectedRow, 0).toString());
                Course course = Course.builder()
                        .courseId(courseId)
                        .courseName(txtCourseName.getText())
                        .level((CourseLevel) cbLevel.getSelectedItem())
                        .fee(new BigDecimal(txtFee.getText()))
                        .durationUnit((DurationUnit) cbDurationUnit.getSelectedItem())
                        .status((Status) cbStatus.getSelectedItem())
                        .build();

                courseService.updateCourse(course);
                JOptionPane.showMessageDialog(this, "Cập nhật khóa học thành công!");
                loadDataToTable(courseService.getAllCourses());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật. Vui lòng kiểm tra lại Dữ liệu học phí!");
            }
        });

        // Sự kiện Xóa
        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khóa học cần xóa (chọn dòng trên bảng)!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa khóa học này?", "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Long courseId = Long.parseLong(tableModel.getValueAt(selectedRow, 0).toString());
                    courseService.removeCourse(courseId);
                    JOptionPane.showMessageDialog(this, "Xóa khóa học thành công!");
                    loadDataToTable(courseService.getAllCourses());

                    // Xóa trắng form sau khi xóa
                    txtCourseName.setText("");
                    txtFee.setText("");
                    txtSearch.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi xóa dữ liệu!");
                }
            }
        });
    }
}