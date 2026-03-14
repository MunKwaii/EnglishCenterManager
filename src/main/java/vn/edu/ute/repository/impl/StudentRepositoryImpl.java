package vn.edu.ute.repository.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Student;
import vn.edu.ute.repository.StudentRepository;
import vn.edu.ute.util.TransactionManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StudentRepositoryImpl implements StudentRepository {
    private final TransactionManager txManager;

    public StudentRepositoryImpl() {
        this.txManager = new TransactionManager();
    }

    @Override
    public void save(Student student) {
        try {
            txManager.runInTransaction(em -> {
                if (student.getStudentId() == null)
                    em.persist(student); // Thêm mới học viên
                else
                    em.merge(student);   // Cập nhật thông tin học viên
                return null;
            });
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu học viên: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Student> findById(Long id) {
        try {
            return txManager.runInTransaction(em -> {
                Student student = em.find(Student.class, id);
                return Optional.ofNullable(student);
            });
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Student> findAll() {
        try {
            return txManager.runInTransaction(em -> {
                // Lấy tất cả học viên
                String jpql = "SELECT s FROM Student s";
                return em.createQuery(jpql, Student.class).getResultList();
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
                Student student = em.find(Student.class, id);
                if (student != null) {
                    em.remove(student);
                }
                return null;
            });
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa học viên: " + e.getMessage(), e);
        }
    }
}