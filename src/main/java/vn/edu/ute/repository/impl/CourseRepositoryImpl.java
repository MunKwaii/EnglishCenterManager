package vn.edu.ute.repository.impl;

import vn.edu.ute.model.Course;
import vn.edu.ute.repository.CourseRepository;
import vn.edu.ute.util.TransactionManager;
import java.util.List;

public class CourseRepositoryImpl implements CourseRepository {
    private final TransactionManager txManager = new TransactionManager();

    @Override
    public List<Course> findAll() {
        try {
            return txManager.runInTransaction(em ->
                    em.createQuery("SELECT c FROM Course c", Course.class).getResultList()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Course findById(Long id) {
        try {
            return txManager.runInTransaction(em -> em.find(Course.class, id));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Course save(Course course) {
        try {
            return txManager.runInTransaction(em -> {
                if (course.getCourseId() == null) {
                    em.persist(course); // Thêm mới
                    return course;
                } else {
                    return em.merge(course); // Cập nhật
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(Long id) {
        try {
            return txManager.runInTransaction(em -> {
                Course course = em.find(Course.class, id);
                if (course != null) {
                    em.remove(course);
                    return true;
                }
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}