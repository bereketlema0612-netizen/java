package database;

import java.sql.*;

public class DBConnection {
    private static final String BASE_URL = "jdbc:mysql://localhost:3306/";
    private static final String USERNAME = "root";  
    private static final String PASSWORD = "";      
    
    // Get connection to student_management database
    public static Connection getConnection() {
        return getConnection("student_management");
    }
    
    // Get connection to specific database
    public static Connection getConnection(String databaseName) {
        Connection connection = null;
        String url = BASE_URL + databaseName;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
            System.out.println("Connected to database: " + databaseName);
            
        } catch (Exception e) {
            System.err.println("Database connection failed for: " + databaseName);
            e.printStackTrace();
        }
        
        return connection;
    }
    
    // Helper method to get grade-level database name
    public static String getGradeDatabase(int gradeLevel) {
        return "grade" + gradeLevel + "_db";
    }
    
    // Helper method to close connections
    public static void closeConnection(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Helper method to get current academic year
    public static String getCurrentAcademicYear() {
        return "2025-2026";
    }
}