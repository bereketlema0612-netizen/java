package servlets;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import database.DBConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/teacherAnnouncements")
public class TeacherAnnouncementsServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"teacher".equals(session.getAttribute("role"))) {
            response.sendRedirect("login_student_teacher.html");
            return;
        }
        
        String teacherId = (String) session.getAttribute("username");
        String action = request.getParameter("action");
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Announcement Management</title>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); min-height: 100vh; }");
        
        // Header
        out.println(".header { background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%); color: white; padding: 20px 40px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); display: flex; justify-content: space-between; align-items: center; }");
        out.println(".header h1 { margin: 0; font-size: 24px; font-weight: 300; }");
        out.println(".logout-btn { background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%); color: white; padding: 10px 20px; border: none; border-radius: 25px; cursor: pointer; font-size: 14px; font-weight: 600; transition: all 0.3s; box-shadow: 0 2px 5px rgba(231,76,60,0.3); }");
        out.println(".logout-btn:hover { background: #c0392b; transform: translateY(-2px); box-shadow: 0 4px 10px rgba(231,76,60,0.4); }");
        
        // Menu
        out.println(".menu { background: rgba(255,255,255,0.95); padding: 15px 40px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); border-bottom: 1px solid rgba(0,0,0,0.08); backdrop-filter: blur(10px); }");
        out.println(".menu a { margin-right: 25px; text-decoration: none; color: #555; padding: 10px 20px; border-radius: 25px; font-size: 15px; font-weight: 500; transition: all 0.3s; display: inline-block; }");
        out.println(".menu a:hover { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white; transform: translateY(-2px); box-shadow: 0 3px 8px rgba(52,152,219,0.35); }");
        out.println(".menu a.active { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white; box-shadow: 0 3px 10px rgba(52,152,219,0.4); }");
        
        // Main Container
        out.println(".main-container { max-width: 1200px; margin: 30px auto; padding: 0 40px; }");
        
        // Card
        out.println(".card { background: rgba(255,255,255,0.95); padding: 30px; border-radius: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.12); margin-bottom: 30px; position: relative; overflow: hidden; }");
        out.println(".card::before { content: ''; position: absolute; top: 0; left: 0; right: 0; height: 4px; background: linear-gradient(90deg, #3498db, #2ecc71, #f39c12); }");
        
        // Section Title
        out.println(".section-title { color: #2c3e50; font-size: 1.8rem; margin-bottom: 25px; font-weight: 700; border-bottom: 2px solid #eaeaea; padding-bottom: 15px; }");
        
        // Button Styles
        out.println(".action-button { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white; padding: 12px 25px; border: none; border-radius: 25px; cursor: pointer; font-size: 15px; font-weight: 500; text-decoration: none; display: inline-block; transition: all 0.3s; }");
        out.println(".action-button:hover { transform: translateY(-2px); box-shadow: 0 4px 15px rgba(52,152,219,0.3); }");
        out.println(".danger-button { background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%); color: white; padding: 8px 15px; border: none; border-radius: 20px; cursor: pointer; font-size: 0.85rem; font-weight: 500; text-decoration: none; display: inline-block; transition: all 0.3s; }");
        out.println(".danger-button:hover { transform: translateY(-2px); box-shadow: 0 4px 10px rgba(231,76,60,0.3); }");
        
        // Announcement list
        out.println(".announcement-list { margin-top: 25px; }");
        out.println(".announcement-item { background: linear-gradient(135deg, #ffffff 0%, #f9f9f9 100%); padding: 25px; border-radius: 15px; margin-bottom: 20px; border-left: 5px solid #3498db; box-shadow: 0 5px 15px rgba(0,0,0,0.05); transition: all 0.3s; }");
        out.println(".announcement-item:hover { transform: translateY(-3px); box-shadow: 0 8px 20px rgba(52,152,219,0.15); }");
        out.println(".announcement-item.urgent { border-left-color: #e74c3c; background: linear-gradient(135deg, #fff5f5 0%, #ffeaea 100%); }");
        out.println(".announcement-title { font-size: 1.3rem; font-weight: 600; color: #2c3e50; margin-bottom: 10px; display: flex; justify-content: space-between; align-items: center; }");
        out.println(".announcement-meta { color: #7f8c8d; font-size: 0.9rem; margin-bottom: 15px; display: flex; gap: 15px; flex-wrap: wrap; }");
        out.println(".announcement-content { color: #34495e; line-height: 1.6; margin-bottom: 15px; }");
        out.println(".announcement-actions { margin-top: 15px; }");
        out.println(".urgent-badge { background: #e74c3c; color: white; padding: 4px 10px; border-radius: 12px; font-size: 0.8rem; font-weight: 600; margin-left: 10px; }");
        
        // Attachment Styles
        out.println(".announcement-attachment { background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); padding: 12px 15px; border-radius: 8px; margin-top: 15px; border-left: 3px solid #3498db; display: flex; align-items: center; gap: 10px; }");
        out.println(".attachment-icon { font-size: 1.2rem; }");
        out.println(".attachment-link { color: #3498db; text-decoration: none; font-weight: 500; transition: all 0.3s; }");
        out.println(".attachment-link:hover { color: #2980b9; text-decoration: underline; }");
        out.println(".attachment-size { color: #7f8c8d; font-size: 0.8rem; margin-left: auto; }");
        
        // Form Styles
        out.println(".form-group { margin-bottom: 20px; }");
        out.println(".form-label { display: block; font-weight: 600; color: #2c3e50; margin-bottom: 8px; }");
        out.println(".form-input, .form-textarea, .form-select { width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 8px; font-size: 1rem; box-sizing: border-box; }");
        out.println(".form-input:focus, .form-textarea:focus, .form-select:focus { outline: none; border-color: #3498db; box-shadow: 0 0 0 3px rgba(52,152,219,0.1); }");
        out.println(".form-textarea { min-height: 150px; resize: vertical; }");
        
        // File Upload Styles
        out.println(".file-input-wrapper { position: relative; overflow: hidden; display: inline-block; width: 100%; }");
        out.println(".file-input-wrapper input[type=file] { font-size: 100px; position: absolute; left: 0; top: 0; opacity: 0; cursor: pointer; }");
        out.println(".file-input-label { background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); border: 2px dashed #ccc; border-radius: 8px; padding: 30px; text-align: center; cursor: pointer; transition: all 0.3s; display: block; }");
        out.println(".file-input-label:hover { border-color: #3498db; background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%); }");
        out.println(".file-icon { font-size: 2rem; color: #7f8c8d; margin-bottom: 10px; }");
        out.println(".file-hint { color: #666; font-size: 0.9rem; margin-top: 5px; }");
        out.println(".file-preview { margin-top: 10px; padding: 10px; background: #f8f9fa; border-radius: 5px; display: none; }");
        out.println(".file-preview.active { display: block; }");
        
        // Submit Buttons
        out.println(".submit-btn { background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%); color: white; padding: 12px 30px; border: none; border-radius: 25px; font-size: 1rem; font-weight: 600; cursor: pointer; transition: all 0.3s; }");
        out.println(".submit-btn:hover { transform: translateY(-2px); box-shadow: 0 4px 15px rgba(46,204,113,0.3); }");
        out.println(".cancel-btn { background: linear-gradient(135deg, #7f8c8d 0%, #95a5a6 100%); color: white; padding: 12px 30px; border: none; border-radius: 25px; font-size: 1rem; font-weight: 600; cursor: pointer; transition: all 0.3s; text-decoration: none; display: inline-block; margin-left: 15px; }");
        out.println(".cancel-btn:hover { transform: translateY(-2px); box-shadow: 0 4px 15px rgba(127,140,141,0.3); }");
        
        // Checkbox
        out.println(".checkbox-label { display: flex; align-items: center; gap: 10px; cursor: pointer; }");
        out.println(".checkbox-input { width: 18px; height: 18px; cursor: pointer; }");
        
        // Statistics grid
        out.println(".stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin-top: 20px; }");
        out.println(".stat-card { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white; padding: 20px; border-radius: 12px; text-align: center; }");
        out.println(".stat-card.danger { background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%); }");
        out.println(".stat-card.success { background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%); }");
        out.println(".stat-number { font-size: 2.5rem; font-weight: bold; margin-bottom: 5px; }");
        out.println(".stat-label { font-size: 0.9rem; opacity: 0.9; }");
        
        // No announcements
        out.println(".no-announcements { text-align: center; padding: 60px 40px; color: #7f8c8d; }");
        out.println(".no-announcements-icon { font-size: 60px; margin-bottom: 20px; opacity: 0.3; }");
        
        // Success/Error Messages
        out.println(".success-message { background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%); color: white; padding: 15px; border-radius: 8px; margin-bottom: 20px; text-align: center; }");
        out.println(".error-message { background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%); color: white; padding: 15px; border-radius: 8px; margin-bottom: 20px; text-align: center; }");
        
        // Responsive
        out.println("@media (max-width: 768px) {");
        out.println(".header { padding: 20px; flex-direction: column; gap: 15px; text-align: center; }");
        out.println(".menu { padding: 15px; text-align: center; }");
        out.println(".menu a { margin: 5px; padding: 8px 15px; }");
        out.println(".main-container { margin: 20px auto; padding: 0 15px; }");
        out.println(".announcement-title { flex-direction: column; align-items: flex-start; gap: 10px; }");
        out.println(".announcement-meta { flex-direction: column; gap: 5px; }");
        out.println(".stats-grid { grid-template-columns: 1fr; }");
        out.println("}");
        
        out.println("</style>");
        
        // JavaScript for file preview
        out.println("<script>");
        out.println("function previewFile(input) {");
        out.println("  const preview = document.getElementById('filePreview');");
        out.println("  const label = document.querySelector('.file-input-label span');");
        out.println("  if (input.files && input.files[0]) {");
        out.println("    const file = input.files[0];");
        out.println("    const fileSize = (file.size / (1024 * 1024)).toFixed(2);");
        out.println("    preview.innerHTML = '📄 <strong>' + file.name + '</strong> (' + fileSize + ' MB)';");
        out.println("    preview.classList.add('active');");
        out.println("    label.textContent = 'Change File';");
        out.println("  } else {");
        out.println("    preview.classList.remove('active');");
        out.println("    label.textContent = 'Choose a file';");
        out.println("  }");
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
        out.println("<a href='teacherAnnouncements' class='active'>📢 Announcements</a>");
        out.println("<a href='teacherCalendar'>📅 Calendar</a>");
        out.println("<a href='teacherProfile'>👤 Profile</a>");
        out.println("</div>");
        
        // Content
        out.println("<div class='main-container'>");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // Get teacher's name
            String teacherName = "Teacher";
            String teacherSql = "SELECT first_name, last_name FROM teachers WHERE teacher_id = ?";
            pstmt = conn.prepareStatement(teacherSql);
            pstmt.setString(1, teacherId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                teacherName = rs.getString("first_name") + " " + rs.getString("last_name");
            }
            rs.close();
            pstmt.close();
            
            // Check if we're posting a new announcement
            if ("post".equals(action)) {
                // Show post announcement form
                out.println("<div class='card'>");
                out.println("<h2 class='section-title'>Post New Announcement</h2>");
                out.println("<form action='postAnnouncement' method='post' enctype='multipart/form-data'>");
                
                out.println("<div class='form-group'>");
                out.println("<label class='form-label'>Title</label>");
                out.println("<input type='text' class='form-input' name='title' required placeholder='Enter announcement title'>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label class='form-label'>Content</label>");
                out.println("<textarea class='form-textarea' name='content' required placeholder='Enter announcement content...'></textarea>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label class='form-label'>Target Audience</label>");
                out.println("<select class='form-select' name='target'>");
                out.println("<option value='all'>All (Students & Teachers)</option>");
                out.println("<option value='students'>All Students</option>");
                out.println("<option value='teachers'>All Teachers</option>");
                out.println("<option value='grade9'>Grade 9 Students</option>");
                out.println("<option value='grade10'>Grade 10 Students</option>");
                out.println("<option value='grade11'>Grade 11 Students</option>");
                out.println("<option value='grade12'>Grade 12 Students</option>");
                out.println("</select>");
                out.println("</div>");
                
                // File Upload Section
                out.println("<div class='form-group'>");
                out.println("<label class='form-label'>Attachment (Optional)</label>");
                out.println("<div class='file-input-wrapper'>");
                out.println("<input type='file' class='form-input' name='attachment' id='fileInput' accept='.pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.txt,.zip,.rar,.jpg,.jpeg,.png' onchange='previewFile(this)'>");
                out.println("<label class='file-input-label'>");
                out.println("<div class='file-icon'>📎</div>");
                out.println("<span>Choose a file</span>");
                out.println("<div class='file-hint'>Max 10MB • PDF, Word, Excel, PowerPoint, Images, ZIP</div>");
                out.println("</label>");
                out.println("</div>");
                out.println("<div id='filePreview' class='file-preview'></div>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label class='checkbox-label'>");
                out.println("<input type='checkbox' class='checkbox-input' name='urgent' value='true'>");
                out.println("<span>Mark as Urgent Announcement</span>");
                out.println("</label>");
                out.println("</div>");
                
                out.println("<div class='form-group'>");
                out.println("<label class='form-label'>Expiry Date (Optional)</label>");
                out.println("<input type='date' class='form-input' name='expiry'>");
                out.println("</div>");
                
                out.println("<input type='hidden' name='teacherId' value='" + teacherId + "'>");
                out.println("<input type='hidden' name='teacherName' value='" + teacherName + "'>");
                
                out.println("<div style='margin-top: 30px;'>");
                out.println("<button type='submit' class='submit-btn'>📢 Post Announcement</button>");
                out.println("<a href='teacherAnnouncements' class='cancel-btn'>Cancel</a>");
                out.println("</div>");
                
                out.println("</form>");
                out.println("</div>");
                
            } else {
                // Show success/error messages
                String posted = request.getParameter("posted");
                String deleted = request.getParameter("deleted");
                String error = request.getParameter("error");
                
                if ("true".equals(posted)) {
                    out.println("<div class='success-message'>");
                    out.println("✅ Announcement posted successfully!");
                    out.println("</div>");
                }
                if ("true".equals(deleted)) {
                    out.println("<div class='success-message'>");
                    out.println("🗑️ Announcement deleted successfully!");
                    out.println("</div>");
                }
                if ("unauthorized".equals(error)) {
                    out.println("<div class='error-message'>");
                    out.println("❌ You are not authorized to delete this announcement!");
                    out.println("</div>");
                }
                if ("database".equals(error)) {
                    out.println("<div class='error-message'>");
                    out.println("❌ Database error occurred!");
                    out.println("</div>");
                }
                
                // Show announcement list
                out.println("<div class='card'>");
                out.println("<div style='display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px;'>");
                out.println("<h2 class='section-title'>Announcements</h2>");
                out.println("<a href='teacherAnnouncements?action=post' class='action-button'>");
                out.println("📝 Post New Announcement");
                out.println("</a>");
                out.println("</div>");
                
                // Get announcements with correct column names
                String announcementsSql = "";
                try {
                    // First, let's check what columns exist
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
                    
                    sqlBuilder.append(" FROM announcements WHERE posted_by = ? OR target_audience IN ('all', 'teachers') ");
                    sqlBuilder.append("ORDER BY posted_date DESC LIMIT 20");
                    
                    announcementsSql = sqlBuilder.toString();
                } catch (Exception e) {
                    // Default SQL
                    announcementsSql = "SELECT announcement_id, title, content, target_audience, is_urgent, " +
                                      "posted_by, posted_date, expiry_date, attachment_filename, attachment_path " +
                                      "FROM announcements WHERE posted_by = ? OR target_audience IN ('all', 'teachers') " +
                                      "ORDER BY posted_date DESC LIMIT 20";
                }
                
                pstmt = conn.prepareStatement(announcementsSql);
                pstmt.setString(1, teacherName);
                rs = pstmt.executeQuery();
                
                boolean hasAnnouncements = false;
                
                out.println("<div class='announcement-list'>");
                
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                
                while (rs.next()) {
                    hasAnnouncements = true;
                    String annId = rs.getString("announcement_id");
                    String title = rs.getString("title");
                    String content = rs.getString("content");
                    String target = rs.getString("target_audience");
                    boolean isUrgent = rs.getBoolean("is_urgent");
                    String postedBy = rs.getString("posted_by");
                    Timestamp postedDate = rs.getTimestamp("posted_date");
                    Date expiryDate = rs.getDate("expiry_date");
                    String attachmentFilename = null;
                    String attachmentPath = null;
                    
                    try {
                        attachmentFilename = rs.getString("attachment_filename");
                        attachmentPath = rs.getString("attachment_path");
                    } catch (SQLException e) {
                        // Columns might not exist yet
                    }
                    
                    String targetText = getTargetDisplayName(target);
                    String postedDateStr = dateFormat.format(postedDate);
                    String expiryDateStr = expiryDate != null ? new SimpleDateFormat("yyyy-MM-dd").format(expiryDate) : null;
                    
                    out.println("<div class='announcement-item" + (isUrgent ? " urgent" : "") + "'>");
                    out.println("<div class='announcement-title'>");
                    out.println("<div style='display: flex; align-items: center;'>");
                    out.println("<span>" + title + "</span>");
                    if (isUrgent) {
                        out.println("<span class='urgent-badge'>URGENT</span>");
                    }
                    out.println("</div>");
                    if (postedBy.equals(teacherName) || postedBy.equals(teacherId)) {
                        out.println("<div class='announcement-actions'>");
                        out.println("<a href='deleteAnnouncement?id=" + annId + "' class='danger-button' onclick='return confirm(\"Are you sure you want to delete this announcement?\")'>Delete</a>");
                        out.println("</div>");
                    }
                    out.println("</div>");
                    
                    out.println("<div class='announcement-meta'>");
                    out.println("<span>👤 Posted by: " + postedBy + "</span>");
                    out.println("<span>📅 " + postedDateStr + "</span>");
                    out.println("<span>🎯 Target: " + targetText + "</span>");
                    if (expiryDateStr != null) {
                        out.println("<span>⏰ Expires: " + expiryDateStr + "</span>");
                    }
                    out.println("</div>");
                    
                    out.println("<div class='announcement-content'>" + content.replace("\n", "<br>") + "</div>");
                    
                    // Display attachment if exists
                    if (attachmentFilename != null && !attachmentFilename.isEmpty() && 
                        attachmentPath != null && !attachmentPath.isEmpty()) {
                        out.println("<div class='announcement-attachment'>");
                        out.println("<div class='attachment-icon'>📎</div>");
                        out.println("<a href='downloadAttachment?path=" + java.net.URLEncoder.encode(attachmentPath, "UTF-8") + 
                                   "&name=" + java.net.URLEncoder.encode(attachmentFilename, "UTF-8") + 
                                   "' class='attachment-link'>");
                        out.println(attachmentFilename);
                        out.println("</a>");
                        out.println("<div class='attachment-size'>Download</div>");
                        out.println("</div>");
                    }
                    
                    out.println("</div>");
                }
                
                if (!hasAnnouncements) {
                    out.println("<div class='no-announcements'>");
                    out.println("<div class='no-announcements-icon'>📢</div>");
                    out.println("<h3 style='color: #7f8c8d; margin-bottom: 15px;'>No Announcements Yet</h3>");
                    out.println("<p>Be the first to post an announcement!</p>");
                    out.println("<a href='teacherAnnouncements?action=post' class='action-button' style='margin-top: 20px;'>Create Your First Announcement</a>");
                    out.println("</div>");
                }
                
                out.println("</div>");
                out.println("</div>");
                
                // Statistics Card
                out.println("<div class='card'>");
                out.println("<h2 class='section-title'>Announcement Statistics</h2>");
                
                try {
                    // Get statistics
                    String totalSql = "SELECT COUNT(*) as total FROM announcements";
                    String urgentSql = "SELECT COUNT(*) as urgent FROM announcements WHERE is_urgent = true";
                    String mySql = "SELECT COUNT(*) as my FROM announcements WHERE posted_by = ?";
                    String withAttachmentSql = "SELECT COUNT(*) as with_attach FROM announcements WHERE attachment_filename IS NOT NULL";
                    
                    Statement stmt = conn.createStatement();
                    ResultSet totalRs = stmt.executeQuery(totalSql);
                    totalRs.next();
                    int totalCount = totalRs.getInt("total");
                    totalRs.close();
                    
                    ResultSet urgentRs = stmt.executeQuery(urgentSql);
                    urgentRs.next();
                    int urgentCount = urgentRs.getInt("urgent");
                    urgentRs.close();
                    
                    PreparedStatement myStmt = conn.prepareStatement(mySql);
                    myStmt.setString(1, teacherName);
                    ResultSet myRs = myStmt.executeQuery();
                    myRs.next();
                    int myCount = myRs.getInt("my");
                    myRs.close();
                    myStmt.close();
                    
                    ResultSet attachRs = stmt.executeQuery(withAttachmentSql);
                    attachRs.next();
                    int attachCount = attachRs.getInt("with_attach");
                    attachRs.close();
                    stmt.close();
                    
                    out.println("<div class='stats-grid'>");
                    out.println("<div class='stat-card'>");
                    out.println("<div class='stat-number'>" + totalCount + "</div>");
                    out.println("<div class='stat-label'>Total Announcements</div>");
                    out.println("</div>");
                    
                    out.println("<div class='stat-card danger'>");
                    out.println("<div class='stat-number'>" + urgentCount + "</div>");
                    out.println("<div class='stat-label'>Urgent Announcements</div>");
                    out.println("</div>");
                    
                    out.println("<div class='stat-card success'>");
                    out.println("<div class='stat-number'>" + myCount + "</div>");
                    out.println("<div class='stat-label'>My Announcements</div>");
                    out.println("</div>");
                    
                    out.println("<div class='stat-card'>");
                    out.println("<div class='stat-number'>" + attachCount + "</div>");
                    out.println("<div class='stat-label'>With Attachments</div>");
                    out.println("</div>");
                    out.println("</div>");
                } catch (Exception e) {
                    out.println("<p style='color: #7f8c8d; text-align: center;'>Statistics not available</p>");
                }
                out.println("</div>");
            }
            
        } catch (Exception e) {
            out.println("<div class='card'>");
            out.println("<h2 class='section-title'>Error Loading Announcements</h2>");
            out.println("<p style='color: #e74c3c;'>Error: " + e.getMessage() + "</p>");
            out.println("<p style='color: #666; font-size: 0.9rem;'>Please check if the 'announcements' table exists with correct columns.</p>");
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
        
        out.println("</div>"); // Close main-container
        out.println("</body>");
        out.println("</html>");
    }
    
    private String getTargetDisplayName(String target) {
        if (target == null) return "Everyone";
        switch(target.toLowerCase()) {
            case "all": return "Everyone";
            case "students": return "All Students";
            case "teachers": return "All Teachers";
            case "grade9": return "Grade 9 Students";
            case "grade10": return "Grade 10 Students";
            case "grade11": return "Grade 11 Students";
            case "grade12": return "Grade 12 Students";
            default: return target;
        }
    }
}