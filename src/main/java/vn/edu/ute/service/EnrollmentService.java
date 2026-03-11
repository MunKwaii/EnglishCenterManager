package vn.edu.ute.service;

import vn.edu.ute.model.AcademicClass;
import vn.edu.ute.model.Student;
import vn.edu.ute.model.Enrollment; // MỚI THÊM
import java.util.List; // MỚI THÊM

public interface EnrollmentService {
    void enrollStudent(Student student, AcademicClass academicClass) throws Exception;
    
    // MỚI THÊM: Khai báo hàm lấy danh sách sinh viên theo lớp
    List<Enrollment> getEnrollmentsByClassId(Long classId) throws Exception;
}