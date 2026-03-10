package vn.edu.ute.view;

import vn.edu.ute.model.AcademicClass;
import vn.edu.ute.model.Student;
import vn.edu.ute.service.AcademicClassService;
import vn.edu.ute.service.EnrollmentService;
import vn.edu.ute.service.StudentService;
import vn.edu.ute.service.impl.AcademicClassServiceImpl;
import vn.edu.ute.service.impl.EnrollmentServiceImpl;
import vn.edu.ute.service.impl.StudentServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EnrollmentPanel extends JPanel {
    // 1. Khai báo thêm Service để lấy dữ liệu Học viên và Lớp học
    private final EnrollmentService enrollmentService = new EnrollmentServiceImpl();
    private final StudentService studentService = new StudentServiceImpl();
    private final AcademicClassService classService = new AcademicClassServiceImpl();

    private JComboBox<Student> cbStudents;
    private JComboBox<AcademicClass> cbClasses;

    public EnrollmentPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 20));
        formPanel.setBorder(BorderFactory.createTitledBorder("Nghiệp vụ Ghi danh Học viên"));

        cbStudents = new JComboBox<>();
        cbClasses = new JComboBox<>();

        // 2. Custom Renderer để hiển thị Tên thay vì hiển thị Object ID
        cbStudents.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Student) {
                    Student s = (Student) value;
                    // Hiển thị Tên kèm SĐT để dễ phân biệt nếu trùng tên
                    setText(s.getFullName() + " - " + s.getPhone());
                }
                return this;
            }
        });

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

        formPanel.add(new JLabel("Chọn Học viên:")); formPanel.add(cbStudents);
        formPanel.add(new JLabel("Chọn Lớp học:")); formPanel.add(cbClasses);

        JButton btnEnroll = new JButton("Tiến hành Ghi danh");
        btnEnroll.addActionListener(e -> handleEnrollment());

        formPanel.add(new JLabel("")); // Dummy label for alignment
        formPanel.add(btnEnroll);

        add(formPanel, BorderLayout.NORTH);

        // 3. Gọi hàm tải dữ liệu khi khởi tạo Panel
        loadComboBoxData();
    }

    // Hàm gọi DB đổ dữ liệu vào ComboBox (Dùng Lambda Stream)
    private void loadComboBoxData() {
        cbStudents.removeAllItems();
        cbClasses.removeAllItems();

        List<Student> students = studentService.getAllStudents();
        if (students != null) {
            students.forEach(cbStudents::addItem);
        }

        List<AcademicClass> classes = classService.getAllClasses();
        if (classes != null) {
            // Bạn có thể dùng filter() ở đây nếu chỉ muốn hiển thị lớp đang "Planned" hoặc "Open"
            classes.forEach(cbClasses::addItem);
        }
    }

    private void handleEnrollment() {
        Student selectedStudent = (Student) cbStudents.getSelectedItem();
        AcademicClass selectedClass = (AcademicClass) cbClasses.getSelectedItem();

        if (selectedStudent != null && selectedClass != null) {
            try {
                enrollmentService.enrollStudent(selectedStudent, selectedClass);
                JOptionPane.showMessageDialog(this, "Ghi danh thành công! Hóa đơn đã được phát hành tự động.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Ghi danh", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Học viên và Lớp học!");
        }
    }
}