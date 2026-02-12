package register;
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import database.DBConnection;

@WebServlet("/AddStudentServlet")
public class AddStudentServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        // Get form data
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
        ResultSet rs = null;
        
        try {
            // Get database connection
            conn = DBConnection.getConnection();
            
            if (conn == null) {
                sendErrorResponse(out, "Database connection failed. Please check database settings.");
                return;
            }
            
            // Generate unique student ID
            String studentId = generateStudentId(conn);
            
            // Insert into users table
            String userSql = "INSERT INTO users (username, password, role, created_at) VALUES (?, ?, ?, NOW())";
            pstmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            
            String username = (firstName.substring(0, 1) + lastName).toLowerCase();
            String defaultPassword = "Student@" + dob.replace("-", "").substring(0, 4);
            
            pstmt.setString(1, username);
            pstmt.setString(2, defaultPassword); // In production, hash this password
            pstmt.setString(3, "student");
            
            int userRows = pstmt.executeUpdate();
            
            if (userRows == 0) {
                sendErrorResponse(out, "Error creating user account");
                return;
            }
            
            // Get generated user_id
            rs = pstmt.getGeneratedKeys();
            int userId = 0;
            if (rs.next()) {
                userId = rs.getInt(1);
            }
            rs.close();
            pstmt.close();
            
            // Insert into students table
            String studentSql = "INSERT INTO students (student_id, user_id, first_name, last_name, dob, " +
                              "gender, email, phone, grade_level, section, address, parent_name, " +
                              "parent_phone, registration_status, registration_date) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURDATE())";
            
            pstmt = conn.prepareStatement(studentSql);
            pstmt.setString(1, studentId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, firstName);
            pstmt.setString(4, lastName);
            pstmt.setString(5, dob);
            pstmt.setString(6, gender);
            pstmt.setString(7, email);
            pstmt.setString(8, phone);
            pstmt.setString(9, gradeLevel);
            pstmt.setString(10, section.toUpperCase());
            pstmt.setString(11, address);
            pstmt.setString(12, parentName);
            pstmt.setString(13, parentPhone);
            pstmt.setString(14, registrationStatus);
            
            int studentRows = pstmt.executeUpdate();
            
            if (studentRows > 0) {
                // Also create record in grade-specific database if needed
                createGradeSpecificRecord(Integer.parseInt(gradeLevel), studentId, firstName, lastName, section);
                
                sendSuccessResponse(out, studentId, firstName, lastName, gradeLevel, 
                                   section, username, defaultPassword);
            } else {
                // Rollback user creation if student creation fails
                String deleteUserSql = "DELETE FROM users WHERE user_id = ?";
                pstmt = conn.prepareStatement(deleteUserSql);
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
                
                sendErrorResponse(out, "Failed to add student. Please try again.");
            }
            
        } catch (SQLException e) {
            sendErrorResponse(out, "Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            sendErrorResponse(out, "Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn, pstmt, rs);
        }
    }
    
    private String generateStudentId(Connection conn) throws SQLException {
        String prefix = "STU";
        String year = String.valueOf(java.time.Year.now().getValue()).substring(2);
        
        String sql = "SELECT student_id FROM students ORDER BY student_id DESC LIMIT 1";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        
        int sequence = 1;
        if (rs.next()) {
            String lastId = rs.getString("student_id");
            if (lastId.startsWith(prefix + year)) {
                String seqStr = lastId.substring(5); // STUYY####
                try {
                    sequence = Integer.parseInt(seqStr) + 1;
                } catch (NumberFormatException e) {
                    sequence = 1;
                }
            }
        }
        rs.close();
        pstmt.close();
        
        return prefix + year + String.format("%04d", sequence);
    }
    
    private void createGradeSpecificRecord(int gradeLevel, String studentId, 
                                          String firstName, String lastName, String section) {
        Connection gradeConn = null;
        PreparedStatement pstmt = null;
        
        try {
            String gradeDbName = DBConnection.getGradeDatabase(gradeLevel);
            gradeConn = DBConnection.getConnection(gradeDbName);
            
            if (gradeConn != null) {
                String sql = "INSERT INTO students (student_id, first_name, last_name, section, " +
                            "academic_year) VALUES (?, ?, ?, ?, ?)";
                pstmt = gradeConn.prepareStatement(sql);
                pstmt.setString(1, studentId);
                pstmt.setString(2, firstName);
                pstmt.setString(3, lastName);
                pstmt.setString(4, section.toUpperCase());
                pstmt.setString(5, DBConnection.getCurrentAcademicYear());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Warning: Could not create grade-specific record: " + e.getMessage());
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
                                    String username, String password) {
        out.println("<html><head><title>Success</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; padding: 40px; text-align: center; background: #f5f7fa; }");
        out.println(".success-box { background: white; border-radius: 15px; padding: 30px; margin: 20px auto; max-width: 700px; box-shadow: 0 10px 30px rgba(0,0,0,0.1); }");
        out.println(".success-title { color: #2ecc71; font-size: 28px; margin-bottom: 20px; font-weight: bold; }");
        out.println(".success-icon { font-size: 60px; color: #2ecc71; margin-bottom: 20px; }");
        out.println(".info-box { background: #f8f9fa; padding: 20px; border-radius: 10px; margin: 20px 0; text-align: left; border-left: 4px solid #3498db; }");
        out.println(".info-row { display: flex; margin-bottom: 10px; }");
        out.println(".info-label { font-weight: bold; color: #2c3e50; width: 180px; }");
        out.println(".info-value { color: #34495e; }");
        out.println(".credentials { background: #fff3cd; border: 2px dashed #f39c12; padding: 15px; border-radius: 8px; margin: 15px 0; }");
        out.println(".btn { display: inline-block; padding: 12px 25px; margin: 10px; border-radius: 8px; text-decoration: none; font-weight: bold; transition: all 0.3s; }");
        out.println(".btn-primary { background: #3498db; color: white; }");
        out.println(".btn-primary:hover { background: #2980b9; transform: translateY(-2px); box-shadow: 0 5px 15px rgba(52,152,219,0.3); }");
        out.println(".btn-secondary { background: #95a5a6; color: white; }");
        out.println(".btn-secondary:hover { background: #7f8c8d; }");
        out.println("</style>");
        out.println("</head><body>");
        
        out.println("<div class='success-box'>");
        out.println("<div class='success-icon'>✅</div>");
        out.println("<h1 class='success-title'>Student Added Successfully!</h1>");
        out.println("<p>The student has been registered in the system.</p>");
        
        out.println("<div class='info-box'>");
        out.println("<div class='info-row'><div class='info-label'>Student ID:</div><div class='info-value'>" + studentId + "</div></div>");
        out.println("<div class='info-row'><div class='info-label'>Name:</div><div class='info-value'>" + firstName + " " + lastName + "</div></div>");
        out.println("<div class='info-row'><div class='info-label'>Grade & Section:</div><div class='info-value'>Grade " + gradeLevel + "-" + section.toUpperCase() + "</div></div>");
        out.println("<div class='info-row'><div class='info-label'>Academic Year:</div><div class='info-value'>" + DBConnection.getCurrentAcademicYear() + "</div></div>");
        out.println("</div>");
        
        out.println("<div class='credentials'>");
        out.println("<h3 style='color: #e67e22; margin-top: 0;'>Login Credentials</h3>");
        out.println("<div class='info-row'><div class='info-label'>Username:</div><div class='info-value'><strong>" + username + "</strong></div></div>");
        out.println("<div class='info-row'><div class='info-label'>Password:</div><div class='info-value'><strong>" + password + "</strong></div></div>");
        out.println("<p style='color: #e74c3c; font-size: 14px; margin-top: 10px;'>⚠️ Please provide these credentials to the student. They should change their password on first login.</p>");
        out.println("</div>");
        
        out.println("<div style='margin-top: 30px;'>");
        out.println("<a href='dashboard.html' class='btn btn-primary'>Return to Dashboard</a>");
        out.println("<a href='AddStudentServlet' class='btn btn-secondary'>Add Another Student</a>");
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
        out.println("<h1 class='error-title'>Registration Failed</h1>");
        out.println("<p style='color: #c0392b; font-size: 16px;'>" + message + "</p>");
        out.println("<div style='margin-top: 30px;'>");
        out.println("<a href='dashboard.html' class='btn btn-primary'>Return to Dashboard</a>");
        out.println("</div>");
        out.println("</div>");
        out.println("</body></html>");
    }
}