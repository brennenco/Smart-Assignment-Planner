USE smart_assignment_planner;

INSERT INTO `User` (email, name) VALUES
('alice@example.com', 'Alice Johnson'),
('bob@example.com', 'Bob Smith'),
('carol@example.com', 'Carol Lee'),
('dave@example.com', 'Dave Kim'),
('erin@example.com', 'Erin Park'),
('frank@example.com', 'Frank Liu'),
('gina@example.com', 'Gina Martinez'),
('henry@example.com', 'Henry O’Neal'),
('irina@example.com', 'Irina Petrov'),
('jason@example.com', 'Jason Clarke'),
('katie@example.com', 'Katie Nguyen'),
('leo@example.com', 'Leo Brown'),
('maria@example.com', 'Maria Sanchez'),
('nick@example.com', 'Nick Walters'),
('olivia@example.com', 'Olivia Chen'),
('peter@example.com', 'Peter Grant'),
('quinn@example.com', 'Quinn Davis'),
('rachel@example.com', 'Rachel Meyer'),
('sam@example.com', 'Sam Patel'),
('tina@example.com', 'Tina Robinson');

INSERT INTO Course (course_name) VALUES
('CS101 - Intro to Programming'),
('MATH201 - Calculus II'),
('HIST150 - World History'),
('PHYS101 - Physics I'),
('ENG200 - Composition'),
('BIO110 - Biology I'),
('CS201 - Data Structures'),
('MATH310 - Linear Algebra'),
('CHEM101 - General Chemistry'),
('PSY101 - Intro to Psychology');

INSERT INTO Takes (email, course_id, grade) VALUES
('alice@example.com', 1, 'A'),
('alice@example.com', 2, 'B+'),
('bob@example.com', 1, 'B'),
('bob@example.com', 3, 'A-'),
('carol@example.com', 2, 'A'),
('carol@example.com', 4, 'B'),
('dave@example.com', 1, 'C+'),
('dave@example.com', 4, 'B-'),
('erin@example.com', 5, NULL),
('frank@example.com', 6, NULL),
('gina@example.com', 1, NULL),
('henry@example.com', 3, NULL),
('irina@example.com', 2, NULL),
('jason@example.com', 4, NULL),
('katie@example.com', 5, NULL),
('leo@example.com', 6, NULL),
('maria@example.com', 7, 'A'),
('nick@example.com', 7, 'B'),
('olivia@example.com', 7, NULL),
('peter@example.com', 8, NULL),
('quinn@example.com', 8, 'A-'),
('rachel@example.com', 9, NULL),
('sam@example.com', 9, 'B+'),
('tina@example.com', 10, NULL),
('alice@example.com', 7, 'A'),
('bob@example.com', 10, NULL),
('carol@example.com', 5, NULL),
('dave@example.com', 6, 'B'),
('erin@example.com', 8, NULL),
('frank@example.com', 3, NULL),
('gina@example.com', 2, NULL),
('henry@example.com', 7, NULL),
('irina@example.com', 9, NULL),
('jason@example.com', 10, NULL),
('katie@example.com', 3, NULL),
('leo@example.com', 8, NULL),
('maria@example.com', 1, 'A'),
('nick@example.com', 2, NULL),
('olivia@example.com', 3, NULL),
('peter@example.com', 4, NULL);

INSERT INTO Assignment (course_id, name, due_date, completion_status, type) VALUES
-- CS101
(1,'Homework 1','2025-11-12',0,'homework'),
(1,'Homework 2','2025-11-19',1,'homework'),
(1,'Project 1','2025-11-25',0,'project'),
(1,'Quiz 1','2025-11-28',0,'quiz'),
(1,'Homework 3','2025-12-03',0,'homework'),
(1,'Final Exam','2025-12-10',0,'exam'),

-- MATH201
(2,'Calc HW 1','2025-11-10',1,'homework'),
(2,'Calc HW 2','2025-11-18',0,'homework'),
(2,'Quiz 1','2025-11-20',0,'quiz'),
(2,'Calc HW 3','2025-11-27',0,'homework'),
(2,'Midterm','2025-12-02',0,'exam'),
(2,'Final Exam','2025-12-12',0,'exam'),

-- HIST150
(3,'Essay 1','2025-11-11',0,'essay'),
(3,'Reading Reflection 1','2025-11-17',0,'reflection'),
(3,'Primary Sources Report','2025-11-24',1,'report'),
(3,'Essay 2','2025-11-29',0,'essay'),
(3,'Reading Reflection 2','2025-12-04',0,'reflection'),
(3,'Final Exam','2025-12-13',0,'exam'),

-- PHYS101
(4,'Lab 1','2025-11-09',1,'lab'),
(4,'Homework 1','2025-11-15',0,'homework'),
(4,'Lab 2','2025-11-22',0,'lab'),
(4,'Quiz 1','2025-11-26',0,'quiz'),
(4,'Homework 2','2025-12-01',0,'homework'),
(4,'Final Exam','2025-12-14',0,'exam'),

-- ENG200
(5,'Essay Draft','2025-11-08',1,'essay'),
(5,'Peer Review','2025-11-14',0,'review'),
(5,'Revised Essay','2025-11-23',0,'essay'),
(5,'Reading Journal','2025-11-30',0,'journal'),
(5,'Portfolio Review','2025-12-06',0,'portfolio'),
(5,'Final Paper','2025-12-15',0,'paper'),

-- BIO110
(6,'Lab Prep','2025-11-07',0,'lab'),
(6,'Lab Report 1','2025-11-16',1,'lab'),
(6,'Bio Quiz 1','2025-11-21',0,'quiz'),
(6,'Lab Report 2','2025-11-25',0,'lab'),
(6,'Bio Quiz 2','2025-12-03',0,'quiz'),
(6,'Final Exam','2025-12-16',0,'exam'),

-- CS201
(7,'Data HW 1','2025-11-09',0,'homework'),
(7,'Linked List Project','2025-11-18',0,'project'),
(7,'Quiz 1','2025-11-22',1,'quiz'),
(7,'Data HW 2','2025-11-28',0,'homework'),
(7,'Trees Project','2025-12-05',0,'project'),
(7,'Final Exam','2025-12-17',0,'exam'),

-- MATH310
(8,'LA HW 1','2025-11-11',0,'homework'),
(8,'LA HW 2','2025-11-20',0,'homework'),
(8,'Matrix Quiz','2025-11-23',0,'quiz'),
(8,'LA HW 3','2025-11-29',1,'homework'),
(8,'Midterm Exam','2025-12-07',0,'exam'),
(8,'Final Exam','2025-12-18',0,'exam'),

-- CHEM101
(9,'Lab Safety Assignment','2025-11-10',1,'lab'),
(9,'Chem HW 1','2025-11-16',0,'homework'),
(9,'Chem Quiz 1','2025-11-21',0,'quiz'),
(9,'Chem Lab 1','2025-11-27',0,'lab'),
(9,'Chem HW 2','2025-12-03',0,'homework'),
(9,'Final Exam','2025-12-19',0,'exam'),

-- PSY101
(10,'Reflection 1','2025-11-12',0,'reflection'),
(10,'Research Summary','2025-11-19',0,'summary'),
(10,'Quiz 1','2025-11-23',1,'quiz'),
(10,'Reflection 2','2025-11-30',0,'reflection'),
(10,'Project 1','2025-12-08',0,'project'),
(10,'Final Exam','2025-12-20',0,'exam');
