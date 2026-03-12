package vn.edu.ute.repository.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Notification;
import vn.edu.ute.repository.NotificationRepository;
import vn.edu.ute.util.TransactionManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NotificationRepositoryImpl implements NotificationRepository {
    private final TransactionManager txManager;

    public NotificationRepositoryImpl() {
        // Sử dụng TransactionManager ông đã viết để quản lý kết nối
        this.txManager = new TransactionManager();
    }

    @Override
    public void save(Notification notification) {
        try {
            txManager.runInTransaction(em -> {
                em.persist(notification); // Chỉ thêm mới
                return null;
            });
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu thông báo: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Notification notification) {
        try {
            txManager.runInTransaction(em -> {
                em.merge(notification); // Chỉ cập nhật
                return null;
            });
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật thông báo: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Notification> findById(Long id) {
        try {
            return txManager.runInTransaction(em -> {
                // JOIN FETCH để lấy luôn thông tin UserAccount của người tạo
                String jpql = "SELECT n FROM Notification n JOIN FETCH n.createdByUser WHERE n.notificationId = :id";
                Notification n = em.createQuery(jpql, Notification.class)
                        .setParameter("id", id)
                        .getSingleResult();
                return Optional.ofNullable(n);
            });
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Notification> findAll() {
        try {
            return txManager.runInTransaction(em -> {
                // LEFT JOIN FETCH để cho phép createdByUser null
                String jpql = "SELECT n FROM Notification n LEFT JOIN FETCH n.createdByUser ORDER BY n.createdAt DESC";
                return em.createQuery(jpql, Notification.class).getResultList();
            });
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            txManager.runInTransaction(em -> {
                Notification n = em.find(Notification.class, id);
                if (n != null) {
                    em.remove(n); // Xóa cứng khỏi Database
                }
                return null;
            });
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa thông báo: " + e.getMessage(), e);
        }
    }
}