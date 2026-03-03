package vn.edu.ute.model;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.ute.model.enums.CourseLevel;
import vn.edu.ute.model.enums.DurationUnit;
import vn.edu.ute.model.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Chú thích: Lớp thực thể ánh xạ bảng 'courses'.
 */
@Entity
@Table(name = "courses")
@Data // Tự động tạo Getter, Setter, toString, equals, hashCode
@NoArgsConstructor // Constructor không đối số
@AllArgsConstructor // Constructor đầy đủ đối số
@Builder // Hỗ trợ khởi tạo object theo pattern builder (tùy chọn)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "course_name", nullable = false, length = 200)
    private String courseName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Chú thích: Chuyển sang Enum để đảm bảo chỉ nhập 1 trong 3 cấp độ
    @Enumerated(EnumType.STRING)
    @Column(name = "level", columnDefinition = "ENUM('Beginner','Intermediate','Advanced')")
    private CourseLevel level;

    @Column(name = "duration")
    private Integer duration;

    // Chú thích: Chuyển sang Enum để tránh nhập sai đơn vị thời gian
    @Enumerated(EnumType.STRING)
    @Column(name = "duration_unit", columnDefinition = "ENUM('Hour','Week')")
    private DurationUnit durationUnit = DurationUnit.Week;

    @Column(name = "fee", nullable = false)
    private BigDecimal fee = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.Active;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}