package vn.edu.ute.service.impl;

import vn.edu.ute.model.Schedule;
import vn.edu.ute.repository.ScheduleRepository;
import vn.edu.ute.repository.impl.ScheduleRepositoryImpl;
import vn.edu.ute.service.ScheduleService;

import java.time.LocalDate;
import java.util.List;

public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository = new ScheduleRepositoryImpl();

    @Override
    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    @Override
    public Schedule findById(Long id) {
        return scheduleRepository.findById(id);
    }

    private void validateSchedule(Schedule schedule) throws Exception {
        if (schedule.getStartTime().isAfter(schedule.getEndTime()) ||
                schedule.getStartTime().equals(schedule.getEndTime())) {
            throw new Exception("Giờ bắt đầu phải trước giờ kết thúc!");
        }

        // Lấy danh sách lịch học trong cùng ngày
        List<Schedule> existingSchedules = scheduleRepository.findByStudyDate(schedule.getStudyDate());

        if (existingSchedules == null || existingSchedules.isEmpty()) {
            return;
        }

        for (Schedule existing : existingSchedules) {
            // bỏ qua nếu là chính nó
            if (schedule.getScheduleId() != null && schedule.getScheduleId().equals(existing.getScheduleId())) {
                continue;
            }

            // Kiểm tra giao nhau: (StartA < EndB) và (EndA > StartB)
            boolean overlaps = schedule.getStartTime().isBefore(existing.getEndTime()) &&
                    schedule.getEndTime().isAfter(existing.getStartTime());

            if (overlaps) {
                // 1. Kiểm tra trùng lớp học (Class Conflict)
                if (schedule.getAcademicClass() != null && existing.getAcademicClass() != null &&
                        schedule.getAcademicClass().getClassId().equals(existing.getAcademicClass().getClassId())) {
                    throw new Exception(
                            "Lớp " + schedule.getAcademicClass().getClassName() + " đã có lịch học trùng vào giờ này.");
                }

                // 2. Kiểm tra trùng phòng học (Room Conflict)
                if (schedule.getRoom() != null && existing.getRoom() != null &&
                        schedule.getRoom().getRoomId().equals(existing.getRoom().getRoomId())) {
                    throw new Exception("Phòng " + schedule.getRoom().getRoomName() + " đã có lớp "
                            + existing.getAcademicClass().getClassName() + " xếp lịch vào giờ này.");
                }

                // 3. Kiểm tra trùng giáo viên (Teacher Conflict)
                if (schedule.getAcademicClass() != null && schedule.getAcademicClass().getTeacher() != null &&
                        existing.getAcademicClass() != null && existing.getAcademicClass().getTeacher() != null &&
                        schedule.getAcademicClass().getTeacher().getTeacherId()
                                .equals(existing.getAcademicClass().getTeacher().getTeacherId())) {
                    throw new Exception("Giáo viên " + schedule.getAcademicClass().getTeacher().getFullName()
                            + " đang dạy lớp " + existing.getAcademicClass().getClassName() + " vào giờ này.");
                }
            }
        }
    }

    @Override
    public Schedule addSchedule(Schedule schedule) throws Exception {
        validateSchedule(schedule);
        return scheduleRepository.save(schedule);
    }

    @Override
    public Schedule updateSchedule(Schedule schedule) throws Exception {
        validateSchedule(schedule);
        return scheduleRepository.save(schedule);
    }

    @Override
    public boolean delete(Long id) {
        return scheduleRepository.delete(id);
    }

    @Override
    public List<Schedule> findByClassId(Long classId) {
        return scheduleRepository.findByClassId(classId);
    }

    @Override
    public List<Schedule> findByStudyDate(LocalDate date) {
        return scheduleRepository.findByStudyDate(date);
    }
}
