package database;

import java.sql.*;

public class CreateDB {
    
    public static void main(String[] args) {
        createDatabaseAndTables();
    }
    
    public static void createDatabaseAndTables() {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            // Connect to MySQL (without database)
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/", "root", "");
            
            stmt = conn.createStatement();
            
            // Create database if not exists
            System.out.println("Creating database...");
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS school_management");
            stmt.executeUpdate("USE school_management");
            
            // Create users table
            System.out.println("Creating users table...");
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "user_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "password VARCHAR(50) NOT NULL, " +
                "role ENUM('student', 'teacher', 'registrar', 'director') NOT NULL, " +
                "status ENUM('active', 'inactive') DEFAULT 'active', " +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
            stmt.executeUpdate(createUsersTable);
            
            // Create students table
            System.out.println("Creating students table...");
            String createStudentsTable = "CREATE TABLE IF NOT EXISTS students (" +
                "student_id VARCHAR(20) PRIMARY KEY, " +
                "first_name VARCHAR(100) NOT NULL, " +
                "last_name VARCHAR(100) NOT NULL, " +
                "grade ENUM('9', '10', '11', '12') NOT NULL, " +
                "dob DATE NOT NULL, " +
                "gender ENUM('Male', 'Female', 'Other') NOT NULL, " +
                "email VARCHAR(100) UNIQUE, " +
                "phone VARCHAR(15), " +
                "address TEXT, " +
                "parent_name VARCHAR(200), " +
                "registration_date DATE, " +
                "registration_status ENUM('pending', 'registered', 'graduated') DEFAULT 'pending'" +
                ")";
            stmt.executeUpdate(createStudentsTable);
            
            // Create teachers table
            System.out.println("Creating teachers table...");
            String createTeachersTable = "CREATE TABLE IF NOT EXISTS teachers (" +
                "teacher_id VARCHAR(20) PRIMARY KEY, " +
                "first_name VARCHAR(100) NOT NULL, " +
                "last_name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100) UNIQUE, " +
                "phone VARCHAR(15), " +
                "subject VARCHAR(100), " +
                "hire_date DATE DEFAULT (CURRENT_DATE)" +
                ")";
            stmt.executeUpdate(createTeachersTable);
            
            // Create results table
            System.out.println("Creating results table...");
            String createResultsTable = "CREATE TABLE IF NOT EXISTS results (" +
                "result_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "student_id VARCHAR(20), " +
                "exam_type ENUM('mid_exam', 'final_exam', 'assignment', 'project', 'quiz') NOT NULL, " +
                "subject VARCHAR(100) NOT NULL, " +
                "score DECIMAL(5,2), " +
                "max_score DECIMAL(5,2) DEFAULT 100.00, " +
                "percentage DECIMAL(5,2), " +
                "grade_letter VARCHAR(2), " +
                "exam_date DATE, " +
                "recorded_by VARCHAR(20), " +
                "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE" +
                ")";
            stmt.executeUpdate(createResultsTable);
            
            // Create announcements table
            System.out.println("Creating announcements table...");
            String createAnnouncementsTable = "CREATE TABLE IF NOT EXISTS announcements (" +
                "announcement_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "title VARCHAR(200) NOT NULL, " +
                "content TEXT NOT NULL, " +
                "posted_by VARCHAR(100) NOT NULL, " +
                "post_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "target_grade ENUM('All', '9', '10', '11', '12') DEFAULT 'All'" +
                ")";
            stmt.executeUpdate(createAnnouncementsTable);
            
            // Create calendar table
            System.out.println("Creating calendar table...");
            String createCalendarTable = "CREATE TABLE IF NOT EXISTS calendar (" +
                "event_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "event_name VARCHAR(200) NOT NULL, " +
                "event_date DATE NOT NULL, " +
                "description TEXT, " +
                "event_type ENUM('holiday', 'exam', 'meeting', 'event', 'deadline') DEFAULT 'event'" +
                ")";
            stmt.executeUpdate(createCalendarTable);
            
            // Insert sample data
            System.out.println("Inserting sample data...");
            insertSampleData(stmt);
            
            System.out.println("Database and tables created successfully!");
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void insertSampleData(Statement stmt) throws SQLException {
        // Insert users
        String insertUsers = "INSERT IGNORE INTO users (username, password, role, status) VALUES " +
            "('admin', '1234', 'director', 'active'), " +
            "('registrar1', '1234', 'registrar', 'active'), " +
            "('teacher1', '1234', 'teacher', 'active'), " +
            "('teacher2', '1234', 'teacher', 'active'), " +
            "('S1001', '1234', 'student', 'active'), " +
            "('S1002', '1234', 'student', 'active'), " +
            "('S1003', '1234', 'student', 'active')";
        stmt.executeUpdate(insertUsers);
        
        // Insert students
        String insertStudents = "INSERT IGNORE INTO students VALUES " +
            "('S1001', 'John', 'Doe', '9', '2008-05-15', 'Male', 'john@school.com', '0912345678', '123 Main St', 'Mr. Doe', '2024-01-15', 'registered'), " +
            "('S1002', 'Jane', 'Smith', '10', '2007-08-22', 'Female', 'jane@school.com', '0912345679', '456 Oak St', 'Mrs. Smith', '2024-01-16', 'registered'), " +
            "('S1003', 'Mike', 'Johnson', '11', '2006-03-10', 'Male', 'mike@school.com', '0912345680', '789 Pine St', 'Mr. Johnson', '2024-01-17', 'registered')";
        stmt.executeUpdate(insertStudents);
        
        System.out.println("Sample data inserted successfully!");
    }
}