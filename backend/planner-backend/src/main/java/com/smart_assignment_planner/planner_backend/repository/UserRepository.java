package com.smart_assignment_planner.planner_backend.repository;

import com.smart_assignment_planner.planner_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

}