package vn.edu.ute.service.impl;

import vn.edu.ute.model.Course;
import vn.edu.ute.repository.CourseRepository;
import vn.edu.ute.repository.impl.CourseRepositoryImpl;
import vn.edu.ute.service.CourseService;

import java.util.List;
import java.util.stream.Collectors;

public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepo = new CourseRepositoryImpl();

    @Override
    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }

    @Override
    public List<Course> getActiveCourses() {
        List<Course> allCourses = courseRepo.findAll();
        if (allCourses == null)
            return java.util.List.of();
        return allCourses.stream()
                .filter(c -> c.getStatus() == vn.edu.ute.model.enums.Status.Active)
                .collect(Collectors.toList());
    }

    @Override
    public Course addCourse(Course course) {
        return courseRepo.save(course);
    }

    @Override
    public Course updateCourse(Course course) {
        return courseRepo.save(course);
    }

    @Override
    public boolean removeCourse(Long id) {
        return courseRepo.delete(id);
    }

    @Override
    public List<Course> searchCourseByName(String keyword) {
        List<Course> allCourses = courseRepo.findAll();
        if (allCourses == null)
            return List.of();

        return allCourses.stream()
                .filter(c -> c.getCourseName().toLowerCase().contains(keyword.toLowerCase())) // tìm theo tn
                .collect(Collectors.toList());
    }

    @Override
    public List<Course> getCoursesByLevel(String level) {
        List<Course> allCourses = courseRepo.findAll();
        if (allCourses == null)
            return List.of();

        return allCourses.stream()
                .filter(c -> c.getLevel().name().equalsIgnoreCase(level)) // Tiìm theo level
                .collect(Collectors.toList());
    }
}