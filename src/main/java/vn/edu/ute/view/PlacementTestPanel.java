package vn.edu.ute.view;

import vn.edu.ute.model.PlacementTest;
import vn.edu.ute.model.Student;
import vn.edu.ute.model.enums.CourseLevel;
import vn.edu.ute.service.PlacementTestService;
import vn.edu.ute.service.StudentService;
import vn.edu.ute.service.impl.PlacementTestServiceImpl;
import vn.edu.ute.service.impl.StudentServiceImpl;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PlacementTestPanel extends JPanel {

    private final PlacementTestService testService = new PlacementTestServiceImpl();
    private final StudentService studentService = new StudentServiceImpl();

    private JTable table;
    private DefaultTableModel tableModel;

    private JComboBox<Student> cbStudent;
    private JTextField txtTestDate, txtScore, txtSuggestedLevel, txtNote;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PlacementTestPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadDataToTable(testService.getAllTests());
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Nhập Điểm Thi Thử"));

        cbStudent = new JComboBox<>();
        loadStudentsToComboBox();
        cbStudent.setRenderer(new DefaultListCellRenderer() {
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

        txtTestDate = new JTextField(LocalDate.now().format(dateFormatter));
        txtScore = new JTextField();
        txtSuggestedLevel = new JTextField();
        txtSuggestedLevel.setEditable(false); // Read-only
        txtSuggestedLevel.setBackground(Color.LIGHT_GRAY);
        txtNote = new JTextField();

        // Lắng nghe sự thay đổi của điểm số để tự động gợi ý level
        txtScore.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateSuggestedLevel(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateSuggestedLevel(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateSuggestedLevel(); }
        });

        formPanel.add(new JLabel("Học viên:"));
        formPanel.add(cbStudent);
        formPanel.add(new JLabel("Ngày thi (dd/MM/yyyy):"));
        formPanel.add(txtTestDate);

        formPanel.add(new JLabel("Điểm thi:"));
        formPanel.add(txtScore);
        formPanel.add(new JLabel("Gợi ý Cấp độ:"));
        formPanel.add(txtSuggestedLevel);

        formPanel.add(new JLabel("Ghi chú:"));
        formPanel.add(txtNote);
        formPanel.add(new JLabel()); // Căn chỉnh
        formPanel.add(new JLabel()); // Căn chỉnh

        return formPanel;
    }

    private void updateSuggestedLevel() {
        try {
            String scoreText = txtScore.getText().trim();
            if (!scoreText.isEmpty()) {
                BigDecimal score = new BigDecimal(scoreText);
                CourseLevel suggested = testService.suggestLevel(score);
                if (suggested != null) {
                    txtSuggestedLevel.setText(suggested.name());
                } else {
                    txtSuggestedLevel.setText("");
                }
            } else {
                txtSuggestedLevel.setText("");
            }
        } catch (NumberFormatException ex) {
            txtSuggestedLevel.setText("Điểm không hợp lệ");
        }
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());

        String[] columns = {"Mã Bài Test", "Tên Học Viên", "Ngày Thi", "Điểm", "Level Đề Xuất", "Ghi Chú"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int selectedRow = table.getSelectedRow();
                Long testId = (Long) tableModel.getValueAt(selectedRow, 0);

                PlacementTest test = testService.getAllTests().stream()
                        .filter(t -> t.getTestId().equals(testId))
                        .findFirst()
                        .orElse(null);

                if (test != null) {
                    if (test.getStudent() != null) {
                        setSelectedItemById(cbStudent, test.getStudent().getStudentId());
                    }
                    txtTestDate.setText(test.getTestDate().format(dateFormatter));
                    txtScore.setText(test.getScore() != null ? test.getScore().toString() : "");
                    txtSuggestedLevel.setText(test.getSuggestedLevel() != null ? test.getSuggestedLevel().name() : "");
                    txtNote.setText(test.getNote() != null ? test.getNote() : "");
                }
            }
        });

        return tablePanel;
    }

    private JPanel createButtonPanel() {
        JPanel actionPanel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdd = new JButton("Thêm mới");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        actionPanel.add(buttonPanel, BorderLayout.EAST);

        setupButtonListeners();

        return actionPanel;
    }

    private void loadStudentsToComboBox() {
        cbStudent.removeAllItems();
        List<Student> students = studentService.getAllStudents();
        if (students != null) {
            for (Student s : students) {
                cbStudent.addItem(s);
            }
        }
    }

    private void setSelectedItemById(JComboBox<Student> comboBox, Long studentId) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            Student item = comboBox.getItemAt(i);
            if (item != null && item.getStudentId().equals(studentId)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void loadDataToTable(List<PlacementTest> tests) {
        tableModel.setRowCount(0);
        if (tests != null) {
            for (PlacementTest t : tests) {
                tableModel.addRow(new Object[]{
                        t.getTestId(),
                        t.getStudent() != null ? t.getStudent().getFullName() : "N/A",
                        t.getTestDate().format(dateFormatter),
                        t.getScore(),
                        t.getSuggestedLevel(),
                        t.getNote()
                });
            }
        }
    }

    private void setupButtonListeners() {
        btnAdd.addActionListener(e -> {
            try {
                Student student = (Student) cbStudent.getSelectedItem();
                if (student == null) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn học viên!");
                    return;
                }

                BigDecimal score = new BigDecimal(txtScore.getText().trim());
                LocalDate date = LocalDate.parse(txtTestDate.getText().trim(), dateFormatter);

                PlacementTest test = PlacementTest.builder()
                        .student(student)
                        .testDate(date)
                        .score(score)
                        .note(txtNote.getText().trim())
                        .build();

                // Service tự gọi suggestLevel
                testService.addTest(test);
                JOptionPane.showMessageDialog(this, "Thêm bài test thành công!");
                loadDataToTable(testService.getAllTests());
                clearForm();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Điểm phải là một số hợp lệ!");
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Ngày thi không hợp lệ! Định dạng: dd/MM/yyyy");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi thêm dữ liệu!");
            }
        });

        btnUpdate.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn bài test để cập nhật!");
                return;
            }

            try {
                Long testId = (Long) tableModel.getValueAt(selectedRow, 0);
                Student student = (Student) cbStudent.getSelectedItem();
                
                BigDecimal score = new BigDecimal(txtScore.getText().trim());
                LocalDate date = LocalDate.parse(txtTestDate.getText().trim(), dateFormatter);

                PlacementTest test = PlacementTest.builder()
                        .testId(testId)
                        .student(student)
                        .testDate(date)
                        .score(score)
                        .note(txtNote.getText().trim())
                        .build();

                testService.updateTest(test);
                JOptionPane.showMessageDialog(this, "Cập nhật bài test thành công!");
                loadDataToTable(testService.getAllTests());
                clearForm();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi cập nhật dữ liệu!");
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn bài test để xóa!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Long testId = (Long) tableModel.getValueAt(selectedRow, 0);
                if (testService.deleteTest(testId)) {
                    JOptionPane.showMessageDialog(this, "Xóa bài test thành công!");
                    loadDataToTable(testService.getAllTests());
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại!");
                }
            }
        });

        btnRefresh.addActionListener(e -> {
            clearForm();
            loadStudentsToComboBox();
            loadDataToTable(testService.getAllTests());
        });
    }

    private void clearForm() {
        if (cbStudent.getItemCount() > 0) cbStudent.setSelectedIndex(0);
        txtTestDate.setText(LocalDate.now().format(dateFormatter));
        txtScore.setText("");
        txtSuggestedLevel.setText("");
        txtNote.setText("");
        table.clearSelection();
    }
}
