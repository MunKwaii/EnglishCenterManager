package vn.edu.ute.repository.impl;

import vn.edu.ute.model.Staff;
import vn.edu.ute.repository.StaffRepository;
import vn.edu.ute.util.TransactionManager;

import java.util.List;
import java.util.Optional;

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
    public List<Staff> findAll() throws Exception {
        return tx.runInTransaction(em ->
                em.createQuery("SELECT s FROM Staff s", Staff.class).getResultList()
        );
    }

    @Override
    public Optional<Staff> findById(Long id) throws Exception {
        return tx.runInTransaction(em -> Optional.ofNullable(em.find(Staff.class, id)));
    }

    @Override
    public void deleteById(Long id) throws Exception {
        tx.runInTransaction(em -> {
            Staff staff = em.find(Staff.class, id);
            if (staff != null) {
                em.remove(staff);
            }
            return null;
        });
    }

    @Override
    public Optional<Staff> findByEmail(String email) throws Exception {
        if (email == null) return Optional.empty();
        return tx.runInTransaction(em -> {
            List<Staff> result = em.createQuery(
                            "SELECT s FROM Staff s WHERE lower(s.email) = lower(:email)", Staff.class)
                    .setParameter("email", email.trim())
                    .setMaxResults(1)
                    .getResultList();
            return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        });
    }

    @Override
    public Optional<Staff> findByPhone(String phone) throws Exception {
        if (phone == null) return Optional.empty();
        return tx.runInTransaction(em -> {
            List<Staff> result = em.createQuery(
                            "SELECT s FROM Staff s WHERE s.phone = :phone", Staff.class)
                    .setParameter("phone", phone.trim())
                    .setMaxResults(1)
                    .getResultList();
            return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        });
    }
}