package com.smart_assignment_planner.planner_backend.service;

import com.smart_assignment_planner.planner_backend.model.Course;
import com.smart_assignment_planner.planner_backend.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // Get all courses
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // Get course by ID
    public Optional<Course> getCourseById(Integer id) {
        return courseRepository.findById(id);
    }

    // Create new course
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    // Update existing course
    public Optional<Course> updateCourse(Integer id, Course updated) {
        return courseRepository.findById(id).map(existing -> {
            existing.setCourseName(updated.getCourseName());
            existing.setTotalPoints(updated.getTotalPoints());
            existing.setUser(updated.getUser());
            existing.setAssignments(updated.getAssignments());
            return courseRepository.save(existing);
        });
    }

    // Delete course
    public boolean deleteCourse(Integer id) {
        return courseRepository.findById(id).map(c -> {
            courseRepository.delete(c);
            return true;
        }).orElse(false);
    }
}