package vn.edu.ute.service;

import vn.edu.ute.model.AcademicClass;
import vn.edu.ute.model.Student;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.Promotion;
import java.util.List;

public interface EnrollmentService {
    // Đã thêm tham số Promotion vào hàm ghi danh
    void enrollStudent(Student student, AcademicClass academicClass, Promotion promotion) throws Exception;
    
    // Hàm lấy danh sách sinh viên theo lớp đã làm trước đó
    List<Enrollment> getEnrollmentsByClassId(Long classId) throws Exception;

    List<Enrollment> getEnrollmentsByStudentId(Long studentId) throws Exception;
}