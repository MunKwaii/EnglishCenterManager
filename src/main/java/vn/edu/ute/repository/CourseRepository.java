package vn.edu.ute.repository;

import vn.edu.ute.model.Course;
import java.util.List;

public interface CourseRepository {
    List<Course> findAll();
    Course findById(Long id);
    Course save(Course course); // Cho cả thêm và cập nhật
    boolean delete(Long id);
}