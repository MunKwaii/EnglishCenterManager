package vn.edu.ute.service;

import vn.edu.ute.model.Teacher;
import java.util.List;

public interface TeacherService {
    List<Teacher> getActiveTeachers();
}
