package vn.edu.ute.view;

import vn.edu.ute.model.AcademicClass;
import vn.edu.ute.model.Promotion;
import vn.edu.ute.model.Student;
import vn.edu.ute.service.AcademicClassService;
import vn.edu.ute.service.EnrollmentService;
import vn.edu.ute.service.PromotionService;
import vn.edu.ute.service.StudentService;
import vn.edu.ute.service.impl.AcademicClassServiceImpl;
import vn.edu.ute.service.impl.EnrollmentServiceImpl;
import vn.edu.ute.service.impl.PromotionServiceImpl;
import vn.edu.ute.service.impl.StudentServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EnrollmentPanel extends JPanel {
    private final EnrollmentService enrollmentService = new EnrollmentServiceImpl();
    private final StudentService studentService = new StudentServiceImpl();
    private final AcademicClassService classService = new AcademicClassServiceImpl();
    // Bổ sung Promotion Service
    private final PromotionService promoService = new PromotionServiceImpl();

    private JComboBox<Student> cbStudents;
    private JComboBox<AcademicClass> cbClasses;
    private JComboBox<Promotion> cbPromotion;

    public EnrollmentPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 20));
        formPanel.setBorder(BorderFactory.createTitledBorder("Nghiệp vụ Ghi danh Học viên"));

        cbStudents = new JComboBox<>();
        cbClasses = new JComboBox<>();
        cbPromotion = new JComboBox<>();

        cbStudents.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Student) {
                    Student s = (Student) value;
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

        // Tùy chỉnh hiển thị cho ComboBox Khuyến mãi
        cbPromotion.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Không áp dụng");
                } else if (value instanceof Promotion) {
                    Promotion p = (Promotion) value;
                    String discountStr = p.getDiscountType().name().equals("Percent") ? p.getDiscountValue() + "%" : p.getDiscountValue() + " VNĐ";
                    setText(p.getPromoName() + " (Giảm " + discountStr + ")");
                }
                return this;
            }
        });

        formPanel.add(new JLabel("Chọn Học viên:")); formPanel.add(cbStudents);
        formPanel.add(new JLabel("Chọn Lớp học:")); formPanel.add(cbClasses);
        formPanel.add(new JLabel("Mã khuyến mãi (Tùy chọn):")); formPanel.add(cbPromotion);

        JButton btnEnroll = new JButton("Tiến hành Ghi danh");
        btnEnroll.addActionListener(e -> handleEnrollment());

        formPanel.add(new JLabel("")); // Căn chỉnh layout
        formPanel.add(btnEnroll);

        add(formPanel, BorderLayout.NORTH);

        loadComboBoxData();
    }

    private void loadComboBoxData() {
        cbStudents.removeAllItems();
        cbClasses.removeAllItems();
        cbPromotion.removeAllItems();

        // Thêm giá trị null đầu tiên cho trường hợp không dùng khuyến mãi
        cbPromotion.addItem(null); 

        List<Student> students = studentService.getAllStudents();
        if (students != null) {
            students.forEach(cbStudents::addItem);
        }

        List<AcademicClass> classes = classService.getAllClasses();
        if (classes != null) {
            classes.forEach(cbClasses::addItem);
        }

        // Tải danh sách các Khuyến mãi đang Active
        List<Promotion> promotions = promoService.getActivePromotions();
        if (promotions != null) {
            promotions.forEach(cbPromotion::addItem);
        }
    }

    private void handleEnrollment() {
        Student selectedStudent = (Student) cbStudents.getSelectedItem();
        AcademicClass selectedClass = (AcademicClass) cbClasses.getSelectedItem();
        Promotion selectedPromo = (Promotion) cbPromotion.getSelectedItem();

        if (selectedStudent != null && selectedClass != null) {
            try {
                // Truyền thêm Promotion vào Service
                enrollmentService.enrollStudent(selectedStudent, selectedClass, selectedPromo);
                JOptionPane.showMessageDialog(this, "Ghi danh thành công! Hóa đơn đã được phát hành tự động.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Ghi danh", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Học viên và Lớp học!");
        }
    }
}