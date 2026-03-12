package vn.edu.ute.service.impl;

import vn.edu.ute.model.Notification;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.model.enums.NotificationTargetRole;
import vn.edu.ute.repository.NotificationRepository;
import vn.edu.ute.repository.impl.NotificationRepositoryImpl;
import vn.edu.ute.service.NotificationService;
import vn.edu.ute.util.PermissionUtils;
import vn.edu.ute.util.UserSession;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository repo = new NotificationRepositoryImpl();

    // 1. Logic lọc thông báo theo Role: Người dùng thấy thông báo của Role mình + thông báo "All"
    @Override
    public List<Notification> getNotificationsForUser(NotificationTargetRole userRole) {
        return repo.findAll().stream()
                .filter(n -> n.getTargetRole() == NotificationTargetRole.All
                        || n.getTargetRole() == userRole)
                .collect(Collectors.toList());
    }

    // 2. Lấy 5 thông báo mới nhất (Sử dụng sorted và limit)
    @Override
    public List<Notification> getTop5LatestNotifications() {
        return repo.findAll().stream()
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    // 3. Tìm kiếm theo từ khóa trong Tiêu đề hoặc Nội dung (Sử dụng filter)
    @Override
    public List<Notification> searchByKeyword(String keyword) {
        String k = keyword.toLowerCase();
        return repo.findAll().stream()
                .filter(n -> n.getTitle().toLowerCase().contains(k)
                        || n.getContent().toLowerCase().contains(k))
                .collect(Collectors.toList());
    }

    // 4. Lấy danh sách toàn bộ Tiêu đề theo một Role cụ thể (Sử dụng map)
    @Override
    public List<String> getAllTitlesByRole(NotificationTargetRole role) {
        return repo.findAll().stream()
                .filter(n -> n.getTargetRole() == role)
                .map(Notification::getTitle)
                .collect(Collectors.toList());
    }

    // 5. Đếm số lượng thông báo của một người soạn (Sử dụng count)
    @Override
    public long countNotificationsByAuthor(Long userId) {
        return repo.findAll().stream()
                .filter(n -> n.getCreatedByUser() != null
                        && n.getCreatedByUser().getUserId().equals(userId))
                .count();
    }

    // 6. Kiểm tra xem có thông báo nào "Khẩn cấp" không (Sử dụng anyMatch)
    @Override
    public boolean hasUrgentNotifications() {
        return repo.findAll().stream()
                .anyMatch(n -> n.getTitle().contains("Khẩn cấp")
                        || n.getContent().contains("Khẩn cấp"));
    }

    // 7. Lấy thông báo mới nhất của một Role cụ thể (Sử dụng findFirst)
    @Override
    public Notification getMostRecentByRole(NotificationTargetRole role) {
        return repo.findAll().stream()
                .filter(n -> n.getTargetRole() == role)
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .findFirst()
                .orElse(null);
    }

    // --- CÁC HÀM CRUD ---

    @Override
    public void createNotification(Notification n) throws Exception {
        if (n.getTitle() == null || n.getTitle().trim().isEmpty()) {
            throw new Exception("Tiêu đề không được để trống!");
        }
        if (n.getContent() == null || n.getContent().trim().isEmpty()) {
            throw new Exception("Nội dung thông báo không được để trống!");
        }
        repo.save(n);
    }

    @Override
    public void updateNotification(Notification n) throws Exception {
        // Kiểm tra notification có tồn tại không
        if (n.getNotificationId() == null) {
            throw new Exception("Không tìm thấy ID thông báo để cập nhật!");
        }

        var existing = repo.findById(n.getNotificationId());
        if (existing.isEmpty()) {
            throw new Exception("Thông báo không tồn tại trong hệ thống!");
        }

        // Validation
        if (n.getTitle() == null || n.getTitle().trim().isEmpty()) {
            throw new Exception("Tiêu đề không được để trống!");
        }
        if (n.getContent() == null || n.getContent().trim().isEmpty()) {
            throw new Exception("Nội dung thông báo không được để trống!");
        }

        repo.update(n);
    }

    @Override
    public void deleteNotification(Long id) throws Exception {
        repo.deleteById(id);
    }

    @Override
    public List<Notification> getAllNotifications() {
        UserAccount currentUser = UserSession.getCurrentUser();
        if (currentUser == null) {
            return List.of(); // Không login thì không xem được gì
        }

        List<Notification> allNotifications = repo.findAll();

        // Admin và Staff: Xem hết
        if (PermissionUtils.canViewAllNotifications(currentUser)) {
            return allNotifications;
        }

        // Student: Chỉ xem Student + All
        if (currentUser.getRole() == vn.edu.ute.model.enums.UserRole.Student) {
            return allNotifications.stream()
                    .filter(n -> n.getTargetRole() == NotificationTargetRole.Student
                              || n.getTargetRole() == NotificationTargetRole.All)
                    .collect(Collectors.toList());
        }

        // Teacher: Chỉ xem Teacher + All
        if (currentUser.getRole() == vn.edu.ute.model.enums.UserRole.Teacher) {
            return allNotifications.stream()
                    .filter(n -> n.getTargetRole() == NotificationTargetRole.Teacher
                              || n.getTargetRole() == NotificationTargetRole.All)
                    .collect(Collectors.toList());
        }

        // Mặc định: Không xem được gì
        return List.of();
    }
}