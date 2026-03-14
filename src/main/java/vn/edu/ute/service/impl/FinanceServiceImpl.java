package vn.edu.ute.service.impl;

import vn.edu.ute.model.*;
import vn.edu.ute.model.enums.*;
import vn.edu.ute.repository.FinanceRepository;
import vn.edu.ute.repository.impl.FinanceRepositoryImpl;
import vn.edu.ute.service.FinanceService;

import java.math.BigDecimal;
import java.time.LocalDateTime; // Đảm bảo đã import thư viện này
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FinanceServiceImpl implements FinanceService {
    private final FinanceRepository financeRepo = new FinanceRepositoryImpl();

    @Override
    public Invoice getInvoiceById(Long invoiceId) throws Exception {
        return financeRepo.getInvoiceById(invoiceId);
    }

    @Override
    public List<Invoice> getUnpaidInvoices() throws Exception {
        return financeRepo.getUnpaidInvoices();
    }

    @Override
    public List<Invoice> getAllInvoices() throws Exception {
        return financeRepo.getAllInvoices();
    }

    @Override
    public List<Invoice> getInvoicesByStudentId(Long studentId) throws Exception {
        return financeRepo.getInvoicesByStudentId(studentId);
    }

    @Override
    public List<Payment> getAllPayments() throws Exception {
        return financeRepo.getAllPayments();
    }

    @Override
    public List<Payment> getPaymentsByInvoiceId(Long invoiceId) throws Exception {
        return financeRepo.getPaymentsByInvoiceId(invoiceId);
    }

    @Override
    public Payment processPayment(Invoice invoice, BigDecimal amount, PaymentMethod method, String refCode) throws Exception {
        // ĐÃ SỬA LỖI: Bổ sung thêm dòng .paymentDate(LocalDateTime.now())
        Payment payment = Payment.builder()
                .student(invoice.getStudent())
                .invoice(invoice)
                .amount(amount)
                .paymentDate(LocalDateTime.now()) // <--- CHÍNH LÀ DÒNG NÀY
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

        // Nếu tổng tiền khách trả đã bằng hoặc lớn hơn tổng hóa đơn -> Cập nhật hóa đơn thành Đã thanh toán
        if (totalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.Paid);
            financeRepo.saveInvoice(invoice);
        }

        return payment;
    }

    @Override
    public Map<PaymentMethod, BigDecimal> getRevenueByPaymentMethod() throws Exception {
        List<Payment> allPayments = financeRepo.getAllPayments(); 
        
        return allPayments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.Completed)
                .collect(Collectors.groupingBy(
                        Payment::getPaymentMethod,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Payment::getAmount,
                                BigDecimal::add
                        )
                ));
    }
}