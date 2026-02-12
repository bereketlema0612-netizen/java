package servlets;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import database.DBConnection;

@WebServlet("/viewProfile")
public class ViewProfileServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"student".equals(session.getAttribute("role"))) {
            response.sendRedirect("login_student_teacher.html");
            return;
        }
        
        String studentId = (String) session.getAttribute("username");
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Student Profile</title>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); min-height: 100vh; }");
        out.println(".header { background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%); color: white; padding: 20px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }");
        out.println(".header h1 { margin: 0; font-size: 1.8rem; font-weight: 300; }");
        out.println(".logout-btn { background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%); color: white; padding: 12px 20px; border: none; border-radius: 25px; cursor: pointer; text-decoration: none; font-weight: 500; transition: all 0.3s ease; box-shadow: 0 2px 5px rgba(231,76,60,0.3); }");
        out.println(".logout-btn:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(231,76,60,0.4); }");
        out.println(".menu { background: rgba(255,255,255,0.95); backdrop-filter: blur(10px); padding: 15px 20px; border-bottom: 1px solid rgba(0,0,0,0.1); display: flex; gap: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }");
        out.println(".menu a { text-decoration: none; color: #555; padding: 12px 20px; border-radius: 25px; font-weight: 500; transition: all 0.3s ease; }");
        out.println(".menu a:hover { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white; transform: translateY(-1px); }");
        out.println(".menu a.active { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white; box-shadow: 0 2px 8px rgba(52,152,219,0.3); }");
        out.println(".container { max-width: 1000px; margin: 30px auto; padding: 0 20px; }");
        out.println(".card { background: rgba(255,255,255,0.95); backdrop-filter: blur(20px); border-radius: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.15); padding: 30px; position: relative; overflow: hidden; }");
        out.println(".card::before { content: ''; position: absolute; top: 0; left: 0; right: 0; height: 4px; background: linear-gradient(90deg, #3498db, #2ecc71, #f39c12); }");
        out.println(".card h2 { color: #2c3e50; margin-top: 0; font-size: 2rem; font-weight: 300; text-align: center; position: relative; }");
        out.println(".card h2::after { content: ''; position: absolute; bottom: -10px; left: 50%; transform: translateX(-50%); width: 50px; height: 3px; background: linear-gradient(90deg, #3498db, #2ecc71); border-radius: 2px; }");
        out.println(".profile-info { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; margin-top: 30px; }");
        out.println(".info-group { background: rgba(255,255,255,0.7); padding: 15px; border-radius: 12px; transition: all 0.3s ease; border-left: 4px solid #3498db; backdrop-filter: blur(10px); }");
        out.println(".info-group:hover { transform: translateX(5px); box-shadow: 0 5px 15px rgba(52,152,219,0.2); border-left-color: #2ecc71; }");
        out.println(".info-label { font-weight: 600; color: #2c3e50; margin-bottom: 5px; font-size: 0.95rem; text-transform: uppercase; letter-spacing: 0.5px; }");
        out.println(".info-value { color: #34495e; font-size: 1.1rem; font-weight: 400; }");
        out.println(".back-btn { background: linear-gradient(135deg, #7f8c8d 0%, #95a5a6 100%); color: white; padding: 12px 25px; border: none; border-radius: 25px; cursor: pointer; text-decoration: none; display: inline-block; margin-top: 25px; font-weight: 500; transition: all 0.3s ease; box-shadow: 0 3px 10px rgba(127,140,141,0.3); text-align: center; width: 100%; }");
        out.println(".back-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(127,140,141,0.4); }");
        out.println("@media (max-width: 768px) {");
        out.println(".profile-info { grid-template-columns: 1fr; gap: 15px; }");
        out.println(".header { flex-direction: column; text-align: center; gap: 15px; padding: 15px; }");
        out.println(".header h1 { font-size: 1.5rem; }");
        out.println(".menu { flex-wrap: wrap; justify-content: center; }");
        out.println(".container { margin: 20px auto; padding: 0 15px; }");
        out.println("}");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        
        // Header
        out.println("<div class='header'>");
        out.println("<h1>Bense High School - Student Portal</h1>");
        out.println("<div>");
        out.println("<span style='margin-right: 15px; font-weight: 500;'>Welcome, " + studentId + "</span>");
        out.println("<a href='logout' class='logout-btn'>Logout</a>");
        out.println("</div>");
        out.println("</div>");
        
        // Menu
        out.println("<div class='menu'>");
        out.println("<a href='dashboard_student.html'>Dashboard</a>");
        out.println("<a href='viewProfile' class='active'>Profile</a>");
        out.println("<a href='viewGrades'>Grades</a>");
        out.println("<a href='viewAnnouncements'>Announcements</a>");
        out.println("<a href='viewCalendar'>Calendar</a>");
        out.println("</div>");
        
        // Content
        out.println("<div class='container'>");
        out.println("<div class='card'>");
        out.println("<h2>Student Profile</h2>");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            // FIX: Using grade_level instead of grade
            String sql = "SELECT student_id, first_name, last_name, grade_level, section, dob, gender, email, phone, address, parent_name, registration_date, registration_status FROM students WHERE student_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                out.println("<div class='profile-info'>");
                
                // Student ID
                out.println("<div class='info-group'>");
                out.println("<div class='info-label'>Student ID</div>");
                out.println("<div class='info-value'>" + rs.getString("student_id") + "</div>");
                out.println("</div>");
                
                // Full Name
                out.println("<div class='info-group'>");
                out.println("<div class='info-label'>Full Name</div>");
                out.println("<div class='info-value'>" + rs.getString("first_name") + " " + rs.getString("last_name") + "</div>");
                out.println("</div>");
                
                // Grade Level - FIXED
                out.println("<div class='info-group'>");
                out.println("<div class='info-label'>Grade Level</div>");
                out.println("<div class='info-value'>Grade " + rs.getInt("grade_level") + " - " + rs.getString("section") + "</div>");
                out.println("</div>");
                
                // Date of Birth
                out.println("<div class='info-group'>");
                out.println("<div class='info-label'>Date of Birth</div>");
                out.println("<div class='info-value'>" + rs.getDate("dob") + "</div>");
                out.println("</div>");
                
                // Gender
                out.println("<div class='info-group'>");
                out.println("<div class='info-label'>Gender</div>");
                out.println("<div class='info-value'>" + rs.getString("gender") + "</div>");
                out.println("</div>");
                
                // Email
                out.println("<div class='info-group'>");
                out.println("<div class='info-label'>Email</div>");
                out.println("<div class='info-value'>" + (rs.getString("email") != null ? rs.getString("email") : "Not provided") + "</div>");
                out.println("</div>");
                
                // Phone
                out.println("<div class='info-group'>");
                out.println("<div class='info-label'>Phone</div>");
                out.println("<div class='info-value'>" + (rs.getString("phone") != null ? rs.getString("phone") : "Not provided") + "</div>");
                out.println("</div>");
                
                // Address
                out.println("<div class='info-group'>");
                out.println("<div class='info-label'>Address</div>");
                out.println("<div class='info-value'>" + (rs.getString("address") != null ? rs.getString("address") : "Not provided") + "</div>");
                out.println("</div>");
                
                // Parent/Guardian
                out.println("<div class='info-group'>");
                out.println("<div class='info-label'>Parent/Guardian</div>");
                out.println("<div class='info-value'>" + (rs.getString("parent_name") != null ? rs.getString("parent_name") : "Not provided") + "</div>");
                out.println("</div>");
                
                // Registration Date
                out.println("<div class='info-group'>");
                out.println("<div class='info-label'>Registration Date</div>");
                out.println("<div class='info-value'>" + rs.getDate("registration_date") + "</div>");
                out.println("</div>");
                
                // Registration Status
                out.println("<div class='info-group'>");
                out.println("<div class='info-label'>Status</div>");
                out.println("<div class='info-value'>" + rs.getString("registration_status") + "</div>");
                out.println("</div>");
                
                out.println("</div>"); // Close profile-info
            } else {
                out.println("<p style='color:#e74c3c; font-size:1.2rem; text-align:center;'>Student not found.</p>");
            }
            
        } catch (Exception e) {
            out.println("<p style='color:#e74c3c; font-size:1.2rem; text-align:center;'>Error: " + e.getMessage() + "</p>");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        out.println("<a href='dashboard_student.html' class='back-btn'>← Back to Dashboard</a>");
        out.println("</div>"); // Close card
        out.println("</div>"); // Close container
        
        out.println("</body>");
        out.println("</html>");
    }
}
