package vn.edu.ute.repository;
import vn.edu.ute.model.Certificate;
import vn.edu.ute.model.Student;
import java.util.List;

public interface CertificateRepository {
    Certificate save(Certificate cert);
    List<Certificate> findAll();
    List<Student> getEligibleStudentsForClass(Long classId);
}