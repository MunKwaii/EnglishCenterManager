package vn.edu.ute.repository.impl;

import vn.edu.ute.model.Schedule;
import vn.edu.ute.repository.ScheduleRepository;
import vn.edu.ute.util.TransactionManager;
import java.time.LocalDate;
import java.util.List;

public class ScheduleRepositoryImpl implements ScheduleRepository {
    private final TransactionManager txManager = new TransactionManager();

    @Override
    public List<Schedule> findAll() {
        try {
            return txManager.runInTransaction(em -> em.createQuery(
                    "SELECT s FROM Schedule s JOIN FETCH s.academicClass c LEFT JOIN FETCH c.course LEFT JOIN FETCH c.teacher LEFT JOIN FETCH s.room",
                    Schedule.class).getResultList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Schedule findById(Long id) {
        try {
            return txManager.runInTransaction(em -> em.find(Schedule.class, id));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Schedule save(Schedule schedule) {
        try {
            return txManager.runInTransaction(em -> {
                if (schedule.getScheduleId() == null) {
                    em.persist(schedule);
                    return schedule;
                } else {
                    return em.merge(schedule);
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
                Schedule schedule = em.find(Schedule.class, id);
                if (schedule != null) {
                    em.remove(schedule);
                    return true;
                }
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Schedule> findByClassId(Long classId) {
        try {
            return txManager.runInTransaction(em -> em.createQuery(
                    "SELECT s FROM Schedule s JOIN FETCH s.academicClass c LEFT JOIN FETCH c.course LEFT JOIN FETCH c.teacher LEFT JOIN FETCH s.room WHERE c.classId = :classId",
                    Schedule.class)
                    .setParameter("classId", classId)
                    .getResultList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Schedule> findByStudyDate(LocalDate date) {
        try {
            return txManager.runInTransaction(em -> em.createQuery(
                    "SELECT s FROM Schedule s JOIN FETCH s.academicClass c LEFT JOIN FETCH c.course LEFT JOIN FETCH c.teacher LEFT JOIN FETCH s.room WHERE s.studyDate = :studyDate",
                    Schedule.class)
                    .setParameter("studyDate", date)
                    .getResultList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
