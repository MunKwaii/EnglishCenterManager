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
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

public class FinancePanel extends JPanel {
    private final FinanceService financeService = new FinanceServiceImpl();
    private JTextField txtInvoiceId, txtAmount, txtRefCode, txtSearch;
    private JComboBox<PaymentMethod> cbMethod;
    private JButton btnPay, btnExport;
    private JLabel lblStatus; // Hiển thị trạng thái/lỗi trực tiếp trên UI

    public FinancePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initTopPanel();
        initFormPanel();
    }

    private void initTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Tìm kiếm Hóa đơn:"));
        
        txtSearch = new JTextField(20);
        // Tính năng Search-as-you-type (Tìm kiếm ngay khi gõ)
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterData(); }
            public void removeUpdate(DocumentEvent e) { filterData(); }
            public void changedUpdate(DocumentEvent e) { filterData(); }
        });
        
        btnExport = new JButton("Xuất CSV");
        btnExport.addActionListener(e -> exportData());

        topPanel.add(txtSearch);
        topPanel.add(btnExport);
        add(topPanel, BorderLayout.NORTH);
    }

    private void initFormPanel() {
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setBorder(BorderFactory.createTitledBorder("Thanh toán Hóa đơn"));

        JPanel paymentForm = new JPanel(new GridLayout(6, 2, 10, 10));

        txtInvoiceId = new JTextField();
        txtAmount = new JTextField();
        txtRefCode = new JTextField();
        cbMethod = new JComboBox<>(PaymentMethod.values());
        lblStatus = new JLabel(" ");
        lblStatus.setForeground(Color.RED);

        paymentForm.add(new JLabel("ID Hóa đơn cần thanh toán:")); paymentForm.add(txtInvoiceId);
        paymentForm.add(new JLabel("Số tiền thanh toán (VNĐ):")); paymentForm.add(txtAmount);
        paymentForm.add(new JLabel("Hình thức:")); paymentForm.add(cbMethod);
        paymentForm.add(new JLabel("Mã giao dịch (Nếu có):")); paymentForm.add(txtRefCode);
        
        btnPay = new JButton("Xác nhận Thanh toán");
        btnPay.addActionListener(e -> processPaymentAsync()); // Sử dụng hàm Async
        
        paymentForm.add(lblStatus); // Chỗ trống hiện lỗi
        paymentForm.add(btnPay);

        formContainer.add(paymentForm, BorderLayout.NORTH);
        add(formContainer, BorderLayout.CENTER);
    }

    private void filterData() {
        // Lambda để xử lý logic tìm kiếm (sẽ áp dụng khi bạn có JTable danh sách hóa đơn)
        String keyword = txtSearch.getText().toLowerCase();
        System.out.println("Đang tìm kiếm: " + keyword);
    }

    private void exportData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file báo cáo CSV");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String path = fileToSave.getAbsolutePath();
            if (!path.endsWith(".csv")) path += ".csv";
            
            try {
                // Giả lập lấy danh sách Payment từ DB (Bạn cần dùng financeRepo thực tế)
                java.util.List<Payment> dummyList = new ArrayList<>(); 
                CsvExportUtil.exportPaymentsToCsv(dummyList, path);
                JOptionPane.showMessageDialog(this, "Xuất file thành công: " + path);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Xử lý thanh toán sử dụng Thread-safe (SwingWorker)
    private void processPaymentAsync() {
        // 1. Validation bằng Lambda (Sử dụng ValidatorUtil đã tạo)
        if (!ValidatorUtil.isNotEmpty.test(txtInvoiceId.getText())) {
            lblStatus.setText("Vui lòng nhập ID Hóa đơn!");
            return;
        }
        if (!ValidatorUtil.isValidAmount.test(txtAmount.getText())) {
            lblStatus.setText("Số tiền không hợp lệ!");
            return;
        }

        lblStatus.setText("Đang xử lý...");
        btnPay.setEnabled(false); // Khóa nút tránh click đúp

        BigDecimal amount = new BigDecimal(txtAmount.getText());
        PaymentMethod method = (PaymentMethod) cbMethod.getSelectedItem();
        String refCode = txtRefCode.getText();
        String invoiceIdStr = txtInvoiceId.getText();

        // 2. Sử dụng SwingWorker để gọi DB dưới Background, không làm đơ giao diện
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // Giả lập lấy Invoice từ DB
                Invoice dummyInvoice = new Invoice(); 
                dummyInvoice.setInvoiceId(Long.parseLong(invoiceIdStr));
                dummyInvoice.setTotalAmount(new BigDecimal("1000000")); // Mock data
                
                financeService.processPayment(dummyInvoice, amount, method, refCode);
                
                // Giả lập thời gian delay mạng/database
                Thread.sleep(1000); 
                return true;
            }

            @Override
            protected void done() {
                btnPay.setEnabled(true);
                try {
                    get(); // Kiểm tra xem có Exception nào quăng ra từ doInBackground không
                    lblStatus.setForeground(new Color(0, 153, 0)); // Màu xanh lá
                    lblStatus.setText("Thanh toán thành công!");
                    JOptionPane.showMessageDialog(FinancePanel.this, "Thanh toán thành công!");
                    
                    // Xóa form
                    txtInvoiceId.setText("");
                    txtAmount.setText("");
                    txtRefCode.setText("");
                } catch (Exception ex) {
                    lblStatus.setForeground(Color.RED);
                    lblStatus.setText("Lỗi hệ thống khi thanh toán!");
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }
}