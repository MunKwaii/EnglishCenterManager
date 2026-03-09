package vn.edu.ute.service;

import vn.edu.ute.model.Student;
import vn.edu.ute.model.enums.Gender;
import java.util.List;

public interface StudentService {
    List<Student> filterByGender(Gender gender);
    boolean isEmailExists(String email);
    List<String> getAllStudentNames();
    Student findByPhone(String phone);
    long countActiveStudents();
    List<Student> getSortedStudentsByName();

    // --- Các hàm CRUD ---
    void addStudent(Student s) throws Exception;
    void deleteStudent(Long id) throws Exception;
    List<Student> getAllStudents();
}