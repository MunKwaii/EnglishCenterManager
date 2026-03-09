package vn.edu.ute.view.panel;

import vn.edu.ute.model.Attendance;
import vn.edu.ute.model.Result;
import vn.edu.ute.model.enums.AttendanceStatus;
import vn.edu.ute.service.AcademicOperationService;
import vn.edu.ute.service.impl.AcademicOperationServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AcademicOperationPanel extends JPanel {
    private final AcademicOperationService operationService = new AcademicOperationServiceImpl();
    private JTabbedPane tabbedPane;

    public AcademicOperationPanel() {
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();

        // Thêm 2 Tab chức năng
        tabbedPane.addTab("Điểm danh Lớp học (Attendance)", createAttendancePanel());
        tabbedPane.addTab("Kết quả Học tập (Result)", createResultPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // --- TAB 1: ĐIỂM DANH ---
    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Thanh công cụ chọn lớp (Giả lập)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("ID Lớp học:"));
        JTextField txtClassId = new JTextField(10);
        topPanel.add(txtClassId);
        JButton btnLoad = new JButton("Tải Danh sách Học viên");
        topPanel.add(btnLoad);
        panel.add(topPanel, BorderLayout.NORTH);

        // Bảng điểm danh
        String[] columns = {"ID Học viên", "Tên Học viên", "Trạng thái", "Ghi chú"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        // Setup JComboBox cho cột "Trạng thái"
        JComboBox<AttendanceStatus> cbStatus = new JComboBox<>(AttendanceStatus.values());
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(cbStatus));

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Nút lưu
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu Điểm danh");
        bottomPanel.add(btnSave);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // SỰ KIỆN LAMBDA (Giả lập xử lý lấy dữ liệu từ Table)
        btnLoad.addActionListener(e -> {
            model.setRowCount(0);
            // Giả lập dữ liệu, thực tế bạn sẽ query DB lấy List<Student> theo ClassID
            model.addRow(new Object[]{1L, "Nguyễn Văn A", AttendanceStatus.Present, ""});
            model.addRow(new Object[]{2L, "Trần Thị B", AttendanceStatus.Absent, "Nghỉ ốm"});
        });

        btnSave.addActionListener(e -> {
            try {
                List<Attendance> attendances = new ArrayList<>();
                // Duyệt table thu thập dữ liệu (Thực tế cần gắn ID thật)
                for (int i = 0; i < model.getRowCount(); i++) {
                    // Logic tạo đối tượng Attendance (Được rút gọn để focus vào UI)
                    // ...
                }
                // operationService.processClassAttendance(attendances);
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
        topPanel.add(new JLabel("ID Lớp học:"));
        JTextField txtClassId = new JTextField(10);
        topPanel.add(txtClassId);
        JButton btnLoad = new JButton("Tải Danh sách Cấp điểm");
        topPanel.add(btnLoad);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID Học viên", "Tên Học viên", "Điểm số", "Xếp loại", "Nhận xét"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu Kết quả Lớp");
        bottomPanel.add(btnSave);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // SỰ KIỆN LAMBDA
        btnLoad.addActionListener(e -> {
            model.setRowCount(0);
            model.addRow(new Object[]{1L, "Nguyễn Văn A", 8.5, "Giỏi", "Tốt"});
        });

        btnSave.addActionListener(e -> {
            try {
                // ... Xử lý lấy dữ liệu từ bảng và tạo đối tượng Result
                // operationService.processClassResults(results);
                JOptionPane.showMessageDialog(this, "Đã cập nhật bảng điểm thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi lưu bảng điểm: " + ex.getMessage());
            }
        });

        return panel;
    }
}