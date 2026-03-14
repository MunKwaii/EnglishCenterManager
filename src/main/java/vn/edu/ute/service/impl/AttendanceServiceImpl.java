package vn.edu.ute.service.impl;

import vn.edu.ute.model.Attendance;
import vn.edu.ute.repository.AttendanceRepository;
import vn.edu.ute.repository.impl.AttendanceRepositoryImpl;
import vn.edu.ute.service.AttendanceService;

import java.time.LocalDate;
import java.util.List;

public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository = new AttendanceRepositoryImpl();

    @Override
    public List<Attendance> getAttendancesByClassId(Long classId) {
        return attendanceRepository.findByClassId(classId);
    }

    @Override
    public List<Attendance> getAttendancesByClassIdAndDate(Long classId, LocalDate date) {
        return attendanceRepository.findByClassIdAndDate(classId, date);
    }

    @Override
    public Attendance saveAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    @Override
    public boolean saveAllAttendances(List<Attendance> attendances) {
        return attendanceRepository.saveAll(attendances);
    }
}
