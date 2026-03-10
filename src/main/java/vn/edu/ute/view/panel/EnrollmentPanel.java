package vn.edu.ute.view.panel;

import vn.edu.ute.model.AcademicClass;
import vn.edu.ute.model.Student;
import vn.edu.ute.service.EnrollmentService;
import vn.edu.ute.service.impl.EnrollmentServiceImpl;

import javax.swing.*;
import java.awt.*;

public class EnrollmentPanel extends JPanel {
    private final EnrollmentService enrollmentService = new EnrollmentServiceImpl();
    private JComboBox<Student> cbStudents; // Cần custom renderer để hiện tên
    private JComboBox<AcademicClass> cbClasses;

    public EnrollmentPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 20));
        formPanel.setBorder(BorderFactory.createTitledBorder("Nghiệp vụ Ghi danh Học viên"));

        // Chú ý: Trong thực tế, bạn sẽ lấy List<Student> từ StudentService để đưa vào ComboBox
        cbStudents = new JComboBox<>(); 
        cbClasses = new JComboBox<>();

        formPanel.add(new JLabel("Chọn Học viên:")); formPanel.add(cbStudents);
        formPanel.add(new JLabel("Chọn Lớp học:")); formPanel.add(cbClasses);

        JButton btnEnroll = new JButton("Tiến hành Ghi danh");
        // Sử dụng LAMBDA gọi Service
        btnEnroll.addActionListener(e -> handleEnrollment());

        formPanel.add(new JLabel("")); // Dummy label for alignment
        formPanel.add(btnEnroll);

        add(formPanel, BorderLayout.NORTH);
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