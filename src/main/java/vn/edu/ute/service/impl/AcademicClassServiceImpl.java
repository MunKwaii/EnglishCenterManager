package vn.edu.ute.service.impl;

import vn.edu.ute.model.AcademicClass;
import vn.edu.ute.model.enums.ClassStatus;
import vn.edu.ute.repository.AcademicClassRepository;
import vn.edu.ute.repository.impl.AcademicClassRepositoryImpl;
import vn.edu.ute.service.AcademicClassService;

import java.util.List;
import java.util.stream.Collectors;

public class AcademicClassServiceImpl implements AcademicClassService {
    private final AcademicClassRepository classRepo = new AcademicClassRepositoryImpl();

    @Override
    public List<AcademicClass> getAllClasses() {
        return classRepo.findAll();
    }

    @Override
    public AcademicClass addClass(AcademicClass academicClass) {
        return classRepo.save(academicClass);
    }

    @Override
    public AcademicClass updateClass(AcademicClass academicClass) {
        return classRepo.save(academicClass);
    }

    @Override
    public boolean removeClass(Long id) {
        return classRepo.delete(id);
    }

    @Override
    public List<AcademicClass> searchClassByName(String keyword) {
        List<AcademicClass> allClasses = classRepo.findAll();
        if (allClasses == null)
            return List.of();

        return allClasses.stream()
                .filter(c -> c.getClassName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AcademicClass> getClassesByStatus(ClassStatus status) {
        List<AcademicClass> allClasses = classRepo.findAll();
        if (allClasses == null)
            return List.of();

        return allClasses.stream()
                .filter(c -> c.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<AcademicClass> getClassesByCourse(Long courseId) {
        List<AcademicClass> allClasses = classRepo.findAll();
        if (allClasses == null)
            return List.of();

        return allClasses.stream()
                .filter(c -> c.getCourse() != null && c.getCourse().getCourseId().equals(courseId))
                .collect(Collectors.toList());
    }

    @Override
    public List<AcademicClass> getClassesByTeacher(Long teacherId) {
        List<AcademicClass> allClasses = classRepo.findAll();
        if (allClasses == null)
            return List.of();

        return allClasses.stream()
                .filter(c -> c.getTeacher() != null && c.getTeacher().getTeacherId().equals(teacherId))
                .collect(Collectors.toList());
    }
}
