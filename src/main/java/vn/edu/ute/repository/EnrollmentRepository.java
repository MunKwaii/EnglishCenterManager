package vn.edu.ute.repository;

import vn.edu.ute.model.Enrollment;

public interface EnrollmentRepository {
    Enrollment save(Enrollment enrollment) throws Exception;
    Long countActiveStudentsInClass(Long classId) throws Exception;
}