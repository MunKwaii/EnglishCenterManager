package vn.edu.ute.view;

import vn.edu.ute.model.Invoice;
import vn.edu.ute.model.Payment;
import vn.edu.ute.model.enums.PaymentMethod;
import vn.edu.ute.service.FinanceService;
import vn.edu.ute.service.impl.FinanceServiceImpl;
import vn.edu.ute.util.UserSession;
import vn.edu.ute.util.ValidatorUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class StudentFeeDialog extends JDialog {
    private FinanceService financeService = new FinanceServiceImpl();
    private JTable feeTable;
    private DefaultTableModel tableModel;
    private Long currentStudentId;
    
    // UI components cho form thanh toán
    private JTextField txtInvoiceId, txtAmount, txtRefCode;
    private JComboBox<PaymentMethod> cbMethod;
    private JButton btnPay;
    private JLabel lblStatus;
    private final DecimalFormat currencyFormat = new DecimalFormat("#,###");

    public StudentFeeDialog(Window owner) {
        super(owner, "Thông tin học phí", ModalityType.APPLICATION_MODAL);
        
        // Retrieve logged in student
        currentStudentId = UserSession.getStudentId();
        if (currentStudentId == null) {
            JOptionPane.showMessageDialog(owner, "Vui lòng đăng nhập với tư cách Học viên để xem học phí.");
            dispose();
            return;
        }

        initComponents();
        loadFeeData();
    }

    private void initComponents() {
        setSize(850, 650);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("Lịch sử nộp Học phí & Hoá đơn");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Table setup
        String[] columns = {"Mã Hóa đơn", "Ngày lập", "Tổng tiền", "Ghi chú", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };

        feeTable = new JTable(tableModel);
        feeTable.setRowHeight(30);
        feeTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        feeTable.setFont(new Font("Arial", Font.PLAIN, 14));

        feeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && feeTable.getSelectedRow() != -1) {
                int row = feeTable.getSelectedRow();
                String invoiceIdStr = tableModel.getValueAt(row, 0).toString().replace("HD-", "");
                txtInvoiceId.setText(invoiceIdStr);
                
                String amountStr = tableModel.getValueAt(row, 2).toString().replaceAll("[^\\d]", "");
                try {
                    double amount = Double.parseDouble(amountStr.isEmpty() ? "0" : amountStr);
                    txtAmount.setText(currencyFormat.format(amount));
                } catch (Exception ex) {
                    txtAmount.setText("0");
                }
                
                String status = tableModel.getValueAt(row, 4).toString();
                
                if (status.equals("Đã thanh toán")) {
                    try {
                        Long invoiceId = Long.parseLong(invoiceIdStr);
                        List<Payment> payments = financeService.getPaymentsByInvoiceId(invoiceId);
                        if (payments != null && !payments.isEmpty()) {
                            Payment lastPayment = payments.get(payments.size() - 1);
                            cbMethod.setSelectedItem(lastPayment.getPaymentMethod());
                            if (lastPayment.getReferenceCode() != null) {
                                txtRefCode.setText(lastPayment.getReferenceCode());
                            } else {
                                txtRefCode.setText("");
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (btnPay != null) btnPay.setEnabled(false);
                    if (cbMethod != null) cbMethod.setEnabled(false);
                    if (txtRefCode != null) txtRefCode.setEditable(false);
                } else {
                    if (cbMethod != null) {
                        cbMethod.setSelectedIndex(0);
                        cbMethod.setEnabled(true);
                    }
                    if (txtRefCode != null) {
                        txtRefCode.setText("");
                        txtRefCode.setEditable(true);
                    }
                    if (btnPay != null) btnPay.setEnabled(true);
                }
                
                if (lblStatus != null) lblStatus.setText(" ");
            }
        });

        // Center align specific columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        feeTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        feeTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        feeTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(feeTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(initFormPanel(), BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Arial", Font.BOLD, 14));
        btnClose.addActionListener(e -> dispose());
        bottomPanel.add(btnClose);

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel initFormPanel() {
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(0, 15, 15, 15),
            BorderFactory.createTitledBorder("Mục Thanh toán Hóa đơn")
        ));

        JPanel paymentForm = new JPanel(new GridLayout(2, 4, 15, 10));
        paymentForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtInvoiceId = new JTextField();
        txtInvoiceId.setEditable(false); 
        txtAmount = new JTextField();
        txtAmount.setEditable(false); // Sinh viên không được tự tiện sửa giá tiền
        txtRefCode = new JTextField();
        cbMethod = new JComboBox<>(PaymentMethod.values());
        
        lblStatus = new JLabel(" ");
        lblStatus.setForeground(Color.RED);

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

        paymentForm.add(new JLabel("ID Hóa đơn:")); paymentForm.add(txtInvoiceId);
        paymentForm.add(new JLabel("Số tiền (VNĐ):")); paymentForm.add(txtAmount);
        
        paymentForm.add(new JLabel("Hình thức:")); paymentForm.add(cbMethod);
        paymentForm.add(new JLabel("Mã giao dịch:")); paymentForm.add(txtRefCode);
        
        btnPay = new JButton("Xác nhận Thanh toán");
        btnPay.setEnabled(false);
        btnPay.setBackground(new Color(46, 204, 113));
        btnPay.setForeground(Color.WHITE);
        btnPay.setFocusPainted(false);
        btnPay.setFont(new Font("Arial", Font.BOLD, 14));
        btnPay.addActionListener(e -> processPaymentAsync());
        
        JPanel bottomAction = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomAction.add(lblStatus);
        bottomAction.add(btnPay);

        formContainer.add(paymentForm, BorderLayout.CENTER);
        formContainer.add(bottomAction, BorderLayout.SOUTH);
        
        return formContainer;
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
                try {
                    get(); 
                    lblStatus.setForeground(new Color(0, 153, 0));
                    lblStatus.setText("Thanh toán thành công!");
                    JOptionPane.showMessageDialog(StudentFeeDialog.this, "Thanh toán thành công!");
                    
                    txtInvoiceId.setText("");
                    txtAmount.setText("");
                    txtRefCode.setText("");
                    cbMethod.setSelectedIndex(0); 
                    feeTable.clearSelection();
                    
                    loadFeeData();
                } catch (Exception ex) {
                    lblStatus.setForeground(Color.RED);
                    lblStatus.setText("Lỗi: " + ex.getMessage());
                    btnPay.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void loadFeeData() {
        try {
            List<Invoice> invoices = financeService.getInvoicesByStudentId(currentStudentId);
            tableModel.setRowCount(0);

            if (invoices == null || invoices.isEmpty()) {
                tableModel.addRow(new Object[]{"", "", "Chưa có dữ liệu học phí", "", ""});
                return;
            }

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale.Builder().setLanguage("vi").setRegion("VN").build());

            for (Invoice invoice : invoices) {
                String dateStr = invoice.getIssueDate() != null ? invoice.getIssueDate().format(dateFormatter) : "";
                String amountStr = invoice.getTotalAmount() != null ? currencyFormat.format(invoice.getTotalAmount()) : "0 đ";
                String note = invoice.getNote() != null ? invoice.getNote() : "";

                tableModel.addRow(new Object[]{
                        "HD-" + invoice.getInvoiceId(),
                        dateStr,
                        amountStr,
                        note,
                        translateStatus(invoice.getStatus().name())
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu học phí: " + ex.getMessage());
        }
    }

    private String translateStatus(String status) {
        switch (status) {
            case "Paid":
                return "Đã thanh toán";
            case "Issued":
                return "Chưa thanh toán";
            case "Draft":
                return "Bản nháp";
            case "Cancelled":
                return "Đã hủy";
            default:
                return status;
        }
    }
}
