package vn.edu.ute.service;
import vn.edu.ute.model.Promotion;
import java.math.BigDecimal;
import java.util.List;

public interface PromotionService {
    List<Promotion> getAllPromotions();
    List<Promotion> getActivePromotions();
    Promotion savePromotion(Promotion promotion) throws Exception;
    boolean deletePromotion(Long id);
    // Hàm logic tính tiền hóa đơn sau khuyến mãi
    BigDecimal calculateDiscountedAmount(BigDecimal originalAmount, Promotion promo);
}