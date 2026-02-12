package register;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import database.DBConnection;

@WebServlet("/UpdateStudentServlet")
public class UpdateStudentServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        // Get form data
        String studentId = request.getParameter("student_id");
        String userId = request.getParameter("user_id");
        String firstName = request.getParameter("first_name");
        String lastName = request.getParameter("last_name");
        String dob = request.getParameter("dob");
        String gender = request.getParameter("gender");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String gradeLevel = request.getParameter("grade_level");
        String section = request.getParameter("section");
        String address = request.getParameter("address");
        String parentName = request.getParameter("parent_name");
        String parentPhone = request.getParameter("parent_phone");
        String registrationStatus = request.getParameter("registration_status");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            // Get database connection
            conn = DBConnection.getConnection();
            
            if (conn == null) {
                sendErrorResponse(out, "Database connection failed.");
                return;
            }
            
            // Update student information
            String sql = "UPDATE students SET first_name = ?, last_name = ?, dob = ?, gender = ?, " +
                        "email = ?, phone = ?, grade_level = ?, section = ?, address = ?, " +
                        "parent_name = ?, parent_phone = ?, registration_status = ? " +
                        "WHERE student_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, dob);
            pstmt.setString(4, gender);
            pstmt.setString(5, email);
            pstmt.setString(6, phone);
            pstmt.setString(7, gradeLevel);
            pstmt.setString(8, section.toUpperCase());
            pstmt.setString(9, address);
            pstmt.setString(10, parentName);
            pstmt.setString(11, parentPhone);
            pstmt.setString(12, registrationStatus);
            pstmt.setString(13, studentId);
            
            int rowsUpdated = pstmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                // Also update grade-specific database if needed
                updateGradeSpecificRecord(Integer.parseInt(gradeLevel), studentId, 
                                         firstName, lastName, section);
                
                sendSuccessResponse(out, studentId, firstName, lastName, gradeLevel, section, registrationStatus);
            } else {
                sendErrorResponse(out, "Student not found or no changes made.");
            }
            
        } catch (SQLException e) {
            sendErrorResponse(out, "Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            sendErrorResponse(out, "Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn, pstmt, null);
        }
    }
    
    private void updateGradeSpecificRecord(int gradeLevel, String studentId, 
                                          String firstName, String lastName, String section) {
        Connection gradeConn = null;
        PreparedStatement pstmt = null;
        
        try {
            String gradeDbName = DBConnection.getGradeDatabase(gradeLevel);
            gradeConn = DBConnection.getConnection(gradeDbName);
            
            if (gradeConn != null) {
                // Check if record exists
                String checkSql = "SELECT * FROM students WHERE student_id = ?";
                pstmt = gradeConn.prepareStatement(checkSql);
                pstmt.setString(1, studentId);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    // Update existing record
                    pstmt.close();
                    String updateSql = "UPDATE students SET first_name = ?, last_name = ?, section = ? " +
                                      "WHERE student_id = ?";
                    pstmt = gradeConn.prepareStatement(updateSql);
                    pstmt.setString(1, firstName);
                    pstmt.setString(2, lastName);
                    pstmt.setString(3, section.toUpperCase());
                    pstmt.setString(4, studentId);
                    pstmt.executeUpdate();
                } else {
                    // Insert new record
                    pstmt.close();
                    String insertSql = "INSERT INTO students (student_id, first_name, last_name, section, " +
                                      "academic_year) VALUES (?, ?, ?, ?, ?)";
                    pstmt = gradeConn.prepareStatement(insertSql);
                    pstmt.setString(1, studentId);
                    pstmt.setString(2, firstName);
                    pstmt.setString(3, lastName);
                    pstmt.setString(4, section.toUpperCase());
                    pstmt.setString(5, DBConnection.getCurrentAcademicYear());
                    pstmt.executeUpdate();
                }
                rs.close();
            }
        } catch (SQLException e) {
            System.err.println("Warning: Could not update grade-specific record: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (gradeConn != null) gradeConn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void sendSuccessResponse(PrintWriter out, String studentId, String firstName, 
                                    String lastName, String gradeLevel, String section,
                                    String registrationStatus) {
        out.println("<html><head><title>Success</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; padding: 40px; text-align: center; background: #f5f7fa; }");
        out.println(".success-box { background: white; border-radius: 15px; padding: 30px; margin: 20px auto; max-width: 700px; box-shadow: 0 10px 30px rgba(0,0,0,0.1); }");
        out.println(".success-title { color: #3498db; font-size: 28px; margin-bottom: 20px; font-weight: bold; }");
        out.println(".success-icon { font-size: 60px; color: #3498db; margin-bottom: 20px; }");
        out.println(".info-box { background: #f8f9fa; padding: 20px; border-radius: 10px; margin: 20px 0; text-align: left; border-left: 4px solid #2ecc71; }");
        out.println(".info-row { display: flex; margin-bottom: 10px; }");
        out.println(".info-label { font-weight: bold; color: #2c3e50; width: 180px; }");
        out.println(".info-value { color: #34495e; }");
        out.println(".btn { display: inline-block; padding: 12px 25px; margin: 10px; border-radius: 8px; text-decoration: none; font-weight: bold; transition: all 0.3s; }");
        out.println(".btn-primary { background: #3498db; color: white; }");
        out.println(".btn-primary:hover { background: #2980b9; transform: translateY(-2px); box-shadow: 0 5px 15px rgba(52,152,219,0.3); }");
        out.println("</style>");
        out.println("</head><body>");
        
        out.println("<div class='success-box'>");
        out.println("<div class='success-icon'>✅</div>");
        out.println("<h1 class='success-title'>Student Updated Successfully!</h1>");
        out.println("<p>The student information has been updated in the system.</p>");
        
        out.println("<div class='info-box'>");
        out.println("<div class='info-row'><div class='info-label'>Student ID:</div><div class='info-value'>" + studentId + "</div></div>");
        out.println("<div class='info-row'><div class='info-label'>Name:</div><div class='info-value'>" + firstName + " " + lastName + "</div></div>");
        out.println("<div class='info-row'><div class='info-label'>Grade & Section:</div><div class='info-value'>Grade " + gradeLevel + "-" + section.toUpperCase() + "</div></div>");
        out.println("<div class='info-row'><div class='info-label'>Status:</div><div class='info-value'><span style='color: " + 
                   (registrationStatus.equals("Active") ? "#27ae60" : 
                    registrationStatus.equals("Pending") ? "#f39c12" : "#e74c3c") + 
                   "; font-weight: bold;'>" + registrationStatus + "</span></div></div>");
        out.println("</div>");
        
        out.println("<div style='margin-top: 30px;'>");
        out.println("<a href='dashboard.html' class='btn btn-primary'>Return to Dashboard</a>");
        out.println("</div>");
        
        out.println("</div>");
        out.println("</body></html>");
    }
    
    private void sendErrorResponse(PrintWriter out, String message) {
        out.println("<html><head><title>Error</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; padding: 40px; text-align: center; background: #f5f7fa; }");
        out.println(".error-box { background: white; border-radius: 15px; padding: 30px; margin: 20px auto; max-width: 600px; box-shadow: 0 10px 30px rgba(0,0,0,0.1); }");
        out.println(".error-title { color: #e74c3c; font-size: 28px; margin-bottom: 20px; font-weight: bold; }");
        out.println(".error-icon { font-size: 60px; color: #e74c3c; margin-bottom: 20px; }");
        out.println(".btn { display: inline-block; padding: 12px 25px; margin: 10px; border-radius: 8px; text-decoration: none; font-weight: bold; transition: all 0.3s; }");
        out.println(".btn-primary { background: #3498db; color: white; }");
        out.println(".btn-primary:hover { background: #2980b9; }");
        out.println("</style>");
        out.println("</head><body>");
        
        out.println("<div class='error-box'>");
        out.println("<div class='error-icon'>❌</div>");
        out.println("<h1 class='error-title'>Update Failed</h1>");
        out.println("<p style='color: #c0392b; font-size: 16px;'>" + message + "</p>");
        out.println("<div style='margin-top: 30px;'>");
        out.println("<a href='dashboard.html' class='btn btn-primary'>Return to Dashboard</a>");
        out.println("</div>");
        out.println("</div>");
        out.println("</body></html>");
    }
}