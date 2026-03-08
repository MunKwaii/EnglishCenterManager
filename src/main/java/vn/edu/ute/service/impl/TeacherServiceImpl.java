package vn.edu.ute.service.impl;

import vn.edu.ute.model.Teacher;
import vn.edu.ute.model.enums.Status;
import vn.edu.ute.repository.TeacherRepository;
import vn.edu.ute.repository.impl.TeacherRepositoryImpl;
import vn.edu.ute.service.TeacherService;

import java.util.List;
import java.util.stream.Collectors;

public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository teacherRepo = new TeacherRepositoryImpl();

    @Override
    public List<Teacher> getActiveTeachers() {
        List<Teacher> allTeachers = teacherRepo.findAll();
        if (allTeachers == null)
            return List.of();

        return allTeachers.stream()
                .filter(t -> t.getStatus() == Status.Active)
                .collect(Collectors.toList());
    }
}
