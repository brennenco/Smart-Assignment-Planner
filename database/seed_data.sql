-- Select the database to use
USE smart_assignment_planner;

-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- Reset all tables
TRUNCATE assignment;
TRUNCATE course;
TRUNCATE user;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Insert users
INSERT INTO user (name, email, password_hash) VALUES
('John Scholar', 'JohnScholar@university.edu', 'hash_pw_1'),
('Jane Student', 'JaneStudent@university.edu', 'hash_pw_2');

-- Insert courses
INSERT INTO course (course_name, total_points, user_id) VALUES
('CS 336 – Database Systems', 1000, 1),
('MATH 265 – Linear Algebra', 900, 1),
('STAT 350 – Probability', 850, 1),
('ENG 101 – Academic Writing', 700, 1),
('CS 240 – Programming in C', 900, 2),
('HIST 210 – World History', 800, 2);

-- =========================
-- CS 336 – Database Systems
-- =========================
INSERT INTO assignment
(status, title, description, due_date, priority, points_possible, points_earned, course_id)
VALUES
-- Two assignments due the same day (overlapping due dates)
(1, 'HW1 – ER Modeling', 'Design ER diagrams', '2026-02-10', 1, 100, 95, 1),
(1, 'Quiz 1 – SQL Basics', 'Intro SQL quiz', '2026-02-10', 2, 50, 45, 1),

-- Completed assignment with zero points earned (edge case)
(1, 'Participation Check', 'In-class activity', '2026-02-17', 3, 10, 0, 1),

-- Past-due but incomplete assignment
(0, 'HW2 – Relational Schema', 'Convert ER to schema', '2026-02-03', 1, 100, NULL, 1),

-- Large, low-priority assignment
(0, 'Final Project', 'Smart Assignment Planner', '2026-04-28', 3, 250, NULL, 1);

-- =========================
-- MATH 265 – Linear Algebra
-- =========================
INSERT INTO assignment VALUES
-- Same due date as CS 336 assignments (cross-course overlap)
(NULL, 1, 'HW1 – Vectors', 'Vector operations', '2026-02-10', 2, 50, 48, 2),

-- Two assignments due on the same day within the same course
(NULL, 1, 'HW2 – Matrices', 'Matrix algebra', '2026-03-12', 2, 50, 45, 2),
(NULL, 0, 'Quiz 1', 'Matrix properties', '2026-03-12', 3, 30, NULL, 2),

-- Final exam
(NULL, 0, 'Final Exam', 'Comprehensive', '2026-05-05', 1, 200, NULL, 2);

-- =========================
-- STAT 350 – Probability
-- =========================
INSERT INTO assignment VALUES
-- Assignment with same title as another course (edge case)
(NULL, 1, 'HW1 – Vectors', 'Vector concepts in probability', '2026-02-10', 2, 60, 55, 3),

-- Normal lab
(NULL, 1, 'Lab 1 – Simulations', 'Monte Carlo lab', '2026-02-20', 2, 40, 38, 3),

-- Incomplete midterm already past due
(NULL, 0, 'Midterm Exam', 'Weeks 1–6', '2026-03-01', 1, 150, NULL, 3);

-- =========================
-- ENG 101 – Academic Writing
-- =========================
INSERT INTO assignment VALUES
-- Two essays due the same day
(NULL, 1, 'Essay 1 – Narrative', 'Personal narrative essay', '2026-03-05', 2, 100, 88, 4),
(NULL, 0, 'Essay 2 – Analysis', 'Textual analysis', '2026-03-05', 1, 120, NULL, 4),

-- High-value final paper
(NULL, 0, 'Final Research Paper', '10–12 page paper', '2026-05-02', 1, 200, NULL, 4);

-- =========================
-- CS 240 – Programming in C
-- =========================
INSERT INTO assignment VALUES
-- Same due date as multiple other courses
(NULL, 1, 'HW1 – Pointers', 'Intro to pointers', '2026-02-10', 2, 80, 75, 5),

-- Midterm
(NULL, 0, 'Midterm Exam', 'C fundamentals', '2026-03-11', 1, 150, NULL, 5);

-- =========================
-- HIST 210 – World History
-- =========================
INSERT INTO assignment VALUES
-- Overlapping due date with multiple other courses
(NULL, 1, 'Reading Response 1', 'Early civilizations', '2026-02-10', 3, 25, 22, 6),

-- Low priority but high points
(NULL, 0, 'Document Analysis', 'Primary source analysis', '2026-03-05', 3, 150, NULL, 6),

-- Completed assignment exactly on due date
(NULL, 1, 'Quiz 1 – Ancient Empires', 'Short answer quiz', '2026-02-20', 2, 50, 50, 6),

-- Past-due and incomplete assignment
(NULL, 0, 'Midterm Exam', 'Chapters 1–6', '2026-03-01', 1, 200, NULL, 6),

-- Final project with same due date as another course’s final
(NULL, 0, 'Final Research Essay', '8–10 page research paper', '2026-05-05', 1, 250, NULL, 6);
