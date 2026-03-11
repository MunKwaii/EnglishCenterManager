package vn.edu.ute.view;

import vn.edu.ute.model.Invoice;
import vn.edu.ute.model.Payment;
import vn.edu.ute.model.enums.PaymentMethod;
import vn.edu.ute.service.FinanceService;
import vn.edu.ute.service.impl.FinanceServiceImpl;
import vn.edu.ute.util.CsvExportUtil;
import vn.edu.ute.util.ValidatorUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class FinancePanel extends JPanel {
    private final FinanceService financeService = new FinanceServiceImpl();
    
    // UI Components
    private JTextField txtInvoiceId, txtAmount, txtRefCode, txtSearch;
    private JComboBox<PaymentMethod> cbMethod;
    private JButton btnPay, btnExport;
    private JLabel lblStatus;
    
    // Table Components
    private JTable invoiceTable;
    private DefaultTableModel tableModel;

    // Trình định dạng tiền tệ (VD: 6,500,000)
    private final DecimalFormat currencyFormat = new DecimalFormat("#,###");

    public FinancePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(initTopPanel(), BorderLayout.NORTH);
        add(initTablePanel(), BorderLayout.CENTER); 
        add(initFormPanel(), BorderLayout.SOUTH);   
        
        // Tải dữ liệu thật từ DB lần đầu tiên
        loadDataToTable(); 

        // Tự động Refresh dữ liệu mỗi khi người dùng click mở Tab này
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                // Làm sạch form khi quay lại tab này
                txtInvoiceId.setText("");
                txtAmount.setText("");
                txtRefCode.setText("");
                cbMethod.setSelectedIndex(0);
                invoiceTable.clearSelection();
                lblStatus.setText(" ");
                
                // Tải lại bảng Hóa đơn (để lấy các hóa đơn mới vừa được ghi danh)
                loadDataToTable();
            }
        });
    }

    private JPanel initTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Tìm kiếm Sinh viên:"));
        
        txtSearch = new JTextField(20);
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterData(); }
            public void removeUpdate(DocumentEvent e) { filterData(); }
            public void changedUpdate(DocumentEvent e) { filterData(); }
        });
        
        btnExport = new JButton("Xuất CSV Doanh Thu");
        btnExport.addActionListener(e -> exportData());

        topPanel.add(txtSearch);
        topPanel.add(btnExport);
        return topPanel;
    }

    private JPanel initTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Danh sách Hóa đơn chờ thanh toán"));

        String[] columns = {"ID Hóa đơn", "Sinh viên", "Tổng tiền (VNĐ)", "Ngày lập", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        invoiceTable = new JTable(tableModel);
        invoiceTable.setRowHeight(25);
        
        invoiceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && invoiceTable.getSelectedRow() != -1) {
                int row = invoiceTable.getSelectedRow();
                txtInvoiceId.setText(tableModel.getValueAt(row, 0).toString());
                
                // Lấy số tiền từ bảng (đã có dấu phẩy) và gán xuống Form
                String amountStr = tableModel.getValueAt(row, 2).toString();
                txtAmount.setText(amountStr);
                
                lblStatus.setText(" ");
            }
        });

        tablePanel.add(new JScrollPane(invoiceTable), BorderLayout.CENTER);
        return tablePanel;
    }

    private JPanel initFormPanel() {
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setBorder(BorderFactory.createTitledBorder("Thanh toán Hóa đơn"));

        JPanel paymentForm = new JPanel(new GridLayout(2, 4, 15, 10));
        paymentForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtInvoiceId = new JTextField();
        txtInvoiceId.setEditable(false); 
        txtAmount = new JTextField();
        txtRefCode = new JTextField();
        cbMethod = new JComboBox<>(PaymentMethod.values());
        
        lblStatus = new JLabel(" ");
        lblStatus.setForeground(Color.RED);

        // Xử lý bật/tắt ô Mã giao dịch
        txtRefCode.setEnabled(false);
        txtRefCode.setBackground(new Color(240, 240, 240));

        cbMethod.addActionListener(e -> {
            PaymentMethod method = (PaymentMethod) cbMethod.getSelectedItem();
            if (method == PaymentMethod.Cash) {
                txtRefCode.setEnabled(false);
                txtRefCode.setText(""); 
                txtRefCode.setBackground(new Color(240, 240, 240));
            } else {
                txtRefCode.setEnabled(true);
                txtRefCode.setBackground(Color.WHITE);
            }
        });

        // Định dạng ô Số tiền (Auto-format)
        txtAmount.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtAmount.setText(txtAmount.getText().replace(",", ""));
            }

            @Override
            public void focusLost(FocusEvent e) {
                String text = txtAmount.getText().replace(",", "").trim();
                if (!text.isEmpty()) {
                    try {
                        double val = Double.parseDouble(text);
                        txtAmount.setText(currencyFormat.format(val));
                    } catch (NumberFormatException ignored) {}
                }
            }
        });

        paymentForm.add(new JLabel("ID Hóa đơn:")); paymentForm.add(txtInvoiceId);
        paymentForm.add(new JLabel("Số tiền (VNĐ):")); paymentForm.add(txtAmount);
        
        paymentForm.add(new JLabel("Hình thức:")); paymentForm.add(cbMethod);
        paymentForm.add(new JLabel("Mã giao dịch:")); paymentForm.add(txtRefCode);
        
        btnPay = new JButton("Xác nhận Thanh toán");
        btnPay.addActionListener(e -> processPaymentAsync());
        
        JPanel bottomAction = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomAction.add(lblStatus);
        bottomAction.add(btnPay);

        formContainer.add(paymentForm, BorderLayout.CENTER);
        formContainer.add(bottomAction, BorderLayout.SOUTH);
        
        return formContainer;
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        try {
            List<Invoice> invoices = financeService.getUnpaidInvoices();
            if (invoices != null) {
                invoices.forEach(inv -> {
                    tableModel.addRow(new Object[]{
                        inv.getInvoiceId(),
                        inv.getStudent() != null ? inv.getStudent().getFullName() : "N/A",
                        currencyFormat.format(inv.getTotalAmount()), 
                        inv.getIssueDate(),
                        inv.getStatus()
                    });
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterData() {
        String keyword = txtSearch.getText().toLowerCase();
        try {
            List<Invoice> invoices = financeService.getUnpaidInvoices();
            tableModel.setRowCount(0);
            invoices.stream()
                    .filter(inv -> inv.getStudent() != null && inv.getStudent().getFullName().toLowerCase().contains(keyword))
                    .forEach(inv -> tableModel.addRow(new Object[]{
                            inv.getInvoiceId(), 
                            inv.getStudent().getFullName(), 
                            currencyFormat.format(inv.getTotalAmount()), 
                            inv.getIssueDate(), 
                            inv.getStatus()
                    }));
        } catch (Exception ignored) {}
    }

    private void exportData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file báo cáo CSV");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String path = fileToSave.getAbsolutePath();
            if (!path.endsWith(".csv")) path += ".csv";
            
            try {
                List<Payment> realPayments = financeService.getAllPayments(); 
                CsvExportUtil.exportPaymentsToCsv(realPayments, path);
                JOptionPane.showMessageDialog(this, "Xuất file thành công: " + path);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void processPaymentAsync() {
        String rawAmount = txtAmount.getText().replace(",", "").trim();

        if (!ValidatorUtil.isNotEmpty.test(txtInvoiceId.getText())) {
            lblStatus.setText("Vui lòng chọn Hóa đơn từ bảng trên!");
            return;
        }
        if (!ValidatorUtil.isValidAmount.test(rawAmount)) {
            lblStatus.setText("Số tiền không hợp lệ!");
            return;
        }

        PaymentMethod method = (PaymentMethod) cbMethod.getSelectedItem();
        String refCode = txtRefCode.getText().trim();

        if (method != PaymentMethod.Cash && refCode.isEmpty()) {
            lblStatus.setText("Vui lòng nhập Mã giao dịch cho hình thức chuyển khoản/thẻ!");
            return;
        }

        lblStatus.setText("Đang xử lý...");
        btnPay.setEnabled(false);

        BigDecimal amount = new BigDecimal(rawAmount);
        Long invoiceId = Long.parseLong(txtInvoiceId.getText());

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Invoice realInvoice = financeService.getInvoiceById(invoiceId);
                if (realInvoice == null) {
                    throw new Exception("Hóa đơn không tồn tại!");
                }
                financeService.processPayment(realInvoice, amount, method, refCode);
                return true;
            }

            @Override
            protected void done() {
                btnPay.setEnabled(true);
                try {
                    get(); 
                    lblStatus.setForeground(new Color(0, 153, 0));
                    lblStatus.setText("Thanh toán thành công!");
                    JOptionPane.showMessageDialog(FinancePanel.this, "Thanh toán thành công!");
                    
                    txtInvoiceId.setText("");
                    txtAmount.setText("");
                    txtRefCode.setText("");
                    cbMethod.setSelectedIndex(0); 
                    invoiceTable.clearSelection();
                    
                    loadDataToTable();
                } catch (Exception ex) {
                    lblStatus.setForeground(Color.RED);
                    lblStatus.setText("Lỗi: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}