package register;
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import database.DBConnection;

@WebServlet("/DeleteStudentServlet")
public class DeleteStudentServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String studentId = request.getParameter("studentId");
        
        if (studentId == null || studentId.trim().isEmpty()) {
            sendErrorResponse(out, "Invalid student ID");
            return;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            // Get database connection
            conn = DBConnection.getConnection();
            
            if (conn == null) {
                sendErrorResponse(out, "Database connection failed.");
                return;
            }
            
            // First, get student info before deletion
            String getStudentSql = "SELECT s.*, u.username FROM students s " +
                                  "LEFT JOIN users u ON s.user_id = u.user_id " +
                                  "WHERE s.student_id = ?";
            pstmt = conn.prepareStatement(getStudentSql);
            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();
            
            if (!rs.next()) {
                sendErrorResponse(out, "Student not found");
                return;
            }
            
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            String gradeLevel = rs.getString("grade_level");
            int userId = rs.getInt("user_id");
            
            rs.close();
            pstmt.close();
            
            // Start transaction
            conn.setAutoCommit(false);
            
            try {
                // Delete from students table
                String deleteStudentSql = "DELETE FROM students WHERE student_id = ?";
                pstmt = conn.prepareStatement(deleteStudentSql);
                pstmt.setString(1, studentId);
                int studentRows = pstmt.executeUpdate();
                pstmt.close();
                
                // Delete from users table
                String deleteUserSql = "DELETE FROM users WHERE user_id = ?";
                pstmt = conn.prepareStatement(deleteUserSql);
                pstmt.setInt(1, userId);
                int userRows = pstmt.executeUpdate();
                
                if (studentRows > 0 && userRows > 0) {
                    conn.commit();
                    
                    // Also delete from grade-specific database if exists
                    deleteFromGradeDatabase(Integer.parseInt(gradeLevel), studentId);
                    
                    sendSuccessResponse(out, studentId, firstName, lastName);
                } else {
                    conn.rollback();
                    sendErrorResponse(out, "Failed to delete student. Please try again.");
                }
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            sendErrorResponse(out, "Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            sendErrorResponse(out, "Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBConnection.closeConnection(conn, pstmt, rs);
        }
    }
    
    private void deleteFromGradeDatabase(int gradeLevel, String studentId) {
        Connection gradeConn = null;
        PreparedStatement pstmt = null;
        
        try {
            String gradeDbName = DBConnection.getGradeDatabase(gradeLevel);
            gradeConn = DBConnection.getConnection(gradeDbName);
            
            if (gradeConn != null) {
                String sql = "DELETE FROM students WHERE student_id = ?";
                pstmt = gradeConn.prepareStatement(sql);
                pstmt.setString(1, studentId);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Warning: Could not delete from grade database: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (gradeConn != null) gradeConn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void sendSuccessResponse(PrintWriter out, String studentId, String firstName, String lastName) {
        out.println("<html><head><title>Success</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; padding: 40px; text-align: center; background: #f5f7fa; }");
        out.println(".success-box { background: white; border-radius: 15px; padding: 30px; margin: 20px auto; max-width: 700px; box-shadow: 0 10px 30px rgba(0,0,0,0.1); }");
        out.println(".success-title { color: #e74c3c; font-size: 28px; margin-bottom: 20px; font-weight: bold; }");
        out.println(".success-icon { font-size: 60px; color: #e74c3c; margin-bottom: 20px; }");
        out.println(".warning-box { background: #fef5e7; border: 2px solid #f39c12; padding: 20px; border-radius: 10px; margin: 20px 0; text-align: left; }");
        out.println(".info-box { background: #f8f9fa; padding: 20px; border-radius: 10px; margin: 20px 0; text-align: left; }");
        out.println(".info-row { display: flex; margin-bottom: 10px; }");
        out.println(".info-label { font-weight: bold; color: #2c3e50; width: 180px; }");
        out.println(".info-value { color: #34495e; }");
        out.println(".btn { display: inline-block; padding: 12px 25px; margin: 10px; border-radius: 8px; text-decoration: none; font-weight: bold; transition: all 0.3s; }");
        out.println(".btn-primary { background: #3498db; color: white; }");
        out.println(".btn-primary:hover { background: #2980b9; transform: translateY(-2px); box-shadow: 0 5px 15px rgba(52,152,219,0.3); }");
        out.println(".btn-secondary { background: #95a5a6; color: white; }");
        out.println(".btn-secondary:hover { background: #7f8c8d; }");
        out.println("</style>");
        out.println("</head><body>");
        
        out.println("<div class='success-box'>");
        out.println("<div class='success-icon'>🗑️</div>");
        out.println("<h1 class='success-title'>Student Deleted Successfully!</h1>");
        
        out.println("<div class='warning-box'>");
        out.println("<h3 style='color: #d35400; margin-top: 0;'>⚠️ Permanent Deletion</h3>");
        out.println("<p>This action cannot be undone. The following has been permanently deleted:</p>");
        out.println("<ul>");
        out.println("<li>Student record from main database</li>");
        out.println("<li>Associated user account</li>");
        out.println("<li>Grade-specific records (if any)</li>");
        out.println("</ul>");
        out.println("</div>");
        
        out.println("<div class='info-box'>");
        out.println("<div class='info-row'><div class='info-label'>Student ID:</div><div class='info-value'><strong>" + studentId + "</strong></div></div>");
        out.println("<div class='info-row'><div class='info-label'>Name:</div><div class='info-value'>" + firstName + " " + lastName + "</div></div>");
        out.println("<div class='info-row'><div class='info-label'>Time:</div><div class='info-value'>" + new java.util.Date() + "</div></div>");
        out.println("</div>");
        
        out.println("<div style='margin-top: 30px;'>");
        out.println("<a href='dashboard.html' class='btn btn-primary'>Return to Dashboard</a>");
        out.println("<button onclick='window.close()' class='btn btn-secondary'>Close Window</button>");
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
        out.println("<h1 class='error-title'>Deletion Failed</h1>");
        out.println("<p style='color: #c0392b; font-size: 16px;'>" + message + "</p>");
        out.println("<p>Possible reasons:</p>");
        out.println("<ul style='text-align: left; display: inline-block;'>");
        out.println("<li>Student has associated records (grades, attendance, etc.)</li>");
        out.println("<li>Database connection issue</li>");
        out.println("<li>Student ID does not exist</li>");
        out.println("</ul>");
        out.println("<div style='margin-top: 30px;'>");
        out.println("<a href='dashboard.html' class='btn btn-primary'>Return to Dashboard</a>");
        out.println("<button onclick='window.close()' class='btn btn-secondary'>Close</button>");
        out.println("</div>");
        out.println("</div>");
        out.println("</body></html>");
    }
}