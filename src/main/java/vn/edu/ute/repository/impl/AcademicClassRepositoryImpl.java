package vn.edu.ute.repository.impl;

import vn.edu.ute.model.AcademicClass;
import vn.edu.ute.repository.AcademicClassRepository;
import vn.edu.ute.util.TransactionManager;
import java.util.List;

public class AcademicClassRepositoryImpl implements AcademicClassRepository {
    private final TransactionManager txManager = new TransactionManager();

    @Override
    public List<AcademicClass> findAll() {
        try {
            return txManager.runInTransaction(
                    em -> em.createQuery(
                            "SELECT c FROM AcademicClass c JOIN FETCH c.course LEFT JOIN FETCH c.room LEFT JOIN FETCH c.teacher",
                            AcademicClass.class).getResultList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public AcademicClass findById(Long id) {
        try {
            return txManager.runInTransaction(em -> em.find(AcademicClass.class, id));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public AcademicClass save(AcademicClass academicClass) {
        try {
            return txManager.runInTransaction(em -> {
                if (academicClass.getClassId() == null) {
                    em.persist(academicClass);
                    return academicClass;
                } else {
                    return em.merge(academicClass);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(Long id) {
        try {
            return txManager.runInTransaction(em -> {
                AcademicClass academicClass = em.find(AcademicClass.class, id);
                if (academicClass != null) {
                    em.remove(academicClass);
                    return true;
                }
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
