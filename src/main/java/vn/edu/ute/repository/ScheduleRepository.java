package vn.edu.ute.repository;

import vn.edu.ute.model.Schedule;
import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository {
    List<Schedule> findAll();

    Schedule findById(Long id);

    Schedule save(Schedule schedule);

    boolean delete(Long id);

    List<Schedule> findByClassId(Long classId);

    List<Schedule> findByStudyDate(LocalDate date);
}
