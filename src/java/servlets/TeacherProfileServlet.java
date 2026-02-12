package servlets;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import database.DBConnection;

@WebServlet("/teacherProfile")
public class TeacherProfileServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"teacher".equals(session.getAttribute("role"))) {
            response.sendRedirect("login_student_teacher.html");
            return;
        }
        
        String teacherId = (String) session.getAttribute("username");
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Teacher Profile</title>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); min-height: 100vh; }");
        
        // Header - Matching Grade Management
        out.println(".header { background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%); color: white; padding: 20px 40px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); display: flex; justify-content: space-between; align-items: center; }");
        out.println(".header h1 { margin: 0; font-size: 24px; font-weight: 300; }");
        out.println(".logout-btn { background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%); color: white; padding: 10px 20px; border: none; border-radius: 25px; cursor: pointer; font-size: 14px; font-weight: 600; transition: all 0.3s; box-shadow: 0 2px 5px rgba(231,76,60,0.3); }");
        out.println(".logout-btn:hover { background: #c0392b; transform: translateY(-2px); box-shadow: 0 4px 10px rgba(231,76,60,0.4); }");
        
        // Menu - Matching Grade Management
        out.println(".menu { background: rgba(255,255,255,0.95); padding: 15px 40px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); border-bottom: 1px solid rgba(0,0,0,0.08); backdrop-filter: blur(10px); }");
        out.println(".menu a { margin-right: 25px; text-decoration: none; color: #555; padding: 10px 20px; border-radius: 25px; font-size: 15px; font-weight: 500; transition: all 0.3s; display: inline-block; }");
        out.println(".menu a:hover { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white; transform: translateY(-2px); box-shadow: 0 3px 8px rgba(52,152,219,0.35); }");
        out.println(".menu a.active { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white; box-shadow: 0 3px 10px rgba(52,152,219,0.4); }");
        
        // Main Container
        out.println(".main-container { max-width: 1200px; margin: 30px auto; padding: 0 40px; }");
        
        // Card - Matching Grade Management
        out.println(".card { background: rgba(255,255,255,0.95); padding: 30px; border-radius: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.12); margin-bottom: 30px; position: relative; overflow: hidden; }");
        out.println(".card::before { content: ''; position: absolute; top: 0; left: 0; right: 0; height: 4px; background: linear-gradient(90deg, #3498db, #2ecc71, #f39c12); }");
        
        // Section Title - Matching Grade Management
        out.println(".section-title { color: #2c3e50; font-size: 1.8rem; margin-bottom: 25px; font-weight: 700; border-bottom: 2px solid #eaeaea; padding-bottom: 15px; }");
        
        // Profile Info Grid - Enhanced to match Grade Management
        out.println(".profile-info { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 25px; margin-top: 30px; }");
        out.println(".info-group { background: linear-gradient(135deg, #ffffff 0%, #f9f9f9 100%); padding: 25px; border-radius: 15px; border-left: 5px solid #3498db; box-shadow: 0 5px 15px rgba(0,0,0,0.05); transition: all 0.3s; }");
        out.println(".info-group:hover { transform: translateY(-5px); box-shadow: 0 10px 25px rgba(52,152,219,0.15); }");
        out.println(".info-label { font-weight: 600; color: #2c3e50; margin-bottom: 10px; font-size: 0.95rem; text-transform: uppercase; letter-spacing: 0.5px; }");
        out.println(".info-value { color: #34495e; font-size: 1.2rem; font-weight: 400; }");
        
        // Edit Button - Matching Grade Management
        out.println(".edit-btn { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white; padding: 12px 30px; border: none; border-radius: 25px; cursor: pointer; font-weight: 500; transition: all 0.3s; text-decoration: none; display: inline-block; margin-top: 30px; }");
        out.println(".edit-btn:hover { transform: translateY(-2px); box-shadow: 0 4px 15px rgba(52,152,219,0.3); }");
        
        // Status badge - Enhanced
        out.println(".status-badge { display: inline-block; padding: 6px 15px; border-radius: 20px; font-size: 0.9rem; font-weight: 600; }");
        out.println(".status-active { background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%); color: white; }");
        out.println(".status-inactive { background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%); color: white; }");
        
        // Teacher info header
        out.println(".teacher-header { display: flex; align-items: center; gap: 20px; margin-bottom: 30px; padding: 20px; background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); border-radius: 15px; }");
        out.println(".teacher-avatar { width: 100px; height: 100px; background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); border-radius: 50%; display: flex; align-items: center; justify-content: center; color: white; font-size: 2.5rem; font-weight: bold; }");
        out.println(".teacher-name { font-size: 2rem; font-weight: 700; color: #2c3e50; margin-bottom: 5px; }");
        out.println(".teacher-id { color: #7f8c8d; font-size: 1.1rem; }");
        
        // Responsive - Matching Grade Management
        out.println("@media (max-width: 768px) {");
        out.println(".header { padding: 20px; flex-direction: column; gap: 15px; text-align: center; }");
        out.println(".menu { padding: 15px; text-align: center; }");
        out.println(".menu a { margin: 5px; padding: 8px 15px; }");
        out.println(".main-container { margin: 20px auto; padding: 0 15px; }");
        out.println(".profile-info { grid-template-columns: 1fr; gap: 15px; }");
        out.println(".teacher-header { flex-direction: column; text-align: center; }");
        out.println(".teacher-avatar { width: 80px; height: 80px; font-size: 2rem; }");
        out.println("}");
        
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        
        // Header
        out.println("<div class='header'>");
        out.println("<h1>Teacher Dashboard</h1>");
        out.println("<div>");
        out.println("<span style='margin-right: 20px;'>Welcome, Teacher " + teacherId + "</span>");
        out.println("<button class='logout-btn' onclick=\"location.href='logout'\">Logout</button>");
        out.println("</div>");
        out.println("</div>");
        
        // Menu
        out.println("<div class='menu'>");
        out.println("<a href='teacherDashboard'>🏠 Dashboard</a>");
        out.println("<a href='teacherGrades'>📊 Grade Management</a>");
        out.println("<a href='teacherAnnouncements'>📢 Announcements</a>");
        out.println("<a href='teacherCalendar'>📅 Calendar</a>");
        out.println("<a href='teacherProfile' class='active'>👤 Profile</a>");
        out.println("</div>");
        
        // Content
        out.println("<div class='main-container'>");
        out.println("<div class='card'>");
        out.println("<h2 class='section-title'>Teacher Profile</h2>");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT teacher_id, first_name, last_name, email, phone, subject_specialty, assigned_grades, hire_date FROM teachers WHERE teacher_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, teacherId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Teacher Header
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String fullName = firstName + " " + lastName;
                String initials = firstName.substring(0, 1) + lastName.substring(0, 1);
                
                out.println("<div class='teacher-header'>");
                out.println("<div class='teacher-avatar'>" + initials + "</div>");
                out.println("<div>");
                out.println("<div class='teacher-name'>" + fullName + "</div>");
                out.println("<div class='teacher-id'>ID: " + teacherId + "</div>");
                out.println("</div>");
                out.println("</div>");
                
                out.println("<div class='profile-info'>");
                
                // Teacher ID
                out.println("<div class='info-group'>");
                out.println("<div class='info-label'>Teacher ID</div>");
                out.println("<div class='info-value'>" + rs.getString("teacher_id") + "</div>");
                out.println("</div>");
                
                // Full Name
                out.println("<div class='info-group'>");
                out.println("<div class='info-label'>Full Name</div>");
                out.println("<div class='info-value'>" + fullName + "</div>");
                out.println("</div>");
                
                // Email
                out.println("<div class='info-group'>");
                out.println("<div class='info-label'>Email Address</div>");
                out.println("<div class='info-value'>" + (rs.getString("email") != null ? rs.getString("email") : "Not provided") + "</div>");
                out.println("</div>");
                
                // Phone
                out.println("<div class='info-group'>");
                out.println("<div class='info-label'>Phone Number</div>");
                out.println("<div class='info-value'>" + (rs.getString("phone") != null ? rs.getString("phone") : "Not provided") + "</div>");
                out.println("</div>");
                
                // Subject Specialty
                out.println("<div class='info-group'>");
                out.println("<div class='info-label'>Subject Specialty</div>");
                out.println("<div class='info-value'>" + (rs.getString("subject_specialty") != null ? rs.getString("subject_specialty") : "Not assigned") + "</div>");
                out.println("</div>");
                
                // Assigned Grades
                out.println("<div class='info-group'>");
                out.println("<div class='info-label'>Assigned Grades</div>");
                String assignedGrades = rs.getString("assigned_grades");
                out.println("<div class='info-value'>" + (assignedGrades != null ? "Grades " + assignedGrades : "Not assigned") + "</div>");
                out.println("</div>");
                
                // Hire Date
                out.println("<div class='info-group'>");
                out.println("<div class='info-label'>Hire Date</div>");
                out.println("<div class='info-value'>" + rs.getDate("hire_date") + "</div>");
                out.println("</div>");
                
                // Assigned Subject (based on teacher ID)
                String assignedSubject = getAssignedSubject(teacherId);
                out.println("<div class='info-group'>");
                out.println("<div class='info-label'>Assigned Subject</div>");
                out.println("<div class='info-value'>" + assignedSubject + "</div>");
                out.println("</div>");
                
                // Status (Active)
                out.println("<div class='info-group'>");
                out.println("<div class='info-label'>Employment Status</div>");
                out.println("<div class='info-value'><span class='status-badge status-active'>ACTIVE</span></div>");
                out.println("</div>");
                
                out.println("</div>"); // Close profile-info
                
                // Edit button
                out.println("<div style='text-align: center; margin-top: 40px;'>");
                out.println("<a href='#' class='edit-btn'>✏️ Edit Profile Information</a>");
                out.println("</div>");
                
            } else {
                out.println("<p style='color: #e74c3c; text-align: center; font-size: 1.2rem;'>Teacher profile not found in database.</p>");
            }
            
        } catch (Exception e) {
            out.println("<p style='color: #e74c3c; text-align: center; font-size: 1.2rem;'>Database Error: " + e.getMessage() + "</p>");
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
        
        out.println("</div>"); // Close card
        out.println("</div>"); // Close main-container
        
        out.println("</body>");
        out.println("</html>");
    }
    
    private String getAssignedSubject(String teacherId) {
        switch(teacherId) {
            case "T001": return "Biology";
            case "T002": return "English";
            case "T003": return "Mathematics";
            case "T004": return "Chemistry";
            case "T005": return "Physics";
            case "T006": return "Art";
            case "T007": return "History";
            case "T008": return "Geography";
            case "T009": return "Civics";
            default: return "Not Assigned";
        }
    }
}