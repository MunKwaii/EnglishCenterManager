package vn.edu.ute.service.impl;

import vn.edu.ute.model.Attendance;
import vn.edu.ute.model.Result;
import vn.edu.ute.repository.AcademicOperationRepository;
import vn.edu.ute.repository.impl.AcademicOperationRepositoryImpl;
import vn.edu.ute.service.AcademicOperationService;
import java.util.List;

public class AcademicOperationServiceImpl implements AcademicOperationService {
    private final AcademicOperationRepository operationRepo = new AcademicOperationRepositoryImpl();

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
        operationRepo.saveResultsBatch(results);
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