package vn.edu.ute.repository;

import vn.edu.ute.model.Attendance;
import vn.edu.ute.model.Result;
import java.util.List;

public interface AcademicOperationRepository {
    void saveAttendancesBatch(List<Attendance> attendances) throws Exception;
    void saveResultsBatch(List<Result> results) throws Exception;
}