package vn.edu.ute.repository.impl;

import vn.edu.ute.model.Attendance;
import vn.edu.ute.model.Result;
import vn.edu.ute.repository.AcademicOperationRepository;
import vn.edu.ute.util.TransactionManager;
import java.util.List;

public class AcademicOperationRepositoryImpl implements AcademicOperationRepository {
    private final TransactionManager tx = new TransactionManager();

    @Override
    public void saveAttendancesBatch(List<Attendance> attendances) throws Exception {
        tx.runInTransaction(em -> {
            // Sử dụng Lambda forEach để lưu hàng loạt trong 1 transaction
            attendances.forEach(attendance -> {
                if (attendance.getAttendanceId() == null) {
                    em.persist(attendance);
                } else {
                    em.merge(attendance);
                }
            });
            return null;
        });
    }

    @Override
    public void saveResultsBatch(List<Result> results) throws Exception {
        tx.runInTransaction(em -> {
            // Sử dụng Lambda forEach để lưu hàng loạt trong 1 transaction
            results.forEach(result -> {
                if (result.getResultId() == null) {
                    em.persist(result);
                } else {
                    em.merge(result);
                }
            });
            return null;
        });
    }
}