package servlets;

import java.io.*;
import java.nio.file.Files;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/downloadAttachment")
public class DownloadAttachmentServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || (!"teacher".equals(session.getAttribute("role")) && 
                                !"student".equals(session.getAttribute("role")))) {
            response.sendRedirect("login_student_teacher.html");
            return;
        }
        
        String filePath = request.getParameter("path");
        String fileName = request.getParameter("name");
        
        if (filePath == null || fileName == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
            return;
        }
        
        // Decode URL parameters
        filePath = java.net.URLDecoder.decode(filePath, "UTF-8");
        fileName = java.net.URLDecoder.decode(fileName, "UTF-8");
        
        String realPath = getServletContext().getRealPath("");
        File file = new File(realPath + File.separator + filePath);
        
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found: " + file.getAbsolutePath());
            return;
        }
        
        // Set response headers
        response.setContentType(getMimeType(fileName));
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", 
                          "attachment; filename=\"" + fileName + "\"");
        
        // Stream the file to response
        try (InputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error reading file");
        }
    }
    
    private String getMimeType(String fileName) {
        String mimeType = "application/octet-stream";
        if (fileName.toLowerCase().endsWith(".pdf")) {
            mimeType = "application/pdf";
        } else if (fileName.toLowerCase().endsWith(".doc") || fileName.toLowerCase().endsWith(".docx")) {
            mimeType = "application/msword";
        } else if (fileName.toLowerCase().endsWith(".xls") || fileName.toLowerCase().endsWith(".xlsx")) {
            mimeType = "application/vnd.ms-excel";
        } else if (fileName.toLowerCase().endsWith(".ppt") || fileName.toLowerCase().endsWith(".pptx")) {
            mimeType = "application/vnd.ms-powerpoint";
        } else if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
            mimeType = "image/jpeg";
        } else if (fileName.toLowerCase().endsWith(".png")) {
            mimeType = "image/png";
        } else if (fileName.toLowerCase().endsWith(".txt")) {
            mimeType = "text/plain";
        } else if (fileName.toLowerCase().endsWith(".zip")) {
            mimeType = "application/zip";
        } else if (fileName.toLowerCase().endsWith(".rar")) {
            mimeType = "application/x-rar-compressed";
        }
        return mimeType;
    }
}