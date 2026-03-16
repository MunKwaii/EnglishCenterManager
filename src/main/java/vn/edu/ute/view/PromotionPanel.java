package vn.edu.ute.view;

import vn.edu.ute.model.Promotion;
import vn.edu.ute.model.enums.DiscountType;
import vn.edu.ute.model.enums.Status;
import vn.edu.ute.service.PromotionService;
import vn.edu.ute.service.impl.PromotionServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Date;

public class PromotionPanel extends JPanel {
    private final PromotionService promoService = new PromotionServiceImpl();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtName, txtValue;
    private JComboBox<DiscountType> cbType;
    private JComboBox<Status> cbStatus;
    private JSpinner spStart, spEnd;

    public PromotionPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        loadData();
    }

    private JPanel createFormPanel() {
        // Container chính bọc cả Form và Nút bấm
        JPanel container = new JPanel(new BorderLayout(0, 10));
        container.setBorder(BorderFactory.createTitledBorder("Quản lý Khuyến mãi"));

        // Form lưới: 4 hàng, 4 cột (chứa 8 cặp Label - Input)
        JPanel form = new JPanel(new GridLayout(4, 4, 15, 10));
        form.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        txtId = new JTextField(); txtId.setEditable(false);
        txtName = new JTextField();
        txtValue = new JTextField();
        cbType = new JComboBox<>(DiscountType.values());
        cbStatus = new JComboBox<>(Status.values());
        
        spStart = new JSpinner(new SpinnerDateModel());
        spStart.setEditor(new JSpinner.DateEditor(spStart, "dd/MM/yyyy"));
        spEnd = new JSpinner(new SpinnerDateModel());
        spEnd.setEditor(new JSpinner.DateEditor(spEnd, "dd/MM/yyyy"));

        // Hàng 1
        form.add(new JLabel("ID:")); form.add(txtId);
        form.add(new JLabel("Tên/Mã KM:")); form.add(txtName);

        // Hàng 2
        form.add(new JLabel("Loại giảm giá:")); form.add(cbType);
        form.add(new JLabel("Giá trị giảm:")); form.add(txtValue);

        // Hàng 3
        form.add(new JLabel("Ngày bắt đầu:")); form.add(spStart);
        form.add(new JLabel("Ngày kết thúc:")); form.add(spEnd);

        // Hàng 4
        form.add(new JLabel("Trạng thái:")); form.add(cbStatus);
        form.add(new JLabel("")); form.add(new JLabel("")); // 2 ô trống để lấp đầy Grid cho đẹp

        // Tách riêng cụm nút bấm xuống dưới cùng, căn phải
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnAdd = new JButton("Thêm mới");
        btnAdd.addActionListener(e -> savePromo(null));
        JButton btnUpdate = new JButton("Cập nhật");
        btnUpdate.addActionListener(e -> {
            if (txtId.getText().isEmpty()) return;
            savePromo(Long.parseLong(txtId.getText()));
        });
        JButton btnDelete = new JButton("Xóa");
        btnDelete.addActionListener(e -> deletePromo());
        JButton btnClear = new JButton("Làm mới");
        btnClear.addActionListener(e -> clearForm());
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        container.add(form, BorderLayout.CENTER);
        container.add(btnPanel, BorderLayout.SOUTH);

        return container;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"ID", "Tên KM", "Loại", "Giá trị", "Bắt đầu", "Kết thúc", "Trạng thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int r = table.getSelectedRow();
                txtId.setText(tableModel.getValueAt(r, 0).toString());
                txtName.setText(tableModel.getValueAt(r, 1).toString());
                cbType.setSelectedItem(DiscountType.valueOf(tableModel.getValueAt(r, 2).toString()));
                txtValue.setText(tableModel.getValueAt(r, 3).toString());
                cbStatus.setSelectedItem(Status.valueOf(tableModel.getValueAt(r, 6).toString()));
            }
        });
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void savePromo(Long id) {
        try {
            Promotion p = Promotion.builder()
                    .promotionId(id)
                    .promoName(txtName.getText())
                    .discountType((DiscountType) cbType.getSelectedItem())
                    .discountValue(new BigDecimal(txtValue.getText()))
                    .startDate(((Date) spStart.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                    .endDate(((Date) spEnd.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                    .status((Status) cbStatus.getSelectedItem())
                    .build();
            promoService.savePromotion(p);
            JOptionPane.showMessageDialog(this, "Thành công!");
            clearForm(); loadData();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
    }

    private void deletePromo() {
        if (txtId.getText().isEmpty()) return;
        try {
            Long id = Long.parseLong(txtId.getText());
            boolean ok = promoService.deletePromotion(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                clearForm(); loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa khuyến mãi này!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa!");
        }
    }

    private void clearForm() {
        txtId.setText(""); txtName.setText(""); txtValue.setText("");
        table.clearSelection();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        promoService.getAllPromotions().forEach(p -> tableModel.addRow(new Object[]{
                p.getPromotionId(), p.getPromoName(), p.getDiscountType(), p.getDiscountValue(),
                p.getStartDate(), p.getEndDate(), p.getStatus()
        }));
    }
}