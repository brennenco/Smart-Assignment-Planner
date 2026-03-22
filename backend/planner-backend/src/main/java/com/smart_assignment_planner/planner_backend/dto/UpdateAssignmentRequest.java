package com.smart_assignment_planner.planner_backend.dto;

import com.smart_assignment_planner.planner_backend.model.AssignmentType;

import java.time.LocalDate;

/** Partial update: only non-null fields are applied. */
public record UpdateAssignmentRequest(
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
