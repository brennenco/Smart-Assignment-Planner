package com.smart_assignment_planner.planner_backend.dto;

import com.smart_assignment_planner.planner_backend.model.UserRole;

public record AdminUserUpdateRequest(String name, String email, UserRole role) {
}
