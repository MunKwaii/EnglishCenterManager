package vn.edu.ute.repository.impl;

import vn.edu.ute.model.Attendance;
import vn.edu.ute.repository.AttendanceRepository;
import vn.edu.ute.util.TransactionManager;

import java.time.LocalDate;
import java.util.List;

public class AttendanceRepositoryImpl implements AttendanceRepository {

    private final TransactionManager txManager = new TransactionManager();

    @Override
    public List<Attendance> findByClassId(Long classId) {
        try {
            return txManager.runInTransaction(em -> em.createQuery(
                    "SELECT a FROM Attendance a " +
                    "JOIN FETCH a.student " +
                    "WHERE a.academicClass.classId = :classId " +
                    "ORDER BY a.attendDate DESC, a.student.fullName ASC", Attendance.class)
                    .setParameter("classId", classId)
                    .getResultList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Attendance> findByClassIdAndDate(Long classId, LocalDate date) {
        try {
            return txManager.runInTransaction(em -> em.createQuery(
                    "SELECT a FROM Attendance a " +
                    "JOIN FETCH a.student " +
                    "WHERE a.academicClass.classId = :classId AND a.attendDate = :date " +
                    "ORDER BY a.student.fullName ASC", Attendance.class)
                    .setParameter("classId", classId)
                    .setParameter("date", date)
                    .getResultList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Attendance save(Attendance attendance) {
        try {
            return txManager.runInTransaction(em -> {
                if (attendance.getAttendanceId() == null) {
                    em.persist(attendance);
                    return attendance;
                } else {
                    return em.merge(attendance);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean saveAll(List<Attendance> attendances) {
        try {
            return txManager.runInTransaction(em -> {
                for (Attendance att : attendances) {
                    if (att.getAttendanceId() == null) {
                        em.persist(att);
                    } else {
                        em.merge(att);
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
