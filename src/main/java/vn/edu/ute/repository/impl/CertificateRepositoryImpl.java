package vn.edu.ute.repository.impl;

import vn.edu.ute.model.Certificate;
import vn.edu.ute.model.Student;
import vn.edu.ute.repository.CertificateRepository;
import vn.edu.ute.util.TransactionManager;
import java.util.Collections;
import java.util.List;

public class CertificateRepositoryImpl implements CertificateRepository {
    private final TransactionManager tx = new TransactionManager();

    @Override
    public Certificate save(Certificate cert) {
        try {
            return tx.runInTransaction(em -> {
                em.persist(cert);
                return cert;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Certificate> findAll() {
        try {
            return tx.runInTransaction(em -> em.createQuery("SELECT c FROM Certificate c JOIN FETCH c.student JOIN FETCH c.academicClass", Certificate.class).getResultList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public List<Student> getEligibleStudentsForClass(Long classId) {
        try {
            // LOGIC CHUẨN: Lấy SV đã Pass và CHƯA có chứng chỉ ở lớp này
            return tx.runInTransaction(em -> em.createQuery(
                    "SELECT e.student FROM Enrollment e " +
                    "WHERE e.academicClass.classId = :classId " +
                    "AND e.result = vn.edu.ute.model.enums.EnrollmentResult.Pass " +
                    "AND e.student.studentId NOT IN (" +
                    "   SELECT c.student.studentId FROM Certificate c WHERE c.academicClass.classId = :classId" +
                    ")", Student.class)
                    .setParameter("classId", classId)
                    .getResultList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}