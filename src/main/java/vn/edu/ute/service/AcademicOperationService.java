package vn.edu.ute.service;

import vn.edu.ute.model.Attendance;
import vn.edu.ute.model.Result;
import java.util.List;

public interface AcademicOperationService {
    void processClassAttendance(List<Attendance> attendances) throws Exception;
    void processClassResults(List<Result> results) throws Exception;
}