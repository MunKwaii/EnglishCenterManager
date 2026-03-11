package vn.edu.ute.view;

import vn.edu.ute.model.AcademicClass;
import vn.edu.ute.model.Course;
import vn.edu.ute.model.Room;
import vn.edu.ute.model.Teacher;
import vn.edu.ute.model.Branch;
import vn.edu.ute.model.enums.ClassStatus;
import vn.edu.ute.service.AcademicClassService;
import vn.edu.ute.service.CourseService;
import vn.edu.ute.service.RoomService;
import vn.edu.ute.service.TeacherService;
import vn.edu.ute.service.BranchService;
import vn.edu.ute.service.impl.AcademicClassServiceImpl;
import vn.edu.ute.service.impl.CourseServiceImpl;
import vn.edu.ute.service.impl.RoomServiceImpl;
import vn.edu.ute.service.impl.TeacherServiceImpl;
import vn.edu.ute.service.impl.BranchServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class ClassPanel extends JPanel {

    // Services
    private final AcademicClassService classService = new AcademicClassServiceImpl();
    private final CourseService courseService = new CourseServiceImpl();
    private final RoomService roomService = new RoomServiceImpl();
    private final TeacherService teacherService = new TeacherServiceImpl();
    private final BranchService branchService = new BranchServiceImpl();

    // UI Components
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtClassName, txtMaxStudent, txtSearch;
    private JSpinner spStartDate, spEndDate;
    private JComboBox<Course> cbCourse;
    private JComboBox<Room> cbRoom;
    private JComboBox<Teacher> cbTeacher;
    private JComboBox<Branch> cbBranch;
    private JComboBox<ClassStatus> cbStatus;

    private JButton btnAdd, btnUpdate, btnDelete, btnSearch, btnRefresh;

    public ClassPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadDataToTable(classService.getAllClasses());
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(5, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Lớp Học"));

        txtClassName = new JTextField();
        txtMaxStudent = new JTextField("0");

        spStartDate = new JSpinner(new SpinnerDateModel());
        spStartDate.setEditor(new JSpinner.DateEditor(spStartDate, "dd/MM/yyyy"));

        spEndDate = new JSpinner(new SpinnerDateModel());
        spEndDate.setEditor(new JSpinner.DateEditor(spEndDate, "dd/MM/yyyy"));

        cbCourse = new JComboBox<>();
        loadActiveCourses();
        cbCourse.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Course) {
                    setText(((Course) value).getCourseName());
                }
                return this;
            }
        });

        cbRoom = new JComboBox<>();
        loadActiveRooms();
        cbRoom.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Room) {
                    setText(((Room) value).getRoomName());
                }
                return this;
            }
        });

        cbTeacher = new JComboBox<>();
        loadActiveTeachers();
        cbTeacher.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Teacher) {
                    setText(((Teacher) value).getFullName() != null ? ((Teacher) value).getFullName()
                            : "Teacher ID: " + ((Teacher) value).getTeacherId());
                }
                return this;
            }
        });

        cbBranch = new JComboBox<>();
        loadActiveBranches();
        cbBranch.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Branch) {
                    setText(((Branch) value).getBranchName());
                }
                return this;
            }
        });

        cbStatus = new JComboBox<>(ClassStatus.values());

        formPanel.add(new JLabel("Tên lớp:"));
        formPanel.add(txtClassName);
        formPanel.add(new JLabel("Sĩ số tối đa:"));
        formPanel.add(txtMaxStudent);

        formPanel.add(new JLabel("Ngày bắt đầu:"));
        formPanel.add(spStartDate);
        formPanel.add(new JLabel("Ngày kết thúc:"));
        formPanel.add(spEndDate);

        formPanel.add(new JLabel("Khóa học:"));
        formPanel.add(cbCourse);
        formPanel.add(new JLabel("Phòng học:"));
        formPanel.add(cbRoom);

        formPanel.add(new JLabel("Giáo viên:"));
        formPanel.add(cbTeacher);
        formPanel.add(new JLabel("Chi nhánh:"));
        formPanel.add(cbBranch);

        formPanel.add(new JLabel("Trạng thái:"));
        formPanel.add(cbStatus);
        
        // Trống 2 ô cuối để căn chỉnh đẹp hơn
        formPanel.add(new JLabel());
        formPanel.add(new JLabel());

        return formPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());

        String[] columns = { "ID", "Tên Lớp", "Khóa Học", "Chi Nhánh", "Phòng", "Giáo Viên", "Bắt Đầu", "Kết Thúc", "Sĩ Số",
                "Trạng Thái" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int selectedRow = table.getSelectedRow();
                Long classId = (Long) tableModel.getValueAt(selectedRow, 0);

                // Fetch the actual object from DB or service instead of parsing strings, or
                // match from combobox items
                AcademicClass aClass = classService.getAllClasses().stream()
                        .filter(c -> c.getClassId().equals(classId))
                        .findFirst()
                        .orElse(null);

                if (aClass != null) {
                    txtClassName.setText(aClass.getClassName());
                    txtMaxStudent.setText(String.valueOf(aClass.getMaxStudent()));

                    if (aClass.getStartDate() != null) {
                        spStartDate.setValue(
                                Date.from(aClass.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    }
                    if (aClass.getEndDate() != null) {
                        spEndDate.setValue(
                                Date.from(aClass.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    }

                    if (aClass.getCourse() != null)
                        setSelectedItemById(cbCourse, aClass.getCourse().getCourseId());
                    if (aClass.getRoom() != null)
                        setSelectedItemById(cbRoom, aClass.getRoom().getRoomId());
                    if (aClass.getTeacher() != null)
                        setSelectedItemById(cbTeacher, aClass.getTeacher().getTeacherId());
                    if (aClass.getBranch() != null)
                        setSelectedItemById(cbBranch, aClass.getBranch().getBranchId());

                    cbStatus.setSelectedItem(aClass.getStatus());
                }
            }
        });

        return tablePanel;
    }

    private JPanel createButtonPanel() {
        JPanel actionPanel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(15);
        btnSearch = new JButton("Tìm kiếm");
        searchPanel.add(new JLabel("Tìm tên lớp:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdd = new JButton("Thêm mới");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        actionPanel.add(searchPanel, BorderLayout.WEST);
        actionPanel.add(buttonPanel, BorderLayout.EAST);

        setupButtonListeners();

        return actionPanel;
    }

    private void loadDataToTable(List<AcademicClass> classes) {
        tableModel.setRowCount(0);
        if (classes != null) {
            for (AcademicClass c : classes) {
                tableModel.addRow(new Object[] {
                        c.getClassId(),
                        c.getClassName(),
                        c.getCourse() != null ? c.getCourse().getCourseName() : "",
                        c.getBranch() != null ? c.getBranch().getBranchName() : "",
                        c.getRoom() != null ? c.getRoom().getRoomName() : "",
                        c.getTeacher() != null ? (c.getTeacher().getFullName() != null ? c.getTeacher().getFullName()
                                : "ID: " + c.getTeacher().getTeacherId()) : "",
                        c.getStartDate(),
                        c.getEndDate(),
                        c.getMaxStudent(),
                        c.getStatus().name()
                });
            }
        }
    }

    private void loadActiveCourses() {
        cbCourse.removeAllItems();
        List<Course> activeCourses = courseService.getActiveCourses();
        if (activeCourses != null) {
            for (Course c : activeCourses)
                cbCourse.addItem(c);
        }
    }

    private void loadActiveRooms() {
        cbRoom.removeAllItems();
        List<Room> activeRooms = roomService.getActiveRooms();
        if (activeRooms != null) {
            for (Room r : activeRooms)
                cbRoom.addItem(r);
        }
    }

    private void loadActiveTeachers() {
        cbTeacher.removeAllItems();
        List<Teacher> activeTeachers = teacherService.getActiveTeachers();
        if (activeTeachers != null) {
            for (Teacher t : activeTeachers)
                cbTeacher.addItem(t);
        }
    }

    private void loadActiveBranches() {
        cbBranch.removeAllItems();
        List<Branch> activeBranches = branchService.getActiveBranches();
        if (activeBranches != null) {
            for (Branch b : activeBranches)
                cbBranch.addItem(b);
        }
    }

    private void setSelectedItemById(JComboBox<?> comboBox, Long id) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            Object item = comboBox.getItemAt(i);
            if (item instanceof Course && ((Course) item).getCourseId().equals(id)) {
                comboBox.setSelectedIndex(i);
                return;
            } else if (item instanceof Room && ((Room) item).getRoomId().equals(id)) {
                comboBox.setSelectedIndex(i);
                return;
            } else if (item instanceof Teacher && ((Teacher) item).getTeacherId().equals(id)) {
                comboBox.setSelectedIndex(i);
                return;
            } else if (item instanceof Branch && ((Branch) item).getBranchId().equals(id)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void setupButtonListeners() {
        btnAdd.addActionListener(e -> {
            try {
                Date sDate = (Date) spStartDate.getValue();
                Date eDate = (Date) spEndDate.getValue();

                AcademicClass aClass = AcademicClass.builder()
                        .className(txtClassName.getText())
                        .course((Course) cbCourse.getSelectedItem())
                        .branch((Branch) cbBranch.getSelectedItem())
                        .room((Room) cbRoom.getSelectedItem())
                        .teacher((Teacher) cbTeacher.getSelectedItem())
                        .startDate(sDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                        .endDate(eDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                        .maxStudent(Integer.parseInt(txtMaxStudent.getText()))
                        .status((ClassStatus) cbStatus.getSelectedItem())
                        .build();

                classService.addClass(aClass);
                JOptionPane.showMessageDialog(this, "Thêm lớp học thành công!");
                loadDataToTable(classService.getAllClasses());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi nhập liệu. Vui lòng kiểm tra lại thông tin!");
            }
        });

        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText();
            List<AcademicClass> result = classService.searchClassByName(keyword);
            loadDataToTable(result);
        });

        btnRefresh.addActionListener(e -> {
            txtClassName.setText("");
            txtMaxStudent.setText("0");
            txtSearch.setText("");
            spStartDate.setValue(new Date());
            spEndDate.setValue(new Date());
            loadActiveCourses();
            loadActiveBranches();
            loadActiveRooms();
            loadActiveTeachers();
            loadDataToTable(classService.getAllClasses());
        });

        btnUpdate.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học cần cập nhật!");
                return;
            }
            try {
                Long classId = (Long) tableModel.getValueAt(selectedRow, 0);
                Date sDate = (Date) spStartDate.getValue();
                Date eDate = (Date) spEndDate.getValue();

                AcademicClass aClass = AcademicClass.builder()
                        .classId(classId)
                        .className(txtClassName.getText())
                        .course((Course) cbCourse.getSelectedItem())
                        .branch((Branch) cbBranch.getSelectedItem())
                        .room((Room) cbRoom.getSelectedItem())
                        .teacher((Teacher) cbTeacher.getSelectedItem())
                        .startDate(sDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                        .endDate(eDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                        .maxStudent(Integer.parseInt(txtMaxStudent.getText()))
                        .status((ClassStatus) cbStatus.getSelectedItem())
                        .build();

                classService.updateClass(aClass);
                JOptionPane.showMessageDialog(this, "Cập nhật lớp học thành công!");
                loadDataToTable(classService.getAllClasses());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật. Vui lòng kiểm tra lại!");
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học cần xóa!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa lớp học này?", "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Long classId = (Long) tableModel.getValueAt(selectedRow, 0);
                    classService.removeClass(classId);
                    JOptionPane.showMessageDialog(this, "Xóa lớp học thành công!");
                    loadDataToTable(classService.getAllClasses());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi xóa dữ liệu!");
                }
            }
        });
    }
}
