package vn.edu.ute.repository.impl;

import vn.edu.ute.model.Enrollment;
import vn.edu.ute.repository.EnrollmentRepository;
import vn.edu.ute.util.TransactionManager;
import java.util.List;
public class EnrollmentRepositoryImpl implements EnrollmentRepository {
    private final TransactionManager tx = new TransactionManager();

    @Override
    public Enrollment save(Enrollment enrollment) throws Exception {
        return tx.runInTransaction(em -> {
            if (enrollment.getEnrollmentId() == null) {
                em.persist(enrollment);
                return enrollment;
            }
            return em.merge(enrollment);
        });
    }

    @Override
    public Long countActiveStudentsInClass(Long classId) throws Exception {
        return tx.runInTransaction(em -> 
            em.createQuery("SELECT COUNT(e) FROM Enrollment e WHERE e.academicClass.classId = :classId AND e.status = 'Enrolled'", Long.class)
              .setParameter("classId", classId)
              .getSingleResult()
        );
    }
    @Override
    public List<Enrollment> getEnrollmentsByClassId(Long classId) throws Exception {
        return tx.runInTransaction(em -> 
            em.createQuery(
                "SELECT e FROM Enrollment e " +
                "JOIN FETCH e.student " +          // Kéo dữ liệu Student để UI hiện Tên
                "JOIN FETCH e.academicClass " +    // Kéo dữ liệu Lớp học
                "WHERE e.academicClass.classId = :classId", Enrollment.class)
              .setParameter("classId", classId)
              .getResultList()
        );
    }

    @Override
    public List<Enrollment> getAllEnrollments() throws Exception {
        return tx.runInTransaction(em -> 
            em.createQuery(
                "SELECT e FROM Enrollment e " +
                "JOIN FETCH e.student " + 
                "JOIN FETCH e.academicClass", Enrollment.class)
              .getResultList()
        );
    }
}