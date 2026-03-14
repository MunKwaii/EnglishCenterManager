package vn.edu.ute.repository;

import vn.edu.ute.model.Enrollment;
import java.util.List;

public interface EnrollmentRepository {
    Enrollment save(Enrollment enrollment) throws Exception;
    Long countActiveStudentsInClass(Long classId) throws Exception;

    List<Enrollment> getEnrollmentsByClassId(Long classId) throws Exception;
    
    List<Enrollment> getEnrollmentsByStudentId(Long studentId) throws Exception;

    List<Enrollment> getAllEnrollments() throws Exception;
}