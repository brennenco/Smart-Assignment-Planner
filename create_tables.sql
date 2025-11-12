
-- Table creation script for Smart Assignment & Course Planner
CREATE TABLE `user` (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(255) NOT NULL UNIQUE,
  display_name VARCHAR(100),
  role ENUM('student','instructor','admin') NOT NULL DEFAULT 'student',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `course` (
  course_id INT AUTO_INCREMENT PRIMARY KEY,
  course_code VARCHAR(20) NOT NULL UNIQUE,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  term VARCHAR(50),
  instructor_id INT NOT NULL,
  FOREIGN KEY (instructor_id) REFERENCES `user`(user_id)
);

CREATE TABLE `enrollment` (
  enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  course_id INT NOT NULL,
  enrollment_date DATE DEFAULT (CURRENT_DATE),
  UNIQUE KEY user_course_unique (user_id, course_id),
  FOREIGN KEY (user_id) REFERENCES `user`(user_id),
  FOREIGN KEY (course_id) REFERENCES `course`(course_id)
);

CREATE TABLE `assignment` (
  assignment_id INT AUTO_INCREMENT PRIMARY KEY,
  course_id INT NOT NULL,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  points DECIMAL(6,2) DEFAULT 0,
  due_date DATETIME,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (course_id) REFERENCES `course`(course_id)
);

CREATE TABLE `submission` (
  submission_id INT AUTO_INCREMENT PRIMARY KEY,
  assignment_id INT NOT NULL,
  user_id INT NOT NULL,
  submitted_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  file_path VARCHAR(1024),
  grade DECIMAL(5,2),
  feedback TEXT,
  UNIQUE KEY assignment_user_unique (assignment_id, user_id),
  FOREIGN KEY (assignment_id) REFERENCES `assignment`(assignment_id),
  FOREIGN KEY (user_id) REFERENCES `user`(user_id)
);

CREATE TABLE `reminder` (
  reminder_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  assignment_id INT NOT NULL,
  remind_at DATETIME,
  method ENUM('email','sms','push') DEFAULT 'email',
  sent_at DATETIME,
  status ENUM('pending','sent','failed') DEFAULT 'pending',
  FOREIGN KEY (user_id) REFERENCES `user`(user_id),
  FOREIGN KEY (assignment_id) REFERENCES `assignment`(assignment_id)
);
