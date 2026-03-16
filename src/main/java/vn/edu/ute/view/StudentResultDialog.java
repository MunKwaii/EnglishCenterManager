package vn.edu.ute.view;

import vn.edu.ute.model.AcademicClass;
import vn.edu.ute.model.Result;
import vn.edu.ute.service.ResultService;
import vn.edu.ute.service.impl.ResultServiceImpl;
import vn.edu.ute.util.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class StudentResultDialog extends JDialog {

    private final AcademicClass currentClass;
    private final ResultService resultService = new ResultServiceImpl();

    public StudentResultDialog(Window owner, AcademicClass currentClass) {
        super(owner, "Bảng điểm lớp: " + currentClass.getClassName(), ModalityType.APPLICATION_MODAL);
        this.currentClass = currentClass;

        setLayout(new BorderLayout(10, 10));
        setSize(500, 300);
        setLocationRelativeTo(owner);
        initComponents();
    }

    private void initComponents() {
        JPanel pnlContent = new JPanel(new GridLayout(4, 2, 10, 10));
        pnlContent.setBorder(new EmptyBorder(20, 20, 20, 20));

        pnlContent.add(new JLabel("Môn học/Khóa học:"));
        pnlContent.add(new JLabel("<html><b>" + (currentClass.getCourse() != null ? currentClass.getCourse().getCourseName() : "N/A") + "</b></html>"));

        pnlContent.add(new JLabel("Lớp:"));
        pnlContent.add(new JLabel("<html><b>" + currentClass.getClassName() + "</b></html>"));
        
        Long studentId = UserSession.getStudentId();
        List<Result> studentResults = resultService.getResultsByStudentId(studentId);
        
        // Lọc kết quả của lớp hiện tại
        Result currentResult = studentResults.stream()
                .filter(r -> r.getAcademicClass().getClassId().equals(currentClass.getClassId()))
                .findFirst().orElse(null);

        String scoreStr = "Chưa có điểm";
        String gradeStr = "N/A";
        String commentStr = "Không có nhận xét";

        if (currentResult != null) {
            if (currentResult.getScore() != null) scoreStr = currentResult.getScore().toString();
            if (currentResult.getGrade() != null && !currentResult.getGrade().isEmpty()) gradeStr = currentResult.getGrade();
            if (currentResult.getComment() != null && !currentResult.getComment().isEmpty()) commentStr = currentResult.getComment();
        }

        pnlContent.add(new JLabel("Điểm số:"));
        JLabel lblScore = new JLabel("<html><b><font color='blue'>" + scoreStr + "</font></b></html>");
        pnlContent.add(lblScore);

        pnlContent.add(new JLabel("Đánh giá/Xếp loại:"));
        pnlContent.add(new JLabel("<html><b>" + gradeStr + "</b></html>"));

        add(pnlContent, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlBottom.add(new JLabel("Nhận xét của GV: " + commentStr));
        pnlBottom.setBorder(new EmptyBorder(0, 10, 20, 10));
        add(pnlBottom, BorderLayout.SOUTH);
    }
}
