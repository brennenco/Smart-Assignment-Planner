package com.smart_assignment_planner.planner_backend.service;

import com.smart_assignment_planner.planner_backend.dto.CreateAssignmentRequest;
import com.smart_assignment_planner.planner_backend.dto.UpdateAssignmentRequest;
import com.smart_assignment_planner.planner_backend.model.Assignment;
import com.smart_assignment_planner.planner_backend.model.AssignmentType;
import com.smart_assignment_planner.planner_backend.model.Course;
import com.smart_assignment_planner.planner_backend.model.User;
import com.smart_assignment_planner.planner_backend.repository.AssignmentRepository;
import com.smart_assignment_planner.planner_backend.repository.CourseRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;

    public AssignmentService(AssignmentRepository assignmentRepository, CourseRepository courseRepository) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
    }

    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    public List<Assignment> getAssignmentsForUser(Integer userId) {
        return assignmentRepository.findByCourse_User_UserId(userId);
    }

    public Optional<Assignment> getAssignmentById(Integer id) {
        return assignmentRepository.findById(id);
    }

    public Optional<Assignment> getAssignmentForViewer(Integer assignmentId, boolean admin, Integer userId) {
        if (admin) {
            return assignmentRepository.findById(assignmentId);
        }
        return assignmentRepository.findByAssignmentIdAndCourse_User_UserId(assignmentId, userId);
    }

    @Transactional
    public Assignment createForUser(CreateAssignmentRequest request, User owner) {
        Course course = courseRepository.findByCourseIdAndUser_UserId(request.courseId(), owner.getUserId())
                .orElseThrow(() -> new AccessDeniedException("Course not found or not owned by you"));
        Assignment a = new Assignment();
        a.setStatus(request.status() != null ? request.status() : false);
        a.setTitle(request.title());
        a.setDescription(request.description());
        a.setDueDate(request.dueDate());
        a.setPriority(request.priority());
        a.setPointsPossible(request.pointsPossible());
        a.setPointsEarned(request.pointsEarned());
        a.setAssignmentType(request.assignmentType() != null ? request.assignmentType() : AssignmentType.ASSIGNMENT);
        a.setCourse(course);
        return assignmentRepository.save(a);
    }

    @Transactional
    public Optional<Assignment> updateAssignment(Integer id, UpdateAssignmentRequest req, boolean admin, Integer userId) {
        return getAssignmentForViewer(id, admin, userId).map(existing -> {
            if (req.courseId() != null) {
                Optional<Course> cOpt = admin
                        ? courseRepository.findById(req.courseId())
                        : courseRepository.findByCourseIdAndUser_UserId(req.courseId(), userId);
                Course c = cOpt.orElseThrow(() -> new AccessDeniedException("Course not found or not allowed"));
                existing.setCourse(c);
            }
            if (req.title() != null) {
                existing.setTitle(req.title());
            }
            if (req.description() != null) {
                existing.setDescription(req.description());
            }
            if (req.dueDate() != null) {
                existing.setDueDate(req.dueDate());
            }
            if (req.priority() != null) {
                existing.setPriority(req.priority());
            }
            if (req.status() != null) {
                existing.setStatus(req.status());
            }
            if (req.pointsPossible() != null) {
                existing.setPointsPossible(req.pointsPossible());
            }
            if (req.pointsEarned() != null) {
                existing.setPointsEarned(req.pointsEarned());
            }
            if (req.assignmentType() != null) {
                existing.setAssignmentType(req.assignmentType());
            }
            return assignmentRepository.save(existing);
        });
    }

    @Transactional
    public boolean deleteAssignment(Integer id, boolean admin, Integer userId) {
        Optional<Assignment> opt = getAssignmentForViewer(id, admin, userId);
        if (opt.isEmpty()) {
            return false;
        }
        assignmentRepository.delete(opt.get());
        return true;
    }
}
