package vn.edu.ute.service;

import vn.edu.ute.model.Invoice;
import vn.edu.ute.model.Payment;
import vn.edu.ute.model.enums.PaymentMethod;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface FinanceService {
    Invoice getInvoiceById(Long invoiceId) throws Exception;
    List<Invoice> getUnpaidInvoices() throws Exception;
    List<Invoice> getAllInvoices() throws Exception;
    List<Invoice> getInvoicesByStudentId(Long studentId) throws Exception;
    List<Payment> getPaymentsByInvoiceId(Long invoiceId) throws Exception;
    Payment processPayment(Invoice invoice, BigDecimal amount, PaymentMethod method, String refCode) throws Exception;
    List<Payment> getAllPayments() throws Exception;
    Map<PaymentMethod, BigDecimal> getRevenueByPaymentMethod() throws Exception;
}