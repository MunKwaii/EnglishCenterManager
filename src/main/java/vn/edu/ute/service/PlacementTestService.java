package vn.edu.ute.service;

import vn.edu.ute.model.PlacementTest;
import vn.edu.ute.model.enums.CourseLevel;
import java.math.BigDecimal;
import java.util.List;

public interface PlacementTestService {
    List<PlacementTest> getAllTests();
    PlacementTest addTest(PlacementTest test);
    PlacementTest updateTest(PlacementTest test);
    boolean deleteTest(Long id);
    
    // Core logic
    CourseLevel suggestLevel(BigDecimal score);
    
    // Thống kê/Tìm kiếm thêm
    List<PlacementTest> findTestsByStudentId(Long studentId);
}
