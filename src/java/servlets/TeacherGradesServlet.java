package servlets;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import database.DBConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/teacherGrades")
public class TeacherGradesServlet extends HttpServlet {
    
    // Teacher to Subject mapping - Each teacher can only update one subject
    private static final Map<String, String> TEACHER_SUBJECTS = new HashMap<>();
    static {
        TEACHER_SUBJECTS.put("T001", "biology");    // Teacher T001 can only update Biology
        TEACHER_SUBJECTS.put("T002", "english");    // Teacher T002 can only update English
        TEACHER_SUBJECTS.put("T003", "mathematics"); // Teacher T003 can only update Mathematics
        TEACHER_SUBJECTS.put("T004", "chemistry");   // Teacher T004 can only update Chemistry
        TEACHER_SUBJECTS.put("T005", "physics");     // Teacher T005 can only update Physics
        TEACHER_SUBJECTS.put("T006", "art");         // Teacher T006 can only update Art
        TEACHER_SUBJECTS.put("T007", "history");     // Teacher T007 can only update History
        TEACHER_SUBJECTS.put("T008", "geography");   // Teacher T008 can only update Geography
        TEACHER_SUBJECTS.put("T009", "civics");      // Teacher T009 can only update Civics
        TEACHER_SUBJECTS.put("T011", "biology");     // Teacher T011 can also update Biology (for grades 11,12)
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"teacher".equals(session.getAttribute("role"))) {
            response.sendRedirect("login_student_teacher.html");
            return;
        }
        
        String teacherId = (String) session.getAttribute("username");
        String gradeParam = request.getParameter("grade");
        String semesterParam = request.getParameter("semester");
        
        if (semesterParam == null || semesterParam.isEmpty()) {
            semesterParam = "First";
        }
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Grade Management</title>");
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
        
        // Filter Controls
        out.println(".filter-controls { background: rgba(255,248,240,0.9); padding: 20px; border-radius: 12px; margin-bottom: 25px; display: flex; flex-wrap: wrap; gap: 15px; align-items: center; border: 2px solid #ffe8cc; }");
        out.println(".filter-group { display: flex; flex-direction: column; }");
        out.println(".filter-label { font-weight: 600; color: #2c3e50; margin-bottom: 5px; font-size: 0.9rem; }");
        out.println(".filter-select { padding: 10px 15px; border: 1px solid #ddd; border-radius: 8px; min-width: 180px; background: white; }");
        out.println(".filter-input { padding: 10px 15px; border: 1px solid #ddd; border-radius: 8px; min-width: 180px; background: #f0f0f0; color: #666; }");
        out.println(".filter-btn { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white; padding: 10px 20px; border: none; border-radius: 8px; cursor: pointer; font-weight: 500; transition: all 0.3s; }");
        out.println(".filter-btn:hover { transform: translateY(-2px); box-shadow: 0 4px 10px rgba(52,152,219,0.3); }");
        
        // Grades Table
        out.println(".grades-table { width: 100%; border-collapse: collapse; margin-top: 20px; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 5px 15px rgba(0,0,0,0.05); }");
        out.println(".grades-table th { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white; padding: 15px; text-align: left; font-weight: 600; }");
        out.println(".grades-table td { padding: 15px; border-bottom: 1px solid #eee; }");
        out.println(".grades-table tr:hover { background: #f9f9f9; }");
        out.println(".grade-input { width: 80px; padding: 8px; border: 1px solid #ddd; border-radius: 5px; text-align: center; }");
        out.println(".grade-input:focus { outline: none; border-color: #3498db; box-shadow: 0 0 0 2px rgba(52,152,219,0.2); }");
        out.println(".update-btn { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white; padding: 8px 16px; border: none; border-radius: 5px; cursor: pointer; font-weight: 500; transition: all 0.3s; margin-right: 5px; }");
        out.println(".update-btn:hover { transform: translateY(-2px); box-shadow: 0 4px 10px rgba(52,152,219,0.3); }");
        out.println(".save-all-btn { background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%); color: white; padding: 12px 30px; border: none; border-radius: 25px; cursor: pointer; font-weight: 600; font-size: 1rem; transition: all 0.3s; }");
        out.println(".save-all-btn:hover { transform: translateY(-2px); box-shadow: 0 4px 15px rgba(46,204,113,0.3); }");
        out.println(".no-data { text-align: center; padding: 40px; color: #7f8c8d; font-style: italic; }");
        
        // Error box
        out.println(".error-box { background: #f8d7da; padding: 20px; border-radius: 12px; border-left: 4px solid #dc3545; margin-bottom: 25px; }");
        out.println(".error-box p { margin: 0; color: #721c24; font-weight: 600; }");
        
        // Loading spinner
        out.println(".loading-spinner { display: none; text-align: center; padding: 20px; }");
        out.println(".spinner { border: 4px solid #f3f3f3; border-top: 4px solid #3498db; border-radius: 50%; width: 40px; height: 40px; animation: spin 1s linear infinite; margin: 0 auto; }");
        out.println("@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }");
        
        // Responsive
        out.println("@media (max-width: 768px) {");
        out.println(".header { padding: 20px; flex-direction: column; gap: 15px; text-align: center; }");
        out.println(".menu { padding: 15px; text-align: center; }");
        out.println(".menu a { margin: 5px; padding: 8px 15px; }");
        out.println(".main-container { padding: 0 20px; }");
        out.println(".filter-controls { flex-direction: column; align-items: stretch; }");
        out.println(".grades-table { font-size: 0.9rem; overflow-x: auto; display: block; }");
        out.println(".grade-input { width: 60px; }");
        out.println("}");
        
        out.println("</style>");
        out.println("<script>");
        out.println("function validateGrade(input) {");
        out.println("    var value = parseFloat(input.value);");
        out.println("    var max = parseFloat(input.getAttribute('data-max'));");
        out.println("    if (isNaN(value) || value < 0) input.value = 0;");
        out.println("    if (value > max) input.value = max;");
        out.println("}");
        out.println("function updateGrade(studentId, subject, semester) {");
        out.println("    var assignId = 'assign-' + studentId;");
        out.println("    var midId = 'mid-' + studentId;");
        out.println("    var finalId = 'final-' + studentId;");
        out.println("    ");
        out.println("    var assignment = document.getElementById(assignId).value;");
        out.println("    var mid = document.getElementById(midId).value;");
        out.println("    var final = document.getElementById(finalId).value;");
        out.println("    ");
        out.println("    if (confirm('Update grades for student ' + studentId + '?')) {");
        out.println("        window.location.href = 'updateGrade?student=' + studentId + '&subject=' + subject + '&semester=' + semester + '&assignment=' + assignment + '&mid=' + mid + '&final=' + final;");
        out.println("    }");
        out.println("}");
        out.println("function loadStudents() {");
        out.println("    var grade = document.getElementById('gradeSelect').value;");
        out.println("    var semester = document.getElementById('semesterSelect').value;");
        out.println("    ");
        out.println("    // Show loading spinner");
        out.println("    document.getElementById('loadingSpinner').style.display = 'block';");
        out.println("    document.getElementById('studentsTable').style.display = 'none';");
        out.println("    ");
        out.println("    // Redirect to load students");
        out.println("    window.location.href = 'teacherGrades?grade=' + grade + '&semester=' + semester;");
        out.println("}");
        out.println("function calculateTotal(studentId) {");
        out.println("    var assignId = 'assign-' + studentId;");
        out.println("    var midId = 'mid-' + studentId;");
        out.println("    var finalId = 'final-' + studentId;");
        out.println("    var totalId = 'total-' + studentId;");
        out.println("    var gradeId = 'grade-' + studentId;");
        out.println("    ");
        out.println("    var assignment = parseFloat(document.getElementById(assignId).value) || 0;");
        out.println("    var mid = parseFloat(document.getElementById(midId).value) || 0;");
        out.println("    var final = parseFloat(document.getElementById(finalId).value) || 0;");
        out.println("    ");
        out.println("    var total = assignment + mid + final;");
        out.println("    document.getElementById(totalId).innerHTML = total.toFixed(1) + ' / 100';");
        out.println("    ");
        out.println("    // Calculate grade letter");
        out.println("    var gradeLetter = 'N/A';");
        out.println("    var gradeColor = '#7f8c8d';");
        out.println("    ");
        out.println("    if (total >= 90) { gradeLetter = 'A+'; gradeColor = '#27ae60'; }");
        out.println("    else if (total >= 85) { gradeLetter = 'A'; gradeColor = '#27ae60'; }");
        out.println("    else if (total >= 80) { gradeLetter = 'B+'; gradeColor = '#f39c12'; }");
        out.println("    else if (total >= 75) { gradeLetter = 'B'; gradeColor = '#f39c12'; }");
        out.println("    else if (total >= 70) { gradeLetter = 'C+'; gradeColor = '#e74c3c'; }");
        out.println("    else if (total >= 60) { gradeLetter = 'C'; gradeColor = '#e74c3c'; }");
        out.println("    else if (total >= 50) { gradeLetter = 'D'; gradeColor = '#8e44ad'; }");
        out.println("    else if (total > 0) { gradeLetter = 'F'; gradeColor = '#7f8c8d'; }");
        out.println("    ");
        out.println("    document.getElementById(gradeId).innerHTML = '<strong style=\"color:' + gradeColor + ';\">' + gradeLetter + '</strong>';");
        out.println("}");
        out.println("function updateAllGrades() {");
        out.println("    var modifiedCount = 0;");
        out.println("    var inputs = document.querySelectorAll('.grade-input');");
        out.println("    ");
        out.println("    // Count modified fields (fields that are not 0)");
        out.println("    for (var i = 0; i < inputs.length; i++) {");
        out.println("        if (inputs[i].value != '0.0' && inputs[i].value != '0' && inputs[i].value != '') {");
        out.println("            modifiedCount++;");
        out.println("        }");
        out.println("    }");
        out.println("    ");
        out.println("    if (modifiedCount === 0) {");
        out.println("        alert('No grades have been modified. Please enter some grades first.');");
        out.println("        return;");
        out.println("    }");
        out.println("    ");
        out.println("    if (confirm('Are you sure you want to save grades for ' + modifiedCount + ' students?')) {");
        out.println("        document.getElementById('bulkUpdateForm').submit();");
        out.println("    }");
        out.println("}");
        out.println("// Auto-load when dropdown changes");
        out.println("document.addEventListener('DOMContentLoaded', function() {");
        out.println("    var gradeSelect = document.getElementById('gradeSelect');");
        out.println("    var semesterSelect = document.getElementById('semesterSelect');");
        out.println("    ");
        out.println("    if (gradeSelect) {");
        out.println("        gradeSelect.addEventListener('change', loadStudents);");
        out.println("    }");
        out.println("    ");
        out.println("    if (semesterSelect) {");
        out.println("        semesterSelect.addEventListener('change', loadStudents);");
        out.println("    }");
        out.println("});");
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
        out.println("<a href='teacherGrades' class='active'>📊 Grade Management</a>");
        out.println("<a href='teacherAnnouncements'>📢 Announcements</a>");
        out.println("<a href='viewCalendar'>📅 Calendar</a>");
        out.println("<a href='teacherProfile'>👤 Profile</a>");
        out.println("</div>");
        
        // Content
        out.println("<div class='main-container'>");
        
        // Display success/error messages
        String successMessage = (String) session.getAttribute("successMessage");
        String errorMessage = (String) session.getAttribute("errorMessage");

        if (successMessage != null) {
            out.println("<div style='margin-bottom: 20px;'>");
            out.println("<div style='background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%); color: #155724; padding: 15px 25px; border-radius: 12px; border-left: 4px solid #28a745; margin-bottom: 20px; box-shadow: 0 4px 10px rgba(40,167,69,0.1);'>");
            out.println("<div style='display: flex; align-items: center;'>");
            out.println("<span style='margin-right: 10px; font-size: 1.2em;'>✓</span>");
            out.println("<div>");
            out.println("<strong style='font-size: 1.1em;'>Success!</strong><br>");
            out.println(successMessage);
            out.println("</div>");
            out.println("</div>");
            out.println("</div>");
            out.println("</div>");
            session.removeAttribute("successMessage");
        }

        if (errorMessage != null) {
            out.println("<div style='margin-bottom: 20px;'>");
            out.println("<div style='background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%); color: #721c24; padding: 15px 25px; border-radius: 12px; border-left: 4px solid #dc3545; margin-bottom: 20px; box-shadow: 0 4px 10px rgba(220,53,69,0.1);'>");
            out.println("<div style='display: flex; align-items: center;'>");
            out.println("<span style='margin-right: 10px; font-size: 1.2em;'>✗</span>");
            out.println("<div>");
            out.println("<strong style='font-size: 1.1em;'>Error!</strong><br>");
            out.println(errorMessage);
            out.println("</div>");
            out.println("</div>");
            out.println("</div>");
            out.println("</div>");
            session.removeAttribute("errorMessage");
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            // Get teacher's assigned subject - ONLY ONE SUBJECT PER TEACHER
            String teacherSubject = TEACHER_SUBJECTS.get(teacherId);
            if (teacherSubject == null) {
                out.println("<div class='card'>");
                out.println("<h2 class='section-title'>Access Denied</h2>");
                out.println("<p style='color: #e74c3c;'>You are not assigned to any subject. Please contact administrator.</p>");
                out.println("</div>");
                return;
            }
            
            // Get teacher's assigned grades from database - FIXED COLUMN NAME
            List<Integer> assignedGrades = new ArrayList<>();
            try {
                Connection mainConn = DBConnection.getConnection();
                String teacherSql = "SELECT assigned_grades FROM teachers WHERE teacher_id = ?";
                pstmt = mainConn.prepareStatement(teacherSql);
                pstmt.setString(1, teacherId);
                rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    String assignedGroups = rs.getString("assigned_grades");
                    if (assignedGroups != null && !assignedGroups.trim().isEmpty()) {
                        // Format: "9,10," or "9,10,11,12" or "11,12"
                        String[] grades = assignedGroups.split(",");
                        for (String grade : grades) {
                            String trimmedGrade = grade.trim();
                            if (!trimmedGrade.isEmpty()) {
                                try {
                                    assignedGrades.add(Integer.parseInt(trimmedGrade));
                                } catch (NumberFormatException e) {
                                    // Ignore non-numeric values
                                }
                            }
                        }
                    }
                }
                
                rs.close();
                pstmt.close();
                mainConn.close();
            } catch (Exception e) {
                e.printStackTrace();
                out.println("<div class='card'>");
                out.println("<h2 class='section-title'>Error</h2>");
                out.println("<p style='color: #e74c3c;'>Error retrieving teacher information: " + e.getMessage() + "</p>");
                out.println("</div>");
                return;
            }
            
            // Check if teacher has any assigned grades
            if (assignedGrades.isEmpty()) {
                out.println("<div class='card'>");
                out.println("<h2 class='section-title'>No Assigned Grades</h2>");
                out.println("<p style='color: #e74c3c;'>You are not assigned to teach any grade levels. Please contact administrator.</p>");
                out.println("</div>");
                return;
            }
            
            // Use grade from parameter or default to first assigned grade
            String currentGrade;
            if (gradeParam != null && !gradeParam.isEmpty()) {
                currentGrade = gradeParam;
                // Validate that the selected grade is in the assigned grades
                try {
                    int selectedGrade = Integer.parseInt(currentGrade);
                    if (!assignedGrades.contains(selectedGrade)) {
                        // If selected grade is not in assigned grades, use first assigned grade
                        currentGrade = String.valueOf(assignedGrades.get(0));
                    }
                } catch (NumberFormatException e) {
                    currentGrade = String.valueOf(assignedGrades.get(0));
                }
            } else {
                // Default to first assigned grade
                currentGrade = String.valueOf(assignedGrades.get(0));
            }
            
            String currentSemester = semesterParam;
            String subjectDisplayName = capitalize(teacherSubject);
            
            // Main Card
            out.println("<div class='card'>");
            out.println("<h2 class='section-title'>Grade Management - " + subjectDisplayName + "</h2>");
            
            out.println("<div class='filter-controls'>");
            
            out.println("<div class='filter-group'>");
            out.println("<label class='filter-label'>Subject</label>");
            out.println("<input type='text' class='filter-input' value='" + subjectDisplayName + "' readonly>");
            out.println("<input type='hidden' id='subject' value='" + teacherSubject + "'>");
            out.println("</div>");
            
            out.println("<div class='filter-group'>");
            out.println("<label class='filter-label'>Grade Level</label>");
            out.println("<select id='gradeSelect' class='filter-select'>");
            for (Integer grade : assignedGrades) {
                out.println("<option value='" + grade + "' " + (String.valueOf(grade).equals(currentGrade) ? "selected" : "") + ">Grade " + grade + "</option>");
            }
            out.println("</select>");
            out.println("</div>");
            
            out.println("<div class='filter-group'>");
            out.println("<label class='filter-label'>Semester</label>");
            out.println("<select id='semesterSelect' class='filter-select'>");
            out.println("<option value='First' " + ("First".equals(currentSemester) ? "selected" : "") + ">First Semester</option>");
            out.println("<option value='Second' " + ("Second".equals(currentSemester) ? "selected" : "") + ">Second Semester</option>");
            out.println("</select>");
            out.println("</div>");
            
            out.println("</div>");
            
            // Loading spinner (hidden by default)
            out.println("<div id='loadingSpinner' class='loading-spinner' style='display: none;'>");
            out.println("<div class='spinner'></div>");
            out.println("<p>Loading students...</p>");
            out.println("</div>");
            
            // Students table container
            out.println("<div id='studentsTable'>");
            
            // Load students for the selected grade
            out.println("<h3 style='margin-top: 30px; color: #2c3e50;'>" + subjectDisplayName + " - Grade " + currentGrade + " (" + currentSemester + " Semester)</h3>");
            
            // Connect to main database to get students
            Connection mainConn = DBConnection.getConnection();
            String studentsSql = "SELECT student_id, first_name, last_name, section FROM students WHERE grade_level = ? ORDER BY section, last_name, first_name";
            pstmt = mainConn.prepareStatement(studentsSql);
            pstmt.setInt(1, Integer.parseInt(currentGrade));
            rs = pstmt.executeQuery();
            
            // Check if we have students
            boolean hasStudents = false;
            
            // Form for bulk updates
            out.println("<form id='bulkUpdateForm' action='updateGrade' method='post'>");
            out.println("<input type='hidden' name='subject' value='" + teacherSubject + "'>");
            out.println("<input type='hidden' name='grade' value='" + currentGrade + "'>");
            out.println("<input type='hidden' name='semester' value='" + currentSemester + "'>");
            out.println("<input type='hidden' name='teacherId' value='" + teacherId + "'>");
            
            out.println("<table class='grades-table'>");
            out.println("<thead>");
            out.println("<tr>");
            out.println("<th>Student ID</th>");
            out.println("<th>Student Name</th>");
            out.println("<th>Section</th>");
            out.println("<th>Assignment (0-10)</th>");
            out.println("<th>Mid Exam (0-30)</th>");
            out.println("<th>Final Exam (0-60)</th>");
            out.println("<th>Total Score</th>");
            out.println("<th>Grade</th>");
            out.println("<th>Last Updated</th>");
            out.println("<th>Action</th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");
            
            int studentCount = 0;
            while (rs.next()) {
                hasStudents = true;
                studentCount++;
                String studentId = rs.getString("student_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String section = rs.getString("section");
                
                // Try to get existing grades from grade database
                double assignment = 0;
                double mid = 0;
                double finalScore = 0;
                double total = 0;
                String gradeLetter = "N/A";
                String lastUpdated = "Not graded";
                
                try {
                    // Connect to grade database
                    String gradeDb = "grade" + currentGrade + "_db";
                    Connection gradeConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + gradeDb, "root", "");
                    String gradeSql = "SELECT assignment_score, mid_score, final_score, graded_date FROM " + teacherSubject + 
                                    " WHERE student_id = ? AND semester = ?";
                    PreparedStatement gradeStmt = gradeConn.prepareStatement(gradeSql);
                    gradeStmt.setString(1, studentId);
                    gradeStmt.setString(2, currentSemester);
                    ResultSet gradeRs = gradeStmt.executeQuery();
                    
                    if (gradeRs.next()) {
                        assignment = gradeRs.getDouble("assignment_score");
                        mid = gradeRs.getDouble("mid_score");
                        finalScore = gradeRs.getDouble("final_score");
                        total = assignment + mid + finalScore;
                        gradeLetter = calculateGradeLetter(total);
                        lastUpdated = gradeRs.getTimestamp("graded_date").toString().substring(0, 10);
                    }
                    
                    gradeRs.close();
                    gradeStmt.close();
                    gradeConn.close();
                } catch (Exception e) {
                    // Table might not exist yet, use default values
                }
                
                out.println("<tr>");
                out.println("<td><strong>" + studentId + "</strong></td>");
                out.println("<td>" + firstName + " " + lastName + "</td>");
                out.println("<td>" + section + "</td>");
                
                // Assignment score input
                out.println("<td>");
                out.println("<input type='number' step='0.1' min='0' max='10' class='grade-input' ");
                out.println("id='assign-" + studentId + "' name='assign_" + studentId + "' ");
                out.println("value='" + assignment + "' data-max='10' onchange='validateGrade(this); calculateTotal(\"" + studentId + "\")'>");
                out.println("</td>");
                
                // Mid exam score input
                out.println("<td>");
                out.println("<input type='number' step='0.1' min='0' max='30' class='grade-input' ");
                out.println("id='mid-" + studentId + "' name='mid_" + studentId + "' ");
                out.println("value='" + mid + "' data-max='30' onchange='validateGrade(this); calculateTotal(\"" + studentId + "\")'>");
                out.println("</td>");
                
                // Final exam score input
                out.println("<td>");
                out.println("<input type='number' step='0.1' min='0' max='60' class='grade-input' ");
                out.println("id='final-" + studentId + "' name='final_" + studentId + "' ");
                out.println("value='" + finalScore + "' data-max='60' onchange='validateGrade(this); calculateTotal(\"" + studentId + "\")'>");
                out.println("</td>");
                
                // Display calculated total
                String gradeColor = "#7f8c8d";
                if (gradeLetter.startsWith("A")) gradeColor = "#27ae60";
                else if (gradeLetter.startsWith("B")) gradeColor = "#f39c12";
                else if (gradeLetter.startsWith("C")) gradeColor = "#e74c3c";
                else if (gradeLetter.startsWith("D")) gradeColor = "#8e44ad";
                
                out.println("<td id='total-" + studentId + "'><strong>" + String.format("%.1f", total) + " / 100</strong></td>");
                out.println("<td id='grade-" + studentId + "'><strong style='color:" + gradeColor + ";'>" + gradeLetter + "</strong></td>");
                
                out.println("<td style='font-size: 0.85rem; color: #7f8c8d;'>" + lastUpdated + "</td>");
                
                // Update button
                out.println("<td>");
                out.println("<button type='button' class='update-btn' onclick=\"updateGrade('" + studentId + "', '" + teacherSubject + "', '" + currentSemester + "')\">Update</button>");
                out.println("</td>");
                
                out.println("</tr>");
            }
            
            if (!hasStudents) {
                out.println("<tr><td colspan='10' class='no-data'>No students found for Grade " + currentGrade + "</td></tr>");
            }
            
            out.println("</tbody>");
            out.println("</table>");
            
            if (hasStudents) {
                // Bulk save button and student count
                out.println("<div style='margin-top: 25px; display: flex; justify-content: space-between; align-items: center;'>");
                out.println("<div>");
                out.println("<p style='color: #2c3e50; font-weight: 600;'>Total Students: " + studentCount + "</p>");
                out.println("</div>");
                out.println("<div>");
                out.println("<button type='button' class='save-all-btn' onclick='updateAllGrades()'>");
                out.println("💾 Save All Changes");
                out.println("</button>");
                out.println("</div>");
                out.println("</div>");
            }
            
            out.println("</form>");
            out.println("</div>"); // Close studentsTable div
            
            out.println("</div>"); // Close card div
            
        } catch (Exception e) {
            out.println("<div class='card'>");
            out.println("<h2 class='section-title'>Error</h2>");
            out.println("<p style='color: #e74c3c;'>" + e.getMessage() + "</p>");
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
    
    private String calculateGradeLetter(double total) {
        if (total >= 90) return "A+";
        if (total >= 85) return "A";
        if (total >= 80) return "B+";
        if (total >= 75) return "B";
        if (total >= 70) return "C+";
        if (total >= 60) return "C";
        if (total >= 50) return "D";
        if (total > 0) return "F";
        return "N/A";
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}