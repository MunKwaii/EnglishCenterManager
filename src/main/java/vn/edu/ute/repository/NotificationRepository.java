package vn.edu.ute.repository;

import vn.edu.ute.model.Notification;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    void save(Notification notification);
    Optional<Notification> findById(Long id);
    List<Notification> findAll();
    void deleteById(Long id);
}