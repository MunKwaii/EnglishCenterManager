package vn.edu.ute.repository.impl;

import vn.edu.ute.model.PlacementTest;
import vn.edu.ute.repository.PlacementTestRepository;
import vn.edu.ute.util.TransactionManager;
import java.util.List;

public class PlacementTestRepositoryImpl implements PlacementTestRepository {
    private final TransactionManager txManager = new TransactionManager();

    @Override
    public List<PlacementTest> findAll() {
        try {
            return txManager.runInTransaction(em ->
                    // Sử dụng JOIN FETCH để lấy luôn thông tin Student tránh LazyInitializationException
                    em.createQuery("SELECT p FROM PlacementTest p LEFT JOIN FETCH p.student", PlacementTest.class).getResultList()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public PlacementTest findById(Long id) {
        try {
            return txManager.runInTransaction(em -> {
                List<PlacementTest> tests = em.createQuery("SELECT p FROM PlacementTest p LEFT JOIN FETCH p.student WHERE p.testId = :id", PlacementTest.class)
                        .setParameter("id", id)
                        .getResultList();
                return tests.isEmpty() ? null : tests.get(0);
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public PlacementTest save(PlacementTest test) {
        try {
            return txManager.runInTransaction(em -> {
                if (test.getTestId() == null) {
                    em.persist(test);
                    return test;
                } else {
                    return em.merge(test);
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
                PlacementTest test = em.find(PlacementTest.class, id);
                if (test != null) {
                    em.remove(test);
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
