package vn.edu.ute.repository;
import vn.edu.ute.model.Promotion;
import java.util.List;

public interface PromotionRepository {
    List<Promotion> findAll();
    Promotion findById(Long id);
    Promotion save(Promotion promotion);
    boolean delete(Long id);
    List<Promotion> findActivePromotions();
}