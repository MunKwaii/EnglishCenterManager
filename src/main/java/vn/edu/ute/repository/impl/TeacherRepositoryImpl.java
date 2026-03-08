package vn.edu.ute.repository.impl;

import vn.edu.ute.model.Teacher;
import vn.edu.ute.repository.TeacherRepository;
import vn.edu.ute.util.TransactionManager;

import java.util.List;

public class TeacherRepositoryImpl implements TeacherRepository {
    private final TransactionManager txManager = new TransactionManager();

    @Override
    public List<Teacher> findAll() {
        try {
            return txManager
                    .runInTransaction(em -> em.createQuery("SELECT t FROM Teacher t", Teacher.class).getResultList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
