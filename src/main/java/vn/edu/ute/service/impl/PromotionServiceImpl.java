package vn.edu.ute.service.impl;

import vn.edu.ute.model.Promotion;
import vn.edu.ute.model.enums.DiscountType;
import vn.edu.ute.repository.PromotionRepository;
import vn.edu.ute.repository.impl.PromotionRepositoryImpl;
import vn.edu.ute.service.PromotionService;
import java.math.BigDecimal;
import java.util.List;

public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository repo = new PromotionRepositoryImpl();

    @Override
    public List<Promotion> getAllPromotions() { return repo.findAll(); }

    @Override
    public List<Promotion> getActivePromotions() { return repo.findActivePromotions(); }

    @Override
    public Promotion savePromotion(Promotion promotion) throws Exception {
        if (promotion.getStartDate().isAfter(promotion.getEndDate())) {
            throw new Exception("Ngày bắt đầu không được lớn hơn ngày kết thúc!");
        }
        return repo.save(promotion);
    }

    @Override
    public boolean deletePromotion(Long id) { return repo.delete(id); }

    @Override
    public BigDecimal calculateDiscountedAmount(BigDecimal originalAmount, Promotion promo) {
        if (promo == null || originalAmount == null) return originalAmount;
        
        BigDecimal discountVal = promo.getDiscountValue();
        if (promo.getDiscountType() == DiscountType.Amount) {
            BigDecimal res = originalAmount.subtract(discountVal);
            return res.compareTo(BigDecimal.ZERO) > 0 ? res : BigDecimal.ZERO;
        } else {
            // Tính theo %
            BigDecimal discount = originalAmount.multiply(discountVal).divide(new BigDecimal(100));
            return originalAmount.subtract(discount);
        }
    }
}