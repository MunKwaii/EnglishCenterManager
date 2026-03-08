package vn.edu.ute.repository;

import vn.edu.ute.model.Teacher;
import java.util.List;

public interface TeacherRepository {
    List<Teacher> findAll();
}
