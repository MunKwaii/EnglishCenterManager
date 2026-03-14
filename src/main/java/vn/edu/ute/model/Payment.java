package vn.edu.ute.model;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.ute.model.enums.PaymentMethod;
import vn.edu.ute.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_date", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime paymentDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false,
            columnDefinition = "ENUM('Cash','Bank','Momo','ZaloPay','Card','Other') DEFAULT 'Cash'")
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.Cash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false,
            columnDefinition = "ENUM('Pending','Completed','Failed','Refunded') DEFAULT 'Completed'")
    @Builder.Default
    private PaymentStatus status = PaymentStatus.Completed;

    @Column(name = "reference_code", length = 100)
    private String referenceCode;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}