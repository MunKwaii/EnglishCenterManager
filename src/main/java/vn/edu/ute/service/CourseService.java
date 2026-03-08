package vn.edu.ute.service;

import vn.edu.ute.model.Course;
import java.util.List;

public interface CourseService {
    List<Course> getAllCourses();

    List<Course> getActiveCourses();

    Course addCourse(Course course);

    Course updateCourse(Course course);

    boolean removeCourse(Long id);

    // 2 hàm dùng Lambda:
    List<Course> searchCourseByName(String keyword);

    List<Course> getCoursesByLevel(String level);
}