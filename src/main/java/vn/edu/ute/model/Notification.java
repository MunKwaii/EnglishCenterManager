package vn.edu.ute.model;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.ute.model.enums.NotificationTargetRole;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_role", nullable = false,
            columnDefinition = "ENUM('All','Student','Teacher','Staff') DEFAULT 'All'")
    private NotificationTargetRole targetRole = NotificationTargetRole.All;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user")
    private UserAccount createdByUser;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}