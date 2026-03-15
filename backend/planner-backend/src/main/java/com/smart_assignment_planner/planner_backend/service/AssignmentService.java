package com.smart_assignment_planner.planner_backend.service;

import com.smart_assignment_planner.planner_backend.model.Assignment;
import com.smart_assignment_planner.planner_backend.repository.AssignmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    public AssignmentService(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    // Get all assignments
    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    // Get assignment by ID
    public Optional<Assignment> getAssignmentById(Integer id) {
        return assignmentRepository.findById(id);
    }

    // Create new assignment
    public Assignment createAssignment(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }

    // Update existing assignment
    public Optional<Assignment> updateAssignment(Integer id, Assignment updated) {
        return assignmentRepository.findById(id).map(existing -> {
            existing.setTitle(updated.getTitle());
            existing.setDescription(updated.getDescription());
            existing.setDueDate(updated.getDueDate());
            existing.setPriority(updated.getPriority());
            existing.setStatus(updated.getStatus());
            existing.setPointsPossible(updated.getPointsPossible());
            existing.setPointsEarned(updated.getPointsEarned());
            existing.setCourse(updated.getCourse());
            return assignmentRepository.save(existing);
        });
    }

    // Delete assignment
    public boolean deleteAssignment(Integer id) {
        return assignmentRepository.findById(id).map(a -> {
            assignmentRepository.delete(a);
            return true;
        }).orElse(false);
    }
}