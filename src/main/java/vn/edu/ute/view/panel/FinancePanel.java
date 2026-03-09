package vn.edu.ute.view.panel;

import vn.edu.ute.model.Invoice;
import vn.edu.ute.model.enums.PaymentMethod;
import vn.edu.ute.service.FinanceService;
import vn.edu.ute.service.impl.FinanceServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class FinancePanel extends JPanel {
    private final FinanceService financeService = new FinanceServiceImpl();
    private JTextField txtInvoiceId, txtAmount, txtRefCode;
    private JComboBox<PaymentMethod> cbMethod;

    public FinancePanel() {
        setLayout(new BorderLayout());
        
        JPanel paymentForm = new JPanel(new GridLayout(5, 2, 10, 10));
        paymentForm.setBorder(BorderFactory.createTitledBorder("Thanh toán Hóa đơn"));

        txtInvoiceId = new JTextField(); // Thực tế nên là 1 JTable chọn hóa đơn
        txtAmount = new JTextField();
        txtRefCode = new JTextField();
        cbMethod = new JComboBox<>(PaymentMethod.values());

        paymentForm.add(new JLabel("ID Hóa đơn cần thanh toán:")); paymentForm.add(txtInvoiceId);
        paymentForm.add(new JLabel("Số tiền thanh toán (VNĐ):")); paymentForm.add(txtAmount);
        paymentForm.add(new JLabel("Hình thức:")); paymentForm.add(cbMethod);
        paymentForm.add(new JLabel("Mã giao dịch (Nếu có):")); paymentForm.add(txtRefCode);

        JButton btnPay = new JButton("Xác nhận Thanh toán");
        btnPay.addActionListener(e -> processPayment());
        
        paymentForm.add(new JLabel());
        paymentForm.add(btnPay);

        add(paymentForm, BorderLayout.NORTH);
    }

    private void processPayment() {
        try {
            // Cần lấy Invoice object từ DB dựa vào txtInvoiceId.getText(), ở đây giả lập:
            // Invoice invoice = ... 
            BigDecimal amount = new BigDecimal(txtAmount.getText());
            PaymentMethod method = (PaymentMethod) cbMethod.getSelectedItem();
            
            // financeService.processPayment(invoice, amount, method, txtRefCode.getText());
            JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: Kiểm tra lại số tiền nhập vào!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}