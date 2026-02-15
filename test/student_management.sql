
CREATE DATABASE student_management;
USE student_management;


CREATE TABLE academic_years (
  year_id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  year_name varchar(10) NOT NULL,
  start_date date NOT NULL,
  end_date date NOT NULL,
  is_current tinyint(1) DEFAULT 0
);

INSERT INTO academic_years (year_id, year_name, start_date, end_date, is_current) VALUES
(1, '2024-2025', '2024-09-01', '2025-06-30', 0),
(2, '2025-2026', '2025-09-01', '2026-06-30', 1);


CREATE TABLE announcements (
  announcement_id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  title varchar(200) NOT NULL,
  content text NOT NULL,
  target_audience enum('all','students','teachers','grade9','grade10','grade11','grade12') DEFAULT 'all',
  is_urgent tinyint(1) DEFAULT 0,
  posted_by varchar(50) DEFAULT NULL,
  posted_date timestamp NOT NULL DEFAULT current_timestamp(),
  expiry_date date DEFAULT NULL,
  attachment_filename varchar(255) DEFAULT NULL,
  attachment_path varchar(500) DEFAULT NULL
);

INSERT INTO announcements (announcement_id, title, content, target_audience, is_urgent, posted_by, posted_date, expiry_date, attachment_filename, attachment_path) VALUES
(1, 'assignment', 'bhgcvbnm , bjh gvrb', 'grade12', 1, 'Abebe Kebede', '2026-01-31 09:51:23', '2026-01-21', NULL, NULL),
(2, 'vdg', 'ydgsvyd gv g wu', 'grade10', 0, 'Abebe Kebede', '2026-01-31 17:12:38', NULL, NULL, NULL),
(3, 'file', 'this is chapter 3 os file read it', 'grade12', 0, 'Abebe Kebede', '2026-01-31 17:57:16', NULL, 'Chapter_3.pdf', 'announcement_uploads/Chapter_3_20260131205716238.pdf');


CREATE TABLE calendar (
  event_id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  event_name varchar(200) NOT NULL,
  event_date date NOT NULL,
  end_date date DEFAULT NULL,
  description text DEFAULT NULL,
  event_type enum('holiday','exam','meeting','event','deadline','semester_start','semester_end') NOT NULL,
  target_grades varchar(20) DEFAULT 'all',
  created_by varchar(50) DEFAULT NULL,
  created_at timestamp NOT NULL DEFAULT current_timestamp()
);


CREATE TABLE certificates (
  certificate_id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  certificate_number varchar(50) DEFAULT NULL UNIQUE,
  certificate_type enum('enrollment','transfer','transcript','completion') DEFAULT NULL,
  student_id varchar(20) DEFAULT NULL,
  student_name varchar(100) DEFAULT NULL,
  grade_level int(11) DEFAULT NULL,
  section varchar(10) DEFAULT NULL,
  issue_date date DEFAULT NULL,
  certificate_data text DEFAULT NULL,
  generated_by varchar(50) DEFAULT NULL,
  created_at timestamp NOT NULL DEFAULT current_timestamp()
);

CREATE TABLE enrollments (
  enrollment_id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  student_id varchar(20) DEFAULT NULL,
  academic_year varchar(20) DEFAULT NULL,
  grade_level int(11) DEFAULT NULL,
  section varchar(10) DEFAULT NULL,
  enrollment_type enum('New','Re-enrollment','Transfer') DEFAULT NULL,
  enrollment_date date DEFAULT NULL,
  status enum('Active','Pending','Withdrawn') DEFAULT NULL,
  created_at timestamp NOT NULL DEFAULT current_timestamp()
);


CREATE TABLE students (
  student_id varchar(50) NOT NULL PRIMARY KEY,
  user_id int(11) DEFAULT NULL,
  first_name varchar(50) NOT NULL,
  last_name varchar(50) NOT NULL,
  grade_level int(11) NOT NULL CHECK (grade_level between 9 and 12),
  section varchar(10) DEFAULT 'A',
  dob date DEFAULT NULL,
  gender enum('Male','Female') NOT NULL,
  email varchar(100) DEFAULT NULL,
  phone varchar(20) DEFAULT NULL,
  address text DEFAULT NULL,
  parent_name varchar(100) DEFAULT NULL,
  parent_phone varchar(20) DEFAULT NULL,
  registration_date date DEFAULT curdate(),
  registration_status enum('Active','Inactive','Graduated','Transferred') DEFAULT 'Active',
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);

INSERT INTO students (student_id, user_id, first_name, last_name, grade_level, section, dob, gender, email, phone, address, parent_name, parent_phone, registration_date, registration_status) VALUES
('STU10001', 16, 'Daniel', 'Haile', 10, 'A', '2009-04-20', 'Male', NULL, NULL, NULL, NULL, NULL, '2026-01-31', 'Active'),
('STU10002', 17, 'Bethlehem', 'Isaac', 10, 'A', '2009-08-12', 'Female', NULL, NULL, NULL, NULL, NULL, '2026-01-31', 'Active'),
('STU10003', 18, 'Mikael', 'Joseph', 10, 'B', '2009-02-28', 'Male', NULL, NULL, NULL, NULL, NULL, '2026-01-31', 'Active'),
('STU11001', 19, 'Feven', 'Kassa', 11, 'A', '2008-06-14', 'Female', NULL, NULL, NULL, NULL, NULL, '2026-01-31', 'Active'),
('STU11002', 20, 'Henok', 'Lemma', 11, 'A', '2008-10-30', 'Male', NULL, NULL, NULL, NULL, NULL, '2026-01-31', 'Active'),
('STU12001', 21, 'Lydia', 'Mengistu', 12, 'A', '2007-09-08', 'Female', NULL, NULL, NULL, NULL, NULL, '2026-01-31', 'Active'),
('STU12002', 22, 'Tewodros', 'Negash', 12, 'A', '2007-12-25', 'Male', NULL, NULL, NULL, NULL, NULL, '2026-01-31', 'Active'),
('STU20262614', 29, 'Beka', 'Beka', 10, 'A', '2026-02-02', 'Male', 'bereketlema0612@gmail.com', NULL, '1212', 'gfhf', '0982921750', '2026-02-03', 'Inactive'),
('STU20265464', 26, 'Beka', 'Beka', 9, 'A', '2026-02-02', 'Male', 'bereketlema0612@gmail.com', NULL, '1212', 'gfhf', '0982921750', '2026-02-03', 'Active'),
('STU20266091', 28, 'Beka', 'Beka', 9, 'A', '2026-02-02', 'Male', 'bereketlema0612@gmail.com', NULL, '1212', 'gfhf', '0982921750', '2026-02-03', 'Active'),
('STU20269320', 27, 'Beka', 'Beka', 10, 'A', '2026-02-01', 'Male', 'bereketlema0612@gmail.com', NULL, '1212', 'gfhf', '0982921750', '2026-02-03', 'Active'),
('STU9001', 11, 'Beka', 'Beka', 9, 'A', '2010-03-15', 'Female', 'bereketlema0612@gmail.com', NULL, '1212', 'gfhf', '0982921750', '2026-01-31', 'Active'),
('STU9002', 12, 'Nahom', 'Berhane', 9, 'A', '2010-07-22', 'Male', NULL, NULL, NULL, NULL, NULL, '2026-01-31', 'Active'),
('STU9003', 13, 'Selam', 'Desta', 9, 'A', '2010-01-10', 'Female', NULL, NULL, NULL, NULL, NULL, '2026-01-31', 'Active'),
('STU9004', 14, 'Abel', 'Fekadu', 9, 'B', '2010-11-05', 'Male', NULL, NULL, NULL, NULL, NULL, '2026-01-31', 'Active'),
('STU9005', 15, 'Ruth', 'Gebre', 9, 'B', '2010-05-18', 'Female', NULL, NULL, NULL, NULL, NULL, '2026-01-31', 'Active');


CREATE TABLE subjects (
  subject_id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  subject_code varchar(10) NOT NULL UNIQUE,
  subject_name varchar(50) NOT NULL,
  max_assignment decimal(4,2) DEFAULT 10.00,
  max_mid decimal(4,2) DEFAULT 30.00,
  max_final decimal(4,2) DEFAULT 60.00
);

INSERT INTO subjects (subject_id, subject_code, subject_name, max_assignment, max_mid, max_final) VALUES
(1, 'BIO', 'Biology', 10.00, 30.00, 60.00),
(2, 'ENG', 'English', 10.00, 30.00, 60.00),
(3, 'MAT', 'Mathematics', 10.00, 30.00, 60.00),
(4, 'CHE', 'Chemistry', 10.00, 30.00, 60.00),
(5, 'PHY', 'Physics', 10.00, 30.00, 60.00),
(6, 'ART', 'Art', 10.00, 30.00, 60.00),
(7, 'HIS', 'History', 10.00, 30.00, 60.00),
(8, 'GEO', 'Geography', 10.00, 30.00, 60.00),
(9, 'CIV', 'Civics', 10.00, 30.00, 60.00);

-- Teachers
CREATE TABLE teachers (
  teacher_id varchar(50) NOT NULL PRIMARY KEY,
  user_id int(11) DEFAULT NULL,
  first_name varchar(50) NOT NULL,
  last_name varchar(50) NOT NULL,
  email varchar(100) DEFAULT NULL,
  phone varchar(20) DEFAULT NULL,
  subject_specialty varchar(50) DEFAULT NULL,
  assigned_grades varchar(50) DEFAULT NULL,
  hire_date date DEFAULT NULL,
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);

INSERT INTO teachers (teacher_id, user_id, first_name, last_name, email, phone, subject_specialty, assigned_grades, hire_date) VALUES
('T001', 2, 'Abebe', 'Kebede', 'abebe.k@school.edu', NULL, 'Biology', '9,10,11,12', NULL),
('T002', 3, 'Sara', 'Hailu', 'sara.h@school.edu', NULL, 'English', '9,10,11,12', NULL),
('T003', 4, 'Dawit', 'Tadesse', 'dawit.t@school.edu', NULL, 'Mathematics', '9,10,11,12', NULL),
('T004', 5, 'Meron', 'Assefa', 'meron.a@school.edu', NULL, 'Chemistry', '9,10,11,12', NULL),
('T005', 6, 'Yonas', 'Girma', 'yonas.g@school.edu', NULL, 'Physics', '9,10,11,12', NULL),
('T006', 7, 'Hana', 'Bekele', 'hana.b@school.edu', NULL, 'Art', '9,10,11,12', NULL),
('T007', 8, 'Solomon', 'Tesfaye', 'solomon.t@school.edu', NULL, 'History', '9,10,11,12', NULL),
('T008', 9, 'Tigist', 'Mulugeta', 'tigist.m@school.edu', NULL, 'Geography', '9,10,11,12', NULL),
('T009', 10, 'Bereket', 'Alemu', 'bereket.a@school.edu', NULL, 'Civics', '9,10,11,12', NULL);

-- Users (Create LAST - due to foreign key dependencies)
CREATE TABLE users (
  user_id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username varchar(50) NOT NULL UNIQUE,
  password varchar(255) NOT NULL,
  role enum('student','teacher','admin','registrar','director') NOT NULL,
  is_active tinyint(1) DEFAULT 1,
  created_at timestamp NOT NULL DEFAULT current_timestamp(),
  last_login timestamp NULL DEFAULT NULL,
  email varchar(100) NOT NULL
);

INSERT INTO users (user_id, username, password, role, is_active, created_at, last_login, email) VALUES
(1, 'admin', '1234', 'admin', 1, '2026-01-31 06:50:31', NULL, ''),
(2, 'T001', '1234', 'teacher', 1, '2026-01-31 06:50:31', NULL, ''),
(3, 'T002', 'teacher123', 'teacher', 1, '2026-01-31 06:50:31', NULL, ''),
(4, 'T003', 'teacher123', 'teacher', 1, '2026-01-31 06:50:31', NULL, ''),
(5, 'T004', 'teacher123', 'teacher', 1, '2026-01-31 06:50:31', NULL, ''),
(6, 'T005', 'teacher123', 'teacher', 1, '2026-01-31 06:50:31', NULL, ''),
(7, 'T006', 'teacher123', 'teacher', 1, '2026-01-31 06:50:31', NULL, ''),
(8, 'T007', 'teacher123', 'teacher', 1, '2026-01-31 06:50:31', NULL, ''),
(9, 'T008', 'teacher123', 'teacher', 1, '2026-01-31 06:50:31', NULL, ''),
(10, 'T009', 'teacher123', 'teacher', 1, '2026-01-31 06:50:31', NULL, ''),
(11, 'STU9001', '1234', 'student', 1, '2026-01-31 06:50:31', NULL, ''),
(12, 'STU9002', 'student123', 'student', 1, '2026-01-31 06:50:31', NULL, ''),
(13, 'STU9003', 'student123', 'student', 1, '2026-01-31 06:50:31', NULL, ''),
(14, 'STU9004', 'student123', 'student', 1, '2026-01-31 06:50:31', NULL, ''),
(15, 'STU9005', 'student123', 'student', 1, '2026-01-31 06:50:31', NULL, ''),
(16, 'STU10001', 'student123', 'student', 1, '2026-01-31 06:50:31', NULL, ''),
(17, 'STU10002', 'student123', 'student', 1, '2026-01-31 06:50:31', NULL, ''),
(18, 'STU10003', 'student123', 'student', 1, '2026-01-31 06:50:31', NULL, ''),
(19, 'STU11001', 'student123', 'student', 1, '2026-01-31 06:50:31', NULL, ''),
(20, 'STU11002', 'student123', 'student', 1, '2026-01-31 06:50:31', NULL, ''),
(21, 'STU12001', '1234', 'student', 1, '2026-01-31 06:50:31', NULL, ''),
(22, 'STU12002', 'student123', 'student', 1, '2026-01-31 06:50:31', NULL, ''),
(24, 'regi001', '1234', 'registrar', 1, '2026-01-31 20:30:19', NULL, ''),
(25, 'regi002', '1234', 'registrar', 1, '2026-01-31 20:30:35', NULL, ''),
(26, 'bbeka81', 'default123', 'student', 1, '2026-02-03 01:07:14', NULL, 'bereketlema0612@gmail.com'),
(27, 'bbeka35', 'default123', 'student', 1, '2026-02-03 01:09:35', NULL, 'bereketlema0612@gmail.com'),
(28, 'bbeka21', 'default123', 'student', 1, '2026-02-03 01:44:20', NULL, 'bereketlema0612@gmail.com'),
(29, 'bbeka17', 'default123', 'student', 1, '2026-02-03 01:55:46', NULL, 'bereketlema0612@gmail.com');


CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    is_read TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);


ALTER TABLE enrollments MODIFY enrollment_type ENUM('New','Re-enrollment','Transfer','Promotion');