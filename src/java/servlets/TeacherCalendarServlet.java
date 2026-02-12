package servlets;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import database.DBConnection;

@WebServlet("/teacherCalendar")
public class TeacherCalendarServlet extends HttpServlet {
    
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
        out.println("<title>Academic Calendar</title>");
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
        
        // Calendar header
        out.println(".calendar-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px; padding: 20px; background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); border-radius: 15px; }");
        out.println(".current-year { font-size: 1.5rem; font-weight: 600; color: #2c3e50; }");
        out.println(".filter-controls { display: flex; gap: 15px; }");
        out.println(".filter-select { padding: 10px 15px; border: 1px solid #ddd; border-radius: 8px; background: white; }");
        
        // Event styling - Enhanced to match Grade Management
        out.println(".event-list { margin-top: 25px; }");
        out.println(".event-item { background: linear-gradient(135deg, #ffffff 0%, #f9f9f9 100%); padding: 25px; border-radius: 15px; margin-bottom: 20px; border-left: 5px solid #3498db; box-shadow: 0 5px 15px rgba(0,0,0,0.05); transition: all 0.3s; display: flex; align-items: center; gap: 20px; }");
        out.println(".event-item:hover { transform: translateY(-5px); box-shadow: 0 10px 25px rgba(52,152,219,0.15); }");
        out.println(".event-item.exam { border-left-color: #e74c3c; background: linear-gradient(135deg, #fff5f5 0%, #ffeaea 100%); }");
        out.println(".event-item.holiday { border-left-color: #f39c12; background: linear-gradient(135deg, #fff8e0 0%, #ffefc6 100%); }");
        out.println(".event-item.meeting { border-left-color: #9b59b6; background: linear-gradient(135deg, #f5eef8 0%, #ebdef4 100%); }");
        out.println(".event-item.deadline { border-left-color: #2ecc71; background: linear-gradient(135deg, #e8f8ef 0%, #d1f2e1 100%); }");
        out.println(".event-item.semester_start { border-left-color: #3498db; background: linear-gradient(135deg, #e8f4fc 0%, #d1e8f2 100%); }");
        out.println(".event-item.semester_end { border-left-color: #8e44ad; background: linear-gradient(135deg, #f4eef8 0%, #e8def4 100%); }");
        
        // Event date box
        out.println(".event-date-box { min-width: 80px; text-align: center; }");
        out.println(".event-month { font-size: 0.9rem; font-weight: 600; color: #7f8c8d; text-transform: uppercase; margin-bottom: 5px; }");
        out.println(".event-day { font-size: 2rem; font-weight: 700; color: #2c3e50; }");
        
        // Event content
        out.println(".event-content { flex: 1; }");
        out.println(".event-title { font-size: 1.3rem; font-weight: 600; color: #2c3e50; margin-bottom: 8px; display: flex; align-items: center; gap: 10px; }");
        out.println(".event-description { color: #34495e; line-height: 1.6; margin-bottom: 10px; }");
        out.println(".event-type { display: inline-block; background: #3498db; color: white; padding: 4px 12px; border-radius: 20px; font-size: 0.8rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em; }");
        out.println(".event-type.exam { background: #e74c3c; }");
        out.println(".event-type.holiday { background: #f39c12; }");
        out.println(".event-type.meeting { background: #9b59b6; }");
        out.println(".event-type.deadline { background: #2ecc71; }");
        out.println(".event-type.semester_start { background: #3498db; }");
        out.println(".event-type.semester_end { background: #8e44ad; }");
        out.println(".event-meta { color: #7f8c8d; font-size: 0.9rem; margin-top: 5px; }");
        
        // No events
        out.println(".no-events { text-align: center; padding: 60px 40px; color: #7f8c8d; }");
        out.println(".no-events-icon { font-size: 60px; margin-bottom: 20px; opacity: 0.3; }");
        
        // Responsive - Matching Grade Management
        out.println("@media (max-width: 768px) {");
        out.println(".header { padding: 20px; flex-direction: column; gap: 15px; text-align: center; }");
        out.println(".menu { padding: 15px; text-align: center; }");
        out.println(".menu a { margin: 5px; padding: 8px 15px; }");
        out.println(".main-container { margin: 20px auto; padding: 0 15px; }");
        out.println(".calendar-header { flex-direction: column; gap: 15px; text-align: center; }");
        out.println(".event-item { flex-direction: column; align-items: flex-start; }");
        out.println(".event-date-box { display: flex; align-items: center; gap: 10px; }");
        out.println(".event-month, .event-day { display: inline; }");
        out.println("}");
        
        out.println("</style>");
        out.println("<script>");
        out.println("function filterEvents() {");
        out.println("    var filter = document.getElementById('eventFilter').value;");
        out.println("    var events = document.querySelectorAll('.event-item');");
        out.println("    events.forEach(function(event) {");
        out.println("        if (filter === 'all' || event.classList.contains(filter)) {");
        out.println("            event.style.display = 'flex';");
        out.println("        } else {");
        out.println("            event.style.display = 'none';");
        out.println("        }");
        out.println("    });");
        out.println("}");
        out.println("</script>");
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
        out.println("<a href='teacherCalendar' class='active'>📅 Calendar</a>");
        out.println("<a href='teacherProfile'>👤 Profile</a>");
        out.println("</div>");
        
        // Content
        out.println("<div class='main-container'>");
        out.println("<div class='card'>");
        out.println("<h2 class='section-title'>Academic Calendar</h2>");
        out.println("<p style='color: #7f8c8d; margin-bottom: 25px;'>Important dates, meetings, and events for teachers.</p>");
        
        // Calendar header with filter
        out.println("<div class='calendar-header'>");
        out.println("<div class='current-year'>Academic Year 2025-2026</div>");
        out.println("<div class='filter-controls'>");
        out.println("<select id='eventFilter' class='filter-select' onchange='filterEvents()'>");
        out.println("<option value='all'>All Events</option>");
        out.println("<option value='exam'>Exams</option>");
        out.println("<option value='holiday'>Holidays</option>");
        out.println("<option value='meeting'>Meetings</option>");
        out.println("<option value='deadline'>Deadlines</option>");
        out.println("<option value='semester_start'>Semester Start</option>");
        out.println("<option value='semester_end'>Semester End</option>");
        out.println("</select>");
        out.println("</div>");
        out.println("</div>");
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            
            // Get calendar events
            String sql = "SELECT event_id, event_name, event_date, description, event_type FROM calendar ORDER BY event_date";
            rs = stmt.executeQuery(sql);
            
            boolean hasEvents = false;
            out.println("<div class='event-list'>");
            
            // Month names for display
            String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
            
            while (rs.next()) {
                hasEvents = true;
                String eventId = rs.getString("event_id");
                String eventName = rs.getString("event_name");
                Date eventDate = rs.getDate("event_date");
                String description = rs.getString("description");
                String eventType = rs.getString("event_type");
                
                // Parse date
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(eventDate);
                int month = cal.get(java.util.Calendar.MONTH);
                int day = cal.get(java.util.Calendar.DAY_OF_MONTH);
                int year = cal.get(java.util.Calendar.YEAR);
                
                out.println("<div class='event-item " + eventType + "'>");
                out.println("<div class='event-date-box'>");
                out.println("<div class='event-month'>" + months[month] + "</div>");
                out.println("<div class='event-day'>" + day + "</div>");
                out.println("</div>");
                out.println("<div class='event-content'>");
                out.println("<div class='event-title'>");
                out.println("<span class='event-type " + eventType + "'>" + eventType.replace("_", " ").toUpperCase() + "</span>");
                out.println(eventName);
                out.println("</div>");
                out.println("<div class='event-description'>" + description + "</div>");
                out.println("<div class='event-meta'>📅 " + eventDate + "</div>");
                out.println("</div>");
                out.println("</div>");
            }
            
            if (!hasEvents) {
                out.println("<div class='no-events'>");
                out.println("<div class='no-events-icon'>📅</div>");
                out.println("<h3 style='color: #7f8c8d; margin-bottom: 15px;'>No Calendar Events</h3>");
                out.println("<p>No events scheduled at this time.</p>");
                out.println("</div>");
            }
            
            out.println("</div>");
            
        } catch (Exception e) {
            out.println("<div class='no-events' style='color:#e74c3c;'>Error loading calendar: " + e.getMessage() + "</div>");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
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
}