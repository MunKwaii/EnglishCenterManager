package vn.edu.ute.view;

import vn.edu.ute.model.AcademicClass;
import vn.edu.ute.model.Room;
import vn.edu.ute.model.Schedule;
import vn.edu.ute.model.Teacher;
import vn.edu.ute.service.AcademicClassService;
import vn.edu.ute.service.RoomService;
import vn.edu.ute.service.ScheduleService;
import vn.edu.ute.service.TeacherService;
import vn.edu.ute.service.impl.AcademicClassServiceImpl;
import vn.edu.ute.service.impl.RoomServiceImpl;
import vn.edu.ute.service.impl.ScheduleServiceImpl;
import vn.edu.ute.service.impl.TeacherServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SchedulePanel extends JPanel {
    private final ScheduleService scheduleService = new ScheduleServiceImpl();
    private final AcademicClassService classService = new AcademicClassServiceImpl();
    private final RoomService roomService = new RoomServiceImpl();
    private final TeacherService teacherService = new TeacherServiceImpl();

    // Table
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private Long selectedScheduleId = null;

    // Filters
    private JComboBox<ClassItem> filterClassCombo;
    private JComboBox<RoomItem> filterRoomCombo;
    private JComboBox<TeacherItem> filterTeacherCombo;
    private JButton btnFilter;

    // Form inputs
    private JComboBox<ClassItem> classCombo;
    private JComboBox<RoomItem> roomCombo;
    private JTextField txtStudyDate;
    private JSpinner spinStartTime;
    private JSpinner spinEndTime;
    private JButton btnAddSchedule;
    private JButton btnUpdateSchedule;
    private JButton btnDeleteSchedule;
    private JButton btnRefreshSchedule;

    public SchedulePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initUI();
        loadInitialData();
        loadSchedules();
    }

    private void initUI() {
        // --- NORTH: Filters ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Bộ lọc (Tìm kiếm Lịch học)"));

        filterClassCombo = new JComboBox<>();
        filterRoomCombo = new JComboBox<>();
        filterTeacherCombo = new JComboBox<>();
        btnFilter = new JButton("Xem thời khóa biểu");

        filterPanel.add(new JLabel("Lớp học:"));
        filterPanel.add(filterClassCombo);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(new JLabel("Phòng học:"));
        filterPanel.add(filterRoomCombo);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(new JLabel("Giáo viên:"));
        filterPanel.add(filterTeacherCombo);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(btnFilter);

        btnFilter.addActionListener(e -> filterSchedules());

        add(filterPanel, BorderLayout.NORTH);

        // --- CENTER: Table ---
        String[] columns = { "ID", "Ngày học", "Bắt đầu", "Kết thúc", "Lớp học", "Phòng học", "Giáo viên" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scheduleTable = new JTable(tableModel);
        scheduleTable.setRowHeight(25);
        scheduleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scheduleTable.setRowSelectionAllowed(true);
        scheduleTable.setCellSelectionEnabled(false);
        add(new JScrollPane(scheduleTable), BorderLayout.CENTER);

        // --- SOUTH: Form ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Xếp lịch học (Tạo mới)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        classCombo = new JComboBox<>();
        roomCombo = new JComboBox<>();
        txtStudyDate = new JTextField(10);
        txtStudyDate.setToolTipText("YYYY-MM-DD");

        spinStartTime = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.HOUR_OF_DAY));
        JSpinner.DateEditor timeEditorStart = new JSpinner.DateEditor(spinStartTime, "HH:mm");
        spinStartTime.setEditor(timeEditorStart);

        spinEndTime = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.HOUR_OF_DAY));
        JSpinner.DateEditor timeEditorEnd = new JSpinner.DateEditor(spinEndTime, "HH:mm");
        spinEndTime.setEditor(timeEditorEnd);

        btnAddSchedule = new JButton("Thêm");
        btnUpdateSchedule = new JButton("Cập nhật");
        btnDeleteSchedule = new JButton("Xóa");
        btnRefreshSchedule = new JButton("Làm mới Form");

        btnUpdateSchedule.setEnabled(false);
        btnDeleteSchedule.setEnabled(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Lớp học:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(classCombo, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Phòng học:"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 0;
        formPanel.add(roomCombo, gbc);

        gbc.gridx = 4;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Ngày học (YYYY-MM-DD):"), gbc);
        gbc.gridx = 5;
        gbc.gridy = 0;
        formPanel.add(txtStudyDate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Giờ bắt đầu:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(spinStartTime, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Giờ kết thúc:"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 1;
        formPanel.add(spinEndTime, gbc);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        actionPanel.add(btnAddSchedule);
        actionPanel.add(btnUpdateSchedule);
        actionPanel.add(btnDeleteSchedule);
        actionPanel.add(btnRefreshSchedule);

        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(actionPanel, gbc);

        btnAddSchedule.addActionListener(e -> addSchedule());
        btnUpdateSchedule.addActionListener(e -> updateSchedule());
        btnDeleteSchedule.addActionListener(e -> deleteSchedule());
        btnRefreshSchedule.addActionListener(e -> clearForm());

        // Table Selection Listener
        scheduleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && scheduleTable.getSelectedRow() != -1) {
                int row = scheduleTable.getSelectedRow();
                selectedScheduleId = (Long) tableModel.getValueAt(row, 0);

                // Populate Form
                txtStudyDate.setText(tableModel.getValueAt(row, 1).toString());

                LocalTime startT = (LocalTime) tableModel.getValueAt(row, 2);
                LocalTime endT = (LocalTime) tableModel.getValueAt(row, 3);

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, startT.getHour());
                cal.set(Calendar.MINUTE, startT.getMinute());
                spinStartTime.setValue(cal.getTime());

                cal.set(Calendar.HOUR_OF_DAY, endT.getHour());
                cal.set(Calendar.MINUTE, endT.getMinute());
                spinEndTime.setValue(cal.getTime());

                String className = (String) tableModel.getValueAt(row, 4);
                if (className != null && !className.isEmpty()) {
                    for (int i = 0; i < classCombo.getItemCount(); i++) {
                        if (classCombo.getItemAt(i).toString().equals(className)) {
                            classCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                } else if (classCombo.getItemCount() > 0) {
                    classCombo.setSelectedIndex(0);
                }

                String roomName = (String) tableModel.getValueAt(row, 5);
                if (roomName != null && !roomName.isEmpty()) {
                    for (int i = 0; i < roomCombo.getItemCount(); i++) {
                        if (roomCombo.getItemAt(i).toString().equals(roomName)) {
                            roomCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                } else if (roomCombo.getItemCount() > 0) {
                    roomCombo.setSelectedIndex(0);
                }

                btnUpdateSchedule.setEnabled(true);
                btnDeleteSchedule.setEnabled(true);
                btnAddSchedule.setEnabled(false);
            }
        });

        add(formPanel, BorderLayout.SOUTH);
    }

    private void loadInitialData() {
        filterClassCombo.addItem(new ClassItem(null, "Tất cả"));
        filterRoomCombo.addItem(new RoomItem(null, "Tất cả"));
        filterTeacherCombo.addItem(new TeacherItem(null, "Tất cả"));

        List<AcademicClass> classes = classService.getAllClasses();
        if (classes != null) {
            for (AcademicClass c : classes) {
                ClassItem item = new ClassItem(c, c.getClassName());
                filterClassCombo.addItem(item);
                classCombo.addItem(item);
            }
        }

        List<Room> rooms = roomService.getAllRooms();
        if (rooms != null) {
            for (Room r : rooms) {
                RoomItem item = new RoomItem(r, r.getRoomName());
                filterRoomCombo.addItem(item);
                roomCombo.addItem(item);
            }
        }

        List<Teacher> teachers = teacherService.getAllTeachers();
        if (teachers != null) {
            for (Teacher t : teachers) {
                TeacherItem item = new TeacherItem(t, t.getFullName());
                filterTeacherCombo.addItem(item);
            }
        }

        txtStudyDate.setText(LocalDate.now().toString());
    }

    private void loadSchedules() {
        List<Schedule> list = scheduleService.findAll();
        updateTable(list);
    }

    private void filterSchedules() {
        List<Schedule> list = scheduleService.findAll();
        if (list == null)
            return;

        ClassItem selectedClass = (ClassItem) filterClassCombo.getSelectedItem();
        RoomItem selectedRoom = (RoomItem) filterRoomCombo.getSelectedItem();
        TeacherItem selectedTeacher = (TeacherItem) filterTeacherCombo.getSelectedItem();

        List<Schedule> filteredList = list.stream().filter(s -> {
            boolean matchClass = selectedClass == null || selectedClass.getAcademicClass() == null ||
                    s.getAcademicClass().getClassId().equals(selectedClass.getAcademicClass().getClassId());
            boolean matchRoom = selectedRoom == null || selectedRoom.getRoom() == null ||
                    (s.getRoom() != null && s.getRoom().getRoomId().equals(selectedRoom.getRoom().getRoomId()));
            boolean matchTeacher = selectedTeacher == null || selectedTeacher.getTeacher() == null ||
                    (s.getAcademicClass().getTeacher() != null && s.getAcademicClass().getTeacher().getTeacherId()
                            .equals(selectedTeacher.getTeacher().getTeacherId()));
            return matchClass && matchRoom && matchTeacher;
        }).collect(Collectors.toList());

        updateTable(filteredList);
    }

    private void updateTable(List<Schedule> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (Schedule s : list) {
                tableModel.addRow(new Object[] {
                        s.getScheduleId(),
                        s.getStudyDate(),
                        s.getStartTime(),
                        s.getEndTime(),
                        s.getAcademicClass() != null ? s.getAcademicClass().getClassName() : "",
                        s.getRoom() != null ? s.getRoom().getRoomName() : "",
                        s.getAcademicClass() != null && s.getAcademicClass().getTeacher() != null
                                ? s.getAcademicClass().getTeacher().getFullName()
                                : ""
                });
            }
        }
    }

    private void addSchedule() {
        ClassItem selectedClassItem = (ClassItem) classCombo.getSelectedItem();
        RoomItem selectedRoomItem = (RoomItem) roomCombo.getSelectedItem();

        if (selectedClassItem == null || selectedClassItem.getAcademicClass() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        AcademicClass aClass = selectedClassItem.getAcademicClass();
        Room room = selectedRoomItem != null ? selectedRoomItem.getRoom() : null;

        LocalDate studyDate;
        try {
            studyDate = LocalDate.parse(txtStudyDate.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Ngày học không hợp lệ! Định dạng chuẩn: YYYY-MM-DD", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date startT = (Date) spinStartTime.getValue();
        Date endT = (Date) spinEndTime.getValue();

        Calendar cal = Calendar.getInstance();
        cal.setTime(startT);
        LocalTime startTime = LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));

        cal.setTime(endT);
        LocalTime endTime = LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));

        Schedule schedule = new Schedule();
        schedule.setAcademicClass(aClass);
        schedule.setRoom(room);
        schedule.setStudyDate(studyDate);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);

        try {
            scheduleService.addSchedule(schedule);
            JOptionPane.showMessageDialog(this, "Thêm lịch học thành công!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            loadSchedules(); // Refresh table
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Trùng Lịch", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSchedule() {
        if (selectedScheduleId == null)
            return;

        ClassItem selectedClassItem = (ClassItem) classCombo.getSelectedItem();
        RoomItem selectedRoomItem = (RoomItem) roomCombo.getSelectedItem();

        if (selectedClassItem == null || selectedClassItem.getAcademicClass() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        AcademicClass aClass = selectedClassItem.getAcademicClass();
        Room room = selectedRoomItem != null ? selectedRoomItem.getRoom() : null;

        LocalDate studyDate;
        try {
            studyDate = LocalDate.parse(txtStudyDate.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Ngày học không hợp lệ! Định dạng chuẩn: YYYY-MM-DD", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date startT = (Date) spinStartTime.getValue();
        Date endT = (Date) spinEndTime.getValue();

        Calendar cal = Calendar.getInstance();
        cal.setTime(startT);
        LocalTime startTime = LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));

        cal.setTime(endT);
        LocalTime endTime = LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));

        Schedule schedule = scheduleService.findById(selectedScheduleId);
        if (schedule == null)
            return;

        schedule.setAcademicClass(aClass);
        schedule.setRoom(room);
        schedule.setStudyDate(studyDate);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);

        try {
            scheduleService.updateSchedule(schedule);
            JOptionPane.showMessageDialog(this, "Cập nhật lịch học thành công!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            loadSchedules(); // Refresh table
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Trùng Lịch", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSchedule() {
        if (selectedScheduleId == null)
            return;

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa lịch học này?", "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = scheduleService.delete(selectedScheduleId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa lịch học thành công!", "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                loadSchedules();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa lịch học thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        scheduleTable.clearSelection();
        selectedScheduleId = null;
        txtStudyDate.setText(LocalDate.now().toString());
        if (classCombo.getItemCount() > 0)
            classCombo.setSelectedIndex(0);
        if (roomCombo.getItemCount() > 0)
            roomCombo.setSelectedIndex(0);

        btnUpdateSchedule.setEnabled(false);
        btnDeleteSchedule.setEnabled(false);
        btnAddSchedule.setEnabled(true);
    }

    // --- Wrapper Classes for JComboBox ---
    static class ClassItem {
        private final AcademicClass academicClass;
        private final String display;

        public ClassItem(AcademicClass academicClass, String display) {
            this.academicClass = academicClass;
            this.display = display;
        }

        public AcademicClass getAcademicClass() {
            return academicClass;
        }

        @Override
        public String toString() {
            return display;
        }
    }

    static class RoomItem {
        private final Room room;
        private final String display;

        public RoomItem(Room room, String display) {
            this.room = room;
            this.display = display;
        }

        public Room getRoom() {
            return room;
        }

        @Override
        public String toString() {
            return display;
        }
    }

    static class TeacherItem {
        private final Teacher teacher;
        private final String display;

        public TeacherItem(Teacher teacher, String display) {
            this.teacher = teacher;
            this.display = display;
        }

        public Teacher getTeacher() {
            return teacher;
        }

        @Override
        public String toString() {
            return display;
        }
    }
}
