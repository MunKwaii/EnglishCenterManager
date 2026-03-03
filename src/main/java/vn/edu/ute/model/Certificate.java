package vn.edu.ute.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "certificates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    private Long certificateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private AcademicClass academicClass;

    @Column(name = "cert_name", nullable = false, length = 150)
    private String certName;

    @Column(name = "issue_date", nullable = false, columnDefinition = "DATE DEFAULT (CURRENT_DATE)")
    private LocalDate issueDate = LocalDate.now();

    @Column(name = "serial_no", unique = true, length = 80)
    private String serialNo;
}