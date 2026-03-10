package vn.edu.ute.repository.impl;

import vn.edu.ute.model.Invoice;
import vn.edu.ute.model.Payment;
import vn.edu.ute.repository.FinanceRepository;
import vn.edu.ute.util.TransactionManager;
import java.util.List;

public class FinanceRepositoryImpl implements FinanceRepository {
    private final TransactionManager tx = new TransactionManager();

    @Override
    public Invoice saveInvoice(Invoice invoice) throws Exception {
        return tx.runInTransaction(em -> {
            if (invoice.getInvoiceId() == null) {
                em.persist(invoice);
                return invoice;
            }
            return em.merge(invoice);
        });
    }

    @Override
    public Payment savePayment(Payment payment) throws Exception {
        return tx.runInTransaction(em -> {
            if (payment.getPaymentId() == null) {
                em.persist(payment);
                return payment;
            }
            return em.merge(payment);
        });
    }

    @Override
    public List<Payment> getPaymentsByInvoiceId(Long invoiceId) throws Exception {
        return tx.runInTransaction(em -> 
            em.createQuery("SELECT p FROM Payment p WHERE p.invoice.invoiceId = :id", Payment.class)
              .setParameter("id", invoiceId)
              .getResultList()
        );
    }

    // --- PHƯƠNG THỨC MỚI ĐƯỢC BỔ SUNG ---
    @Override
    public List<Payment> getAllPayments() throws Exception {
        return tx.runInTransaction(em -> 
            em.createQuery("SELECT p FROM Payment p JOIN FETCH p.invoice JOIN FETCH p.student", Payment.class)
              .getResultList()
        );
    }
}