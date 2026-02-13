CREATE DATABASE grade12_db;
USE grade12_db;


CREATE TABLE art (
  result_id int(11) NOT NULL,
  student_id varchar(50) NOT NULL,
  student_name varchar(100) NOT NULL,
  academic_year varchar(10) NOT NULL DEFAULT '2025-2026',
  semester enum('First','Second') NOT NULL,
  assignment_score decimal(4,2) DEFAULT 0.00 CHECK (assignment_score >= 0 and assignment_score <= 10),
  mid_score decimal(4,2) DEFAULT 0.00 CHECK (mid_score >= 0 and mid_score <= 30),
  final_score decimal(4,2) DEFAULT 0.00 CHECK (final_score >= 0 and final_score <= 60),
  total_score decimal(5,2) GENERATED ALWAYS AS (assignment_score + mid_score + final_score) STORED,
  grade_letter char(2) GENERATED ALWAYS AS (
    case 
      when assignment_score + mid_score + final_score >= 90 then 'A+'
      when assignment_score + mid_score + final_score >= 85 then 'A'
      when assignment_score + mid_score + final_score >= 80 then 'B+'
      when assignment_score + mid_score + final_score >= 75 then 'B'
      when assignment_score + mid_score + final_score >= 70 then 'C+'
      when assignment_score + mid_score + final_score >= 60 then 'C'
      when assignment_score + mid_score + final_score >= 50 then 'D'
      else 'F'
    end
  ) STORED,
  graded_by varchar(50) DEFAULT NULL,
  graded_date timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (result_id),
  UNIQUE KEY unique_student_semester (student_id, academic_year, semester)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO art (result_id, student_id, student_name, academic_year, semester, assignment_score, mid_score, final_score, graded_by, graded_date) VALUES
(1, 'STU12001', 'Lydia Mengistu', '2025-2026', 'First', 9.50, 28.00, 56.00, 'T006', '2026-01-31 07:26:54'),
(2, 'STU12002', 'Tewodros Negash', '2025-2026', 'First', 7.00, 21.00, 42.00, 'T006', '2026-01-31 07:26:54'),
(3, 'STU12001', 'Lydia Mengistu', '2025-2026', 'Second', 9.50, 28.00, 56.00, 'T006', '2026-01-31 07:26:54'),
(4, 'STU12002', 'Tewodros Negash', '2025-2026', 'Second', 7.00, 21.00, 42.00, 'T006', '2026-01-31 07:26:54');

ALTER TABLE art MODIFY result_id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

CREATE TABLE biology (
  result_id int(11) NOT NULL,
  student_id varchar(50) NOT NULL,
  student_name varchar(100) NOT NULL,
  academic_year varchar(10) NOT NULL DEFAULT '2025-2026',
  semester enum('First','Second') NOT NULL,
  assignment_score decimal(4,2) DEFAULT 0.00 CHECK (assignment_score >= 0 and assignment_score <= 10),
  mid_score decimal(4,2) DEFAULT 0.00 CHECK (mid_score >= 0 and mid_score <= 30),
  final_score decimal(4,2) DEFAULT 0.00 CHECK (final_score >= 0 and final_score <= 60),
  total_score decimal(5,2) GENERATED ALWAYS AS (assignment_score + mid_score + final_score) STORED,
  grade_letter char(2) GENERATED ALWAYS AS (
    case 
      when assignment_score + mid_score + final_score >= 90 then 'A+'
      when assignment_score + mid_score + final_score >= 85 then 'A'
      when assignment_score + mid_score + final_score >= 80 then 'B+'
      when assignment_score + mid_score + final_score >= 75 then 'B'
      when assignment_score + mid_score + final_score >= 70 then 'C+'
      when assignment_score + mid_score + final_score >= 60 then 'C'
      when assignment_score + mid_score + final_score >= 50 then 'D'
      else 'F'
    end
  ) STORED,
  graded_by varchar(50) DEFAULT NULL,
  graded_date timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (result_id),
  UNIQUE KEY unique_student_semester (student_id, academic_year, semester)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO biology (result_id, student_id, student_name, academic_year, semester, assignment_score, mid_score, final_score, graded_by, graded_date) VALUES
(1, 'STU12001', 'Lydia Mengistu', '2025-2026', 'First', 9.50, 28.00, 56.00, 'T001', '2026-01-31 07:26:54'),
(2, 'STU12002', 'Tewodros Negash', '2025-2026', 'First', 8.50, 26.00, 52.00, 'T001', '2026-01-31 07:26:54'),
(3, 'STU12001', 'Lydia Mengistu', '2025-2026', 'Second', NULL, 15.00, 30.20, 'T001', '2026-01-31 09:41:44'),
(4, 'STU12002', 'Tewodros Negash', '2025-2026', 'Second', NULL, 26.00, 52.00, 'T001', '2026-01-31 09:41:00');

ALTER TABLE biology MODIFY result_id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

CREATE TABLE chemistry (
  result_id int(11) NOT NULL,
  student_id varchar(50) NOT NULL,
  student_name varchar(100) NOT NULL,
  academic_year varchar(10) NOT NULL DEFAULT '2025-2026',
  semester enum('First','Second') NOT NULL,
  assignment_score decimal(4,2) DEFAULT 0.00 CHECK (assignment_score >= 0 and assignment_score <= 10),
  mid_score decimal(4,2) DEFAULT 0.00 CHECK (mid_score >= 0 and mid_score <= 30),
  final_score decimal(4,2) DEFAULT 0.00 CHECK (final_score >= 0 and final_score <= 60),
  total_score decimal(5,2) GENERATED ALWAYS AS (assignment_score + mid_score + final_score) STORED,
  grade_letter char(2) GENERATED ALWAYS AS (
    case 
      when assignment_score + mid_score + final_score >= 90 then 'A+'
      when assignment_score + mid_score + final_score >= 85 then 'A'
      when assignment_score + mid_score + final_score >= 80 then 'B+'
      when assignment_score + mid_score + final_score >= 75 then 'B'
      when assignment_score + mid_score + final_score >= 70 then 'C+'
      when assignment_score + mid_score + final_score >= 60 then 'C'
      when assignment_score + mid_score + final_score >= 50 then 'D'
      else 'F'
    end
  ) STORED,
  graded_by varchar(50) DEFAULT NULL,
  graded_date timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (result_id),
  UNIQUE KEY unique_student_semester (student_id, academic_year, semester)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO chemistry (result_id, student_id, student_name, academic_year, semester, assignment_score, mid_score, final_score, graded_by, graded_date) VALUES
(1, 'STU12001', 'Lydia Mengistu', '2025-2026', 'First', 8.50, 26.00, 52.00, 'T004', '2026-01-31 07:26:54'),
(2, 'STU12002', 'Tewodros Negash', '2025-2026', 'First', 8.00, 24.00, 48.00, 'T004', '2026-01-31 07:26:54'),
(3, 'STU12001', 'Lydia Mengistu', '2025-2026', 'Second', 1.00, 8.00, 20.00, 'T004', '2026-01-31 07:26:54'),
(4, 'STU12002', 'Tewodros Negash', '2025-2026', 'Second', 8.00, 24.00, 48.00, 'T004', '2026-01-31 07:26:54');

ALTER TABLE chemistry MODIFY result_id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

CREATE TABLE civics (
  result_id int(11) NOT NULL,
  student_id varchar(50) NOT NULL,
  student_name varchar(100) NOT NULL,
  academic_year varchar(10) NOT NULL DEFAULT '2025-2026',
  semester enum('First','Second') NOT NULL,
  assignment_score decimal(4,2) DEFAULT 0.00 CHECK (assignment_score >= 0 and assignment_score <= 10),
  mid_score decimal(4,2) DEFAULT 0.00 CHECK (mid_score >= 0 and mid_score <= 30),
  final_score decimal(4,2) DEFAULT 0.00 CHECK (final_score >= 0 and final_score <= 60),
  total_score decimal(5,2) GENERATED ALWAYS AS (assignment_score + mid_score + final_score) STORED,
  grade_letter char(2) GENERATED ALWAYS AS (
    case 
      when assignment_score + mid_score + final_score >= 90 then 'A+'
      when assignment_score + mid_score + final_score >= 85 then 'A'
      when assignment_score + mid_score + final_score >= 80 then 'B+'
      when assignment_score + mid_score + final_score >= 75 then 'B'
      when assignment_score + mid_score + final_score >= 70 then 'C+'
      when assignment_score + mid_score + final_score >= 60 then 'C'
      when assignment_score + mid_score + final_score >= 50 then 'D'
      else 'F'
    end
  ) STORED,
  graded_by varchar(50) DEFAULT NULL,
  graded_date timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (result_id),
  UNIQUE KEY unique_student_semester (student_id, academic_year, semester)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO civics (result_id, student_id, student_name, academic_year, semester, assignment_score, mid_score, final_score, graded_by, graded_date) VALUES
(1, 'STU12001', 'Lydia Mengistu', '2025-2026', 'First', 9.50, 28.00, 55.00, 'T009', '2026-01-31 07:26:54'),
(2, 'STU12002', 'Tewodros Negash', '2025-2026', 'First', 8.00, 24.00, 50.00, 'T009', '2026-01-31 07:26:54'),
(3, 'STU12001', 'Lydia Mengistu', '2025-2026', 'Second', 4.00, 8.00, 40.00, 'T009', '2026-01-31 07:26:54'),
(4, 'STU12002', 'Tewodros Negash', '2025-2026', 'Second', 8.00, 24.00, 50.00, 'T009', '2026-01-31 07:26:54');

ALTER TABLE civics MODIFY result_id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

CREATE TABLE english (
  result_id int(11) NOT NULL,
  student_id varchar(50) NOT NULL,
  student_name varchar(100) NOT NULL,
  academic_year varchar(10) NOT NULL DEFAULT '2025-2026',
  semester enum('First','Second') NOT NULL,
  assignment_score decimal(4,2) DEFAULT 0.00 CHECK (assignment_score >= 0 and assignment_score <= 10),
  mid_score decimal(4,2) DEFAULT 0.00 CHECK (mid_score >= 0 and mid_score <= 30),
  final_score decimal(4,2) DEFAULT 0.00 CHECK (final_score >= 0 and final_score <= 60),
  total_score decimal(5,2) GENERATED ALWAYS AS (assignment_score + mid_score + final_score) STORED,
  grade_letter char(2) GENERATED ALWAYS AS (
    case 
      when assignment_score + mid_score + final_score >= 90 then 'A+'
      when assignment_score + mid_score + final_score >= 85 then 'A'
      when assignment_score + mid_score + final_score >= 80 then 'B+'
      when assignment_score + mid_score + final_score >= 75 then 'B'
      when assignment_score + mid_score + final_score >= 70 then 'C+'
      when assignment_score + mid_score + final_score >= 60 then 'C'
      when assignment_score + mid_score + final_score >= 50 then 'D'
      else 'F'
    end
  ) STORED,
  graded_by varchar(50) DEFAULT NULL,
  graded_date timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (result_id),
  UNIQUE KEY unique_student_semester (student_id, academic_year, semester)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO english (result_id, student_id, student_name, academic_year, semester, assignment_score, mid_score, final_score, graded_by, graded_date) VALUES
(1, 'STU12001', 'Lydia Mengistu', '2025-2026', 'First', 10.00, 29.00, 58.00, 'T002', '2026-01-31 07:26:54'),
(2, 'STU12002', 'Tewodros Negash', '2025-2026', 'First', 8.00, 24.00, 48.00, 'T002', '2026-01-31 07:26:54'),
(3, 'STU12001', 'Lydia Mengistu', '2025-2026', 'Second', 10.00, 29.00, 58.00, 'T002', '2026-01-31 07:26:54'),
(4, 'STU12002', 'Tewodros Negash', '2025-2026', 'Second', 8.00, 24.00, 48.00, 'T002', '2026-01-31 07:26:54');

ALTER TABLE english MODIFY result_id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

CREATE TABLE geography (
  result_id int(11) NOT NULL,
  student_id varchar(50) NOT NULL,
  student_name varchar(100) NOT NULL,
  academic_year varchar(10) NOT NULL DEFAULT '2025-2026',
  semester enum('First','Second') NOT NULL,
  assignment_score decimal(4,2) DEFAULT 0.00 CHECK (assignment_score >= 0 and assignment_score <= 10),
  mid_score decimal(4,2) DEFAULT 0.00 CHECK (mid_score >= 0 and mid_score <= 30),
  final_score decimal(4,2) DEFAULT 0.00 CHECK (final_score >= 0 and final_score <= 60),
  total_score decimal(5,2) GENERATED ALWAYS AS (assignment_score + mid_score + final_score) STORED,
  grade_letter char(2) GENERATED ALWAYS AS (
    case 
      when assignment_score + mid_score + final_score >= 90 then 'A+'
      when assignment_score + mid_score + final_score >= 85 then 'A'
      when assignment_score + mid_score + final_score >= 80 then 'B+'
      when assignment_score + mid_score + final_score >= 75 then 'B'
      when assignment_score + mid_score + final_score >= 70 then 'C+'
      when assignment_score + mid_score + final_score >= 60 then 'C'
      when assignment_score + mid_score + final_score >= 50 then 'D'
      else 'F'
    end
  ) STORED,
  graded_by varchar(50) DEFAULT NULL,
  graded_date timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (result_id),
  UNIQUE KEY unique_student_semester (student_id, academic_year, semester)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO geography (result_id, student_id, student_name, academic_year, semester, assignment_score, mid_score, final_score, graded_by, graded_date) VALUES
(1, 'STU12001', 'Lydia Mengistu', '2025-2026', 'First', 8.50, 26.00, 52.00, 'T008', '2026-01-31 07:26:54'),
(2, 'STU12002', 'Tewodros Negash', '2025-2026', 'First', 9.00, 27.00, 54.00, 'T008', '2026-01-31 07:26:54'),
(3, 'STU12001', 'Lydia Mengistu', '2025-2026', 'Second', 8.50, 26.00, 52.00, 'T008', '2026-01-31 07:26:54'),
(4, 'STU12002', 'Tewodros Negash', '2025-2026', 'Second', 9.00, 27.00, 54.00, 'T008', '2026-01-31 07:26:54');

ALTER TABLE geography MODIFY result_id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

CREATE TABLE history (
  result_id int(11) NOT NULL,
  student_id varchar(50) NOT NULL,
  student_name varchar(100) NOT NULL,
  academic_year varchar(10) NOT NULL DEFAULT '2025-2026',
  semester enum('First','Second') NOT NULL,
  assignment_score decimal(4,2) DEFAULT 0.00 CHECK (assignment_score >= 0 and assignment_score <= 10),
  mid_score decimal(4,2) DEFAULT 0.00 CHECK (mid_score >= 0 and mid_score <= 30),
  final_score decimal(4,2) DEFAULT 0.00 CHECK (final_score >= 0 and final_score <= 60),
  total_score decimal(5,2) GENERATED ALWAYS AS (assignment_score + mid_score + final_score) STORED,
  grade_letter char(2) GENERATED ALWAYS AS (
    case 
      when assignment_score + mid_score + final_score >= 90 then 'A+'
      when assignment_score + mid_score + final_score >= 85 then 'A'
      when assignment_score + mid_score + final_score >= 80 then 'B+'
      when assignment_score + mid_score + final_score >= 75 then 'B'
      when assignment_score + mid_score + final_score >= 70 then 'C+'
      when assignment_score + mid_score + final_score >= 60 then 'C'
      when assignment_score + mid_score + final_score >= 50 then 'D'
      else 'F'
    end
  ) STORED,
  graded_by varchar(50) DEFAULT NULL,
  graded_date timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (result_id),
  UNIQUE KEY unique_student_semester (student_id, academic_year, semester)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO history (result_id, student_id, student_name, academic_year, semester, assignment_score, mid_score, final_score, graded_by, graded_date) VALUES
(1, 'STU12001', 'Lydia Mengistu', '2025-2026', 'First', 9.00, 27.00, 54.00, 'T007', '2026-01-31 07:26:54'),
(2, 'STU12002', 'Tewodros Negash', '2025-2026', 'First', 8.50, 26.00, 52.00, 'T007', '2026-01-31 07:26:54'),
(3, 'STU12001', 'Lydia Mengistu', '2025-2026', 'Second', 9.00, 27.00, 54.00, 'T007', '2026-01-31 07:26:54'),
(4, 'STU12002', 'Tewodros Negash', '2025-2026', 'Second', 8.50, 26.00, 52.00, 'T007', '2026-01-31 07:26:54');

ALTER TABLE history MODIFY result_id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

CREATE TABLE mathematics (
  result_id int(11) NOT NULL,
  student_id varchar(50) NOT NULL,
  student_name varchar(100) NOT NULL,
  academic_year varchar(10) NOT NULL DEFAULT '2025-2026',
  semester enum('First','Second') NOT NULL,
  assignment_score decimal(4,2) DEFAULT 0.00 CHECK (assignment_score >= 0 and assignment_score <= 10),
  mid_score decimal(4,2) DEFAULT 0.00 CHECK (mid_score >= 0 and mid_score <= 30),
  final_score decimal(4,2) DEFAULT 0.00 CHECK (final_score >= 0 and final_score <= 60),
  total_score decimal(5,2) GENERATED ALWAYS AS (assignment_score + mid_score + final_score) STORED,
  grade_letter char(2) GENERATED ALWAYS AS (
    case 
      when assignment_score + mid_score + final_score >= 90 then 'A+'
      when assignment_score + mid_score + final_score >= 85 then 'A'
      when assignment_score + mid_score + final_score >= 80 then 'B+'
      when assignment_score + mid_score + final_score >= 75 then 'B'
      when assignment_score + mid_score + final_score >= 70 then 'C+'
      when assignment_score + mid_score + final_score >= 60 then 'C'
      when assignment_score + mid_score + final_score >= 50 then 'D'
      else 'F'
    end
  ) STORED,
  graded_by varchar(50) DEFAULT NULL,
  graded_date timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (result_id),
  UNIQUE KEY unique_student_semester (student_id, academic_year, semester)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO mathematics (result_id, student_id, student_name, academic_year, semester, assignment_score, mid_score, final_score, graded_by, graded_date) VALUES
(1, 'STU12001', 'Lydia Mengistu', '2025-2026', 'First', 9.00, 27.00, 54.00, 'T003', '2026-01-31 07:26:54'),
(2, 'STU12002', 'Tewodros Negash', '2025-2026', 'First', 9.50, 28.00, 56.00, 'T003', '2026-01-31 07:26:54'),
(3, 'STU12001', 'Lydia Mengistu', '2025-2026', 'Second', 9.00, 27.00, 54.00, 'T003', '2026-01-31 07:26:54'),
(4, 'STU12002', 'Tewodros Negash', '2025-2026', 'Second', 9.50, 28.00, 56.00, 'T003', '2026-01-31 07:26:54');

ALTER TABLE mathematics MODIFY result_id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

CREATE TABLE physics (
  result_id int(11) NOT NULL,
  student_id varchar(50) NOT NULL,
  student_name varchar(100) NOT NULL,
  academic_year varchar(10) NOT NULL DEFAULT '2025-2026',
  semester enum('First','Second') NOT NULL,
  assignment_score decimal(4,2) DEFAULT 0.00 CHECK (assignment_score >= 0 and assignment_score <= 10),
  mid_score decimal(4,2) DEFAULT 0.00 CHECK (mid_score >= 0 and mid_score <= 30),
  final_score decimal(4,2) DEFAULT 0.00 CHECK (final_score >= 0 and final_score <= 60),
  total_score decimal(5,2) GENERATED ALWAYS AS (assignment_score + mid_score + final_score) STORED,
  grade_letter char(2) GENERATED ALWAYS AS (
    case 
      when assignment_score + mid_score + final_score >= 90 then 'A+'
      when assignment_score + mid_score + final_score >= 85 then 'A'
      when assignment_score + mid_score + final_score >= 80 then 'B+'
      when assignment_score + mid_score + final_score >= 75 then 'B'
      when assignment_score + mid_score + final_score >= 70 then 'C+'
      when assignment_score + mid_score + final_score >= 60 then 'C'
      when assignment_score + mid_score + final_score >= 50 then 'D'
      else 'F'
    end
  ) STORED,
  graded_by varchar(50) DEFAULT NULL,
  graded_date timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (result_id),
  UNIQUE KEY unique_student_semester (student_id, academic_year, semester)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO physics (result_id, student_id, student_name, academic_year, semester, assignment_score, mid_score, final_score, graded_by, graded_date) VALUES
(1, 'STU12001', 'Lydia Mengistu', '2025-2026', 'First', 8.00, 25.00, 50.00, 'T005', '2026-01-31 07:26:54'),
(2, 'STU12002', 'Tewodros Negash', '2025-2026', 'First', 9.50, 28.00, 55.00, 'T005', '2026-01-31 07:26:54'),
(3, 'STU12001', 'Lydia Mengistu', '2025-2026', 'Second', 8.00, 25.00, 50.00, 'T005', '2026-01-31 07:26:54'),
(4, 'STU12002', 'Tewodros Negash', '2025-2026', 'Second', 9.50, 28.00, 55.00, 'T005', '2026-01-31 07:26:54');

ALTER TABLE physics MODIFY result_id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;