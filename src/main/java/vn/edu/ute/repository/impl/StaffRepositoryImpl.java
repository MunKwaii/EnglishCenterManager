package vn.edu.ute.repository.impl;

import vn.edu.ute.model.Staff;
import vn.edu.ute.repository.StaffRepository;
import vn.edu.ute.util.TransactionManager;
import java.util.List;

public class StaffRepositoryImpl implements StaffRepository {
    private final TransactionManager tx = new TransactionManager();

    @Override
    public void save(Staff staff) throws Exception {
        tx.runInTransaction(em -> {
            if (staff.getStaffId() == null) {
                em.persist(staff);
            } else {
                em.merge(staff);
            }
            return null;
        });
    }

    @Override
    public List<Staff> findAllActive() throws Exception {
        return tx.runInTransaction(em -> 
            em.createQuery("SELECT s FROM Staff s WHERE s.status = 'Active'", Staff.class).getResultList()
        );
    }

    @Override
    public Staff findById(Long id) throws Exception {
        return tx.runInTransaction(em -> em.find(Staff.class, id));
    }
}