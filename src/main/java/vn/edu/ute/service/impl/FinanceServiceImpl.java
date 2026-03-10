package vn.edu.ute.service.impl;

import vn.edu.ute.model.*;
import vn.edu.ute.model.enums.*;
import vn.edu.ute.repository.FinanceRepository;
import vn.edu.ute.repository.impl.FinanceRepositoryImpl;
import vn.edu.ute.service.FinanceService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FinanceServiceImpl implements FinanceService {
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
        
        // Sử dụng Stream và Lambda để tính tổng tiền đã trả
        BigDecimal totalPaid = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.Completed)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // So sánh chính xác BigDecimal
        if (totalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.Paid);
            financeRepo.saveInvoice(invoice);
        }

        return payment;
    }

    // --- TÍNH NĂNG MỚI: Thống kê doanh thu ---
    
    // Thống kê tổng doanh thu theo từng phương thức thanh toán
    public Map<PaymentMethod, BigDecimal> getRevenueByPaymentMethod() throws Exception {
        List<Payment> allPayments = financeRepo.getAllPayments(); // Cần đảm bảo Repo có hàm này
        
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