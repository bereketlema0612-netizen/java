package servlets;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import database.DBConnection;

@WebServlet("/viewCalendar")
public class ViewCalendarServlet extends HttpServlet {
    
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
        out.println("<title>Academic Calendar</title>");
        out.println("<style>");
        // Same global look as other pages
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
        out.println(".card p { text-align: center; color: #7f8c8d; font-size: 1.05rem; margin-bottom: 25px; }");
        
        // Event styling kept, just visually upgraded to match other pages
        out.println(".event { border-left: 5px solid #9b59b6; padding: 18px 20px; margin-bottom: 18px; background: rgba(255,255,255,0.85); border-radius: 14px; box-shadow: 0 4px 15px rgba(0,0,0,0.08); transition: all 0.3s ease; position: relative; overflow: hidden; backdrop-filter: blur(10px); }");
        out.println(".event::before { content: ''; position: absolute; top: 0; right: 0; width: 0; height: 100%; background: linear-gradient(90deg, transparent, rgba(155,89,182,0.12)); transition: width 0.3s ease; }");
        out.println(".event:hover { transform: translateY(-4px); box-shadow: 0 10px 25px rgba(155,89,182,0.25); }");
        out.println(".event:hover::before { width: 100%; }");
        out.println(".event-date { font-weight: 600; color: #2c3e50; margin-bottom: 6px; font-size: 0.95rem; letter-spacing: 0.03em; text-transform: uppercase; }");
        out.println(".event-title { color: #2c3e50; margin-bottom: 6px; font-size: 1.2rem; font-weight: 600; }");
        out.println(".event-description { color: #34495e; line-height: 1.6; font-size: 1.02rem; }");
        // Keep existing event-type badges but restyled to match
        out.println(".event-type { display: inline-block; background: #9b59b6; color: white; padding: 4px 10px; border-radius: 20px; font-size: 0.75rem; margin-right: 10px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em; }");
        out.println(".event-type.exam { background: #e74c3c; }");
        out.println(".event-type.holiday { background: #f39c12; }");
        
        out.println(".no-events { text-align: center; color: #7f8c8d; font-size: 1.1rem; padding: 35px 20px; background: rgba(255,255,255,0.6); border-radius: 15px; }");
        out.println(".back-btn { background: linear-gradient(135deg, #7f8c8d 0%, #95a5a6 100%); color: white; padding: 14px 30px; border: none; border-radius: 25px; cursor: pointer; text-decoration: none; display: inline-block; margin: 30px auto 0; font-weight: 500; transition: all 0.3s ease; box-shadow: 0 3px 10px rgba(127,140,141,0.3); text-align: center; width: 100%; max-width: 250px; }");
        out.println(".back-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(127,140,141,0.4); }");
        
        out.println("@media (max-width: 768px) {");
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
        out.println("<a href='viewProfile'>Profile</a>");
        out.println("<a href='viewGrades'>Grades</a>");
        out.println("<a href='viewAnnouncements'>Announcements</a>");
        out.println("<a href='viewCalendar' class='active'>Calendar</a>");
        out.println("</div>");
        
        // Content
        out.println("<div class='container'>");
        out.println("<div class='card'>");
        out.println("<h2>Academic Calendar</h2>");
        out.println("<p>Important dates and events</p>");
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            
            // Get current year events
            String sql = "SELECT event_name, event_date, description, event_type FROM calendar ORDER BY event_date";
            rs = stmt.executeQuery(sql);
            
            boolean hasEvents = false;
            while (rs.next()) {
                hasEvents = true;
                String eventName = rs.getString("event_name");
                Date eventDate = rs.getDate("event_date");
                String description = rs.getString("description");
                String eventType = rs.getString("event_type"); // exam, holiday, etc.
                
                out.println("<div class='event'>");
                out.println("<div class='event-date'>" + eventDate + "</div>");
                out.println("<div class='event-title'>");
                out.println("<span class='event-type " + eventType + "'>" + eventType.toUpperCase() + "</span>");
                out.println(eventName);
                out.println("</div>");
                out.println("<div class='event-description'>" + description + "</div>");
                out.println("</div>");
            }
            
            if (!hasEvents) {
                out.println("<div class='no-events'>No calendar events scheduled.</div>");
            }
            
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
        
        out.println("<a href='dashboard_student.html' class='back-btn'>← Back to Dashboard</a>");
        out.println("</div>"); // Close card
        out.println("</div>"); // Close container
        
        out.println("</body>");
        out.println("</html>");
    }
}
