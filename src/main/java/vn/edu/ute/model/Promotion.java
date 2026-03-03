package vn.edu.ute.model;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.ute.model.enums.DiscountType;
import vn.edu.ute.model.enums.Status;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Long promotionId;

    @Column(name = "promo_name", nullable = false, length = 150)
    private String promoName;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false,
            columnDefinition = "ENUM('Percent','Amount') DEFAULT 'Percent'")
    private DiscountType discountType = DiscountType.Percent;

    @Column(name = "discount_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountValue = BigDecimal.ZERO;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('Active','Inactive') DEFAULT 'Active'")
    private Status status = Status.Active;

    // Quan hệ một khuyến mãi có thể áp dụng cho nhiều hóa đơn
    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY)
    private List<Invoice> invoices;
}