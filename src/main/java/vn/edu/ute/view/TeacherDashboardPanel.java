package vn.edu.ute.view;

import vn.edu.ute.model.AcademicClass;
import vn.edu.ute.service.AcademicClassService;
import vn.edu.ute.service.impl.AcademicClassServiceImpl;
import vn.edu.ute.util.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class TeacherDashboardPanel extends JPanel {

    private final AcademicClassService classService = new AcademicClassServiceImpl();
    private JPanel pnlClassCards;

    public TeacherDashboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Header: Chào mừng giáo viên
        add(createWelcomePanel(), BorderLayout.NORTH);

        // 2. Class List (Main Content)
        add(createClassListPanel(), BorderLayout.CENTER);
        
        loadTeacherClasses();
    }

    private JPanel createWelcomePanel() {
        JPanel pnlWelcome = new JPanel(new BorderLayout());
        pnlWelcome.setOpaque(false);
        
        JLabel lblWelcome = new JLabel("BẢNG ĐIỀU KHIỂN GIẢNG VIÊN");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblWelcome.setForeground(new Color(44, 62, 80));
        pnlWelcome.add(lblWelcome, BorderLayout.WEST);

        return pnlWelcome;
    }

    private JScrollPane createClassListPanel() {
        pnlClassCards = new JPanel(new GridLayout(0, 3, 20, 20)); // Xếp thẻ lớp học 3 cột
        pnlClassCards.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(pnlClassCards);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

    private void loadTeacherClasses() {
        pnlClassCards.removeAll();
        Long teacherId = UserSession.getTeacherId();

        if (teacherId == null) {
            JLabel lblError = new JLabel("Lỗi: Không tìm thấy thông tin Giáo viên trong phiên đăng nhập.");
            lblError.setForeground(Color.RED);
            pnlClassCards.add(lblError);
            return;
        }

        List<AcademicClass> myClasses = classService.getClassesByTeacher(teacherId);

        if (myClasses.isEmpty()) {
            JLabel lblEmpty = new JLabel("Hiện chưa có lớp học nào được phân công.");
            lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            pnlClassCards.add(lblEmpty);
        } else {
            for (AcademicClass cls : myClasses) {
                pnlClassCards.add(createSingleClassCard(cls));
            }
        }

        pnlClassCards.revalidate();
        pnlClassCards.repaint();
    }

    private JPanel createSingleClassCard(AcademicClass cls) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Title lớp học
        JLabel lblClassName = new JLabel(cls.getClassName());
        lblClassName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblClassName.setForeground(new Color(41, 128, 185));
        card.add(lblClassName, BorderLayout.NORTH);

        // Thông tin chi tiết nằm ở giữa
        JPanel pnlInfo = new JPanel(new GridLayout(4, 1, 2, 2));
        pnlInfo.setOpaque(false);
        pnlInfo.add(new JLabel("Khóa học: " + (cls.getCourse() != null ? cls.getCourse().getCourseName() : "N/A")));
        pnlInfo.add(new JLabel("Phòng: " + (cls.getRoom() != null ? cls.getRoom().getRoomName() : "N/A")));
        pnlInfo.add(new JLabel("Thời gian: " + cls.getStartDate() + " -> " + (cls.getEndDate() != null ? cls.getEndDate() : "")));
        
        JLabel lblStatus = new JLabel("Trạng thái: " + cls.getStatus());
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        pnlInfo.add(lblStatus);
        
        card.add(pnlInfo, BorderLayout.CENTER);

        // Các nút thao tác (Điểm danh / Nhập điểm) nằm ở dưới
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlActions.setOpaque(false);
        
        JButton btnAttendance = new JButton("Điểm danh");
        btnAttendance.setBackground(new Color(39, 174, 96));
        btnAttendance.setForeground(Color.BLACK);
        btnAttendance.setFocusPainted(false);
        
        JButton btnScore = new JButton("Nhập điểm");
        btnScore.setBackground(new Color(243, 156, 18));
        btnScore.setForeground(Color.BLACK);
        btnScore.setFocusPainted(false);

        // Mở dialog Điểm danh
        btnAttendance.addActionListener(e -> {
            TeacherAttendanceDialog dialog = new TeacherAttendanceDialog(SwingUtilities.getWindowAncestor(this), cls);
            dialog.setVisible(true);
        });
        
        // Mở dialog Nhập điểm
        btnScore.addActionListener(e -> {
            TeacherGradingDialog dialog = new TeacherGradingDialog(SwingUtilities.getWindowAncestor(this), cls);
            dialog.setVisible(true);
        });

        pnlActions.add(btnAttendance);
        pnlActions.add(btnScore);

        card.add(pnlActions, BorderLayout.SOUTH);

        return card;
    }
}
