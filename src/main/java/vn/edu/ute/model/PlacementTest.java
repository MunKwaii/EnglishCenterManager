package vn.edu.ute.model;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.ute.model.enums.CourseLevel;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "placement_tests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlacementTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_id")
    private Long testId;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "test_date", nullable = false)
    private LocalDate testDate = LocalDate.now();

    @Column(precision = 5, scale = 2)
    private BigDecimal score;

    @Enumerated(EnumType.STRING)
    @Column(name = "suggested_level",
            columnDefinition = "ENUM('Beginner','Intermediate','Advanced')")
    private CourseLevel suggestedLevel;

    @Column(length = 255)
    private String note;
}