package vn.edu.ute.service;

import vn.edu.ute.model.Teacher;

import java.util.List;

public interface TeacherService {
    List<Teacher> findBySpecialty(String specialty);
    boolean isMailExist(String mail);
    List<String> getAllTeacherNames();
    Teacher findByPhone(String phone);
    long countActiveTeachers();
    List<Teacher> getSortedTeachers();
}
