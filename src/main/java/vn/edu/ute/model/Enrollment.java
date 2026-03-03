package vn.edu.ute.model;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.ute.model.enums.EnrollmentResult;
import vn.edu.ute.model.enums.EnrollmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "class_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id") // Ánh xạ BIGINT UNSIGNED
    private Long enrollmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private AcademicClass academicClass;

    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('Enrolled','Dropped','Completed') DEFAULT 'Enrolled'")
    private EnrollmentStatus status = EnrollmentStatus.Enrolled;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('Pass','Fail','NA') DEFAULT 'NA'")
    private EnrollmentResult result = EnrollmentResult.NA;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt; // Dữ liệu hệ thống

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt; // Thời điểm cập nhật cuối
}