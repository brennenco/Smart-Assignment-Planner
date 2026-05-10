package com.smart_assignment_planner.planner_backend.controller;

import com.smart_assignment_planner.planner_backend.dto.CreateAssignmentRequest;
import com.smart_assignment_planner.planner_backend.dto.UpdateAssignmentRequest;
import com.smart_assignment_planner.planner_backend.model.Assignment;
import com.smart_assignment_planner.planner_backend.model.User;
import com.smart_assignment_planner.planner_backend.security.SecurityUtil;
import com.smart_assignment_planner.planner_backend.service.AssignmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping
    public List<Assignment> list() {
        boolean admin = SecurityUtil.isAdmin();
        Integer uid = SecurityUtil.currentUserId();
        if (admin) {
            return assignmentService.getAllAssignments();
        }
        return assignmentService.getAssignmentsForUser(uid);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assignment> getById(@PathVariable Integer id) {
        return assignmentService.getAssignmentForViewer(id, SecurityUtil.isAdmin(), SecurityUtil.currentUserId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Assignment> create(@RequestBody CreateAssignmentRequest request) {
        User owner = SecurityUtil.currentUserEntity();
        Assignment created = assignmentService.createForUser(request, owner);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Assignment> update(@PathVariable Integer id, @RequestBody UpdateAssignmentRequest request) {
        return assignmentService.updateAssignment(id, request, SecurityUtil.isAdmin(), SecurityUtil.currentUserId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (assignmentService.deleteAssignment(id, SecurityUtil.isAdmin(), SecurityUtil.currentUserId())) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
