package vn.edu.ute.repository;

import vn.edu.ute.model.Invoice;
import vn.edu.ute.model.Payment;
import java.util.List;

public interface FinanceRepository {
    Invoice saveInvoice(Invoice invoice) throws Exception;
    Invoice getInvoiceById(Long invoiceId) throws Exception;
    List<Invoice> getUnpaidInvoices() throws Exception;
    
    Payment savePayment(Payment payment) throws Exception;
    List<Payment> getPaymentsByInvoiceId(Long invoiceId) throws Exception;
    List<Payment> getAllPayments() throws Exception;
}