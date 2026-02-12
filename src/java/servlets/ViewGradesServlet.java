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

@WebServlet("/viewGrades")
public class ViewGradesServlet extends HttpServlet {

    private static final String[] SUBJECTS = {
        "biology", "english", "mathematics", "chemistry",
        "physics", "art", "history", "geography", "civics"
    };

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"student".equals(session.getAttribute("role"))) {
            response.sendRedirect("login_student_teacher.html");
            return;
        }

        String studentId = (String) session.getAttribute("username");
        String semester = request.getParameter("semester");
        if (semester == null || semester.isEmpty()) {
            semester = "First";
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // --------------- HTML HEAD + CSS (unchanged design) ---------------
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>My Grades</title>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); min-height: 100vh; }");
        out.println(".header { background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%); color: white; padding: 20px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }");
        out.println(".header h1 { margin: 0; font-size: 1.8rem; font-weight: 300; }");
        out.println(".logout-btn { background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%); color: white; padding: 12px 20px; border: none; border-radius: 25px; cursor: pointer; text-decoration: none; font-weight: 500; transition: all 0.3s ease; box-shadow: 0 2px 5px rgba(231,76,60,0.3); }");
        out.println(".logout-btn:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(231,76,60,0.4); }");
        out.println(".menu { background: rgba(255,255,255,0.95); backdrop-filter: blur(10px); padding: 15px 20px; border-bottom: 1px solid rgba(0,0,0,0.1); display: flex; gap: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }");
        out.println(".menu a { text-decoration: none; color: #555; padding: 12px 20px; border-radius: 25px; font-weight: 500; transition: all 0.3s ease; }");
        out.println(".menu a:hover { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white; transform: translateY(-1px); }");
        out.println(".menu a.active { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white; box-shadow: 0 2px 8px rgba(52,152,219,0.3); }");
        out.println(".container { max-width: 1200px; margin: 30px auto; padding: 0 20px; }");
        out.println(".card { background: rgba(255,255,255,0.95); backdrop-filter: blur(20px); border-radius: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.15); padding: 25px 25px 30px; margin-bottom: 25px; position: relative; overflow: hidden; }");
        out.println(".card::before { content: ''; position: absolute; top: 0; left: 0; right: 0; height: 4px; background: linear-gradient(90deg, #3498db, #2ecc71, #f39c12); }");
        out.println(".card h2 { color: #2c3e50; margin-top: 0; font-size: 1.8rem; font-weight: 300; padding-bottom: 10px; margin-bottom: 15px; border-bottom: none; text-align: left; position: relative; }");
        out.println(".card h2::after { content: ''; position: absolute; bottom: 0; left: 0; width: 60px; height: 3px; background: linear-gradient(90deg, #3498db, #2ecc71); border-radius: 2px; }");
        out.println(".semester-selector { margin-bottom: 20px; padding: 15px 20px; background: rgba(240,248,255,0.9); border-radius: 12px; display: flex; flex-wrap: wrap; align-items: center; gap: 10px; }");
        out.println(".semester-selector label { font-weight: 600; margin-right: 5px; color: #2c3e50; }");
        out.println(".semester-select { padding: 8px 12px; border: 1px solid #ddd; border-radius: 20px; min-width: 160px; outline: none; }");
        out.println(".view-btn { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white; padding: 8px 18px; border: none; border-radius: 20px; cursor: pointer; font-weight: 500; transition: all 0.3s ease; }");
        out.println(".view-btn:hover { transform: translateY(-1px); box-shadow: 0 4px 10px rgba(52,152,219,0.4); }");
        out.println(".subject-card { margin-bottom: 12px; border-radius: 14px; overflow: hidden; border: 1px solid rgba(0,0,0,0.05); background: rgba(255,255,255,0.9); box-shadow: 0 4px 15px rgba(0,0,0,0.05); }");
        out.println(".subject-header { background: rgba(249,249,249,0.95); padding: 14px 18px; cursor: pointer; display: flex; justify-content: space-between; align-items: center; transition: background 0.3s ease; }");
        out.println(".subject-header:hover { background: rgba(240,240,240,0.95); }");
        out.println(".subject-title { font-weight: 600; color: #2c3e50; font-size: 1.05rem; }");
        out.println(".subject-teacher { font-size: 0.85rem; color: #777; margin-top: 3px; }");
        out.println(".subject-details { padding: 0; max-height: 0; overflow: hidden; transition: max-height 0.3s ease-out, padding 0.3s ease-out; }");
        out.println(".subject-details.open { padding: 15px 18px 18px; max-height: 300px; }");
        out.println(".grades-table { width: 100%; border-collapse: collapse; font-size: 0.95rem; }");
        out.println(".grades-table th { background: #3498db; color: white; padding: 10px; text-align: left; font-weight: 600; }");
        out.println(".grades-table td { padding: 10px; border-bottom: 1px solid #eee; }");
        out.println(".total-row { background: #f9f9f9; font-weight: 600; }");
        out.println(".grade-aplus { color: #27ae60; font-weight: 700; }");
        out.println(".grade-a { color: #2ecc71; font-weight: 700; }");
        out.println(".grade-bplus { color: #f39c12; font-weight: 700; }");
        out.println(".grade-b { color: #e67e22; font-weight: 700; }");
        out.println(".grade-cplus { color: #e74c3c; font-weight: 700; }");
        out.println(".grade-c { color: #c0392b; font-weight: 700; }");
        out.println(".grade-d { color: #8e44ad; font-weight: 700; }");
        out.println(".grade-f { color: #7f8c8d; font-weight: 700; }");
        out.println(".history-table { width: 100%; border-collapse: collapse; margin-top: 15px; font-size: 0.95rem; }");
        out.println(".history-table th { background: #2c3e50; color: white; padding: 10px; text-align: left; }");
        out.println(".history-table td { padding: 10px; border-bottom: 1px solid #eee; }");
        out.println(".history-table tr:hover { background: #f9f9f9; }");
        out.println(".no-data { text-align: center; padding: 40px; color: #7f8c8d; font-style: italic; }");
        out.println(".back-btn { background: linear-gradient(135deg, #7f8c8d 0%, #95a5a6 100%); color: white; padding: 12px 22px; border: none; border-radius: 25px; cursor: pointer; text-decoration: none; display: inline-block; margin: 20px 0 30px; font-weight: 500; transition: all 0.3s ease; box-shadow: 0 3px 10px rgba(127,140,141,0.3); }");
        out.println(".back-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(127,140,141,0.4); }");
        out.println(".history-selector { margin-bottom: 20px; padding: 15px 20px; background: rgba(240,248,255,0.9); border-radius: 12px; display: flex; flex-wrap: wrap; align-items: center; gap: 10px; }");
        out.println(".history-selector label { font-weight: 600; margin-right: 5px; color: #2c3e50; }");
        out.println(".history-select { padding: 8px 12px; border: 1px solid #ddd; border-radius: 20px; min-width: 160px; outline: none; }");
        out.println(".semester-btn-container { margin-top: 15px; display: flex; gap: 10px; flex-wrap: wrap; }");
        out.println(".semester-btn { background: linear-gradient(135deg, #7f8c8d 0%, #95a5a6 100%); color: white; padding: 8px 16px; border: none; border-radius: 20px; cursor: pointer; font-weight: 500; transition: all 0.3s ease; }");
        out.println(".semester-btn:hover { transform: translateY(-1px); box-shadow: 0 4px 10px rgba(127,140,141,0.4); }");
        out.println(".semester-btn.active { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); }");
        out.println(".history-content { display: none; margin-top: 20px; border: 1px solid #ddd; border-radius: 12px; padding: 15px; background: #f9f9f9; }");
        out.println(".history-content.active { display: block; }");
        out.println("@media (max-width: 768px) { .header { flex-direction: column; text-align: center; gap: 15px; padding: 15px; } .header h1 { font-size: 1.5rem; } .menu { flex-wrap: wrap; justify-content: center; } .container { margin: 20px auto; padding: 0 15px; } .semester-selector { flex-direction: column; align-items: flex-start; } }");
        out.println("</style>");
        out.println("<script>");
        out.println("function toggleSubject(subjectId) {");
        out.println("  var details = document.getElementById('details-' + subjectId);");
        out.println("  if (details.classList.contains('open')) { details.classList.remove('open'); } else { details.classList.add('open'); }");
        out.println("}");
        out.println("function changeSemester() {");
        out.println("  var semester = document.getElementById('semester').value;");
        out.println("  window.location.href = 'viewGrades?semester=' + semester;");
        out.println("}");
        out.println("function showGradeHistory(gradeLevel) {");
        out.println("  // Hide all semester buttons and content");
        out.println("  var semesterBtns = document.querySelectorAll('.semester-btn-container');");
        out.println("  var historyContents = document.querySelectorAll('.history-content');");
        out.println("  semesterBtns.forEach(function(btn) { btn.style.display = 'none'; });");
        out.println("  historyContents.forEach(function(content) { content.classList.remove('active'); });");
        out.println("  // Show semester buttons for selected grade");
        out.println("  var semesterBtnContainer = document.getElementById('semester-btns-' + gradeLevel);");
        out.println("  if (semesterBtnContainer) {");
        out.println("    semesterBtnContainer.style.display = 'flex';");
        out.println("  }");
        out.println("  // Reset semester buttons active state");
        out.println("  var semesterBtns = document.querySelectorAll('.semester-btn');");
        out.println("  semesterBtns.forEach(function(btn) { btn.classList.remove('active'); });");
        out.println("}");
        out.println("function showSemesterHistory(gradeLevel, semester) {");
        out.println("  // Hide all content");
        out.println("  var historyContents = document.querySelectorAll('.history-content');");
        out.println("  historyContents.forEach(function(content) { content.classList.remove('active'); });");
        out.println("  // Show selected content");
        out.println("  var selectedContent = document.getElementById('history-content-' + gradeLevel + '-' + semester);");
        out.println("  if (selectedContent) {");
        out.println("    selectedContent.classList.add('active');");
        out.println("  }");
        out.println("  // Update active class on buttons");
        out.println("  var semesterBtns = document.querySelectorAll('.semester-btn');");
        out.println("  semesterBtns.forEach(function(btn) { btn.classList.remove('active'); });");
        out.println("  var activeBtn = document.getElementById('btn-' + gradeLevel + '-' + semester);");
        out.println("  if (activeBtn) {");
        out.println("    activeBtn.classList.add('active');");
        out.println("  }");
        out.println("}");
        out.println("</script>");
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
        out.println("<a href='viewGrades' class='active'>Grades</a>");
        out.println("<a href='viewAnnouncements'>Announcements</a>");
        out.println("<a href='viewCalendar'>Calendar</a>");
        out.println("</div>");

        out.println("<div class='container'>");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();

            String studentSql = "SELECT grade_level, first_name, last_name FROM students WHERE student_id = ?";
            pstmt = conn.prepareStatement(studentSql);
            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();

            int currentGradeLevel = 0;
            String studentName = "";
            if (rs.next()) {
                currentGradeLevel = rs.getInt("grade_level");
                studentName = rs.getString("first_name") + " " + rs.getString("last_name");
            }
            rs.close();
            pstmt.close();

            if (currentGradeLevel == 0) {
                out.println("<div class='card'><h2>My Grades</h2>");
                out.println("<div class='no-data'><p>Student information not found.</p></div></div>");
            } else {
                Map<String, String> teacherNames = getTeacherNames(conn);

                out.println("<div class='card'>");
                out.println("<h2>Current Grades - Grade " + currentGradeLevel + " (" + semester + " Semester)</h2>");
                out.println("<p><strong>Student:</strong> " + studentName + "</p>");

                out.println("<div class='semester-selector'>");
                out.println("<label for='semester'>Select Semester:</label>");
                out.println("<select id='semester' class='semester-select'>");
                out.println("<option value='First'" + ("First".equals(semester) ? " selected" : "") + ">First Semester</option>");
                out.println("<option value='Second'" + ("Second".equals(semester) ? " selected" : "") + ">Second Semester</option>");
                out.println("</select>");
                out.println("<button class='view-btn' onclick='changeSemester()'>View Grades</button>");
                out.println("</div>");

                String currentGradeDb = "grade" + currentGradeLevel + "_db";
                Connection currentGradeConn = null;
                boolean hasCurrentGrades = false;

                try {
                    String currentGradeDbUrl = "jdbc:mysql://localhost:3306/" + currentGradeDb;
                    currentGradeConn = DriverManager.getConnection(currentGradeDbUrl, "root", "");

                    for (String subject : SUBJECTS) {
                        String gradeSql =
                                "SELECT assignment_score, mid_score, final_score, total_score, grade_letter, graded_by " +
                                "FROM " + subject + " WHERE student_id = ? AND semester = ?";

                        pstmt = currentGradeConn.prepareStatement(gradeSql);
                        pstmt.setString(1, studentId);
                        pstmt.setString(2, semester);
                        rs = pstmt.executeQuery();

                        if (rs.next()) {
                            hasCurrentGrades = true;
                            double assignment = rs.getDouble("assignment_score");
                            double mid = rs.getDouble("mid_score");
                            double finalScore = rs.getDouble("final_score");
                            double total = rs.getDouble("total_score");
                            String gradeLetter = rs.getString("grade_letter");
                            String gradedById = rs.getString("graded_by");
                            String teacherName = teacherNames.getOrDefault(gradedById, "Not Available");

                            String gradeClass = "grade-" + gradeLetter.toLowerCase().replace("+", "plus");

                            out.println("<div class='subject-card'>");
                            out.println("<div class='subject-header' onclick='toggleSubject(\"" + subject + "\")'>");
                            out.println("<div>");
                            out.println("<div class='subject-title'>" + capitalize(subject) + "</div>");
                            out.println("<div class='subject-teacher'>Teacher: " + teacherName + "</div>");
                            out.println("</div>");
                            out.println("<div style='text-align: right;'><div style='font-size: 20px; color: #3498db;'>▼</div></div>");
                            out.println("</div>");
                            out.println("<div class='subject-details' id='details-" + subject + "'>");
                            out.println("<table class='grades-table'>");
                            out.println("<tr><th>Assessment</th><th>Score</th></tr>");
                            out.println("<tr><td>Assignment</td><td>" + assignment + " / 10</td></tr>");
                            out.println("<tr><td>Mid Exam</td><td>" + mid + " / 30</td></tr>");
                            out.println("<tr><td>Final Exam</td><td>" + finalScore + " / 60</td></tr>");
                            out.println("<tr class='total-row'><td>Total Score</td><td>" +
                                        String.format("%.1f", total) + " / 100</td></tr>");
                            out.println("<tr class='total-row'><td>Grade</td><td class='" + gradeClass + "'>" +
                                        gradeLetter + "</td></tr>");
                            out.println("</table>");
                            out.println("</div></div>");
                        }
                        rs.close();
                        pstmt.close();
                    }

                    if (!hasCurrentGrades) {
                        out.println("<div class='no-data'><p>No grades found for " + semester +
                                    " Semester of Grade " + currentGradeLevel + ".</p></div>");
                    }

                    currentGradeConn.close();

                } catch (Exception e) {
                    out.println("<div class='no-data'>");
                    out.println("<p style='color:red;'>Error loading current grades: " + e.getMessage() + "</p>");
                    out.println("<p>Database: " + currentGradeDb + " may not exist or connection failed.</p>");
                    out.println("</div>");
                }

                out.println("</div>"); // close current grades card

                // Academic history - NEW VERSION with single dropdown
                out.println("<div class='card'>");
                out.println("<h2>Academic History</h2>");

                // Determine which previous grades are available
                List<Integer> previousGrades = new ArrayList<>();
                if (currentGradeLevel == 10) {
                    previousGrades.add(9);
                } else if (currentGradeLevel == 11) {
                    previousGrades.add(10);
                    previousGrades.add(9);
                } else if (currentGradeLevel == 12) {
                    previousGrades.add(11);
                    previousGrades.add(10);
                    previousGrades.add(9);
                }

                if (!previousGrades.isEmpty()) {
                    out.println("<div class='history-selector'>");
                    out.println("<label for='history-grade'>Select Previous Grade:</label>");
                    out.println("<select id='history-grade' class='history-select' onchange='showGradeHistory(this.value)'>");
                    out.println("<option value=''>Select Grade</option>");
                    for (int grade : previousGrades) {
                        out.println("<option value='" + grade + "'>Grade " + grade + " History</option>");
                    }
                    out.println("</select>");
                    out.println("</div>");

                    // Create semester buttons and content for each previous grade
                    for (int grade : previousGrades) {
                        // Semester buttons container (initially hidden)
                        out.println("<div class='semester-btn-container' id='semester-btns-" + grade + "' style='display: none;'>");
                        out.println("<button id='btn-" + grade + "-First' class='semester-btn' onclick='showSemesterHistory(" + grade + ", \"First\")'>First Semester</button>");
                        out.println("<button id='btn-" + grade + "-Second' class='semester-btn' onclick='showSemesterHistory(" + grade + ", \"Second\")'>Second Semester</button>");
                        out.println("</div>");
                        
                        // History content for each semester
                        out.println("<div class='history-content' id='history-content-" + grade + "-First'>");
                        displayGradeHistoryForSemester(out, studentId, grade, "First", teacherNames);
                        out.println("</div>");
                        
                        out.println("<div class='history-content' id='history-content-" + grade + "-Second'>");
                        displayGradeHistoryForSemester(out, studentId, grade, "Second", teacherNames);
                        out.println("</div>");
                    }
                } else if (currentGradeLevel == 9) {
                    out.println("<p>This is your first year. No previous grade history available.</p>");
                } else {
                    out.println("<p>No previous grade history available.</p>");
                }

                out.println("</div>"); // history card

                // Academic summary
                out.println("<div class='card'>");
                out.println("<h2>Academic Summary</h2>");

                try {
                    String currentGradeDbUrl = "jdbc:mysql://localhost:3306/" + currentGradeDb;
                    Connection summaryConn = DriverManager.getConnection(currentGradeDbUrl, "root", "");

                    double totalPoints = 0;
                    int subjectCount = 0;

                    for (String subject : SUBJECTS) {
                        String summarySql = "SELECT grade_letter FROM " + subject +
                                            " WHERE student_id = ? AND semester = ?";
                        pstmt = summaryConn.prepareStatement(summarySql);
                        pstmt.setString(1, studentId);
                        pstmt.setString(2, semester);
                        rs = pstmt.executeQuery();

                        if (rs.next()) {
                            String gradeLetter = rs.getString("grade_letter");
                            totalPoints += getGradePoints(gradeLetter);
                            subjectCount++;
                        }
                        rs.close();
                        pstmt.close();
                    }

                    double gpa = subjectCount > 0 ? totalPoints / subjectCount : 0;

                    out.println("<div style='display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px;'>");
                    out.println("<div style='padding: 15px; background: #3498db; color: white; border-radius: 12px; text-align: center; box-shadow: 0 4px 12px rgba(52,152,219,0.4);'>");
                    out.println("<div style='font-size: 14px;'>Current Grade Level</div>");
                    out.println("<div style='font-size: 24px; font-weight: bold;'>" + currentGradeLevel + "</div>");
                    out.println("</div>");
                    out.println("<div style='padding: 15px; background: #2ecc71; color: white; border-radius: 12px; text-align: center; box-shadow: 0 4px 12px rgba(46,204,113,0.4);'>");
                    out.println("<div style='font-size: 14px;'>Current Semester GPA</div>");
                    out.println("<div style='font-size: 24px; font-weight: bold;'>" + String.format("%.2f", gpa) + "</div>");
                    out.println("<div style='font-size: 12px;'>Scale: 4.0</div>");
                    out.println("</div>");
                    out.println("<div style='padding: 15px; background: #9b59b6; color: white; border-radius: 12px; text-align: center; box-shadow: 0 4px 12px rgba(155,89,182,0.4);'>");
                    out.println("<div style='font-size: 14px;'>Subjects with Grades</div>");
                    out.println("<div style='font-size: 24px; font-weight: bold;'>" + subjectCount + "/9</div>");
                    out.println("<div style='font-size: 12px;'>Total Subjects</div>");
                    out.println("</div>");
                    out.println("</div>");

                    out.println("<div style='margin-top: 20px; padding: 15px; background: #f9f9f9; border-radius: 12px;'>");
                    out.println("<h3 style='margin-top: 0; color: #2c3e50;'>Performance Note</h3>");
                    if (gpa >= 3.5) {
                        out.println("<p style='color: #27ae60;'><strong>Excellent Performance!</strong> Keep up the great work!</p>");
                    } else if (gpa >= 3.0) {
                        out.println("<p style='color: #2ecc71;'><strong>Good Performance!</strong> You're doing well!</p>");
                    } else if (gpa >= 2.0) {
                        out.println("<p style='color: #f39c12;'><strong>Satisfactory Performance.</strong> Room for improvement.</p>");
                    } else {
                        out.println("<p style='color: #e74c3c;'><strong>Needs Improvement.</strong> Consider seeking academic support.</p>");
                    }
                    out.println("</div>");

                    summaryConn.close();

                } catch (Exception e) {
                    out.println("<p>Academic summary not available.</p>");
                }

                out.println("</div>"); // summary card
            }

        } catch (Exception e) {
            out.println("<div class='card'><h2>My Grades</h2>");
            out.println("<div class='no-data'><p style='color:red;'>Error loading grades: " + e.getMessage() + "</p></div></div>");
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }

        out.println("<br>");
        out.println("<a href='dashboard_student.html' class='back-btn'>Back to Dashboard</a>");
        out.println("</div>"); // container
        out.println("</body></html>");
    }

    private void displayGradeHistoryForSemester(PrintWriter out, String studentId, int gradeLevel, String semester,
                                               Map<String, String> teacherNames) {
        Connection gradeConn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String gradeDb = "grade" + gradeLevel + "_db";
            String gradeDbUrl = "jdbc:mysql://localhost:3306/" + gradeDb;
            gradeConn = DriverManager.getConnection(gradeDbUrl, "root", "");

            boolean hasHistory = false;

            out.println("<h3 style='margin-top: 0; color: #2c3e50;'>Grade " + gradeLevel + " - " + semester + " Semester</h3>");
            out.println("<table class='history-table'>");
            out.println("<tr><th>Subject</th><th>Assignment</th><th>Mid Exam</th><th>Final Exam</th><th>Total</th><th>Grade</th><th>Teacher</th></tr>");

            for (String subject : SUBJECTS) {
                String historySql =
                        "SELECT assignment_score, mid_score, final_score, total_score, grade_letter, graded_by " +
                        "FROM " + subject + " WHERE student_id = ? AND semester = ?";

                pstmt = gradeConn.prepareStatement(historySql);
                pstmt.setString(1, studentId);
                pstmt.setString(2, semester);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    hasHistory = true;
                    double assignment = rs.getDouble("assignment_score");
                    double mid = rs.getDouble("mid_score");
                    double finalScore = rs.getDouble("final_score");
                    double total = rs.getDouble("total_score");
                    String gradeLetter = rs.getString("grade_letter");
                    String gradedById = rs.getString("graded_by");
                    String teacherName = teacherNames.getOrDefault(gradedById, "N/A");

                    String gradeClass = "grade-" + gradeLetter.toLowerCase().replace("+", "plus");

                    out.println("<tr>");
                    out.println("<td>" + capitalize(subject) + "</td>");
                    out.println("<td>" + assignment + "/10</td>");
                    out.println("<td>" + mid + "/30</td>");
                    out.println("<td>" + finalScore + "/60</td>");
                    out.println("<td><strong>" + String.format("%.1f", total) + "/100</strong></td>");
                    out.println("<td class='" + gradeClass + "'>" + gradeLetter + "</td>");
                    out.println("<td>" + teacherName + "</td>");
                    out.println("</tr>");
                }
                rs.close();
                pstmt.close();
            }

            out.println("</table>");

            if (!hasHistory) {
                out.println("<p style='color: #666; text-align: center;'>No grade history found for " + semester + " Semester of Grade " + gradeLevel + ".</p>");
            }

            gradeConn.close();

        } catch (Exception e) {
            out.println("<p style='color: #666; text-align: center;'>Grade history for Grade " + gradeLevel + " (" + semester + " Semester) not available.</p>");
        }
    }

    private Map<String, String> getTeacherNames(Connection conn) throws SQLException {
        Map<String, String> teacherMap = new HashMap<>();

        try {
            String teacherSql = "SELECT teacher_id, first_name, last_name FROM teachers";
            PreparedStatement pstmt = conn.prepareStatement(teacherSql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String teacherId = rs.getString("teacher_id");
                String teacherName = rs.getString("first_name") + " " + rs.getString("last_name");
                teacherMap.put(teacherId, teacherName);
            }
            rs.close();
            pstmt.close();
        } catch (Exception ignored) {}

        // fallback names
        teacherMap.putIfAbsent("T001", "Mr. Abebe Kebede");
        teacherMap.putIfAbsent("T002", "Ms. Almaz Girma");
        teacherMap.putIfAbsent("T003", "Mr. Tesfaye Lemma");
        teacherMap.putIfAbsent("T004", "Ms. Sara Mohammed");
        teacherMap.putIfAbsent("T005", "Mr. Daniel Worku");
        teacherMap.putIfAbsent("T006", "Ms. Eden Tadesse");
        teacherMap.putIfAbsent("T007", "Mr. Yohannes Assefa");
        teacherMap.putIfAbsent("T008", "Ms. Ruth Tekle");
        teacherMap.putIfAbsent("T009", "Mr. Samuel Bekele");

        return teacherMap;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private double getGradePoints(String gradeLetter) {
        if (gradeLetter == null) return 0.0;
        switch (gradeLetter) {
            case "A+":
            case "A":  return 4.0;
            case "B+": return 3.5;
            case "B":  return 3.0;
            case "C+": return 2.5;
            case "C":  return 2.0;
            case "D":  return 1.0;
            case "F":  return 0.0;
            default:   return 0.0;
        }
    }
}