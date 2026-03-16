package vn.edu.ute.repository;

import vn.edu.ute.model.Attendance;
import vn.edu.ute.model.Result;
import java.util.List;

public interface AcademicOperationRepository {
    void saveAttendancesBatch(List<Attendance> attendances) throws Exception;
    void saveResultsBatch(List<Result> results) throws Exception;
    
    // --- CÁC HÀM MỚI BỔ SUNG ĐỂ LOAD DỮ LIỆU TỪ LỚP HỌC ---
    List<Attendance> getAttendancesByClassId(Long classId) throws Exception;
    List<Result> getResultsByClassId(Long classId) throws Exception;
}