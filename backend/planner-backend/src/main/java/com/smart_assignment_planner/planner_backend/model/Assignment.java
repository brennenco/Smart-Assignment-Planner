package com.smart_assignment_planner.planner_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "assignment")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer assignmentId;

    private Boolean status;

    private String title;

    private String description;

    private LocalDate dueDate;

    private Integer priority;

    private Integer pointsPossible;

    private Integer pointsEarned;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_type")
    private AssignmentType assignmentType = AssignmentType.ASSIGNMENT;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonIgnore // Prevent infinite JSON recursion (Assignment -> course -> assignments -> ...)
    private Course course;

    public Integer getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Integer assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getPointsPossible() {
        return pointsPossible;
    }

    public void setPointsPossible(Integer pointsPossible) {
        this.pointsPossible = pointsPossible;
    }

    public Integer getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(Integer pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public AssignmentType getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(AssignmentType assignmentType) {
        this.assignmentType = assignmentType;
    }

    @JsonProperty("courseId")
    public Integer getCourseIdForJson() {
        return course != null ? course.getCourseId() : null;
    }

    /** Course title for API consumers (course entity is not serialized). */
    @JsonProperty("courseName")
    public String getCourseNameForJson() {
        return course != null ? course.getCourseName() : null;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}