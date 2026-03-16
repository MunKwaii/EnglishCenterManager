package vn.edu.ute.view;

import vn.edu.ute.model.AcademicClass;
import vn.edu.ute.model.Attendance;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.Result;
import vn.edu.ute.model.Student;
import vn.edu.ute.model.enums.AttendanceStatus;
import vn.edu.ute.service.AcademicClassService;
import vn.edu.ute.service.AcademicOperationService;
import vn.edu.ute.service.EnrollmentService;
import vn.edu.ute.service.impl.AcademicClassServiceImpl;
import vn.edu.ute.service.impl.AcademicOperationServiceImpl;
import vn.edu.ute.service.impl.EnrollmentServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AcademicOperationPanel extends JPanel {
    // Khai báo các Service kết nối Database
    private final AcademicOperationService operationService = new AcademicOperationServiceImpl();
    private final EnrollmentService enrollmentService = new EnrollmentServiceImpl();
    // THÊM MỚI: Service lấy danh sách lớp học
    private final AcademicClassService classService = new AcademicClassServiceImpl();

    private JTabbedPane tabbedPane;
    
    // THÊM MỚI: Khai báo ComboBox
    private JComboBox<AcademicClass> cbClassAttendance;
    private JComboBox<AcademicClass> cbClassResult;

    public AcademicOperationPanel() {
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        
        // Khởi tạo ComboBox và cấu hình hiển thị tên lớp
        cbClassAttendance = new JComboBox<>();
        cbClassResult = new JComboBox<>();
        setupClassComboBox(cbClassAttendance);
        setupClassComboBox(cbClassResult);
        loadClassesToComboBox();

        // Thêm 2 Tab chức năng
        tabbedPane.addTab("Điểm danh Lớp học (Attendance)", createAttendancePanel());
        tabbedPane.addTab("Kết quả Học tập (Result)", createResultPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // Hàm cấu hình hiển thị cho ComboBox
    private void setupClassComboBox(JComboBox<AcademicClass> cb) {
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof AcademicClass) {
                    AcademicClass c = (AcademicClass) value;
                    setText(c.getClassName() + " (" + c.getStatus() + ")");
                }
                return this;
            }
        });
    }

    // Hàm tải dữ liệu các lớp học vào ComboBox
    private void loadClassesToComboBox() {
        List<AcademicClass> classes = classService.getAllClasses();
        if (classes != null) {
            classes.forEach(c -> {
                cbClassAttendance.addItem(c);
                cbClassResult.addItem(c);
            });
        }
    }

    // --- TAB 1: ĐIỂM DANH ---
    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Thanh công cụ chọn lớp
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Chọn Lớp học:"));
        
        // THAY THẾ: Dùng ComboBox thay vì JTextField
        topPanel.add(cbClassAttendance);
        
        topPanel.add(Box.createHorizontalStrut(15)); // Tạo khoảng cách
        topPanel.add(new JLabel("Ngày (YYYY-MM-DD):"));
        JTextField txtDate = new JTextField(10);
        txtDate.setText(LocalDate.now().toString());
        topPanel.add(txtDate);

        JButton btnLoad = new JButton("Tải Danh sách Học viên");
        topPanel.add(btnLoad);
        panel.add(topPanel, BorderLayout.NORTH);

        // Bảng điểm danh
        String[] columns = {"ID Học viên", "Tên Học viên", "Trạng thái", "Ghi chú"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Chỉ cho phép sửa ở cột Trạng thái (2) và Ghi chú (3)
                return column == 2 || column == 3;
            }
        };
        JTable table = new JTable(model);
        
        JComboBox<AttendanceStatus> cbStatus = new JComboBox<>(AttendanceStatus.values());
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(cbStatus));

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Nút lưu
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu Điểm danh");
        bottomPanel.add(btnSave);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // --- SỰ KIỆN TẢI DANH SÁCH ---
        btnLoad.addActionListener(e -> {
            AcademicClass selectedClass = (AcademicClass) cbClassAttendance.getSelectedItem();
            if (selectedClass == null) {
                JOptionPane.showMessageDialog(panel, "Vui lòng chọn Lớp học!");
                return;
            }

            try {
                Long classId = selectedClass.getClassId();
                model.setRowCount(0);

                // Lấy danh sách ghi danh thật từ Database
                List<Enrollment> enrollments = enrollmentService.getEnrollmentsByClassId(classId);
                
                if (enrollments == null || enrollments.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Không tìm thấy học viên nào trong lớp học này!");
                    return;
                }

                for (Enrollment enrollment : enrollments) {
                    Student student = enrollment.getStudent();
                    model.addRow(new Object[]{
                        student.getStudentId(), 
                        student.getFullName(), 
                        AttendanceStatus.Present, 
                        ""
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Lỗi khi tải dữ liệu: " + ex.getMessage());
            }
        });

        // --- SỰ KIỆN LƯU ĐIỂM DANH ---
        btnSave.addActionListener(e -> {
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(panel, "Bảng danh sách trống!");
                return;
            }

            try {
                AcademicClass selectedClass = (AcademicClass) cbClassAttendance.getSelectedItem();
                LocalDate attendDate = LocalDate.parse(txtDate.getText().trim());

                List<Attendance> attendances = new ArrayList<>();
                
                for (int i = 0; i < model.getRowCount(); i++) {
                    Long studentId = (Long) model.getValueAt(i, 0);
                    AttendanceStatus status = (AttendanceStatus) model.getValueAt(i, 2);
                    String note = model.getValueAt(i, 3) != null ? model.getValueAt(i, 3).toString() : "";
                    
                    Student student = new Student();
                    student.setStudentId(studentId);

                    Attendance attendance = Attendance.builder()
                            .student(student)
                            .academicClass(selectedClass)
                            .attendDate(attendDate)
                            .status(status)
                            .note(note)
                            .build();
                            
                    attendances.add(attendance);
                }
                
                operationService.processClassAttendance(attendances);
                JOptionPane.showMessageDialog(this, "Đã lưu thông tin điểm danh hàng loạt thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        });

        return panel;
    }

    // --- TAB 2: KẾT QUẢ HỌC TẬP ---
    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Chọn Lớp học:"));
        
        // THAY THẾ: Dùng ComboBox thay vì JTextField
        topPanel.add(cbClassResult);
        
        JButton btnLoad = new JButton("Tải Danh sách Cấp điểm");
        topPanel.add(Box.createHorizontalStrut(15));
        topPanel.add(btnLoad);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID Học viên", "Tên Học viên", "Điểm số", "Xếp loại", "Nhận xét"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 2;
            }
        };
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu Kết quả Lớp");
        bottomPanel.add(btnSave);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // --- SỰ KIỆN TẢI DANH SÁCH ---
        btnLoad.addActionListener(e -> {
            AcademicClass selectedClass = (AcademicClass) cbClassResult.getSelectedItem();
            if (selectedClass == null) {
                JOptionPane.showMessageDialog(panel, "Vui lòng chọn Lớp học!");
                return;
            }

            try {
                Long classId = selectedClass.getClassId();
                model.setRowCount(0);

                List<Enrollment> enrollments = enrollmentService.getEnrollmentsByClassId(classId);
                
                if (enrollments == null || enrollments.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Không tìm thấy học viên nào trong lớp học này!");
                    return;
                }

                for (Enrollment enrollment : enrollments) {
                    Student student = enrollment.getStudent();
                    model.addRow(new Object[]{
                        student.getStudentId(), 
                        student.getFullName(), 
                        0.0, 
                        "", 
                        ""
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Lỗi khi tải dữ liệu: " + ex.getMessage());
            }
        });

        // --- SỰ KIỆN LƯU KẾT QUẢ ---
        btnSave.addActionListener(e -> {
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(panel, "Bảng danh sách trống!");
                return;
            }
            
            try {
                AcademicClass selectedClass = (AcademicClass) cbClassResult.getSelectedItem();
                List<Result> results = new ArrayList<>();
                
                for (int i = 0; i < model.getRowCount(); i++) {
                    Long studentId = (Long) model.getValueAt(i, 0);
                    
                    double scoreVal = 0.0;
                    try {
                        scoreVal = Double.parseDouble(model.getValueAt(i, 2).toString());
                    } catch (NumberFormatException ignored) {}
                    
                    BigDecimal score = BigDecimal.valueOf(scoreVal);
                    String grade = model.getValueAt(i, 3) != null ? model.getValueAt(i, 3).toString() : "";
                    String comment = model.getValueAt(i, 4) != null ? model.getValueAt(i, 4).toString() : "";
                    
                    Student student = new Student();
                    student.setStudentId(studentId);

                    Result result = Result.builder()
                            .student(student)
                            .academicClass(selectedClass)
                            .score(score)
                            .grade(grade)
                            .comment(comment)
                            .build();
                            
                    results.add(result);
                }
                
                operationService.processClassResults(results);
                JOptionPane.showMessageDialog(this, "Đã cập nhật bảng điểm thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi lưu bảng điểm: " + ex.getMessage());
            }
        });

        return panel;
    }
}