package vn.edu.ute.service;
import vn.edu.ute.model.Certificate;
import vn.edu.ute.model.Student;
import java.util.List;

public interface CertificateService {
    void issueCertificatesBatch(List<Certificate> certificates) throws Exception;
    List<Student> getEligibleStudents(Long classId);
    Certificate getCertificateByStudentIdAndClassId(Long studentId, Long classId);
}