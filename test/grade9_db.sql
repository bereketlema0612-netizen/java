CREATE DATABASE grade9_db;
USE grade9_db;

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
(1, 'STU9001', 'Kidist Alemayehu', '2025-2026', 'First', 9.00, 27.00, 54.00, 'T006', '2026-01-31 07:01:31'),
(2, 'STU9002', 'Nahom Berhane', '2025-2026', 'First', 6.50, 20.00, 40.00, 'T006', '2026-01-31 07:01:31'),
(3, 'STU9003', 'Selam Desta', '2025-2026', 'First', 10.00, 29.00, 58.00, 'T006', '2026-01-31 07:01:31'),
(4, 'STU9004', 'Abel Fekadu', '2025-2026', 'First', 7.50, 22.00, 44.00, 'T006', '2026-01-31 07:01:31'),
(5, 'STU9005', 'Ruth Gebre', '2025-2026', 'First', 9.50, 28.00, 56.00, 'T006', '2026-01-31 07:01:31'),
(6, 'STU9001', 'Kidist Alemayehu', '2025-2026', 'Second', 9.00, 27.00, 54.00, 'T006', '2026-01-31 07:01:31'),
(7, 'STU9002', 'Nahom Berhane', '2025-2026', 'Second', 6.50, 20.00, 40.00, 'T006', '2026-01-31 07:01:31'),
(8, 'STU9003', 'Selam Desta', '2025-2026', 'Second', 10.00, 29.00, 58.00, 'T006', '2026-01-31 07:01:31'),
(9, 'STU9004', 'Abel Fekadu', '2025-2026', 'Second', 7.50, 22.00, 44.00, 'T006', '2026-01-31 07:01:31'),
(10, 'STU9005', 'Ruth Gebre', '2025-2026', 'Second', 9.50, 28.00, 56.00, 'T006', '2026-01-31 07:01:31'),
(11, 'STU12001', 'Lydia Mengistu', '2022-2023', 'First', 8.50, 26.00, 52.00, 'T006', '2026-01-31 08:10:08'),
(12, 'STU12002', 'Tewodros Negash', '2022-2023', 'First', 7.00, 22.00, 44.00, 'T006', '2026-01-31 08:10:08'),
(13, 'STU12001', 'Lydia Mengistu', '2022-2023', 'Second', 8.50, 26.00, 52.00, 'T006', '2026-01-31 08:10:08'),
(14, 'STU12002', 'Tewodros Negash', '2022-2023', 'Second', 7.00, 22.00, 44.00, 'T006', '2026-01-31 08:10:08'),
(15, 'STU10001', 'Daniel Haile', '2024-2025', 'First', 8.50, 26.00, 52.00, 'T006', '2026-01-31 08:17:03'),
(16, 'STU10002', 'Bethlehem Isaac', '2024-2025', 'First', 7.00, 22.00, 44.00, 'T006', '2026-01-31 08:17:03'),
(17, 'STU10001', 'Daniel Haile', '2024-2025', 'Second', 8.50, 26.00, 52.00, 'T006', '2026-01-31 08:17:03'),
(18, 'STU10002', 'Bethlehem Isaac', '2024-2025', 'Second', 7.00, 22.00, 44.00, 'T006', '2026-01-31 08:17:03'),
(19, 'STU11001', 'Feven Kassa', '2024-2025', 'First', 9.50, 28.00, 56.00, 'T006', '2026-01-31 08:33:30'),
(20, 'STU11002', 'Henok Lemma', '2024-2025', 'First', 6.50, 19.00, 40.00, 'T006', '2026-01-31 08:33:30'),
(21, 'STU11001', 'Feven Kassa', '2024-2025', 'Second', 9.00, 27.00, 55.00, 'T006', '2026-01-31 08:36:04'),
(22, 'STU11002', 'Henok Lemma', '2024-2025', 'Second', 7.00, 21.00, 42.00, 'T006', '2026-01-31 08:36:04');

ALTER TABLE art MODIFY result_id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

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
(1, 'STU9001', 'Kidist Alemayehu', '2025-2026', 'First', 8.50, 25.00, 48.00, 'T001', '2026-01-31 07:01:30'),
(2, 'STU9002', 'Nahom Berhane', '2025-2026', 'First', 9.00, 27.00, 52.00, 'T001', '2026-01-31 07:01:30'),
(3, 'STU9003', 'Selam Desta', '2025-2026', 'First', 7.00, 22.00, 45.00, 'T001', '2026-01-31 07:01:30'),
(4, 'STU9004', 'Abel Fekadu', '2025-2026', 'First', 8.00, 24.00, 50.00, 'T001', '2026-01-31 07:01:30'),
(5, 'STU9005', 'Ruth Gebre', '2025-2026', 'First', 6.50, 20.00, 42.00, 'T001', '2026-01-31 07:01:30'),
(6, 'STU9001', 'Kidist Alemayehu', '2025-2026', 'Second', 8.50, 25.00, 48.00, 'T001', '2026-01-31 07:01:30'),
(7, 'STU9002', 'Nahom Berhane', '2025-2026', 'Second', 9.00, 27.00, 52.00, 'T001', '2026-01-31 07:01:30'),
(8, 'STU9003', 'Selam Desta', '2025-2026', 'Second', 7.00, 22.00, 45.00, 'T001', '2026-01-31 07:01:30'),
(9, 'STU9004', 'Abel Fekadu', '2025-2026', 'Second', 8.00, 24.00, 50.00, 'T001', '2026-01-31 07:01:30'),
(10, 'STU9005', 'Ruth Gebre', '2025-2026', 'Second', 6.50, 20.00, 42.00, 'T001', '2026-01-31 07:01:30'),
(11, 'STU12001', 'Lydia Mengistu', '2022-2023', 'First', 8.00, 24.00, 48.00, 'T001', '2026-01-31 08:10:08'),
(12, 'STU12002', 'Tewodros Negash', '2022-2023', 'First', 9.00, 27.00, 54.00, 'T001', '2026-01-31 08:10:08'),
(13, 'STU12001', 'Lydia Mengistu', '2022-2023', 'Second', 8.50, 26.00, 52.00, 'T001', '2026-01-31 08:10:08'),
(14, 'STU12002', 'Tewodros Negash', '2022-2023', 'Second', 7.50, 23.00, 46.00, 'T001', '2026-01-31 08:10:08'),
(15, 'STU10001', 'Daniel Haile', '2024-2025', 'First', 8.00, 24.00, 48.00, 'T001', '2026-01-31 08:17:03'),
(16, 'STU10002', 'Bethlehem Isaac', '2024-2025', 'First', 9.00, 27.00, 54.00, 'T001', '2026-01-31 08:17:03'),
(17, 'STU10001', 'Daniel Haile', '2024-2025', 'Second', 8.50, 26.00, 52.00, 'T001', '2026-01-31 08:17:03'),
(18, 'STU10002', 'Bethlehem Isaac', '2024-2025', 'Second', 7.50, 23.00, 46.00, 'T001', '2026-01-31 08:17:03'),
(19, 'STU11001', 'Feven Kassa', '2024-2025', 'First', 9.00, 27.00, 54.00, 'T001', '2026-01-31 08:33:30'),
(20, 'STU11002', 'Henok Lemma', '2024-2025', 'First', 8.00, 24.00, 48.00, 'T001', '2026-01-31 08:33:30'),
(21, 'STU11001', 'Feven Kassa', '2024-2025', 'Second', 8.50, 26.00, 53.00, 'T001', '2026-01-31 08:36:04'),
(22, 'STU11002', 'Henok Lemma', '2024-2025', 'Second', 8.50, 25.00, 51.00, 'T001', '2026-01-31 08:36:04');

ALTER TABLE biology MODIFY result_id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

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
(1, 'STU9001', 'Kidist Alemayehu', '2025-2026', 'First', 8.00, 24.00, 50.00, 'T004', '2026-01-31 07:01:31'),
(2, 'STU9002', 'Nahom Berhane', '2025-2026', 'First', 9.00, 27.00, 54.00, 'T004', '2026-01-31 07:01:31'),
(3, 'STU9003', 'Selam Desta', '2025-2026', 'First', 7.50, 23.00, 46.00, 'T004', '2026-01-31 07:01:31'),
(4, 'STU9004', 'Abel Fekadu', '2025-2026', 'First', 6.50, 20.00, 42.00, 'T004', '2026-01-31 07:01:31'),
(5, 'STU9005', 'Ruth Gebre', '2025-2026', 'First', 8.50, 25.00, 48.00, 'T004', '2026-01-31 07:01:31'),
(6, 'STU9001', 'Kidist Alemayehu', '2025-2026', 'Second', 8.00, 24.00, 50.00, 'T004', '2026-01-31 07:01:31'),
(7, 'STU9002', 'Nahom Berhane', '2025-2026', 'Second', 9.00, 27.00, 54.00, 'T004', '2026-01-31 07:01:31'),
(8, 'STU9003', 'Selam Desta', '2025-2026', 'Second', 7.50, 23.00, 46.00, 'T004', '2026-01-31 07:01:31'),
(9, 'STU9004', 'Abel Fekadu', '2025-2026', 'Second', 6.50, 20.00, 42.00, 'T004', '2026-01-31 07:01:31'),
(10, 'STU9005', 'Ruth Gebre', '2025-2026', 'Second', 8.50, 25.00, 48.00, 'T004', '2026-01-31 07:01:31'),
(11, 'STU12001', 'Lydia Mengistu', '2022-2023', 'First', 8.50, 25.00, 50.00, 'T004', '2026-01-31 08:10:08'),
(12, 'STU12002', 'Tewodros Negash', '2022-2023', 'First', 7.50, 23.00, 46.00, 'T004', '2026-01-31 08:10:08'),
(13, 'STU12001', 'Lydia Mengistu', '2022-2023', 'Second', 8.00, 24.00, 50.00, 'T004', '2026-01-31 08:10:08'),
(14, 'STU12002', 'Tewodros Negash', '2022-2023', 'Second', 7.50, 23.00, 46.00, 'T004', '2026-01-31 08:10:08'),
(15, 'STU10001', 'Daniel Haile', '2024-2025', 'First', 8.50, 25.00, 50.00, 'T004', '2026-01-31 08:17:03'),
(16, 'STU10002', 'Bethlehem Isaac', '2024-2025', 'First', 7.50, 23.00, 46.00, 'T004', '2026-01-31 08:17:03'),
(17, 'STU10001', 'Daniel Haile', '2024-2025', 'Second', 8.00, 24.00, 50.00, 'T004', '2026-01-31 08:17:03'),
(18, 'STU10002', 'Bethlehem Isaac', '2024-2025', 'Second', 7.50, 23.00, 46.00, 'T004', '2026-01-31 08:17:03'),
(19, 'STU11001', 'Feven Kassa', '2024-2025', 'First', 8.00, 25.00, 50.00, 'T004', '2026-01-31 08:33:30'),
(20, 'STU11002', 'Henok Lemma', '2024-2025', 'First', 7.50, 23.00, 46.00, 'T004', '2026-01-31 08:33:30'),
(21, 'STU11001', 'Feven Kassa', '2024-2025', 'Second', 8.00, 25.00, 51.00, 'T004', '2026-01-31 08:36:04'),
(22, 'STU11002', 'Henok Lemma', '2024-2025', 'Second', 7.50, 23.00, 47.00, 'T004', '2026-01-31 08:36:04');

ALTER TABLE chemistry MODIFY result_id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

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
(1, 'STU9001', 'Kidist Alemayehu', '2025-2026', 'First', 9.00, 27.00, 54.00, 'T009', '2026-01-31 07:01:31'),
(2, 'STU9002', 'Nahom Berhane', '2025-2026', 'First', 8.00, 24.00, 48.00, 'T009', '2026-01-31 07:01:31'),
(3, 'STU9003', 'Selam Desta', '2025-2026', 'First', 8.50, 25.00, 50.00, 'T009', '2026-01-31 07:01:31'),
(4, 'STU9004', 'Abel Fekadu', '2025-2026', 'First', 7.50, 22.00, 46.00, 'T009', '2026-01-31 07:01:31'),
(5, 'STU9005', 'Ruth Gebre', '2025-2026', 'First', 9.50, 28.00, 55.00, 'T009', '2026-01-31 07:01:31'),
(6, 'STU9001', 'Kidist Alemayehu', '2025-2026', 'Second', 9.00, 27.00, 54.00, 'T009', '2026-01-31 07:01:31'),
(7, 'STU9002', 'Nahom Berhane', '2025-2026', 'Second', 8.00, 24.00, 48.00, 'T009', '2026-01-31 07:01:31'),
(8, 'STU9003', 'Selam Desta', '2025-2026', 'Second', 8.50, 25.00, 50.00, 'T009', '2026-01-31 07:01:31'),
(9, 'STU9004', 'Abel Fekadu', '2025-2026', 'Second', 7.50, 22.00, 46.00, 'T009', '2026-01-31 07:01:31'),
(10, 'STU9005', 'Ruth Gebre', '2025-2026', 'Second', 9.50, 28.00, 55.00, 'T009', '2026-01-31 07:01:31'),
(11, 'STU12001', 'Lydia Mengistu', '2022-2023', 'First', 9.00, 27.00, 54.00, 'T009', '2026-01-31 08:10:08'),
(12, 'STU12002', 'Tewodros Negash', '2022-2023', 'First', 8.00, 24.00, 50.00, 'T009', '2026-01-31 08:10:08'),
(13, 'STU12001', 'Lydia Mengistu', '2022-2023', 'Second', 9.00, 27.00, 54.00, 'T009', '2026-01-31 08:10:08'),
(14, 'STU12002', 'Tewodros Negash', '2022-2023', 'Second', 8.00, 24.00, 50.00, 'T009', '2026-01-31 08:10:08'),
(15, 'STU10001', 'Daniel Haile', '2024-2025', 'First', 9.00, 27.00, 54.00, 'T009', '2026-01-31 08:17:03'),
(16, 'STU10002', 'Bethlehem Isaac', '2024-2025', 'First', 8.00, 24.00, 50.00, 'T009', '2026-01-31 08:17:03'),
(17, 'STU10001', 'Daniel Haile', '2024-2025', 'Second', 9.00, 27.00, 54.00, 'T009', '2026-01-31 08:17:03'),
(18, 'STU10002', 'Bethlehem Isaac', '2024-2025', 'Second', 8.00, 24.00, 50.00, 'T009', '2026-01-31 08:17:03'),
(19, 'STU11001', 'Feven Kassa', '2024-2025', 'First', 9.00, 27.00, 54.00, 'T009', '2026-01-31 08:33:30'),
(20, 'STU11002', 'Henok Lemma', '2024-2025', 'First', 8.50, 25.00, 50.00, 'T009', '2026-01-31 08:33:30'),
(21, 'STU11001', 'Feven Kassa', '2024-2025', 'Second', 9.00, 27.00, 55.00, 'T009', '2026-01-31 08:36:04'),
(22, 'STU11002', 'Henok Lemma', '2024-2025', 'Second', 8.00, 24.00, 50.00, 'T009', '2026-01-31 08:36:04');

ALTER TABLE civics MODIFY result_id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

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
(1, 'STU9001', 'Kidist Alemayehu', '2025-2026', 'First', 9.00, 26.00, 50.00, 'T002', '2026-01-31 07:01:30'),
(2, 'STU9002', 'Nahom Berhane', '2025-2026', 'First', 7.50, 23.00, 46.00, 'T002', '2026-01-31 07:01:30'),
(3, 'STU9003', 'Selam Desta', '2025-2026', 'First', 8.50, 25.00, 48.00, 'T002', '2026-01-31 07:01:30'),
(4, 'STU9004', 'Abel Fekadu', '2025-2026', 'First', 7.00, 21.00, 44.00, 'T002', '2026-01-31 07:01:30'),
(5, 'STU9005', 'Ruth Gebre', '2025-2026', 'First', 9.50, 28.00, 55.00, 'T002', '2026-01-31 07:01:30'),
(6, 'STU9001', 'Kidist Alemayehu', '2025-2026', 'Second', 9.00, 26.00, 50.00, 'T002', '2026-01-31 07:01:30'),
(7, 'STU9002', 'Nahom Berhane', '2025-2026', 'Second', 7.50, 23.00, 46.00, 'T002', '2026-01-31 07:01:30'),
(8, 'STU9003', 'Selam Desta', '2025-2026', 'Second', 8.50, 25.00, 48.00, 'T002', '2026-01-31 07:01:30'),
(9, 'STU9004', 'Abel Fekadu', '2025-2026', 'Second', 7.00, 21.00, 44.00, 'T002', '2026-01-31 07:01:30'),
(10, 'STU9005', 'Ruth Gebre', '2025-2026', 'Second', 9.50, 28.00, 55.00, 'T002', '2026-01-31 07:01:30'),
(11, 'STU12001', 'Lydia Mengistu', '2022-2023', 'First', 7.50, 23.00, 46.00, 'T002', '2026-01-31 08:10:08'),
(12, 'STU12002', 'Tewodros Negash', '2022-2023', 'First', 8.50, 26.00, 52.00, 'T002', '2026-01-31 08:10:08'),
(13, 'STU12001', 'Lydia Mengistu', '2022-2023', 'Second', 9.00, 27.00, 54.00, 'T002', '2026-01-31 08:10:08'),
(14, 'STU12002', 'Tewodros Negash', '2022-2023', 'Second', 8.00, 24.00, 50.00, 'T002', '2026-01-31 08:10:08'),
(15, 'STU10001', 'Daniel Haile', '2024-2025', 'First', 7.50, 23.00, 46.00, 'T002', '2026-01-31 08:17:03'),
(16, 'STU10002', 'Bethlehem Isaac', '2024-2025', 'First', 8.50, 26.00, 52.00, 'T002', '2026-01-31 08:17:03'),
(17, 'STU10001', 'Daniel Haile', '2024-2025', 'Second', 9.00, 27.00, 54.00, 'T002', '2026-01-31 08:17:03'),
(18, 'STU10002', 'Bethlehem Isaac', '2024-2025', 'Second', 8.00, 24.00, 50.00, 'T002', '2026-01-31 08:17:03'),
(19, 'STU11001', 'Feven Kassa', '2024-2025', 'First', 9.50, 28.00, 56.00, 'T002', '2026-01-31 08:33:30'),
(20, 'STU11002', 'Henok Lemma', '2024-2025', 'First', 7.50, 22.00, 44.00, 'T002', '2026-01-31 08:33:30'),
(21, 'STU11001', 'Feven Kassa', '2024-2025', 'Second', 9.00, 27.00, 54.00, 'T002', '2026-01-31 08:36:04'),
(22, 'STU11002', 'Henok Lemma', '2024-2025', 'Second', 8.00, 24.00, 48.00, 'T002', '2026-01-31 08:36:04');

ALTER TABLE english MODIFY result_id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

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
(1, 'STU9001', 'Kidist Alemayehu', '2025-2026', 'First', 8.00, 25.00, 50.00, 'T008', '2026-01-31 07:01:31'),
(2, 'STU9002', 'Nahom Berhane', '2025-2026', 'First', 8.50, 26.00, 52.00, 'T008', '2026-01-31 07:01:31'),
(3, 'STU9003', 'Selam Desta', '2025-2026', 'First', 7.00, 21.00, 44.00, 'T008', '2026-01-31 07:01:31'),
(4, 'STU9004', 'Abel Fekadu', '2025-2026', 'First', 9.00, 27.00, 54.00, 'T008', '2026-01-31 07:01:31'),
(5, 'STU9005', 'Ruth Gebre', '2025-2026', 'First', 6.50, 19.00, 40.00, 'T008', '2026-01-31 07:01:31'),
(6, 'STU9001', 'Kidist Alemayehu', '2025-2026', 'Second', 8.00, 25.00, 50.00, 'T008', '2026-01-31 07:01:31'),
(7, 'STU9002', 'Nahom Berhane', '2025-2026', 'Second', 8.50, 26.00, 52.00, 'T008', '2026-01-31 07:01:31'),
(8, 'STU9003', 'Selam Desta', '2025-2026', 'Second', 7.00, 21.00, 44.00, 'T008', '2026-01-31 07:01:31'),
(9, 'STU9004', 'Abel Fekadu', '2025-2026', 'Second', 9.00, 27.00, 54.00, 'T008', '2026-01-31 07:01:31'),
(10, 'STU9005', 'Ruth Gebre', '2025-2026', 'Second', 6.50, 19.00, 40.00, 'T008', '2026-01-31 07:01:31'),
(11, 'STU12001', 'Lydia Mengistu', '2022-2023', 'First', 8.00, 25.00, 50.00, 'T008', '2026-01-31 08:10:08'),
(12, 'STU12002', 'Tewodros Negash', '2022-2023', 'First', 7.50, 23.00, 46.00, 'T008', '2026-01-31 08:10:08'),
(13, 'STU12001', 'Lydia Mengistu', '2022-2023', 'Second', 8.00, 25.00, 50.00, 'T008', '2026-01-31 08:10:08'),
(14, 'STU12002', 'Tewodros Negash', '2022-2023', 'Second', 7.50, 23.00, 46.00, 'T008', '2026-01-31 08:10:08'),
(15, 'STU10001', 'Daniel Haile', '2024-2025', 'First', 8.00, 25.00, 50.00, 'T008', '2026-01-31 08:17:03'),
(16, 'STU10002', 'Bethlehem Isaac', '2024-2025', 'First', 7.50, 23.00, 46.00, 'T008', '2026-01-31 08:17:03'),
(17, 'STU10001', 'Daniel Haile', '2024-2025', 'Second', 8.00, 25.00, 50.00, 'T008', '2026-01-31 08:17:03'),
(18, 'STU10002', 'Bethlehem Isaac', '2024-2025', 'Second', 7.50, 23.00, 46.00, 'T008', '2026-01-31 08:17:03'),
(19, 'STU11001', 'Feven Kassa', '2024-2025', 'First', 8.00, 25.00, 50.00, 'T008', '2026-01-31 08:33:30'),
(20, 'STU11002', 'Henok Lemma', '2024-2025', 'First', 8.50, 26.00, 52.00, 'T008', '2026-01-31 08:33:30'),
(21, 'STU11001', 'Feven Kassa', '2024-2025', 'Second', 8.50, 25.00, 51.00, 'T008', '2026-01-31 08:36:04'),
(22, 'STU11002', 'Henok Lemma', '2024-2025', 'Second', 8.50, 26.00, 53.00, 'T008', '2026-01-31 08:36:04');

ALTER TABLE geography MODIFY result_id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

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
(1, 'STU9001', 'Kidist Alemayehu', '2025-2026', 'First', 8.50, 26.00, 52.00, 'T007', '2026-01-31 07:01:31'),
(2, 'STU9002', 'Nahom Berhane', '2025-2026', 'First', 7.00, 22.00, 44.00, 'T007', '2026-01-31 07:01:31'),
(3, 'STU9003', 'Selam Desta', '2025-2026', 'First', 9.00, 27.00, 54.00, 'T007', '2026-01-31 07:01:31'),
(4, 'STU9004', 'Abel Fekadu', '2025-2026', 'First', 8.00, 24.00, 48.00, 'T007', '2026-01-31 07:01:31'),
(5, 'STU9005', 'Ruth Gebre', '2025-2026', 'First', 7.50, 23.00, 46.00, 'T007', '2026-01-31 07:01:31'),
(6, 'STU9001', 'Kidist Alemayehu', '2025-2026', 'Second', 8.50, 26.00, 52.00, 'T007', '2026-01-31 07:01:31'),
(7, 'STU9002', 'Nahom Berhane', '2025-2026', 'Second', 7.00, 22.00, 44.00, 'T007', '2026-01-31 07:01:31'),
(8, 'STU9003', 'Selam Desta', '2025-2026', 'Second', 9.00, 27.00, 54.00, 'T007', '2026-01-31 07:01:31'),
(9, 'STU9004', 'Abel Fekadu', '2025-2026', 'Second', 8.00, 24.00, 48.00, 'T007', '2026-01-31 07:01:31'),
(10, 'STU9005', 'Ruth Gebre', '2025-2026', 'Second', 7.50, 23.00, 46.00, 'T007', '2026-01-31 07:01:31'),
(11, 'STU12001', 'Lydia Mengistu', '2022-2023', 'First', 9.00, 27.00, 54.00, 'T007', '2026-01-31 08:10:08'),
(12, 'STU12002', 'Tewodros Negash', '2022-2023', 'First', 8.00, 24.00, 48.00, 'T007', '2026-01-31 08:10:08'),
(13, 'STU12001', 'Lydia Mengistu', '2022-2023', 'Second', 9.00, 27.00, 54.00, 'T007', '2026-01-31 08:10:08'),
(14, 'STU12002', 'Tewodros Negash', '2022-2023', 'Second', 8.00, 24.00, 48.00, 'T007', '2026-01-31 08:10:08'),
(15, 'STU10001', 'Daniel Haile', '2024-2025', 'First', 9.00, 27.00, 54.00, 'T007', '2026-01-31 08:17:03'),
(16, 'STU10002', 'Bethlehem Isaac', '2024-2025', 'First', 8.00, 24.00, 48.00, 'T007', '2026-01-31 08:17:03'),
(17, 'STU10001', 'Daniel Haile', '2024-2025', 'Second', 9.00, 27.00, 54.00, 'T007', '2026-01-31 08:17:03'),
(18, 'STU10002', 'Bethlehem Isaac', '2024-2025', 'Second', 8.00, 24.00, 48.00, 'T007', '2026-01-31 08:17:03'),
(19, 'STU11001', 'Feven Kassa', '2024-2025', 'First', 8.50, 26.00, 52.00, 'T007', '2026-01-31 08:33:30'),
(20, 'STU11002', 'Henok Lemma', '2024-2025', 'First', 8.00, 24.00, 48.00, 'T007', '2026-01-31 08:33:30'),
(21, 'STU11001', 'Feven Kassa', '2024-2025', 'Second', 8.50, 26.00, 53.00, 'T007', '2026-01-31 08:36:04'),
(22, 'STU11002', 'Henok Lemma', '2024-2025', 'Second', 8.00, 24.00, 49.00, 'T007', '2026-01-31 08:36:04');

ALTER TABLE history MODIFY result_id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

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
(1, 'STU9001', 'Kidist Alemayehu', '2025-2026', 'First', 9.50, 28.00, 55.00, 'T003', '2026-01-31 07:01:31'),
(2, 'STU9002', 'Nahom Berhane', '2025-2026', 'First', 10.00, 29.00, 58.00, 'T003', '2026-01-31 07:01:31'),
(3, 'STU9003', 'Selam Desta', '2025-2026', 'First', 6.00, 18.00, 38.00, 'T003', '2026-01-31 07:01:31'),
(4, 'STU9004', 'Abel Fekadu', '2025-2026', 'First', 8.50, 26.00, 52.00, 'T003', '2026-01-31 07:01:31'),
(5, 'STU9005', 'Ruth Gebre', '2025-2026', 'First', 7.00, 22.00, 45.00, 'T003', '2026-01-31 07:01:31'),
(6, 'STU9001', 'Kidist Alemayehu', '2025-2026', 'Second', 9.50, 28.00, 55.00, 'T003', '2026-01-31 07:01:31'),
(7, 'STU9002', 'Nahom Berhane', '2025-2026', 'Second', 10.00, 29.00, 58.00, 'T003', '2026-01-31 07:01:31'),
(8, 'STU9003', 'Selam Desta', '2025-2026', 'Second', 6.00, 18.00, 38.00, 'T003', '2026-01-31 07:01:31'),
(9, 'STU9004', 'Abel Fekadu', '2025-2026', 'Second', 8.50, 26.00, 52.00, 'T003', '2026-01-31 07:01:31'),
(10, 'STU9005', 'Ruth Gebre', '2025-2026', 'Second', 7.00, 22.00, 45.00, 'T003', '2026-01-31 07:01:31'),
(11, 'STU12001', 'Lydia Mengistu', '2022-2023', 'First', 9.00, 27.00, 54.00, 'T003', '2026-01-31 08:10:08'),
(12, 'STU12002', 'Tewodros Negash', '2022-2023', 'First', 8.00, 25.00, 50.00, 'T003', '2026-01-31 08:10:08'),
(13, 'STU12001', 'Lydia Mengistu', '2022-2023', 'Second', 9.50, 28.00, 56.00, 'T003', '2026-01-31 08:10:08'),
(14, 'STU12002', 'Tewodros Negash', '2022-2023', 'Second', 8.50, 26.00, 52.00, 'T003', '2026-01-31 08:10:08'),
(15, 'STU10001', 'Daniel Haile', '2024-2025', 'First', 9.00, 27.00, 54.00, 'T003', '2026-01-31 08:17:03'),
(16, 'STU10002', 'Bethlehem Isaac', '2024-2025', 'First', 8.00, 25.00, 50.00, 'T003', '2026-01-31 08:17:03'),
(17, 'STU10001', 'Daniel Haile', '2024-2025', 'Second', 9.50, 28.00, 56.00, 'T003', '2026-01-31 08:17:03'),
(18, 'STU10002', 'Bethlehem Isaac', '2024-2025', 'Second', 8.50, 26.00, 52.00, 'T003', '2026-01-31 08:17:03'),
(19, 'STU11001', 'Feven Kassa', '2024-2025', 'First', 8.50, 26.00, 52.00, 'T003', '2026-01-31 08:33:30'),
(20, 'STU11002', 'Henok Lemma', '2024-2025', 'First', 9.50, 28.00, 56.00, 'T003', '2026-01-31 08:33:30'),
(21, 'STU11001', 'Feven Kassa', '2024-2025', 'Second', 8.50, 27.00, 54.00, 'T003', '2026-01-31 08:36:04'),
(22, 'STU11002', 'Henok Lemma', '2024-2025', 'Second', 9.00, 28.00, 56.00, 'T003', '2026-01-31 08:36:04');

ALTER TABLE mathematics MODIFY result_id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

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
(1, 'STU9001', 'Kidist Alemayehu', '2025-2026', 'First', 7.50, 23.00, 46.00, 'T005', '2026-01-31 07:01:31'),
(2, 'STU9002', 'Nahom Berhane', '2025-2026', 'First', 9.50, 28.00, 56.00, 'T005', '2026-01-31 07:01:31'),
(3, 'STU9003', 'Selam Desta', '2025-2026', 'First', 6.00, 19.00, 40.00, 'T005', '2026-01-31 07:01:31'),
(4, 'STU9004', 'Abel Fekadu', '2025-2026', 'First', 8.00, 25.00, 50.00, 'T005', '2026-01-31 07:01:31'),
(5, 'STU9005', 'Ruth Gebre', '2025-2026', 'First', 7.00, 21.00, 44.00, 'T005', '2026-01-31 07:01:31'),
(6, 'STU9001', 'Kidist Alemayehu', '2025-2026', 'Second', 7.50, 23.00, 46.00, 'T005', '2026-01-31 07:01:31'),
(7, 'STU9002', 'Nahom Berhane', '2025-2026', 'Second', 9.50, 28.00, 56.00, 'T005', '2026-01-31 07:01:31'),
(8, 'STU9003', 'Selam Desta', '2025-2026', 'Second', 6.00, 19.00, 40.00, 'T005', '2026-01-31 07:01:31'),
(9, 'STU9004', 'Abel Fekadu', '2025-2026', 'Second', 8.00, 25.00, 50.00, 'T005', '2026-01-31 07:01:31'),
(10, 'STU9005', 'Ruth Gebre', '2025-2026', 'Second', 7.00, 21.00, 44.00, 'T005', '2026-01-31 07:01:31'),
(11, 'STU12001', 'Lydia Mengistu', '2022-2023', 'First', 9.00, 27.00, 54.00, 'T005', '2026-01-31 08:10:08'),
(12, 'STU12002', 'Tewodros Negash', '2022-2023', 'First', 8.00, 24.00, 50.00, 'T005', '2026-01-31 08:10:08'),
(13, 'STU12001', 'Lydia Mengistu', '2022-2023', 'Second', 9.00, 27.00, 54.00, 'T005', '2026-01-31 08:10:08'),
(14, 'STU12002', 'Tewodros Negash', '2022-2023', 'Second', 8.00, 25.00, 50.00, 'T005', '2026-01-31 08:10:08'),
(15, 'STU10001', 'Daniel Haile', '2024-2025', 'First', 9.00, 27.00, 54.00, 'T005', '2026-01-31 08:17:03'),
(16, 'STU10002', 'Bethlehem Isaac', '2024-2025', 'First', 8.00, 24.00, 50.00, 'T005', '2026-01-31 08:17:03'),
(17, 'STU10001', 'Daniel Haile', '2024-2025', 'Second', 9.00, 27.00, 54.00, 'T005', '2026-01-31 08:17:03'),
(18, 'STU10002', 'Bethlehem Isaac', '2024-2025', 'Second', 8.00, 25.00, 50.00, 'T005', '2026-01-31 08:17:03'),
(19, 'STU11001', 'Feven Kassa', '2024-2025', 'First', 7.50, 23.00, 46.00, 'T005', '2026-01-31 08:33:30'),
(20, 'STU11002', 'Henok Lemma', '2024-2025', 'First', 9.00, 27.00, 54.00, 'T005', '2026-01-31 08:33:30'),
(21, 'STU11001', 'Feven Kassa', '2024-2025', 'Second', 7.50, 24.00, 48.00, 'T005', '2026-01-31 08:36:04'),
(22, 'STU11002', 'Henok Lemma', '2024-2025', 'Second', 8.50, 26.00, 53.00, 'T005', '2026-01-31 08:36:04');

ALTER TABLE physics MODIFY result_id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;
