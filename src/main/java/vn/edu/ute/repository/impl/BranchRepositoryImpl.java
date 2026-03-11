package vn.edu.ute.repository.impl;

import vn.edu.ute.model.Branch;
import vn.edu.ute.repository.BranchRepository;
import vn.edu.ute.util.TransactionManager;
import java.util.List;

public class BranchRepositoryImpl implements BranchRepository {
    private final TransactionManager txManager = new TransactionManager();

    @Override
    public List<Branch> findAll() {
        try {
            return txManager.runInTransaction(em ->
                    em.createQuery("SELECT b FROM Branch b", Branch.class).getResultList()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Branch findById(Long id) {
        try {
            return txManager.runInTransaction(em -> em.find(Branch.class, id));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Branch save(Branch branch) {
        try {
            return txManager.runInTransaction(em -> {
                if (branch.getBranchId() == null) {
                    em.persist(branch); // Thêm mới
                    return branch;
                } else {
                    return em.merge(branch); // Cập nhật
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
                Branch branch = em.find(Branch.class, id);
                if (branch != null) {
                    em.remove(branch);
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
