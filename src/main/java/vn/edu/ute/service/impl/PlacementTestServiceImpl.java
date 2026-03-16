package vn.edu.ute.service.impl;

import vn.edu.ute.model.PlacementTest;
import vn.edu.ute.model.enums.CourseLevel;
import vn.edu.ute.repository.PlacementTestRepository;
import vn.edu.ute.repository.impl.PlacementTestRepositoryImpl;
import vn.edu.ute.service.PlacementTestService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class PlacementTestServiceImpl implements PlacementTestService {
    
    private final PlacementTestRepository testRepo = new PlacementTestRepositoryImpl();

    @Override
    public List<PlacementTest> getAllTests() {
        return testRepo.findAll();
    }

    @Override
    public PlacementTest addTest(PlacementTest test) {
        // Tự động set suggestedLevel trước khi lưu nếu chưa có
        if (test.getSuggestedLevel() == null && test.getScore() != null) {
            test.setSuggestedLevel(suggestLevel(test.getScore()));
        }
        return testRepo.save(test);
    }

    @Override
    public PlacementTest updateTest(PlacementTest test) {
        if (test.getScore() != null) {
            test.setSuggestedLevel(suggestLevel(test.getScore()));
        }
        return testRepo.save(test);
    }

    @Override
    public boolean deleteTest(Long id) {
        return testRepo.delete(id);
    }

    @Override
    public CourseLevel suggestLevel(BigDecimal score) {
        if (score == null) return null;
        
        double scoreVal = score.doubleValue();
        if (scoreVal < 4.0) {
            return CourseLevel.Beginner;
        } else if (scoreVal <= 6.5) {
            return CourseLevel.Intermediate;
        } else {
            return CourseLevel.Advanced;
        }
    }

    @Override
    public List<PlacementTest> findTestsByStudentId(Long studentId) {
        List<PlacementTest> allTests = testRepo.findAll();
        if (allTests == null) return List.of();
        
        return allTests.stream()
                .filter(t -> t.getStudent() != null && t.getStudent().getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }
}
