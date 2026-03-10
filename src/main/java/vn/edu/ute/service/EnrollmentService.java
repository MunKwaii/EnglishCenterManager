package vn.edu.ute.service;

import vn.edu.ute.model.AcademicClass;
import vn.edu.ute.model.Student;

public interface EnrollmentService {
    void enrollStudent(Student student, AcademicClass academicClass) throws Exception;
}