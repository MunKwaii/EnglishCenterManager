package vn.edu.ute.model;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.ute.model.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Chú thích: Lớp thực thể ánh xạ bảng 'teachers'.
 * Đã cập nhật sử dụng Lombok và Enum Status để đồng bộ hệ thống.
 */
@Entity
@Table(name = "teachers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // Hỗ trợ khởi tạo: Teacher.builder().fullName("Nguyen Van A").build();
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_id")
    private Long teacherId;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "phone", unique = true, length = 20)
    private String phone;

    @Column(name = "email", unique = true, length = 150)
    private String email;

    // Chú thích: Chuyên môn giảng dạy (Ví dụ: IELTS, TOEIC...)
    @Column(name = "specialty", length = 100)
    private String specialty;

    // Chú thích: Ngày vào làm
    @Column(name = "hire_date")
    private LocalDate hireDate;

    // Chú thích: Sử dụng Enum Status thay vì String
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('Active','Inactive') DEFAULT 'Active'")
    private Status status = Status.Active;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return fullName != null ? fullName : "Teacher ID: " + teacherId;
    }
}