package vn.edu.ute.model;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.ute.model.enums.StaffRole;
import vn.edu.ute.model.enums.Status;
import java.time.LocalDateTime;

@Entity
@Table(name = "staffs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
    private Long staffId;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "ENUM('Admin','Consultant','Accountant','Manager','Other') DEFAULT 'Other'")
    private StaffRole role = StaffRole.Other;

    @Column(unique = true, length = 20)
    private String phone;

    @Column(unique = true, length = 150)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('Active','Inactive') DEFAULT 'Active'")
    private Status status = Status.Active;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}