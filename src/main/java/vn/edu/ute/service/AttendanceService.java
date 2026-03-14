package vn.edu.ute.service;

import vn.edu.ute.model.Attendance;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    List<Attendance> getAttendancesByClassId(Long classId);

    List<Attendance> getAttendancesByClassIdAndDate(Long classId, LocalDate date);

    Attendance saveAttendance(Attendance attendance);

    boolean saveAllAttendances(List<Attendance> attendances);
}
