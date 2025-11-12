Create DATABASE smart_assignment_planner;
USE smart_assignment_planner;

-- Users
Create Table If Not Exists User (email VARCHAR(100) PRIMARY KEY, name VARCHAR(100));

-- Courses
Create Table if Not Exists Course(course_id INT AUTO_INCREMENT PRIMARY KEY, course_name VARCHAR(100) NOT NULL);

-- Enrollments (which student takes which course)
Create Table If Not Exists Takes(email VARCHAR(100), course_id INT, grade VARCHAR(5), 
PRIMARY KEY (email, course_id), 
FOREIGN KEY (email) REFERENCES User(email),
FOREIGN KEY (course_id) REFERENCES Course(course_id));

Create Table If Not Exists Assignment(assignment_id INT AUTO_INCREMENT PRIMARY KEY, Course_id INT, name VARCHAR(100) NOT NULL, due_date DATE, completion_status VARCHAR(20), type VARCHAR(20), FOREIGN KEY (course_id) REFERENCES Course(course_id));

