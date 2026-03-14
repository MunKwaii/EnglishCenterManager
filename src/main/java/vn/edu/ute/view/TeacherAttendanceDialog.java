package vn.edu.ute.view;

import vn.edu.ute.model.AcademicClass;
import vn.edu.ute.model.Attendance;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.enums.AttendanceStatus;
import vn.edu.ute.service.AttendanceService;
import vn.edu.ute.service.EnrollmentService;
import vn.edu.ute.service.impl.AttendanceServiceImpl;
import vn.edu.ute.service.impl.EnrollmentServiceImpl;
import vn.edu.ute.util.PermissionUtils;
import vn.edu.ute.util.UserSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TeacherAttendanceDialog extends JDialog {

    private final AcademicClass currentClass;
    private final AttendanceService attendanceService = new AttendanceServiceImpl();
    private final EnrollmentService enrollmentService = new EnrollmentServiceImpl();

    private JTable tblAttendance;
    private DefaultTableModel tableModel;
    private JSpinner spinDate;
    private JButton btnLoad, btnSave;
    private List<Enrollment> enrollments;

    public TeacherAttendanceDialog(Window owner, AcademicClass currentClass) {
        super(owner, "Điểm danh lớp: " + currentClass.getClassName(), ModalityType.APPLICATION_MODAL);
        this.currentClass = currentClass;

        setLayout(new BorderLayout(10, 10));
        setSize(800, 600);
        setLocationRelativeTo(owner);
        initComponents();
        loadEnrollments();

        // Kiểm tra phân quyền (Phase 4)
        boolean canTakeAttendance = PermissionUtils.canTakeAttendance(UserSession.getCurrentUser(), currentClass);
        if (!canTakeAttendance) {
            btnSave.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Bạn không có quyền sửa điểm danh lớp này (Chỉ xem).", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void initComponents() {
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTop.add(new JLabel("Chọn ngày:"));
        
        spinDate = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinDate, "dd/MM/yyyy");
        spinDate.setEditor(dateEditor);
        pnlTop.add(spinDate);

        btnLoad = new JButton("Tải điểm danh");
        btnSave = new JButton("Lưu thay đổi");
        pnlTop.add(btnLoad);
        pnlTop.add(btnSave);

        add(pnlTop, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Mã HV", "Tên Học Viên", "Trạng thái", "Ghi chú"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Chỉ cho sửa cột Trạng thái (2) và Ghi chú (3) nếu có quyền
                if (!PermissionUtils.canTakeAttendance(UserSession.getCurrentUser(), currentClass)) {
                    return false;
                }
                return column == 2 || column == 3;
            }
        };

        tblAttendance = new JTable(tableModel);
        tblAttendance.setRowHeight(30);

        // ComboBox cho cột Trạng thái
        TableColumn statusColumn = tblAttendance.getColumnModel().getColumn(2);
        JComboBox<AttendanceStatus> cbStatus = new JComboBox<>(AttendanceStatus.values());
        statusColumn.setCellEditor(new DefaultCellEditor(cbStatus));

        add(new JScrollPane(tblAttendance), BorderLayout.CENTER);

        btnLoad.addActionListener(e -> fetchAttendanceForDate());
        btnSave.addActionListener(e -> saveAttendance());
    }

    private void loadEnrollments() {
        try {
            enrollments = enrollmentService.getEnrollmentsByClassId(currentClass.getClassId());
            if (enrollments.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lớp này hiện chưa có học viên nào.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                fetchAttendanceForDate(); // Tải mặc định cho ngày hiện tại
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy danh sách học viên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fetchAttendanceForDate() {
        if (enrollments == null || enrollments.isEmpty()) return;
        
        java.util.Date selectedDate = (java.util.Date) spinDate.getValue();
        LocalDate localDate = new java.sql.Date(selectedDate.getTime()).toLocalDate();

        List<Attendance> existingAttendances = attendanceService.getAttendancesByClassIdAndDate(currentClass.getClassId(), localDate);

        tableModel.setRowCount(0);

        for (Enrollment enr : enrollments) {
            // Check nếu đã có điểm danh trong DB
            Attendance att = existingAttendances.stream()
                    .filter(a -> a.getStudent().getStudentId().equals(enr.getStudent().getStudentId()))
                    .findFirst().orElse(null);

            AttendanceStatus status = att != null ? att.getStatus() : AttendanceStatus.Present;
            String note = att != null ? att.getNote() : "";

            tableModel.addRow(new Object[]{
                    enr.getStudent().getStudentId(),
                    enr.getStudent().getFullName(),
                    status,
                    note
            });
        }
    }

    private void saveAttendance() {
        if (tblAttendance.isEditing()) {
            tblAttendance.getCellEditor().stopCellEditing();
        }

        java.util.Date selectedDate = (java.util.Date) spinDate.getValue();
        LocalDate localDate = new java.sql.Date(selectedDate.getTime()).toLocalDate();
        List<Attendance> attendancesToSave = new ArrayList<>();

        List<Attendance> existingAttendances = attendanceService.getAttendancesByClassIdAndDate(currentClass.getClassId(), localDate);

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Long studentId = (Long) tableModel.getValueAt(i, 0);
            AttendanceStatus status = (AttendanceStatus) tableModel.getValueAt(i, 2);
            String note = (String) tableModel.getValueAt(i, 3);

            Attendance.AttendanceBuilder builder = Attendance.builder()
                    .academicClass(currentClass)
                    .attendDate(localDate)
                    .status(status)
                    .note(note);

            // Tìm Enrollment
            Enrollment currentEnr = enrollments.stream().filter(e -> e.getStudent().getStudentId().equals(studentId)).findFirst().orElse(null);
            if(currentEnr != null) {
                builder.student(currentEnr.getStudent());
            }

            // Gắn ID nếu đã tồn tại để thực hiện Update
            Attendance existing = existingAttendances.stream().filter(a -> a.getStudent().getStudentId().equals(studentId)).findFirst().orElse(null);
            if (existing != null) {
                builder.attendanceId(existing.getAttendanceId());
            }

            attendancesToSave.add(builder.build());
        }

        boolean success = attendanceService.saveAllAttendances(attendancesToSave);
        if (success) {
            JOptionPane.showMessageDialog(this, "Đã lưu điểm danh thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            fetchAttendanceForDate(); // Refresh
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu điểm danh.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
