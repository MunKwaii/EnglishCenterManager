package vn.edu.ute.view;

import vn.edu.ute.model.AcademicClass;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.Result;
import vn.edu.ute.model.Student;
import vn.edu.ute.service.AcademicClassService;
import vn.edu.ute.service.AcademicOperationService;
import vn.edu.ute.service.EnrollmentService;
import vn.edu.ute.service.ResultService;
import vn.edu.ute.service.impl.AcademicClassServiceImpl;
import vn.edu.ute.service.impl.AcademicOperationServiceImpl;
import vn.edu.ute.service.impl.EnrollmentServiceImpl;
import vn.edu.ute.service.impl.ResultServiceImpl;
import vn.edu.ute.util.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ResultPanel extends JPanel {
    private final ResultService resultService = new ResultServiceImpl();
    private final AcademicOperationService academicOperationService = new AcademicOperationServiceImpl();
    private final AcademicClassService classService = new AcademicClassServiceImpl();
    private final EnrollmentService enrollmentService = new EnrollmentServiceImpl();

    private JComboBox<AcademicClass> cbClasses;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Result> currentResults = new ArrayList<>();
    private List<Enrollment> currentEnrollments = new ArrayList<>();

    public ResultPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- TOP PANEL ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Nhập điểm & Đánh giá"));
        
        cbClasses = new JComboBox<>();
        cbClasses.setRenderer(new DefaultListCellRenderer() {
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

        loadClasses();
        
        JButton btnLoad = new JButton("Tải danh sách học viên");
        btnLoad.setBackground(new Color(52, 152, 219));
        btnLoad.setForeground(Color.BLACK);
        btnLoad.setFocusPainted(false);
        
        topPanel.add(new JLabel("Chọn Lớp học:"));
        topPanel.add(cbClasses);
        topPanel.add(btnLoad);

        // --- CENTER TABLE ---
        String[] columns = {"ID SV", "Tên Sinh Viên", "Điểm số", "Xếp loại / Đánh giá", "Nhận xét của GV"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override 
            public boolean isCellEditable(int r, int c) { 
                return c >= 2; // Chỉ cho phép sửa cột Điểm, Xếp loại, Nhận xét
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Long.class;
                return String.class;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        
        // --- BOTTOM PANEL ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu Bảng Điểm");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.BLACK);
        btnSave.setFont(new Font("Arial", Font.BOLD, 14));
        btnSave.setFocusPainted(false);
        bottomPanel.add(btnSave);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- SỰ KIỆN TẢI DANH SÁCH ---
        btnLoad.addActionListener(e -> loadStudentResults());

        // --- SỰ KIỆN LƯU BẢNG ĐIỂM ---
        btnSave.addActionListener(e -> saveResults());
    }

    private void loadClasses() {
        List<AcademicClass> classes = classService.getAllClasses();
        Long currentTeacherId = UserSession.getTeacherId();
        
        if (classes != null) {
            for (AcademicClass c : classes) {
                if (currentTeacherId != null) {
                    // Nếu là giáo viên, chỉ hiện lớp mình dạy
                    if (c.getTeacher() != null && c.getTeacher().getTeacherId().equals(currentTeacherId)) {
                        cbClasses.addItem(c);
                    }
                } else {
                    // Admin/Staff hiện tất cả
                    cbClasses.addItem(c);
                }
            }
        }
    }

    private void loadStudentResults() {
        AcademicClass cls = (AcademicClass) cbClasses.getSelectedItem();
        if (cls == null) return;
        
        tableModel.setRowCount(0);
        
        try {
            // Lấy danh sách SV đang học lớp này
            currentEnrollments = enrollmentService.getEnrollmentsByClassId(cls.getClassId());
            // Lấy điểm hiện tại của lớp
            currentResults = resultService.getResultsByClassId(cls.getClassId());
            
            if (currentEnrollments == null || currentEnrollments.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lớp này chưa có Học viên nào!");
                return;
            }

            for (Enrollment e : currentEnrollments) {
                Student s = e.getStudent();
                
                // Tìm xem sv này đã có điểm chưa
                Result existingResult = null;
                if (currentResults != null) {
                    existingResult = currentResults.stream()
                        .filter(r -> r.getStudent().getStudentId().equals(s.getStudentId()))
                        .findFirst().orElse(null);
                }
                
                String score = "";
                String grade = "";
                String comment = "";
                
                if (existingResult != null) {
                    if (existingResult.getScore() != null) score = existingResult.getScore().toString();
                    if (existingResult.getGrade() != null) grade = existingResult.getGrade();
                    if (existingResult.getComment() != null) comment = existingResult.getComment();
                }
                
                tableModel.addRow(new Object[]{
                    s.getStudentId(), 
                    s.getFullName(), 
                    score, 
                    grade, 
                    comment
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + ex.getMessage());
        }
    }

    private void saveResults() {
        AcademicClass cls = (AcademicClass) cbClasses.getSelectedItem();
        if (cls == null || tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để lưu!");
            return;
        }

        List<Result> resultsToSave = new ArrayList<>();
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Long studentId = (Long) tableModel.getValueAt(i, 0);
            String scoreStr = (String) tableModel.getValueAt(i, 2);
            String grade = (String) tableModel.getValueAt(i, 3);
            String comment = (String) tableModel.getValueAt(i, 4);
            
            // Xóa khoảng trắng
            scoreStr = (scoreStr != null) ? scoreStr.trim() : "";
            grade = (grade != null) ? grade.trim() : "";
            comment = (comment != null) ? comment.trim() : "";
            
            // Validate điểm
            BigDecimal score = null;
            if (!scoreStr.isEmpty()) {
                try {
                    score = new BigDecimal(scoreStr);
                    if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(new BigDecimal("10.0")) > 0) {
                        JOptionPane.showMessageDialog(this, "Điểm của sinh viên ID " + studentId + " không hợp lệ (Phải từ 0-10)");
                        return; // Dừng lại không lưu gì cả
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Điểm của sinh viên ID " + studentId + " không đúng định dạng số!");
                    return;
                }
            }
            
            Result r = new Result();
            r.setAcademicClass(cls);
            
            Student s = new Student();
            s.setStudentId(studentId);
            r.setStudent(s);
            
            r.setScore(score);
            r.setGrade(grade);
            r.setComment(comment);

            // Gắn Id nếu đã tồn tại để thực hiện Update thay vì Insert mới
            if (currentResults != null) {
                final Long sId = studentId;
                Result exist = currentResults.stream().filter(cr -> cr.getStudent().getStudentId().equals(sId)).findFirst().orElse(null);
                if (exist != null) {
                    r.setResultId(exist.getResultId());
                }
            }
            
            resultsToSave.add(r);
        }
        
        try {
            // Sử dụng AcademicOperationService để tự động cập nhật cả Trạng thái Ghi danh (Pass/Fail)
            academicOperationService.processClassResults(resultsToSave);
            JOptionPane.showMessageDialog(this, "Lưu bảng điểm và cập nhật trạng thái môn học thành công!");
            loadStudentResults(); // Reload để nhận ID mới
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu điểm: " + ex.getMessage());
        }
    }
}
