package vn.edu.ute.model;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.ute.model.enums.Status;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "branches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branch_id")
    private Long branchId;

    @Column(name = "branch_name", nullable = false, unique = true, length = 150)
    private String branchName;

    @Column(length = 255)
    private String address;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('Active','Inactive') DEFAULT 'Active'")
    private Status status = Status.Active;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // Quan hệ một chi nhánh có nhiều phòng
    @OneToMany(mappedBy = "branch", fetch = FetchType.LAZY)
    private List<Room> rooms;

    // Quan hệ một chi nhánh có nhiều lớp học
    @OneToMany(mappedBy = "branch", fetch = FetchType.LAZY)
    private List<AcademicClass> classes;
}