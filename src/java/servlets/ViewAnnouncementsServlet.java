package servlets;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import database.DBConnection;
import java.text.SimpleDateFormat;

@WebServlet("/viewAnnouncements")
public class ViewAnnouncementsServlet extends HttpServlet {
    
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
        out.println("<title>School Announcements</title>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); min-height: 100vh; }");
        out.println(".header { background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%); color: white; padding: 20px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }");
        out.println(".header h1 { margin: 0; font-size: 1.8rem; font-weight: 300; }");
        out.println(".logout-btn { background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%); color: white; padding: 12px 20px; border: none; border-radius: 25px; cursor: pointer; text-decoration: none; font-weight: 500; transition: all 0.3s ease; box-shadow: 0 2px 5px rgba(231,76,60,0.3); }");
        out.println(".logout-btn:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(231,76,60,0.4); }");
        out.println(".menu { background: rgba(255,255,255,0.95); backdrop-filter: blur(10px); padding: 15px 20px; border-bottom: 1px solid rgba(0,0,0,0.1); display: flex; gap: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); flex-wrap: wrap; }");
        out.println(".menu a { text-decoration: none; color: #555; padding: 12px 20px; border-radius: 25px; font-weight: 500; transition: all 0.3s ease; }");
        out.println(".menu a:hover { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white; transform: translateY(-1px); }");
        out.println(".menu a.active { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white; box-shadow: 0 2px 8px rgba(52,152,219,0.3); }");
        out.println(".container { max-width: 1000px; margin: 30px auto; padding: 0 20px; }");
        out.println(".card { background: rgba(255,255,255,0.95); backdrop-filter: blur(20px); border-radius: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.15); padding: 30px; position: relative; overflow: hidden; }");
        out.println(".card::before { content: ''; position: absolute; top: 0; left: 0; right: 0; height: 4px; background: linear-gradient(90deg, #3498db, #2ecc71, #f39c12); }");
        out.println(".card h2 { color: #2c3e50; margin-top: 0; font-size: 2rem; font-weight: 300; text-align: center; position: relative; }");
        out.println(".card h2::after { content: ''; position: absolute; bottom: -10px; left: 50%; transform: translateX(-50%); width: 50px; height: 3px; background: linear-gradient(90deg, #3498db, #2ecc71); border-radius: 2px; }");
        out.println(".subtitle { text-align: center; color: #7f8c8d; font-size: 1.1rem; margin-bottom: 30px; font-weight: 300; }");
        out.println(".announcements-list { display: flex; flex-direction: column; gap: 20px; }");
        
        // Announcement Item Styles
        out.println(".announcement { background: linear-gradient(135deg, #ffffff 0%, #f9f9f9 100%); border-radius: 15px; padding: 25px; transition: all 0.3s ease; border-left: 5px solid #3498db; box-shadow: 0 5px 15px rgba(0,0,0,0.05); position: relative; }");
        out.println(".announcement:hover { transform: translateY(-3px); box-shadow: 0 8px 20px rgba(52,152,219,0.15); }");
        out.println(".announcement.urgent { border-left-color: #e74c3c; background: linear-gradient(135deg, #fff5f5 0%, #ffeaea 100%); }");
        out.println(".announcement-title { font-size: 1.4rem; font-weight: 600; color: #2c3e50; margin-bottom: 10px; display: flex; justify-content: space-between; align-items: center; }");
        out.println(".announcement-meta { display: flex; align-items: center; gap: 15px; margin-bottom: 15px; color: #7f8c8d; font-size: 0.95rem; flex-wrap: wrap; }");
        out.println(".announcement-content { color: #34495e; line-height: 1.6; font-size: 1.05rem; margin-bottom: 15px; }");
        
        // Urgent Badge
        out.println(".urgent-badge { background: #e74c3c; color: white; padding: 4px 10px; border-radius: 12px; font-size: 0.8rem; font-weight: 600; margin-left: 10px; }");
        
        // Attachment Styles
        out.println(".announcement-attachment { background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); padding: 12px 15px; border-radius: 8px; margin-top: 15px; border-left: 3px solid #3498db; display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }");
        out.println(".attachment-icon { font-size: 1.2rem; }");
        out.println(".attachment-link { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white; padding: 8px 16px; border-radius: 20px; text-decoration: none; font-weight: 500; transition: all 0.3s ease; display: inline-flex; align-items: center; gap: 8px; }");
        out.println(".attachment-link:hover { transform: translateY(-2px); box-shadow: 0 4px 10px rgba(52,152,219,0.3); }");
        out.println(".attachment-info { color: #7f8c8d; font-size: 0.9rem; margin-left: auto; }");
        out.println(".attachment-desc { color: #666; font-size: 0.9rem; }");
        
        // File Type Icons
        out.println(".file-icon-pdf { color: #e74c3c; }");
        out.println(".file-icon-doc { color: #3498db; }");
        out.println(".file-icon-xls { color: #27ae60; }");
        out.println(".file-icon-ppt { color: #f39c12; }");
        out.println(".file-icon-img { color: #9b59b6; }");
        out.println(".file-icon-zip { color: #34495e; }");
        out.println(".file-icon-txt { color: #7f8c8d; }");
        
        // No announcements
        out.println(".no-announcements { text-align: center; color: #7f8c8d; font-size: 1.2rem; padding: 40px 20px; background: rgba(255,255,255,0.5); border-radius: 15px; }");
        out.println(".no-announcements-icon { font-size: 60px; margin-bottom: 20px; opacity: 0.3; }");
        
        // Back button
        out.println(".back-btn { background: linear-gradient(135deg, #7f8c8d 0%, #95a5a6 100%); color: white; padding: 14px 30px; border: none; border-radius: 25px; cursor: pointer; text-decoration: none; display: inline-block; margin: 30px auto 0; font-weight: 500; transition: all 0.3s ease; box-shadow: 0 3px 10px rgba(127,140,141,0.3); text-align: center; width: 100%; max-width: 250px; }");
        out.println(".back-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(127,140,141,0.4); }");
        
        // Responsive
        out.println("@media (max-width: 768px) {");
        out.println(".header { flex-direction: column; text-align: center; gap: 15px; padding: 15px; }");
        out.println(".header h1 { font-size: 1.5rem; }");
        out.println(".menu { justify-content: center; }");
        out.println(".announcement-title { flex-direction: column; align-items: flex-start; gap: 10px; }");
        out.println(".announcement-meta { flex-direction: column; align-items: flex-start; gap: 5px; }");
        out.println(".container { margin: 20px auto; padding: 0 15px; }");
        out.println(".announcement-attachment { flex-direction: column; align-items: flex-start; gap: 10px; }");
        out.println(".attachment-info { margin-left: 0; }");
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
        out.println("<a href='viewAnnouncements' class='active'>Announcements</a>");
        out.println("<a href='viewCalendar'>Calendar</a>");
        out.println("</div>");
        
        // Content
        out.println("<div class='container'>");
        out.println("<div class='card'>");
        out.println("<h2>School Announcements</h2>");
        out.println("<div class='subtitle'>Latest news, updates, and study materials from teachers</div>");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // Get student's grade level
            String studentSql = "SELECT grade_level FROM students WHERE student_id = ?";
            pstmt = conn.prepareStatement(studentSql);
            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();
            
            int gradeLevel = 9; // Default
            if (rs.next()) {
                gradeLevel = rs.getInt("grade_level");
            }
            rs.close();
            pstmt.close();
            
            // Get announcements relevant to the student
            String gradeTarget = "grade" + gradeLevel;
            
            // Try to get announcements with correct column names
            String announcementsSql = "";
            try {
                DatabaseMetaData meta = conn.getMetaData();
                ResultSet columns = meta.getColumns(null, null, "announcements", null);
                boolean hasTargetAudience = false;
                boolean hasAttachmentFilename = false;
                boolean hasAttachmentPath = false;
                
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME").toLowerCase();
                    if ("target_audience".equals(columnName)) hasTargetAudience = true;
                    if ("attachment_filename".equals(columnName)) hasAttachmentFilename = true;
                    if ("attachment_path".equals(columnName)) hasAttachmentPath = true;
                }
                columns.close();
                
                // Build SQL based on available columns
                StringBuilder sqlBuilder = new StringBuilder("SELECT announcement_id, title, content, ");
                
                if (hasTargetAudience) {
                    sqlBuilder.append("target_audience, ");
                } else {
                    sqlBuilder.append("audience as target_audience, ");
                }
                
                sqlBuilder.append("is_urgent, posted_by, posted_date, expiry_date");
                
                if (hasAttachmentFilename) {
                    sqlBuilder.append(", attachment_filename");
                }
                if (hasAttachmentPath) {
                    sqlBuilder.append(", attachment_path");
                }
                
                sqlBuilder.append(" FROM announcements WHERE (target_audience = 'all' OR target_audience = 'students' OR target_audience = ?) ");
                sqlBuilder.append("AND (expiry_date IS NULL OR expiry_date >= CURRENT_DATE) ");
                sqlBuilder.append("ORDER BY is_urgent DESC, posted_date DESC");
                
                announcementsSql = sqlBuilder.toString();
            } catch (Exception e) {
                // Default SQL
                announcementsSql = "SELECT announcement_id, title, content, target_audience, is_urgent, " +
                                  "posted_by, posted_date, expiry_date, attachment_filename, attachment_path " +
                                  "FROM announcements WHERE (target_audience = 'all' OR target_audience = 'students' OR target_audience = ?) " +
                                  "AND (expiry_date IS NULL OR expiry_date >= CURRENT_DATE) " +
                                  "ORDER BY is_urgent DESC, posted_date DESC";
            }
            
            pstmt = conn.prepareStatement(announcementsSql);
            pstmt.setString(1, gradeTarget);
            rs = pstmt.executeQuery();
            
            out.println("<div class='announcements-list'>");
            boolean hasAnnouncements = false;
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SimpleDateFormat expiryFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            while (rs.next()) {
                hasAnnouncements = true;
                String annId = rs.getString("announcement_id");
                String title = rs.getString("title");
                String content = rs.getString("content");
                String target = rs.getString("target_audience");
                boolean isUrgent = rs.getBoolean("is_urgent");
                String postedBy = rs.getString("posted_by");
                Timestamp postedDate = rs.getTimestamp("posted_date");
                java.sql.Date expiryDate = rs.getDate("expiry_date");
                String attachmentFilename = null;
                String attachmentPath = null;
                
                try {
                    attachmentFilename = rs.getString("attachment_filename");
                    attachmentPath = rs.getString("attachment_path");
                } catch (SQLException e) {
                    // Columns might not exist yet
                }
                
                String postedDateStr = dateFormat.format(postedDate);
                String expiryDateStr = (expiryDate != null) ? expiryFormat.format(expiryDate) : null;
                
                // Get file icon class based on file extension
                String fileIconClass = getFileIconClass(attachmentFilename);
                
                out.println("<div class='announcement" + (isUrgent ? " urgent" : "") + "'>");
                out.println("<div class='announcement-title'>");
                out.println("<div style='display: flex; align-items: center;'>");
                out.println("<span>" + title + "</span>");
                if (isUrgent) {
                    out.println("<span class='urgent-badge'>URGENT</span>");
                }
                out.println("</div>");
                out.println("</div>");
                
                out.println("<div class='announcement-meta'>");
                out.println("<span>👤 <strong>Posted by:</strong> " + postedBy + "</span>");
                out.println("<span>📅 <strong>Date:</strong> " + postedDateStr + "</span>");
                if (expiryDateStr != null) {
                    out.println("<span>⏰ <strong>Expires:</strong> " + expiryDateStr + "</span>");
                }
                out.println("</div>");
                
                out.println("<div class='announcement-content'>" + content.replace("\n", "<br>") + "</div>");
                
                // Display attachment if exists
                if (attachmentFilename != null && !attachmentFilename.isEmpty() && 
                    attachmentPath != null && !attachmentPath.isEmpty()) {
                    out.println("<div class='announcement-attachment'>");
                    out.println("<div class='attachment-icon " + fileIconClass + "'>📎</div>");
                    out.println("<div style='flex: 1;'>");
                    out.println("<div class='attachment-desc'><strong>Attached File:</strong> " + attachmentFilename + "</div>");
                    out.println("</div>");
                    out.println("<a href='downloadAttachment?path=" + java.net.URLEncoder.encode(attachmentPath, "UTF-8") + 
                               "&name=" + java.net.URLEncoder.encode(attachmentFilename, "UTF-8") + 
                               "' class='attachment-link' target='_blank'>");
                    out.println("📥 Download");
                    out.println("</a>");
                    out.println("<div class='attachment-info'>Click to download</div>");
                    out.println("</div>");
                }
                
                out.println("</div>"); // Close announcement
            }
            
            if (!hasAnnouncements) {
                out.println("<div class='no-announcements'>");
                out.println("<div class='no-announcements-icon'>📢</div>");
                out.println("<h3>No Announcements Available</h3>");
                out.println("<p>There are no announcements for you at the moment.</p>");
                out.println("<p>Check back later for updates from your teachers!</p>");
                out.println("</div>");
            }
            
            out.println("</div>"); // Close announcements-list
            
        } catch (Exception e) {
            out.println("<div class='no-announcements' style='color:#e74c3c;'>");
            out.println("<div class='no-announcements-icon'>❌</div>");
            out.println("<h3>Error Loading Announcements</h3>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("</div>");
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
        
        out.println("<div style='text-align: center; margin-top: 30px;'>");
        out.println("<a href='dashboard_student.html' class='back-btn'>← Back to Dashboard</a>");
        out.println("</div>");
        out.println("</div>"); // Close card
        out.println("</div>"); // Close container
        
        // Footer note about attachments
        out.println("<div style='text-align: center; margin-top: 20px; padding: 15px; color: #7f8c8d; font-size: 0.9rem;'>");
        out.println("<p>📎 Attachments may include assignments, worksheets, study materials, or important documents from your teachers.</p>");
        out.println("</div>");
        
        out.println("</body>");
        out.println("</html>");
    }
    
    private String getFileIconClass(String filename) {
        if (filename == null) return "";
        String lowerName = filename.toLowerCase();
        if (lowerName.endsWith(".pdf")) return "file-icon-pdf";
        if (lowerName.endsWith(".doc") || lowerName.endsWith(".docx")) return "file-icon-doc";
        if (lowerName.endsWith(".xls") || lowerName.endsWith(".xlsx")) return "file-icon-xls";
        if (lowerName.endsWith(".ppt") || lowerName.endsWith(".pptx")) return "file-icon-ppt";
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || lowerName.endsWith(".png") || lowerName.endsWith(".gif")) return "file-icon-img";
        if (lowerName.endsWith(".zip") || lowerName.endsWith(".rar")) return "file-icon-zip";
        if (lowerName.endsWith(".txt")) return "file-icon-txt";
        return "";
    }
}