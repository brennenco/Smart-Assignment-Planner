package com.smart_assignment_planner.planner_backend.dto;

import com.smart_assignment_planner.planner_backend.model.User;
import com.smart_assignment_planner.planner_backend.model.UserRole;

public record UserResponse(
        Integer userId,
        String name,
        String email,
        UserRole role
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
