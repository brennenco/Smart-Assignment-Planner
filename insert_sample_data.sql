
-- Sample data insertion for Smart Assignment & Course Planner

-- Users
INSERT INTO `user` (username, email, display_name, role) VALUES
('alice', 'alice@example.edu', 'Alice Johnson', 'student'),
('bob', 'bob@example.edu', 'Bob Chen', 'student'),
('carol', 'carol@example.edu', 'Carol White', 'instructor'),
('dave', 'dave@example.edu', 'Dave Smith', 'student'),
('erin', 'erin@example.edu', 'Erin Park', 'instructor');

-- Courses
INSERT INTO `course` (course_code, title, description, term, instructor_id) VALUES
('CS101','Intro to Computer Science','Basics of CS','Fall 2025', 3),
('MATH201','Calculus I','Differential calculus','Fall 2025', 5),
('ENG150','College Writing','Rhetoric and composition','Fall 2025', 3);

-- Enrollments
INSERT INTO `enrollment` (user_id, course_id, enrollment_date) VALUES
(1,1,'2025-08-20'),
(2,1,'2025-08-21'),
(4,1,'2025-08-22'),
(1,2,'2025-08-20'),
(2,3,'2025-08-21');

-- Assignments
INSERT INTO `assignment` (course_id, title, description, points, due_date) VALUES
(1,'Homework 1','Intro exercises',100,'2025-09-15 23:59:00'),
(1,'Project Proposal','Group project proposal',200,'2025-10-01 23:59:00'),
(2,'Problem Set 1','Limits and continuity',100,'2025-09-10 23:59:00'),
(3,'Essay 1','Diagnostic essay',100,'2025-09-12 23:59:00'),
(1,'Quiz 1','Short quiz',20,'2025-09-05 10:00:00');

-- Submissions
INSERT INTO `submission` (assignment_id, user_id, submitted_at, file_path, grade, feedback) VALUES
(1,1,'2025-09-14 20:12:00','/submissions/alice_hw1.pdf',95,'Good work'),
(1,2,'2025-09-15 09:05:00','/submissions/bob_hw1.pdf',88,'Nice effort'),
(5,1,'2025-09-05 09:50:00','/submissions/alice_quiz1.pdf',18,'Well done'),
(2,1,'2025-09-30 14:00:00','/submissions/alice_proj_prop.pdf',NULL,NULL),
(4,2,'2025-09-11 11:00:00','/submissions/bob_essay1.pdf',92,'Strong thesis');

-- Reminders
INSERT INTO `reminder` (user_id, assignment_id, remind_at, method, sent_at, status) VALUES
(1,2,'2025-09-30 09:00:00','email',NULL,'pending'),
(2,1,'2025-09-14 08:00:00','push','2025-09-14 08:00:05','sent'),
(1,5,'2025-09-05 08:30:00','sms','2025-09-05 08:30:01','sent'),
(4,1,'2025-09-13 12:00:00','email',NULL,'pending');
