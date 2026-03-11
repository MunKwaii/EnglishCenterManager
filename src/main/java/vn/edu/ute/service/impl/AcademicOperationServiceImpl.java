package vn.edu.ute.service.impl;

import vn.edu.ute.model.Attendance;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.Result;
import vn.edu.ute.model.enums.EnrollmentResult;
import vn.edu.ute.repository.AcademicOperationRepository;
import vn.edu.ute.repository.EnrollmentRepository;
import vn.edu.ute.repository.impl.AcademicOperationRepositoryImpl;
import vn.edu.ute.repository.impl.EnrollmentRepositoryImpl;
import vn.edu.ute.service.AcademicOperationService;

import java.math.BigDecimal;
import java.util.List;

public class AcademicOperationServiceImpl implements AcademicOperationService {
    private final AcademicOperationRepository operationRepo = new AcademicOperationRepositoryImpl();
    // Bổ sung thêm EnrollmentRepository để cập nhật trạng thái
    private final EnrollmentRepository enrollmentRepo = new EnrollmentRepositoryImpl();

    @Override
    public void processClassAttendance(List<Attendance> attendances) throws Exception {
        if (attendances == null || attendances.isEmpty()) {
            throw new Exception("Danh sách điểm danh trống!");
        }
        operationRepo.saveAttendancesBatch(attendances);
    }

    @Override
    public void processClassResults(List<Result> results) throws Exception {
        if (results == null || results.isEmpty()) {
            throw new Exception("Danh sách điểm trống!");
        }
        
        // 1. Lưu điểm số vào bảng Results như bình thường
        operationRepo.saveResultsBatch(results);

        // 2. ĐỒNG BỘ TRẠNG THÁI PASS/FAIL SANG BẢNG GHI DANH
        // Lấy danh sách ghi danh của lớp học hiện tại
        Long classId = results.get(0).getAcademicClass().getClassId();
        List<Enrollment> enrollments = enrollmentRepo.getEnrollmentsByClassId(classId);

        for (Result r : results) {
            for (Enrollment e : enrollments) {
                // Khớp đúng ID học viên
                if (e.getStudent().getStudentId().equals(r.getStudent().getStudentId())) {
                    // Logic: Nếu điểm >= 5.0 thì được đánh giá là Pass
                    if (r.getScore() != null && r.getScore().compareTo(new BigDecimal("5.0")) >= 0) {
                        e.setResult(EnrollmentResult.Pass);
                    } else {
                        e.setResult(EnrollmentResult.Fail);
                    }
                    // Cập nhật lại bản ghi danh
                    enrollmentRepo.save(e);
                    break;
                }
            }
        }
    }

    @Override
    public List<Attendance> getAttendancesByClassId(Long classId) throws Exception {
        return operationRepo.getAttendancesByClassId(classId);
    }

    @Override
    public List<Result> getResultsByClassId(Long classId) throws Exception {
        return operationRepo.getResultsByClassId(classId);
    }
}