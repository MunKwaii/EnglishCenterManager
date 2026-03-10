package vn.edu.ute.service;

import vn.edu.ute.model.Invoice;
import vn.edu.ute.model.Payment;
import vn.edu.ute.model.enums.PaymentMethod;
import java.math.BigDecimal;
import java.util.Map;

public interface FinanceService {
    Payment processPayment(Invoice invoice, BigDecimal amount, PaymentMethod method, String refCode) throws Exception;
    
    // --- PHƯƠNG THỨC MỚI ĐƯỢC BỔ SUNG ---
    Map<PaymentMethod, BigDecimal> getRevenueByPaymentMethod() throws Exception;
}