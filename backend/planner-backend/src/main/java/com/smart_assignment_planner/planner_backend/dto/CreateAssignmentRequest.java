package com.smart_assignment_planner.planner_backend.dto;

import com.smart_assignment_planner.planner_backend.model.AssignmentType;

import java.time.LocalDate;

public record CreateAssignmentRequest(
        Integer courseId,
        String title,
        String description,
        LocalDate dueDate,
        Integer priority,
        Integer pointsPossible,
        Integer pointsEarned,
        Boolean status,
        AssignmentType assignmentType
) {
}
