package servlets;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import database.DBConnection;

@WebServlet("/deleteAnnouncement")
public class DeleteAnnouncementServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"teacher".equals(session.getAttribute("role"))) {
            response.sendRedirect("login_student_teacher.html");
            return;
        }
        
        String teacherId = (String) session.getAttribute("username");
        String announcementId = request.getParameter("id");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // First, check if the teacher owns this announcement and get attachment info
            String checkSql = "SELECT posted_by, attachment_path FROM announcements WHERE announcement_id = ?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, announcementId);
            ResultSet rs = pstmt.executeQuery();
            
            boolean canDelete = false;
            String postedBy = "";
            String attachmentPath = null;
            
            if (rs.next()) {
                postedBy = rs.getString("posted_by");
                attachmentPath = rs.getString("attachment_path");
                // Teacher can delete if they posted it
                canDelete = postedBy.equals(teacherId) || 
                           postedBy.contains(teacherId) || 
                           postedBy.contains(teacherId.substring(0, Math.min(3, teacherId.length())));
            }
            rs.close();
            pstmt.close();
            
            if (canDelete) {
                // Delete the announcement
                String deleteSql = "DELETE FROM announcements WHERE announcement_id = ?";
                pstmt = conn.prepareStatement(deleteSql);
                pstmt.setString(1, announcementId);
                pstmt.executeUpdate();
                
                // Delete the attached file if exists
                if (attachmentPath != null && !attachmentPath.isEmpty()) {
                    try {
                        String realPath = getServletContext().getRealPath("");
                        File file = new File(realPath + File.separator + attachmentPath);
                        if (file.exists()) {
                            boolean deleted = file.delete();
                            if (!deleted) {
                                System.err.println("Failed to delete file: " + file.getAbsolutePath());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace(); // Log but don't stop the deletion
                    }
                }
                
                // Redirect back to announcements
                response.sendRedirect("teacherAnnouncements?deleted=true");
            } else {
                // Not authorized
                response.sendRedirect("teacherAnnouncements?error=unauthorized");
            }
            
        } catch (Exception e) {
            response.sendRedirect("teacherAnnouncements?error=database");
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
}