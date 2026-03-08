package vn.edu.ute.service.impl;

import vn.edu.ute.model.Teacher;
import vn.edu.ute.service.TeacherService;

import java.util.ArrayList;
import java.util.List;

public class TeacherServiceImpl implements TeacherService {

    @Override
    public List<Teacher> getActiveTeachers() {
        // Mock data for now since TeacherRepository is not fully implemented
        List<Teacher> teachers = new ArrayList<>();
        Teacher t1 = new Teacher();
        t1.setTeacherId(1L);
        // t1.setFullName("John Doe"); // Assuming simple naming, let's just return
        // empty/mocked depending on the model
        return teachers;
    }
}
