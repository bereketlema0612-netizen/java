package utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.Part;

public class FileUploadUtil {
    
    private static final String UPLOAD_DIR = "announcement_uploads";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    public static UploadResult handleFileUpload(Part filePart, String realPath) {
        UploadResult result = new UploadResult();
        
        if (filePart == null || filePart.getSize() == 0) {
            result.setSuccess(true);
            return result;
        }
        
        // Check file size
        if (filePart.getSize() > MAX_FILE_SIZE) {
            result.setErrorMessage("File size exceeds 10MB limit");
            return result;
        }
        
        try {
            // Get filename
            String fileName = extractFileName(filePart);
            
            // Check if file has a name
            if (fileName == null || fileName.isEmpty()) {
                result.setSuccess(true);
                return result;
            }
            
            // Sanitize filename
            fileName = sanitizeFileName(fileName);
            
            // Generate unique filename
            String uniqueFileName = generateUniqueFileName(fileName);
            
            // Create upload directory if it doesn't exist
            String uploadPath = realPath + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // Save file
            String filePath = uploadPath + File.separator + uniqueFileName;
            try (InputStream input = filePart.getInputStream();
                 OutputStream output = new FileOutputStream(filePath)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
            
            result.setSuccess(true);
            result.setFileName(uniqueFileName);
            result.setOriginalFileName(fileName);
            result.setFilePath(UPLOAD_DIR + "/" + uniqueFileName);
            result.setFileSize(filePart.getSize());
            
        } catch (Exception e) {
            result.setErrorMessage("Error uploading file: " + e.getMessage());
        }
        
        return result;
    }
    
    private static String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        if (contentDisp == null) {
            return null;
        }
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                String fileName = s.substring(s.indexOf("=") + 2, s.length() - 1);
                if (fileName.contains("\\")) {
                    fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
                } else if (fileName.contains("/")) {
                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                }
                return fileName;
            }
        }
        return "";
    }
    
    private static String sanitizeFileName(String fileName) {
        if (fileName == null) return "";
        // Remove path traversal attempts
        fileName = fileName.replace("..", "");
        // Replace special characters
        fileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        return fileName;
    }
    
    private static String generateUniqueFileName(String fileName) {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String extension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = fileName.substring(dotIndex);
            fileName = fileName.substring(0, dotIndex);
        }
        // Limit filename length
        if (fileName.length() > 50) {
            fileName = fileName.substring(0, 50);
        }
        return fileName + "_" + timestamp + extension;
    }
    
    public static class UploadResult {
        private boolean success;
        private String errorMessage;
        private String fileName;
        private String originalFileName;
        private String filePath;
        private long fileSize;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public String getOriginalFileName() { return originalFileName; }
        public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    }
}