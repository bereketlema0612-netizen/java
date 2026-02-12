package register;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import database.DBConnection;

@WebServlet("/SearchStudentServlet")
public class SearchStudentServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        // Get search parameters
        String studentId = request.getParameter("studentId");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String gradeLevel = request.getParameter("gradeLevel");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            // Get database connection
            conn = DBConnection.getConnection();
            
            if (conn == null) {
                out.println("<h3 style='color: red; text-align: center;'>Database connection failed</h3>");
                return;
            }
            
            // Build SQL query dynamically based on search criteria
            StringBuilder sql = new StringBuilder(
                "SELECT s.*, u.username FROM students s " +
                "LEFT JOIN users u ON s.user_id = u.user_id WHERE 1=1"
            );
            
            if (studentId != null && !studentId.trim().isEmpty()) {
                sql.append(" AND s.student_id LIKE ?");
            }
            if (firstName != null && !firstName.trim().isEmpty()) {
                sql.append(" AND s.first_name LIKE ?");
            }
            if (lastName != null && !lastName.trim().isEmpty()) {
                sql.append(" AND s.last_name LIKE ?");
            }
            if (gradeLevel != null && !gradeLevel.trim().isEmpty()) {
                sql.append(" AND s.grade_level = ?");
            }
            
            sql.append(" ORDER BY s.last_name, s.first_name");
            
            pstmt = conn.prepareStatement(sql.toString());
            
            // Set parameters
            int paramIndex = 1;
            if (studentId != null && !studentId.trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + studentId + "%");
            }
            if (firstName != null && !firstName.trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + firstName + "%");
            }
            if (lastName != null && !lastName.trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + lastName + "%");
            }
            if (gradeLevel != null && !gradeLevel.trim().isEmpty()) {
                pstmt.setString(paramIndex++, gradeLevel);
            }
            
            rs = pstmt.executeQuery();
            
            // Generate HTML response
            generateSearchResultsHTML(out, rs, studentId, firstName, lastName, gradeLevel);
            
        } catch (SQLException e) {
            out.println("<h3 style='color: red; text-align: center;'>Database error: " + e.getMessage() + "</h3>");
            e.printStackTrace();
        } catch (Exception e) {
            out.println("<h3 style='color: red; text-align: center;'>Error: " + e.getMessage() + "</h3>");
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn, pstmt, rs);
        }
    }
    
    private void generateSearchResultsHTML(PrintWriter out, ResultSet rs, 
                                          String studentId, String firstName, 
                                          String lastName, String gradeLevel) throws SQLException {
        
        out.println("<html><head><title>Search Results</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background: #f5f7fa; }");
        out.println(".container { max-width: 1200px; margin: 0 auto; }");
        out.println(".header { background: white; padding: 20px; border-radius: 10px; margin-bottom: 20px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        out.println(".results-title { color: #2c3e50; margin-bottom: 20px; }");
        out.println(".search-summary { background: #e8f4fc; padding: 15px; border-radius: 8px; margin-bottom: 20px; }");
        out.println(".results-table { width: 100%; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        out.println(".results-table th { background: #3498db; color: white; padding: 15px; text-align: left; }");
        out.println(".results-table td { padding: 15px; border-bottom: 1px solid #eee; }");
        out.println(".results-table tr:hover { background: #f9f9f9; }");
        out.println(".no-results { text-align: center; padding: 40px; color: #7f8c8d; background: white; border-radius: 10px; }");
        out.println(".status-badge { padding: 5px 12px; border-radius: 20px; font-size: 12px; font-weight: 500; }");
        out.println(".status-active { background: #d4edda; color: #155724; }");
        out.println(".status-pending { background: #fff3cd; color: #856404; }");
        out.println(".status-inactive { background: #f8f9fa; color: #6c757d; }");
        out.println(".status-transferred { background: #d1ecf1; color: #0c5460; }");
        out.println(".action-btn { padding: 6px 12px; border: none; border-radius: 5px; cursor: pointer; font-size: 12px; font-weight: 500; margin: 2px; }");
        out.println(".edit-btn { background: #3498db; color: white; }");
        out.println(".edit-btn:hover { background: #2980b9; }");
        out.println(".view-btn { background: #2ecc71; color: white; }");
        out.println(".view-btn:hover { background: #27ae60; }");
        out.println(".btn { display: inline-block; padding: 10px 20px; background: #3498db; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; }");
        out.println(".btn:hover { background: #2980b9; }");
        out.println("</style>");
        out.println("</head><body>");
        
        out.println("<div class='container'>");
        out.println("<div class='header'>");
        out.println("<h1 class='results-title'>Search Results</h1>");
        
        // Show search criteria
        out.println("<div class='search-summary'>");
        out.println("<strong>Search Criteria:</strong> ");
        boolean hasCriteria = false;
        if (studentId != null && !studentId.trim().isEmpty()) {
            out.println("Student ID: \"" + studentId + "\" ");
            hasCriteria = true;
        }
        if (firstName != null && !firstName.trim().isEmpty()) {
            out.println("First Name: \"" + firstName + "\" ");
            hasCriteria = true;
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            out.println("Last Name: \"" + lastName + "\" ");
            hasCriteria = true;
        }
        if (gradeLevel != null && !gradeLevel.trim().isEmpty()) {
            out.println("Grade Level: " + gradeLevel + " ");
            hasCriteria = true;
        }
        if (!hasCriteria) {
            out.println("Showing all students");
        }
        out.println("</div>");
        out.println("</div>");
        
        int count = 0;
        StringBuilder tableRows = new StringBuilder();
        
        while (rs.next()) {
            count++;
            String currentStudentId = rs.getString("student_id");
            String currentFirstName = rs.getString("first_name");
            String currentLastName = rs.getString("last_name");
            String currentGradeLevel = rs.getString("grade_level");
            String currentSection = rs.getString("section");
            String currentGender = rs.getString("gender");
            String currentEmail = rs.getString("email");
            String currentStatus = rs.getString("registration_status");
            String currentUsername = rs.getString("username");
            
            String statusClass = getStatusClass(currentStatus);
            
            tableRows.append("<tr>")
                    .append("<td>").append(currentStudentId).append("</td>")
                    .append("<td>").append(currentFirstName).append(" ").append(currentLastName).append("</td>")
                    .append("<td>Grade ").append(currentGradeLevel).append("</td>")
                    .append("<td>").append(currentSection).append("</td>")
                    .append("<td>").append(currentGender).append("</td>")
                    .append("<td>").append(currentEmail != null ? currentEmail : "-").append("</td>")
                    .append("<td><span class='status-badge status-").append(statusClass).append("'>")
                    .append(currentStatus).append("</span></td>")
                    .append("<td>")
                    .append("<button class='action-btn edit-btn' onclick=\"window.opener.showEditForm(")
                    .append("{student_id:'").append(currentStudentId).append("',")
                    .append("user_id:'").append(rs.getInt("user_id")).append("',")
                    .append("first_name:'").append(currentFirstName.replace("'", "\\'")).append("',")
                    .append("last_name:'").append(currentLastName.replace("'", "\\'")).append("',")
                    .append("dob:'").append(rs.getString("dob")).append("',")
                    .append("gender:'").append(currentGender).append("',")
                    .append("email:'").append(currentEmail != null ? currentEmail.replace("'", "\\'") : "").append("',")
                    .append("phone:'").append(rs.getString("phone") != null ? rs.getString("phone").replace("'", "\\'") : "").append("',")
                    .append("grade_level:'").append(currentGradeLevel).append("',")
                    .append("section:'").append(currentSection).append("',")
                    .append("address:'").append(rs.getString("address") != null ? rs.getString("address").replace("'", "\\'") : "").append("',")
                    .append("parent_name:'").append(rs.getString("parent_name") != null ? rs.getString("parent_name").replace("'", "\\'") : "").append("',")
                    .append("parent_phone:'").append(rs.getString("parent_phone") != null ? rs.getString("parent_phone").replace("'", "\\'") : "").append("',")
                    .append("registration_status:'").append(currentStatus).append("'")
                    .append("}); window.close();\">Edit</button>")
                    .append("</td>")
                    .append("</tr>");
        }
        
        if (count == 0) {
            out.println("<div class='no-results'>");
            out.println("<h3>No Students Found</h3>");
            out.println("<p>No students match your search criteria.</p>");
            out.println("<a href='javascript:window.close()' class='btn'>Close Window</a>");
            out.println("</div>");
        } else {
            out.println("<p><strong>Found " + count + " student(s)</strong></p>");
            out.println("<table class='results-table'>");
            out.println("<thead><tr>");
            out.println("<th>Student ID</th>");
            out.println("<th>Name</th>");
            out.println("<th>Grade</th>");
            out.println("<th>Section</th>");
            out.println("<th>Gender</th>");
            out.println("<th>Email</th>");
            out.println("<th>Status</th>");
            out.println("<th>Action</th>");
            out.println("</tr></thead>");
            out.println("<tbody>");
            out.println(tableRows.toString());
            out.println("</tbody>");
            out.println("</table>");
            
            out.println("<div style='margin-top: 20px;'>");
            out.println("<a href='javascript:window.close()' class='btn'>Close Window</a>");
            out.println("</div>");
        }
        
        out.println("</div>");
        out.println("</body></html>");
    }
    
    private String getStatusClass(String status) {
        if (status == null) return "inactive";
        status = status.toLowerCase();
        if (status.contains("active")) return "active";
        if (status.contains("pending")) return "pending";
        if (status.contains("inactive")) return "inactive";
        if (status.contains("transfer")) return "transferred";
        return "inactive";
    }
    
    // Handle POST requests (for AJAX calls)
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}