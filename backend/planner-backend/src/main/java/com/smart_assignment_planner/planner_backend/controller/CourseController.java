package com.smart_assignment_planner.planner_backend.controller;

import com.smart_assignment_planner.planner_backend.dto.CreateCourseRequest;
import com.smart_assignment_planner.planner_backend.model.Course;
import com.smart_assignment_planner.planner_backend.model.User;
import com.smart_assignment_planner.planner_backend.security.SecurityUtil;
import com.smart_assignment_planner.planner_backend.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<Course> list() {
        boolean admin = SecurityUtil.isAdmin();
        Integer uid = SecurityUtil.currentUserId();
        if (admin) {
            return courseService.getAllCourses();
        }
        return courseService.getCoursesForUser(uid);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getById(@PathVariable Integer id) {
        return courseService.getCourseForViewer(id, SecurityUtil.isAdmin(), SecurityUtil.currentUserId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Course> create(@RequestBody CreateCourseRequest request) {
        User owner = SecurityUtil.currentUserEntity();
        Course created = courseService.createForUser(request, owner);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> update(@PathVariable Integer id, @RequestBody Course patch) {
        return courseService.updateCourse(id, patch, SecurityUtil.isAdmin(), SecurityUtil.currentUserId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (courseService.deleteCourse(id, SecurityUtil.isAdmin(), SecurityUtil.currentUserId())) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
