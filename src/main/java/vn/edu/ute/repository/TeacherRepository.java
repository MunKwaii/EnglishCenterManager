package vn.edu.ute.repository;

import java.util.List;
import java.util.Optional;
import vn.edu.ute.model.Teacher;


public interface TeacherRepository {
    void save(Teacher teacher);
    Optional<Teacher> findById(Long id);
    List<Teacher> findAll();
    void deleteById(Long id);
}
