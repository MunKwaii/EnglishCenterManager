package vn.edu.ute.repository.impl;

import vn.edu.ute.model.Result;
import vn.edu.ute.repository.ResultRepository;
import vn.edu.ute.util.TransactionManager;

import java.util.List;

public class ResultRepositoryImpl implements ResultRepository {

    private final TransactionManager txManager = new TransactionManager();

    @Override
    public List<Result> findByClassId(Long classId) {
        try {
            return txManager.runInTransaction(em -> em.createQuery(
                    "SELECT r FROM Result r " +
                    "JOIN FETCH r.student " +
                    "WHERE r.academicClass.classId = :classId " +
                    "ORDER BY r.student.fullName ASC", Result.class)
                    .setParameter("classId", classId)
                    .getResultList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Result> findByStudentId(Long studentId) {
        try {
            return txManager.runInTransaction(em -> em.createQuery(
                    "SELECT r FROM Result r " +
                    "JOIN FETCH r.academicClass c " +
                    "JOIN FETCH c.course " +
                    "WHERE r.student.studentId = :studentId " +
                    "ORDER BY r.academicClass.startDate DESC", Result.class)
                    .setParameter("studentId", studentId)
                    .getResultList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Result save(Result result) {
        try {
            return txManager.runInTransaction(em -> {
                if (result.getResultId() == null) {
                    em.persist(result);
                    return result;
                } else {
                    return em.merge(result);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean saveAll(List<Result> results) {
        try {
            return txManager.runInTransaction(em -> {
                for (Result res : results) {
                    if (res.getResultId() == null) {
                        em.persist(res);
                    } else {
                        em.merge(res);
                    }
                }
                return true;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
