package vn.edu.ute.service.impl;

import vn.edu.ute.model.*;
import vn.edu.ute.model.enums.*;
import vn.edu.ute.repository.FinanceRepository;
import vn.edu.ute.repository.impl.FinanceRepositoryImpl;
import vn.edu.ute.service.FinanceService;
import java.math.BigDecimal;
import java.util.List;

public class FinanceServiceImpl implements FinanceService {
    // Sửa lỗi khởi tạo bằng cách gọi class Impl
    private final FinanceRepository financeRepo = new FinanceRepositoryImpl();

    @Override
    public Payment processPayment(Invoice invoice, BigDecimal amount, PaymentMethod method, String refCode) throws Exception {
        Payment payment = Payment.builder()
                .student(invoice.getStudent())
                .invoice(invoice)
                .amount(amount)
                .paymentMethod(method)
                .status(PaymentStatus.Completed)
                .referenceCode(refCode)
                .build();
        
        financeRepo.savePayment(payment);

        List<Payment> payments = financeRepo.getPaymentsByInvoiceId(invoice.getInvoiceId());
        BigDecimal totalPaid = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.Completed)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.Paid);
            financeRepo.saveInvoice(invoice);
        }

        return payment;
    }
}