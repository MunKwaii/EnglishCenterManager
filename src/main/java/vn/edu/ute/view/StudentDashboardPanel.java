package vn.edu.ute.view;

import vn.edu.ute.model.AcademicClass;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.service.EnrollmentService;
import vn.edu.ute.service.impl.EnrollmentServiceImpl;
import vn.edu.ute.util.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class StudentDashboardPanel extends JPanel {

    private final EnrollmentService enrollmentService = new EnrollmentServiceImpl();
    private JPanel pnlClassCards;

    public StudentDashboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Header: Lời chào và Thông tin nhanh
        add(createWelcomePanel(), BorderLayout.NORTH);

        // 2. Class List (Main Content)
        add(createClassListPanel(), BorderLayout.CENTER);
        
        loadStudentClasses();
    }

    private JPanel createWelcomePanel() {
        JPanel pnlWelcome = new JPanel(new BorderLayout());
        pnlWelcome.setOpaque(false);
        
        JLabel lblWelcome = new JLabel("BẢNG ĐIỀU KHIỂN HỌC VIÊN");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblWelcome.setForeground(new Color(44, 62, 80));
        pnlWelcome.add(lblWelcome, BorderLayout.WEST);

        // Nút xem tiến độ
        JButton btnProgress = new JButton("Xem Tiến độ học tập");
        btnProgress.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnProgress.setBackground(new Color(142, 68, 173));
        btnProgress.setForeground(Color.WHITE);
        btnProgress.setFocusPainted(false);
        btnProgress.addActionListener(e -> {
            StudentProgressDialog progressDialog = new StudentProgressDialog(SwingUtilities.getWindowAncestor(this));
            progressDialog.setVisible(true);
        });

        // Nút xem chi tiết học phí
        JButton btnFee = new JButton("Xem thông tin Học phí");
        btnFee.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnFee.setBackground(new Color(39, 174, 96));
        btnFee.setForeground(Color.WHITE);
        btnFee.setFocusPainted(false);
        btnFee.addActionListener(e -> {
            StudentFeeDialog feeDialog = new StudentFeeDialog(SwingUtilities.getWindowAncestor(this));
            feeDialog.setVisible(true);
        });

        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlRight.setOpaque(false);
        pnlRight.add(btnProgress);
        pnlRight.add(btnFee);

        pnlWelcome.add(pnlRight, BorderLayout.EAST);

        return pnlWelcome;
    }

    private JScrollPane createClassListPanel() {
        pnlClassCards = new JPanel(new GridLayout(0, 2, 20, 20)); // Xếp thẻ lớp học 2 cột cho Student để dễ nhìn giờ học
        pnlClassCards.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(pnlClassCards);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

    private void loadStudentClasses() {
        pnlClassCards.removeAll();
        Long studentId = UserSession.getStudentId();

        if (studentId == null) {
            JLabel lblError = new JLabel("Lỗi: Không tìm thấy thông tin Học viên trong phiên đăng nhập.");
            lblError.setForeground(Color.RED);
            pnlClassCards.add(lblError);
            return;
        }

        try {
            List<Enrollment> myEnrollments = enrollmentService.getEnrollmentsByStudentId(studentId);

            if (myEnrollments.isEmpty()) {
                JLabel lblEmpty = new JLabel("Bạn chưa đăng ký lớp học nào.");
                lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                pnlClassCards.add(lblEmpty);
            } else {
                for (Enrollment enr : myEnrollments) {
                    pnlClassCards.add(createSingleClassCard(enr));
                }
            }
        } catch (Exception ex) {
            JLabel lblError = new JLabel("Lỗi kết nối CSDL: " + ex.getMessage());
            lblError.setForeground(Color.RED);
            pnlClassCards.add(lblError);
        }

        pnlClassCards.revalidate();
        pnlClassCards.repaint();
    }

    private JPanel createSingleClassCard(Enrollment enr) {
        AcademicClass cls = enr.getAcademicClass();
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Title lớp học
        JLabel lblClassName = new JLabel(cls.getClassName());
        lblClassName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblClassName.setForeground(new Color(142, 68, 173)); // Màu tím cho Student nhìn khác Teacher
        card.add(lblClassName, BorderLayout.NORTH);

        // Thông tin chi tiết
        JPanel pnlInfo = new JPanel(new GridLayout(4, 1, 2, 2));
        pnlInfo.setOpaque(false);
        pnlInfo.add(new JLabel("Khóa học: " + (cls.getCourse() != null ? cls.getCourse().getCourseName() : "N/A")));
        pnlInfo.add(new JLabel("Giáo viên: " + (cls.getTeacher() != null ? cls.getTeacher().getFullName() : "N/A")));
        pnlInfo.add(new JLabel("Phòng học: " + (cls.getRoom() != null ? cls.getRoom().getRoomName() : "N/A")));
        pnlInfo.add(new JLabel("Thời gian: " + cls.getStartDate() + " -> " + (cls.getEndDate() != null ? cls.getEndDate() : "Đang cập nhật")));
        
        card.add(pnlInfo, BorderLayout.CENTER);

        // Các nút thao tác
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlActions.setOpaque(false);
        
        JLabel lblEnrollStatus = new JLabel("Trạng thái: " + enr.getStatus());
        lblEnrollStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        pnlActions.add(lblEnrollStatus);

        JButton btnViewScore = new JButton("Xem bảng điểm");
        btnViewScore.setBackground(new Color(52, 152, 219));
        btnViewScore.setForeground(Color.WHITE);
        btnViewScore.setFocusPainted(false);

        // Mở dialog Xem bảng điểm
        btnViewScore.addActionListener(e -> {
            StudentResultDialog dialog = new StudentResultDialog(SwingUtilities.getWindowAncestor(this), cls);
            dialog.setVisible(true);
        });

        pnlActions.add(btnViewScore);
        card.add(pnlActions, BorderLayout.SOUTH);

        return card;
    }
}
