package com.smart_assignment_planner.planner_backend.repository;

import com.smart_assignment_planner.planner_backend.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {

}