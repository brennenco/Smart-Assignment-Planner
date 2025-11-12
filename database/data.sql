
-- Users
INSERT INTO User (email, name) VALUES
('alice@example.com', 'Alice Johnson'),
('bob@example.com', 'Bob Smith'),
('carol@example.com', 'Carol Lee'),
('dave@example.com', 'Dave Kim');

-- Courses
INSERT INTO Course (course_name) VALUES
('CS101 - Intro to Programming'),
('MATH201 - Calculus II'),
('HIST150 - World History'),
('PHYS101 - Physics I');

-- Takes relationships
INSERT INTO Takes (email, course_id) VALUES
('alice@example.com', 1),
('alice@example.com', 2),
('bob@example.com', 1),
('bob@example.com', 3),
('carol@example.com', 2),
('carol@example.com', 4),
('dave@example.com', 1),
('dave@example.com', 4);

-- Assignments
INSERT INTO Assignment (course_id, name, due_date, completion_status) VALUES
(1, 'Homework 1', '2025-11-15', FALSE),
(1, 'Project 1', '2025-11-20', FALSE),
(1, 'Midterm Exam', '2025-11-25', FALSE),
(2, 'Calculus Homework 1', '2025-11-17', TRUE),
(2, 'Calculus Homework 2', '2025-11-22', FALSE),
(3, 'Essay 1', '2025-11-18', TRUE),
(3, 'Essay 2', '2025-11-23', FALSE),
(4, 'Lab Report 1', '2025-11-16', TRUE),
(4, 'Lab Report 2', '2025-11-21', FALSE);