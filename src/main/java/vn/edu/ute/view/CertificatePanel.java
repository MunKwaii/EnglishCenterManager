package vn.edu.ute.view;

import vn.edu.ute.model.AcademicClass;
import vn.edu.ute.model.Certificate;
import vn.edu.ute.model.Student;
import vn.edu.ute.service.AcademicClassService;
import vn.edu.ute.service.CertificateService;
import vn.edu.ute.service.impl.AcademicClassServiceImpl;
import vn.edu.ute.service.impl.CertificateServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CertificatePanel extends JPanel {
    private final CertificateService certService = new CertificateServiceImpl();
    private final AcademicClassService classService = new AcademicClassServiceImpl();
    
    private JComboBox<AcademicClass> cbClasses;
    private JTable table;
    private DefaultTableModel tableModel;

    public CertificatePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tăng khoảng cách giữa các thành phần trên thanh công cụ cho thoáng
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Cấp Chứng Chỉ (Dành cho SV Pass)"));
        
        cbClasses = new JComboBox<>();
        
        // FIX LỖI UI: Thêm Renderer để hiển thị tên lớp đẹp mắt thay vì chuỗi Object thô
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

        // Tải danh sách lớp học
        List<AcademicClass> classes = classService.getAllClasses();
        if (classes != null) {
            classes.forEach(cbClasses::addItem);
        }
        
        JButton btnLoad = new JButton("Tải danh sách đủ điều kiện");
        topPanel.add(new JLabel("Chọn Lớp học:"));
        topPanel.add(cbClasses);
        topPanel.add(btnLoad);

        // Bảng cho phép gõ trực tiếp Tên chứng chỉ và Serial
        String[] columns = {"ID SV", "Tên Sinh Viên", "Tên Chứng Chỉ", "Số Serial (Bắt buộc)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c >= 2; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25); // Nới rộng chiều cao hàng để dễ nhìn hơn

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Xác nhận Cấp phát");
        bottomPanel.add(btnSave);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- SỰ KIỆN TẢI DANH SÁCH ---
        btnLoad.addActionListener(e -> {
            AcademicClass cls = (AcademicClass) cbClasses.getSelectedItem();
            if(cls != null) {
                tableModel.setRowCount(0);
                try {
                    List<Student> students = certService.getEligibleStudents(cls.getClassId());
                    if(students == null || students.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Không có SV nào đủ điều kiện hoặc tất cả đã được cấp chứng chỉ!");
                    } else {
                        // Tự động lấy tên Khóa học làm tên Chứng chỉ mặc định
                        String defaultCertName = "Chứng nhận hoàn thành " + (cls.getCourse() != null ? cls.getCourse().getCourseName() : cls.getClassName());
                        students.forEach(s -> tableModel.addRow(new Object[]{
                            s.getStudentId(), 
                            s.getFullName(), 
                            defaultCertName, 
                            ""
                        }));
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + ex.getMessage());
                }
            }
        });

        // --- SỰ KIỆN LƯU CHỨNG CHỈ ---
        btnSave.addActionListener(e -> {
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Bảng danh sách trống!");
                return;
            }
            try {
                AcademicClass cls = (AcademicClass) cbClasses.getSelectedItem();
                List<Certificate> certs = new ArrayList<>();
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String serial = tableModel.getValueAt(i, 3).toString().trim();
                    // Chỉ lấy những dòng mà User có gõ Số Serial
                    if(!serial.isEmpty()) {
                        Student s = new Student(); s.setStudentId((Long) tableModel.getValueAt(i, 0));
                        certs.add(Certificate.builder()
                                .student(s).academicClass(cls)
                                .certName(tableModel.getValueAt(i, 2).toString())
                                .serialNo(serial).issueDate(LocalDate.now())
                                .build());
                    }
                }
                if(certs.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Bạn chưa nhập Số Serial cho bất kỳ SV nào!"); return;
                }
                certService.issueCertificatesBatch(certs);
                JOptionPane.showMessageDialog(this, "Đã cấp phát " + certs.size() + " chứng chỉ thành công!");
                tableModel.setRowCount(0); // Clear bảng sau khi cấp thành công
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        });
    }
}