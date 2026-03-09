package vn.edu.ute.service;

import vn.edu.ute.model.AcademicClass;
import vn.edu.ute.model.enums.ClassStatus;
import java.util.List;

public interface AcademicClassService {
    List<AcademicClass> getAllClasses();

    AcademicClass addClass(AcademicClass academicClass);

    AcademicClass updateClass(AcademicClass academicClass);

    boolean removeClass(Long id);

    // Sử dụng Lambda/Stream
    List<AcademicClass> searchClassByName(String keyword);

    List<AcademicClass> getClassesByStatus(ClassStatus status);

    List<AcademicClass> getClassesByCourse(Long courseId);
}
