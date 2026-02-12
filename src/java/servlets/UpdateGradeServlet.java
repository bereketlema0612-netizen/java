package servlets;

import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/updateGrade")
public class UpdateGradeServlet extends HttpServlet {
    
    // Handle single student update (from Update button)
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        updateSingleGrade(request, response);
    }
    
    // Handle bulk update (from Save All Changes button)
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        updateBulkGrades(request, response);
    }
    
    private void updateSingleGrade(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"teacher".equals(session.getAttribute("role"))) {
            response.sendRedirect("login_student_teacher.html");
            return;
        }
        
        String teacherId = (String) session.getAttribute("username");
        String studentId = request.getParameter("student");
        String subject = request.getParameter("subject");
        String semester = request.getParameter("semester");
        String assignmentStr = request.getParameter("assignment");
        String midStr = request.getParameter("mid");
        String finalStr = request.getParameter("final");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            // Get student name from main database
            Connection mainConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student_management", "root", "");
            String studentSql = "SELECT first_name, last_name, grade_level FROM students WHERE student_id = ?";
            pstmt = mainConn.prepareStatement(studentSql);
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            String studentName = "";
            int gradeLevel = 10;
            
            if (rs.next()) {
                studentName = rs.getString("first_name") + " " + rs.getString("last_name");
                gradeLevel = rs.getInt("grade_level");
            }
            rs.close();
            pstmt.close();
            mainConn.close();
            
            // Update grade in grade database
            String gradeDb = "grade" + gradeLevel + "_db";
            String gradeDbUrl = "jdbc:mysql://localhost:3306/" + gradeDb;
            conn = DriverManager.getConnection(gradeDbUrl, "root", "");
            
            double assignment = Double.parseDouble(assignmentStr);
            double mid = Double.parseDouble(midStr);
            double finalScore = Double.parseDouble(finalStr);
            
            // Insert or update grade
            String updateSql = "INSERT INTO " + subject + " (student_id, student_name, academic_year, semester, " +
                             "assignment_score, mid_score, final_score, graded_by) " +
                             "VALUES (?, ?, '2025-2026', ?, ?, ?, ?, ?) " +
                             "ON DUPLICATE KEY UPDATE " +
                             "assignment_score = VALUES(assignment_score), " +
                             "mid_score = VALUES(mid_score), " +
                             "final_score = VALUES(final_score), " +
                             "graded_by = VALUES(graded_by), " +
                             "graded_date = CURRENT_TIMESTAMP";
            
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setString(1, studentId);
            pstmt.setString(2, studentName);
            pstmt.setString(3, semester);
            pstmt.setDouble(4, assignment);
            pstmt.setDouble(5, mid);
            pstmt.setDouble(6, finalScore);
            pstmt.setString(7, teacherId);
            
            pstmt.executeUpdate();
            
            // Set success message
            session.setAttribute("successMessage", "Successfully updated grade for student " + studentId + "!");
            
            // Redirect back to grades page
            response.sendRedirect("teacherGrades?subject=" + subject + "&grade=" + gradeLevel + "&semester=" + semester);
            
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Error updating grade: " + e.getMessage());
            response.sendRedirect("teacherGrades");
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void updateBulkGrades(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"teacher".equals(session.getAttribute("role"))) {
            response.sendRedirect("login_student_teacher.html");
            return;
        }
        
        String teacherId = (String) session.getAttribute("username");
        String subject = request.getParameter("subject");
        String gradeLevelStr = request.getParameter("grade");
        String semester = request.getParameter("semester");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            int gradeLevel = Integer.parseInt(gradeLevelStr);
            int updateCount = 0;
            
            // Connect to grade database
            String gradeDb = "grade" + gradeLevel + "_db";
            String gradeDbUrl = "jdbc:mysql://localhost:3306/" + gradeDb;
            conn = DriverManager.getConnection(gradeDbUrl, "root", "");
            
            // Connect to main database for student names
            Connection mainConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student_management", "root", "");
            
            // Get all parameter names
            Enumeration<String> paramNames = request.getParameterNames();
            
            // Map to store student data
            Map<String, StudentGrade> studentGrades = new HashMap<>();
            
            // First pass: collect all data from parameters
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                
                if (paramName.startsWith("assign_") || paramName.startsWith("mid_") || paramName.startsWith("final_")) {
                    String[] parts = paramName.split("_");
                    if (parts.length != 2) continue;
                    
                    String scoreType = parts[0]; // assign, mid, or final
                    String studentId = parts[1];
                    String scoreValue = request.getParameter(paramName);
                    
                    // Get or create student grade object
                    StudentGrade studentGrade = studentGrades.get(studentId);
                    if (studentGrade == null) {
                        studentGrade = new StudentGrade(studentId);
                        studentGrades.put(studentId, studentGrade);
                    }
                    
                    // Set the appropriate score
                    switch(scoreType) {
                        case "assign":
                            studentGrade.setAssignmentScore(Double.parseDouble(scoreValue));
                            break;
                        case "mid":
                            studentGrade.setMidScore(Double.parseDouble(scoreValue));
                            break;
                        case "final":
                            studentGrade.setFinalScore(Double.parseDouble(scoreValue));
                            break;
                    }
                }
            }
            
            // Second pass: update database for each student
            for (StudentGrade studentGrade : studentGrades.values()) {
                // Get student name from main database
                String studentSql = "SELECT first_name, last_name FROM students WHERE student_id = ?";
                pstmt = mainConn.prepareStatement(studentSql);
                pstmt.setString(1, studentGrade.getStudentId());
                ResultSet rs = pstmt.executeQuery();
                
                String studentName = "";
                if (rs.next()) {
                    studentName = rs.getString("first_name") + " " + rs.getString("last_name");
                }
                rs.close();
                pstmt.close();
                
                // Insert or update grade
                String updateSql = "INSERT INTO " + subject + " (student_id, student_name, academic_year, semester, " +
                                 "assignment_score, mid_score, final_score, graded_by) " +
                                 "VALUES (?, ?, '2025-2026', ?, ?, ?, ?, ?) " +
                                 "ON DUPLICATE KEY UPDATE " +
                                 "assignment_score = VALUES(assignment_score), " +
                                 "mid_score = VALUES(mid_score), " +
                                 "final_score = VALUES(final_score), " +
                                 "graded_by = VALUES(graded_by), " +
                                 "graded_date = CURRENT_TIMESTAMP";
                
                pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1, studentGrade.getStudentId());
                pstmt.setString(2, studentName);
                pstmt.setString(3, semester);
                pstmt.setDouble(4, studentGrade.getAssignmentScore());
                pstmt.setDouble(5, studentGrade.getMidScore());
                pstmt.setDouble(6, studentGrade.getFinalScore());
                pstmt.setString(7, teacherId);
                
                pstmt.executeUpdate();
                pstmt.close();
                
                updateCount++;
            }
            
            mainConn.close();
            
            // Set success message
            session.setAttribute("successMessage", "Successfully updated grades for " + updateCount + " students!");
            
            // Redirect back to grades page
            response.sendRedirect("teacherGrades?subject=" + subject + "&grade=" + gradeLevel + "&semester=" + semester);
            
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Error updating grades: " + e.getMessage());
            response.sendRedirect("teacherGrades?subject=" + subject + "&grade=" + gradeLevelStr + "&semester=" + semester);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    // Helper class to store student grade data
    private class StudentGrade {
        private String studentId;
        private double assignmentScore;
        private double midScore;
        private double finalScore;
        
        public StudentGrade(String studentId) {
            this.studentId = studentId;
            this.assignmentScore = 0;
            this.midScore = 0;
            this.finalScore = 0;
        }
        
        // Getters and setters
        public String getStudentId() { return studentId; }
        public double getAssignmentScore() { return assignmentScore; }
        public void setAssignmentScore(double score) { this.assignmentScore = score; }
        public double getMidScore() { return midScore; }
        public void setMidScore(double score) { this.midScore = score; }
        public double getFinalScore() { return finalScore; }
        public void setFinalScore(double score) { this.finalScore = score; }
    }
}