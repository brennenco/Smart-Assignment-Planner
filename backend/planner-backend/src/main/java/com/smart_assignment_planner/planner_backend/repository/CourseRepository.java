package com.smart_assignment_planner.planner_backend.repository;

import com.smart_assignment_planner.planner_backend.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Integer> {

}