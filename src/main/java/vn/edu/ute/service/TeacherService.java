package vn.edu.ute.service;

import java.util.List;
import vn.edu.ute.model.Teacher;

public interface TeacherService {
    List<Teacher> findBySpecialty(String specialty);
    boolean isEmailExists(String mail);
    List<String> getAllTeacherNames();
    Teacher findByPhone(String phone);
    long countActiveTeachers();
    List<Teacher> getSortedTeachers();
    void addTeacher(Teacher t) throws Exception;
    void deleteTeacher(Long id) throws Exception;
    List<Teacher> getAllTeachers();
    List<Teacher> getActiveTeachers();
}
