package servlets;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import database.DBConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import utils.FileUploadUtil;
import javax.servlet.http.Part;

@WebServlet("/postAnnouncement")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 10 * 1024 * 1024, // 10MB
    maxRequestSize = 20 * 1024 * 1024 // 20MB
)
public class PostAnnouncementServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"teacher".equals(session.getAttribute("role"))) {
            response.sendRedirect("login_student_teacher.html");
            return;
        }
        
        // Get form parameters
        String teacherId = request.getParameter("teacherId");
        String teacherName = request.getParameter("teacherName");
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String target = request.getParameter("target");
        String urgent = request.getParameter("urgent");
        String expiryDateStr = request.getParameter("expiry");
        
        // Handle file upload
        Part filePart = request.getPart("attachment");
        FileUploadUtil.UploadResult uploadResult = null;
        
        if (filePart != null && filePart.getSize() > 0) {
            String realPath = getServletContext().getRealPath("");
            uploadResult = FileUploadUtil.handleFileUpload(filePart, realPath);
            
            if (!uploadResult.isSuccess()) {
                sendErrorPage(response, "File Upload Error", uploadResult.getErrorMessage());
                return;
            }
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "INSERT INTO announcements (title, content, target_audience, is_urgent, posted_by, expiry_date, attachment_filename, attachment_path) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setString(3, target);
            pstmt.setBoolean(4, "true".equals(urgent));
            pstmt.setString(5, teacherName);
            
            // Parse expiry date if provided
            if (expiryDateStr != null && !expiryDateStr.isEmpty()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date utilDate = dateFormat.parse(expiryDateStr);
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                pstmt.setDate(6, sqlDate);
            } else {
                pstmt.setNull(6, java.sql.Types.DATE);
            }
            
            // Set attachment info if file was uploaded
            if (uploadResult != null && uploadResult.isSuccess() && uploadResult.getFileName() != null) {
                pstmt.setString(7, uploadResult.getOriginalFileName());
                pstmt.setString(8, uploadResult.getFilePath());
            } else {
                pstmt.setNull(7, java.sql.Types.VARCHAR);
                pstmt.setNull(8, java.sql.Types.VARCHAR);
            }
            
            pstmt.executeUpdate();
            
            // Success response
            response.sendRedirect("teacherAnnouncements?posted=true");
            
        } catch (Exception e) {
            // If there was an error, delete the uploaded file
            if (uploadResult != null && uploadResult.getFilePath() != null) {
                try {
                    String realPath = getServletContext().getRealPath("");
                    String filePath = realPath + File.separator + uploadResult.getFilePath();
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            
            sendErrorPage(response, "Error Posting Announcement", e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void sendErrorPage(HttpServletResponse response, String title, String message) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Error</title>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background: linear-gradient(135deg, #fff5f5 0%, #ffeaea 100%); min-height: 100vh; display: flex; justify-content: center; align-items: center; }");
        out.println(".error-card { background: white; padding: 40px; border-radius: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.12); text-align: center; max-width: 500px; width: 90%; border-left: 5px solid #e74c3c; }");
        out.println(".error-icon { font-size: 60px; color: #e74c3c; margin-bottom: 20px; }");
        out.println(".error-btn { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white; padding: 12px 30px; border-radius: 25px; text-decoration: none; display: inline-block; margin-top: 20px; transition: all 0.3s; border: none; cursor: pointer; font-size: 16px; }");
        out.println(".error-btn:hover { transform: translateY(-2px); box-shadow: 0 4px 15px rgba(52,152,219,0.3); }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        
        out.println("<div class='error-card'>");
        out.println("<div class='error-icon'>❌</div>");
        out.println("<h2 style='color: #e74c3c; margin-bottom: 20px;'>" + title + "</h2>");
        out.println("<p style='color: #666; line-height: 1.6;'>" + message + "</p>");
        out.println("<button onclick=\"window.location.href='teacherAnnouncements'\" class='error-btn'>Back to Announcements</button>");
        out.println("</div>");
        
        out.println("</body>");
        out.println("</html>");
    }
}