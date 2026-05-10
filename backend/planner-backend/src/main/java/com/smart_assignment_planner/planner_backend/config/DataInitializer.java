package com.smart_assignment_planner.planner_backend.config;

import com.smart_assignment_planner.planner_backend.model.*;
import com.smart_assignment_planner.planner_backend.repository.AssignmentRepository;
import com.smart_assignment_planner.planner_backend.repository.CourseRepository;
import com.smart_assignment_planner.planner_backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

/**
 * Seeds an admin account and demo data when the database has no users.
 */
@Configuration
public class DataInitializer {

    @Bean
    @Order(1)
    CommandLineRunner seedDemoData(
            UserRepository userRepository,
            CourseRepository courseRepository,
            AssignmentRepository assignmentRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }

            User admin = new User();
            admin.setName("System Admin");
            admin.setEmail("admin@planner.local");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);

            User john = new User();
            john.setName("John Scholar");
            john.setEmail("JohnScholar@university.edu");
            john.setPasswordHash(passwordEncoder.encode("password123"));
            john.setRole(UserRole.USER);
            john = userRepository.save(john);

            User jane = new User();
            jane.setName("Jane Student");
            jane.setEmail("JaneStudent@university.edu");
            jane.setPasswordHash(passwordEncoder.encode("password123"));
            jane.setRole(UserRole.USER);
            jane = userRepository.save(jane);

            Course c1 = new Course();
            c1.setCourseName("CS 336 - Database Systems");
            c1.setTotalPoints(1000);
            c1.setUser(john);
            c1 = courseRepository.save(c1);

            Course c2 = new Course();
            c2.setCourseName("MATH 265 - Linear Algebra");
            c2.setTotalPoints(900);
            c2.setUser(john);
            c2 = courseRepository.save(c2);

            Course c3 = new Course();
            c3.setCourseName("CS 240 - Programming in C");
            c3.setTotalPoints(900);
            c3.setUser(jane);
            c3 = courseRepository.save(c3);

            saveAssignment(assignmentRepository, c1, true, "HW1 - ER Modeling", "Design ER diagrams",
                    LocalDate.of(2026, 2, 10), 1, 100, 95, AssignmentType.ASSIGNMENT);
            saveAssignment(assignmentRepository, c1, true, "Quiz 1 - SQL Basics", "Intro SQL quiz",
                    LocalDate.of(2026, 2, 10), 2, 50, 45, AssignmentType.QUIZ);
            saveAssignment(assignmentRepository, c1, false, "HW2 - Relational Schema", "Convert ER to schema",
                    LocalDate.of(2026, 2, 3), 1, 100, null, AssignmentType.ASSIGNMENT);

            saveAssignment(assignmentRepository, c2, true, "HW1 - Vectors", "Vector operations",
                    LocalDate.of(2026, 2, 10), 2, 50, 48, AssignmentType.ASSIGNMENT);

            saveAssignment(assignmentRepository, c3, true, "HW1 - Pointers", "Intro to pointers",
                    LocalDate.of(2026, 2, 10), 2, 80, 75, AssignmentType.ASSIGNMENT);
        };
    }

    private static void saveAssignment(
            AssignmentRepository assignmentRepository,
            Course course,
            boolean completed,
            String title,
            String description,
            LocalDate due,
            int priority,
            int ptsPossible,
            Integer ptsEarned,
            AssignmentType type) {
        Assignment a = new Assignment();
        a.setStatus(completed);
        a.setTitle(title);
        a.setDescription(description);
        a.setDueDate(due);
        a.setPriority(priority);
        a.setPointsPossible(ptsPossible);
        a.setPointsEarned(ptsEarned);
        a.setAssignmentType(type);
        a.setCourse(course);
        assignmentRepository.save(a);
    }
}
