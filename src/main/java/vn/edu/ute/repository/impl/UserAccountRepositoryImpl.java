package vn.edu.ute.repository.impl;

import jakarta.persistence.NoResultException;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.repository.UserAccountRepository;
import vn.edu.ute.util.TransactionManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserAccountRepositoryImpl implements UserAccountRepository {
    private final TransactionManager txManager;

    public UserAccountRepositoryImpl() {
        this.txManager = new TransactionManager();
    }

    @Override
    public void save(UserAccount account) {
        try {
            txManager.runInTransaction(em -> {
                if (account.getUserId() == null) em.persist(account);
                else em.merge(account);
                return null;
            });
        } catch (Exception e) {
            throw new RuntimeException("Lỗi lưu tài khoản: " + e.getMessage());
        }
    }

    @Override
    public Optional<UserAccount> findByUsername(String username) {
        try {
            return txManager.runInTransaction(em -> {
                try {
                    // Sử dụng LEFT JOIN FETCH để lấy kèm thông tin định danh tùy theo Role
                    String jpql = "SELECT u FROM UserAccount u " +
                            "LEFT JOIN FETCH u.teacher " +
                            "LEFT JOIN FETCH u.student " +
                            "LEFT JOIN FETCH u.staff " +
                            "WHERE u.username = :user AND u.isActive = true";

                    UserAccount account = em.createQuery(jpql, UserAccount.class)
                            .setParameter("user", username)
                            .getSingleResult();
                    return Optional.of(account);
                } catch (NoResultException e) {
                    return Optional.empty();
                }
            });
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserAccount> findById(Long id) {
        try {
            return txManager.runInTransaction(em -> Optional.ofNullable(em.find(UserAccount.class, id)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<UserAccount> findAll() {
        try {
            return txManager.runInTransaction(em -> {
                String jpql = "SELECT u FROM UserAccount u " +
                        "LEFT JOIN FETCH u.teacher " +
                        "LEFT JOIN FETCH u.student " +
                        "LEFT JOIN FETCH u.staff";
                return em.createQuery(jpql, UserAccount.class).getResultList();
            });
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            txManager.runInTransaction(em -> {
                UserAccount account = em.find(UserAccount.class, id);
                if (account != null) em.remove(account);
                return null;
            });
        } catch (Exception e) {
            throw new RuntimeException("Lỗi xóa tài khoản!");
        }
    }
}