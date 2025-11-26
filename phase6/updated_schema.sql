-- ============================================================
-- SCHEMA FOR Smart Assignment Planner
-- Updated to match CRUD features and expanded sample data
-- ============================================================

CREATE DATABASE IF NOT EXISTS smart_assignment_planner;
USE smart_assignment_planner;

-- ============================================================
-- USERS TABLE
-- ============================================================
DROP TABLE IF EXISTS `User`;
CREATE TABLE `User` (
    email VARCHAR(100) PRIMARY KEY,
    name VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

-- ============================================================
-- COURSES TABLE
-- ============================================================
DROP TABLE IF EXISTS Course;
CREATE TABLE Course (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(150) NOT NULL
) ENGINE=InnoDB;

-- ============================================================
-- TAKES TABLE (USER ENROLLMENTS)
-- PRIMARY KEY = (email, course_id)
-- ============================================================
DROP TABLE IF EXISTS Takes;
CREATE TABLE Takes (
    email VARCHAR(100) NOT NULL,
    course_id INT NOT NULL,
    grade VARCHAR(5),
    PRIMARY KEY (email, course_id),
    FOREIGN KEY (email) REFERENCES `User`(email)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (course_id) REFERENCES Course(course_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- ASSIGNMENTS TABLE
-- completion_status = 0 (incomplete) or 1 (complete)
-- ============================================================
DROP TABLE IF EXISTS Assignment;
CREATE TABLE Assignment (
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    name VARCHAR(200) NOT NULL,
    due_date DATE,
    completion_status TINYINT(1) DEFAULT 0,
    type VARCHAR(50),
    FOREIGN KEY (course_id) REFERENCES Course(course_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;
