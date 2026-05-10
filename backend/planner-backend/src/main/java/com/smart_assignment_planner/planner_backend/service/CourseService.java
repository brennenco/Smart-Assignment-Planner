package com.smart_assignment_planner.planner_backend.service;

import com.smart_assignment_planner.planner_backend.dto.CreateCourseRequest;
import com.smart_assignment_planner.planner_backend.model.Course;
import com.smart_assignment_planner.planner_backend.model.User;
import com.smart_assignment_planner.planner_backend.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getCoursesForUser(Integer userId) {
        return courseRepository.findByUser_UserId(userId);
    }

    public Optional<Course> getCourseById(Integer id) {
        return courseRepository.findById(id);
    }

    /** Admin sees any course; user only own. */
    public Optional<Course> getCourseForViewer(Integer courseId, boolean admin, Integer userId) {
        if (admin) {
            return courseRepository.findById(courseId);
        }
        return courseRepository.findByCourseIdAndUser_UserId(courseId, userId);
    }

    @Transactional
    public Course createForUser(CreateCourseRequest request, User owner) {
        Course course = new Course();
        course.setCourseName(request.courseName());
        course.setTotalPoints(request.totalPoints());
        course.setUser(owner);
        return courseRepository.save(course);
    }

    @Transactional
    public Optional<Course> updateCourse(Integer id, Course patch, boolean admin, Integer userId) {
        return getCourseForViewer(id, admin, userId).map(existing -> {
            if (patch.getCourseName() != null) {
                existing.setCourseName(patch.getCourseName());
            }
            if (patch.getTotalPoints() != null) {
                existing.setTotalPoints(patch.getTotalPoints());
            }
            return courseRepository.save(existing);
        });
    }

    @Transactional
    public boolean deleteCourse(Integer id, boolean admin, Integer userId) {
        Optional<Course> opt = getCourseForViewer(id, admin, userId);
        if (opt.isEmpty()) {
            return false;
        }
        courseRepository.delete(opt.get());
        return true;
    }
}
