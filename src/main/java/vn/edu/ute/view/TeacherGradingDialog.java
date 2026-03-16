package vn.edu.ute.view;

import vn.edu.ute.model.AcademicClass;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.Result;
import vn.edu.ute.service.EnrollmentService;
import vn.edu.ute.service.ResultService;
import vn.edu.ute.service.impl.EnrollmentServiceImpl;
import vn.edu.ute.service.impl.ResultServiceImpl;
import vn.edu.ute.util.PermissionUtils;
import vn.edu.ute.util.UserSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TeacherGradingDialog extends JDialog {

    private final AcademicClass currentClass;
    private final ResultService resultService = new ResultServiceImpl();
    private final EnrollmentService enrollmentService = new EnrollmentServiceImpl();

    private JTable tblResults;
    private DefaultTableModel tableModel;
    private JButton btnSave;
    private List<Enrollment> enrollments;

    public TeacherGradingDialog(Window owner, AcademicClass currentClass) {
        super(owner, "Nhập điểm lớp: " + currentClass.getClassName(), ModalityType.APPLICATION_MODAL);
        this.currentClass = currentClass;

        setLayout(new BorderLayout(10, 10));
        setSize(800, 500);
        setLocationRelativeTo(owner);
        initComponents();
        loadResults();

        // Kiểm tra phân quyền (Phase 4)
        boolean canGrade = PermissionUtils.canEditScore(UserSession.getCurrentUser()) && PermissionUtils.canTakeAttendance(UserSession.getCurrentUser(), currentClass);
        if (!canGrade) {
            btnSave.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Bạn không có quyền sửa điểm lớp này (Chỉ xem).", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void initComponents() {
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Lưu điểm số");
        pnlTop.add(btnSave);
        add(pnlTop, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Mã HV", "Tên Học Viên", "Điểm số", "Xếp loại", "Nhận xét"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Chỉ cho sửa cột Điểm (2), Loại (3), Nhận xét (4) nếu có quyền
                boolean canGrade = PermissionUtils.canEditScore(UserSession.getCurrentUser()) && PermissionUtils.canTakeAttendance(UserSession.getCurrentUser(), currentClass);
                if (!canGrade) return false;
                return column == 2 || column == 3 || column == 4;
            }
        };

        tblResults = new JTable(tableModel);
        tblResults.setRowHeight(30);
        add(new JScrollPane(tblResults), BorderLayout.CENTER);

        btnSave.addActionListener(e -> saveResults());
    }

    private void loadResults() {
        try {
            enrollments = enrollmentService.getEnrollmentsByClassId(currentClass.getClassId());
            if (enrollments.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lớp này hiện chưa có học viên nào.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            List<Result> existingResults = resultService.getResultsByClassId(currentClass.getClassId());
            tableModel.setRowCount(0);

            for (Enrollment enr : enrollments) {
                Result res = existingResults.stream()
                        .filter(r -> r.getStudent().getStudentId().equals(enr.getStudent().getStudentId()))
                        .findFirst().orElse(null);

                BigDecimal score = res != null ? res.getScore() : null;
                String grade = res != null ? res.getGrade() : "";
                String comment = res != null ? res.getComment() : "";

                tableModel.addRow(new Object[]{
                        enr.getStudent().getStudentId(),
                        enr.getStudent().getFullName(),
                        score,
                        grade,
                        comment
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải bảng điểm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveResults() {
        if (tblResults.isEditing()) {
            tblResults.getCellEditor().stopCellEditing();
        }

        List<Result> resultsToSave = new ArrayList<>();
        List<Result> existingResults = resultService.getResultsByClassId(currentClass.getClassId());

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Long studentId = (Long) tableModel.getValueAt(i, 0);
            
            Object scoreObj = tableModel.getValueAt(i, 2);
            BigDecimal score = null;
            try {
                if (scoreObj != null && !scoreObj.toString().trim().isEmpty()) {
                    score = new BigDecimal(scoreObj.toString().trim());
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Điểm số không hợp lệ ở dòng " + (i+1), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String grade = tableModel.getValueAt(i, 3) != null ? tableModel.getValueAt(i, 3).toString() : "";
            String comment = tableModel.getValueAt(i, 4) != null ? tableModel.getValueAt(i, 4).toString() : "";

            Result.ResultBuilder builder = Result.builder()
                    .academicClass(currentClass)
                    .score(score)
                    .grade(grade)
                    .comment(comment);

            Enrollment currentEnr = enrollments.stream().filter(e -> e.getStudent().getStudentId().equals(studentId)).findFirst().orElse(null);
            if(currentEnr != null) {
                builder.student(currentEnr.getStudent());
            }

            Result existing = existingResults.stream().filter(r -> r.getStudent().getStudentId().equals(studentId)).findFirst().orElse(null);
            if (existing != null) {
                builder.resultId(existing.getResultId());
            }

            resultsToSave.add(builder.build());
        }

        boolean success = resultService.saveAllResults(resultsToSave);
        if (success) {
            JOptionPane.showMessageDialog(this, "Lưu điểm số thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadResults(); // Refresh
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu bảng điểm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
