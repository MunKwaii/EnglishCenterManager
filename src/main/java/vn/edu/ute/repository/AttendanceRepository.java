package vn.edu.ute.repository;

import vn.edu.ute.model.Attendance;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository {
    List<Attendance> findByClassId(Long classId);
    List<Attendance> findByClassIdAndDate(Long classId, LocalDate date);
    Attendance save(Attendance attendance);
    boolean saveAll(List<Attendance> attendances);
}
