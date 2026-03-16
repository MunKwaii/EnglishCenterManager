package vn.edu.ute.view;

import vn.edu.ute.model.Result;
import vn.edu.ute.service.ResultService;
import vn.edu.ute.service.impl.ResultServiceImpl;
import vn.edu.ute.util.UserSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentProgressDialog extends JDialog {
    private ResultService resultService = new ResultServiceImpl();
    private JTable progressTable;
    private DefaultTableModel tableModel;
    private Long currentStudentId;

    public StudentProgressDialog(Window owner) {
        super(owner, "Tiến độ học tập cá nhân", ModalityType.APPLICATION_MODAL);
        
        // Retrieve logged in student
        currentStudentId = UserSession.getStudentId();
        if (currentStudentId == null) {
            JOptionPane.showMessageDialog(owner, "Vui lòng đăng nhập với tư cách Học viên để xem tiến độ.");
            dispose();
            return;
        }

        initComponents();
        loadProgressData();
    }

    private void initComponents() {
        setSize(900, 500);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("Tiến độ các khóa học đã và đang học");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Table setup
        String[] columns = {"Khóa học", "Lớp", "Ngày BĐ", "Ngày KT", "Điểm", "Xếp loại"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };

        progressTable = new JTable(tableModel);
        progressTable.setRowHeight(30);
        progressTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        progressTable.setFont(new Font("Arial", Font.PLAIN, 14));

        // Center align specific columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        progressTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        progressTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        progressTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        progressTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(progressTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Arial", Font.BOLD, 14));
        btnClose.addActionListener(e -> dispose());
        bottomPanel.add(btnClose);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadProgressData() {
        try {
            List<Result> results = resultService.getResultsByStudentId(currentStudentId);
            tableModel.setRowCount(0);

            if (results == null || results.isEmpty()) {
                tableModel.addRow(new Object[]{"Chưa ghi danh khóa học nào", "", "", "", "", ""});
                return;
            }

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Result result : results) {
                String courseName = "N/A";
                String className = "N/A";
                String startDateStr = "";
                String endDateStr = "";
                
                if (result.getAcademicClass() != null) {
                    className = result.getAcademicClass().getClassName();
                    startDateStr = result.getAcademicClass().getStartDate() != null ? 
                                    result.getAcademicClass().getStartDate().format(dateFormatter) : "";
                    endDateStr = result.getAcademicClass().getEndDate() != null ? 
                                    result.getAcademicClass().getEndDate().format(dateFormatter) : "";
                                    
                    if (result.getAcademicClass().getCourse() != null) {
                        courseName = result.getAcademicClass().getCourse().getCourseName();
                    }
                }

                String scoreStr = result.getScore() != null ? String.valueOf(result.getScore()) : "Chưa có";
                String gradeStr = result.getGrade() != null ? translateGrade(result.getGrade()) : "Đang học";

                tableModel.addRow(new Object[]{
                        courseName,
                        className,
                        startDateStr,
                        endDateStr,
                        scoreStr,
                        gradeStr
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu tiến độ: " + ex.getMessage());
        }
    }

    private String translateGrade(String grade) {
        switch (grade) {
            case "Excellent": return "Xuất sắc";
            case "Good": return "Giỏi";
            case "Fair": return "Khá";
            case "Average": return "Trung bình";
            case "Poor": return "Kém";
            case "Fail": return "Trượt";
            default: return grade;
        }
    }
}
