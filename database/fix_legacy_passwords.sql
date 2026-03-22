-- Fix login for users created BEFORE Spring Security (plain "hash_pw_*" is not BCrypt).
-- Run in MySQL after selecting your database:
--   mysql -u sap_user -p smart_assignment_planner < fix_legacy_passwords.sql
--
-- After this script:
--   John & Jane:  email as below, password = password123
--   Admin:        admin@planner.local, password = admin123
--
-- If your emails differ, run: SELECT user_id, email, role FROM user;
-- then edit the WHERE / INSERT below.

USE smart_assignment_planner;

-- BCrypt for "password123" (generated with Spring BCryptPasswordEncoder)
SET @pwd_user = '$2a$10$ST2Wvo260svD/2mM/RMWJeJpHVvu0DsLNEZAd8nMDof0.pn5Xfj52';
-- BCrypt for "admin123"
SET @pwd_admin = '$2a$10$rOT0C2/ERW/jSXn8cgXqMebgWkEWRqjJ6hHfMknRlmLiCWFVg.3pm';

-- 1) Reset passwords for the original seed users (adjust emails if yours differ)
UPDATE user
SET password_hash = @pwd_user,
    role = 'USER'
WHERE email IN ('JohnScholar@university.edu', 'JaneStudent@university.edu');

-- 2) Any other non-admin row that still looks like a legacy placeholder (optional)
UPDATE user
SET password_hash = @pwd_user,
    role = COALESCE(NULLIF(role, ''), 'USER')
WHERE email <> 'admin@planner.local'
  AND (password_hash LIKE 'hash_pw_%' OR password_hash NOT LIKE '$2a$%');

-- 3) Ensure an admin account exists (creates or updates by email)
INSERT INTO user (name, email, password_hash, role)
VALUES ('System Admin', 'admin@planner.local', @pwd_admin, 'ADMIN')
ON DUPLICATE KEY UPDATE
    password_hash = VALUES(password_hash),
    role = 'ADMIN';
