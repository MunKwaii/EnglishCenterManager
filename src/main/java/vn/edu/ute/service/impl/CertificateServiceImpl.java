package vn.edu.ute.service.impl;
import vn.edu.ute.model.Certificate;
import vn.edu.ute.model.Student;
import vn.edu.ute.repository.CertificateRepository;
import vn.edu.ute.repository.impl.CertificateRepositoryImpl;
import vn.edu.ute.service.CertificateService;
import java.util.List;

public class CertificateServiceImpl implements CertificateService {
    private final CertificateRepository repo = new CertificateRepositoryImpl();

    @Override
    public void issueCertificatesBatch(List<Certificate> certificates) throws Exception {
        for(Certificate cert : certificates) {
            if(cert.getSerialNo() == null || cert.getSerialNo().isEmpty()) {
                throw new Exception("Số Serial không được để trống!");
            }
            repo.save(cert);
        }
    }

    @Override
    public List<Student> getEligibleStudents(Long classId) {
        return repo.getEligibleStudentsForClass(classId);
    }

    @Override
    public Certificate getCertificateByStudentIdAndClassId(Long studentId, Long classId) {
        return repo.getCertificateByStudentIdAndClassId(studentId, classId);
    }
}