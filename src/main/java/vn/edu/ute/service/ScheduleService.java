package vn.edu.ute.service;

import vn.edu.ute.model.Schedule;
import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    List<Schedule> findAll();

    Schedule findById(Long id);

    Schedule addSchedule(Schedule schedule) throws Exception;

    Schedule updateSchedule(Schedule schedule) throws Exception;

    boolean delete(Long id);

    List<Schedule> findByClassId(Long classId);

    List<Schedule> findByStudyDate(LocalDate date);
}
