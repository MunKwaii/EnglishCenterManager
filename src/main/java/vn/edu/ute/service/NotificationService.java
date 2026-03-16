package vn.edu.ute.service;

import vn.edu.ute.model.Notification;
import vn.edu.ute.model.enums.NotificationTargetRole;
import java.util.List;

public interface NotificationService {
    // --- Các hàm truy vấn Lambda (Streams API) ---
    List<Notification> getNotificationsForUser(NotificationTargetRole userRole);
    List<Notification> getTop5LatestNotifications();
    List<Notification> searchByKeyword(String keyword);
    List<String> getAllTitlesByRole(NotificationTargetRole role);
    long countNotificationsByAuthor(Long userId);
    boolean hasUrgentNotifications();
    Notification getMostRecentByRole(NotificationTargetRole role);

    // --- Các hàm CRUD cơ bản ---
    void createNotification(Notification n) throws Exception;
    void updateNotification(Notification n) throws Exception;
    void deleteNotification(Long id) throws Exception;
    List<Notification> getAllNotifications();
}