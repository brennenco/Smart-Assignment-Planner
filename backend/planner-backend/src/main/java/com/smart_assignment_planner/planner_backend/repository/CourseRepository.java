package com.smart_assignment_planner.planner_backend.repository;

import com.smart_assignment_planner.planner_backend.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Integer> {

    List<Course> findByUser_UserId(Integer userId);

    Optional<Course> findByCourseIdAndUser_UserId(Integer courseId, Integer userId);
}