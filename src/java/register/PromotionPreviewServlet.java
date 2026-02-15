package register;

import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import database.DBConnection;

@WebServlet("/PromotionPreviewServlet")
public class PromotionPreviewServlet extends HttpServlet {

    private static final String[] SUBJECTS = {
        "biology", "english", "mathematics", "chemistry",
        "physics", "art", "history", "geography", "civics"
    };

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"registrar".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int fromGrade = Integer.parseInt(request.getParameter("fromGrade"));
        int toGrade = Integer.parseInt(request.getParameter("toGrade"));
        String newAcademicYear = request.getParameter("academicYear");
        String newSection = request.getParameter("newSection");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<Map<String, Object>> eligible = new ArrayList<>();
        List<Map<String, Object>> notEligibleFs = new ArrayList<>();
        List<Map<String, Object>> incomplete = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            String currentYear = getCurrentAcademicYear(conn);

            // Get all active students in the source grade
            String studentSql = "SELECT student_id, first_name, last_name, section FROM students WHERE grade_level=? AND registration_status='Active'";
            ps = conn.prepareStatement(studentSql);
            ps.setInt(1, fromGrade);
            rs = ps.executeQuery();

            while (rs.next()) {
                String studentId = rs.getString("student_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String currentSection = rs.getString("section");

                // Check if all subjects have grades for both semesters
                boolean allSubjectsComplete = checkAllSubjectsComplete(studentId, fromGrade, currentYear);
                if (!allSubjectsComplete) {
                    Map<String, Object> s = new HashMap<>();
                    s.put("student_id", studentId);
                    s.put("name", firstName + " " + lastName);
                    s.put("section", currentSection);
                    incomplete.add(s);
                    continue;
                }

                int failCount = countFailingSubjects(studentId, fromGrade, currentYear);

                Map<String, Object> student = new HashMap<>();
                student.put("student_id", studentId);
                student.put("name", firstName + " " + lastName);
                student.put("section", currentSection);
                student.put("failCount", failCount);

                if (failCount < 4) {
                    eligible.add(student);
                } else {
                    notEligibleFs.add(student);
                }
            }

            // Build HTML output
            out.println("<h4>✅ Eligible for Promotion (Failing subjects < 4)</h4>");
            if (eligible.isEmpty()) {
                out.println("<p>No eligible students.</p>");
            } else {
                out.println("<table class='data-table'><thead><tr><th>Student ID</th><th>Name</th><th>Current Section</th><th>Failing Subjects</th></tr></thead><tbody>");
                for (Map<String, Object> s : eligible) {
                    out.println("<tr>");
                    out.println("<td>" + s.get("student_id") + "</td>");
                    out.println("<td>" + s.get("name") + "</td>");
                    out.println("<td>" + s.get("section") + "</td>");
                    out.println("<td>" + s.get("failCount") + "</td>");
                    out.println("</tr>");
                }
                out.println("</tbody></table>");
            }

            out.println("<h4 style='margin-top:20px;'>❌ Not Eligible (Failing subjects ≥ 4)</h4>");
            if (notEligibleFs.isEmpty()) {
                out.println("<p>None.</p>");
            } else {
                out.println("<table class='data-table'><thead><tr><th>Student ID</th><th>Name</th><th>Current Section</th><th>Failing Subjects</th></tr></thead><tbody>");
                for (Map<String, Object> s : notEligibleFs) {
                    out.println("<tr>");
                    out.println("<td>" + s.get("student_id") + "</td>");
                    out.println("<td>" + s.get("name") + "</td>");
                    out.println("<td>" + s.get("section") + "</td>");
                    out.println("<td>" + s.get("failCount") + "</td>");
                    out.println("</tr>");
                }
                out.println("</tbody></table>");
            }

            out.println("<h4 style='margin-top:20px;'>⚠️ Incomplete Grades (missing some subjects/semesters)</h4>");
            if (incomplete.isEmpty()) {
                out.println("<p>All students have complete grade records.</p>");
            } else {
                out.println("<table class='data-table'><thead><tr><th>Student ID</th><th>Name</th><th>Current Section</th></tr></thead><tbody>");
                for (Map<String, Object> s : incomplete) {
                    out.println("<tr>");
                    out.println("<td>" + s.get("student_id") + "</td>");
                    out.println("<td>" + s.get("name") + "</td>");
                    out.println("<td>" + s.get("section") + "</td>");
                    out.println("</tr>");
                }
                out.println("</tbody></table>");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("<p class='error'>Database error</p>");
        } finally {
            DBConnection.closeConnection(conn, ps, rs);
        }
    }

    private boolean checkAllSubjectsComplete(String studentId, int gradeLevel, String currentYear) {
        String gradeDb = "grade" + gradeLevel + "_db";
        String gradeDbUrl = "jdbc:mysql://localhost:3306/" + gradeDb;
        String dbUser = "root";
        String dbPass = "";

        try (Connection gradeConn = DriverManager.getConnection(gradeDbUrl, dbUser, dbPass)) {
            for (String subject : SUBJECTS) {
                // Check if student has grades for both semesters
                String sql = "SELECT COUNT(*) FROM " + subject +
                             " WHERE student_id=? AND academic_year=? AND semester IN ('First','Second')";
                try (PreparedStatement ps = gradeConn.prepareStatement(sql)) {
                    ps.setString(1, studentId);
                    ps.setString(2, currentYear);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int count = rs.getInt(1);
                            if (count != 2) { // must have both semesters
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int countFailingSubjects(String studentId, int gradeLevel, String currentYear) {
        String gradeDb = "grade" + gradeLevel + "_db";
        String gradeDbUrl = "jdbc:mysql://localhost:3306/" + gradeDb;
        String dbUser = "root";
        String dbPass = "";

        int failCount = 0;
        try (Connection gradeConn = DriverManager.getConnection(gradeDbUrl, dbUser, dbPass)) {
            for (String subject : SUBJECTS) {
                String sql = "SELECT grade_letter FROM " + subject +
                             " WHERE student_id=? AND academic_year=? AND semester IN ('First','Second')";
                try (PreparedStatement ps = gradeConn.prepareStatement(sql)) {
                    ps.setString(1, studentId);
                    ps.setString(2, currentYear);
                    try (ResultSet rs = ps.executeQuery()) {
                        boolean subjectFailed = false;
                        while (rs.next()) {
                            if ("F".equals(rs.getString("grade_letter"))) {
                                subjectFailed = true;
                            }
                        }
                        if (subjectFailed) {
                            failCount++;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return failCount;
    }

    private String getCurrentAcademicYear(Connection conn) throws SQLException {
        String sql = "SELECT year_name FROM academic_years WHERE is_current=1";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("year_name");
            }
        }
        return "2025-2026";
    }
}