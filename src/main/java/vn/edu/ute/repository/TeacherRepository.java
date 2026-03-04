package vn.edu.ute.repository;

import vn.edu.ute.model.Teacher;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository {
    void save(Teacher teacher);
    Optional<Teacher> findById(Long id);
    List<Teacher> findAll();
    void deleteById(Long id);
}
