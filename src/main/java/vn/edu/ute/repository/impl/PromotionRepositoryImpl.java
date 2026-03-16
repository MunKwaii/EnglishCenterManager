package vn.edu.ute.repository.impl;

import vn.edu.ute.model.Promotion;
import vn.edu.ute.repository.PromotionRepository;
import vn.edu.ute.util.TransactionManager;
import java.util.Collections;
import java.util.List;

public class PromotionRepositoryImpl implements PromotionRepository {
    private final TransactionManager tx = new TransactionManager();

    @Override
    public List<Promotion> findAll() {
        try {
            return tx.runInTransaction(em -> em.createQuery("SELECT p FROM Promotion p", Promotion.class).getResultList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public Promotion findById(Long id) {
        try {
            return tx.runInTransaction(em -> em.find(Promotion.class, id));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Promotion save(Promotion promotion) {
        try {
            return tx.runInTransaction(em -> {
                if (promotion.getPromotionId() == null) {
                    em.persist(promotion);
                    return promotion;
                }
                return em.merge(promotion);
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(Long id) {
        try {
            return tx.runInTransaction(em -> {
                Promotion p = em.find(Promotion.class, id);
                if (p != null) {
                    em.remove(p);
                    return true;
                }
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Promotion> findActivePromotions() {
        try {
            return tx.runInTransaction(em -> em.createQuery(
                    "SELECT p FROM Promotion p WHERE p.status = 'Active' AND p.startDate <= CURRENT_DATE AND p.endDate >= CURRENT_DATE", 
                    Promotion.class).getResultList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}