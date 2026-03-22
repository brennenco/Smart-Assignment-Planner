package com.smart_assignment_planner.planner_backend.repository;

import com.smart_assignment_planner.planner_backend.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {

    List<Assignment> findByCourse_User_UserId(Integer userId);

    Optional<Assignment> findByAssignmentIdAndCourse_User_UserId(Integer assignmentId, Integer userId);
}