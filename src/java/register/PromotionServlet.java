package registrar;

import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import database.DBConnection;

@WebServlet("/PromotionServlet")
public class PromotionServlet extends HttpServlet {

    private static final String[] SUBJECTS = {
        "biology", "english", "mathematics", "chemistry",
        "physics", "art", "history", "geography", "civics"
    };

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<String> promotedStudents = new ArrayList<>();
        List<String> notPromotedFs = new ArrayList<>();
        List<String> incompleteStudents = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

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
                    incompleteStudents.add(studentId);
                    String message = "You were not promoted because your grade records are incomplete for Grade " + fromGrade + ". Please contact the registrar.";
                    insertNotification(conn, studentId, message);
                    continue;
                }

                int failCount = countFailingSubjects(studentId, fromGrade, currentYear);

                if (failCount < 4) {
                    promotedStudents.add(studentId);
                    // Create new enrollment in target grade
                    String targetSection = (newSection != null && !newSection.isEmpty()) ? newSection : currentSection;
                    insertEnrollment(conn, studentId, newAcademicYear, toGrade, targetSection);
                    // Promotion notification
                    String message = "Congratulations! You have been promoted to Grade " + toGrade + " for the academic year " + newAcademicYear + ".";
                    insertNotification(conn, studentId, message);
                } else {
                    notPromotedFs.add(studentId);
                    String message = "You have not been promoted because you failed in " + failCount + " subjects. Please contact the registrar for more information.";
                    insertNotification(conn, studentId, message);
                }
            }
            rs.close();
            ps.close();

            // Update students table for promoted students
            if (!promotedStudents.isEmpty()) {
                String updateSql = "UPDATE students SET grade_level=? WHERE student_id=?";
                ps = conn.prepareStatement(updateSql);
                for (String sid : promotedStudents) {
                    ps.setInt(1, toGrade);
                    ps.setString(2, sid);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print(
                "Promoted: " + promotedStudents.size() + 
                ", Not promoted (failed subjects ≥ 4): " + notPromotedFs.size() +
                ", Incomplete grades: " + incompleteStudents.size()
            );

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("Database error: " + e.getMessage());
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
                String sql = "SELECT COUNT(*) FROM " + subject +
                             " WHERE student_id=? AND academic_year=? AND semester IN ('First','Second')";
                try (PreparedStatement ps = gradeConn.prepareStatement(sql)) {
                    ps.setString(1, studentId);
                    ps.setString(2, currentYear);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int count = rs.getInt(1);
                            if (count != 2) return false;
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

    private void insertEnrollment(Connection conn, String studentId, String academicYear,
                                  int gradeLevel, String section) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, academic_year, grade_level, section, " +
                     "enrollment_type, enrollment_date, status) VALUES (?, ?, ?, ?, 'Promotion', CURDATE(), 'Active')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, academicYear);
            ps.setInt(3, gradeLevel);
            ps.setString(4, section);
            ps.executeUpdate();
        }
    }

    private void insertNotification(Connection conn, String studentId, String message) throws SQLException {
        String sql = "INSERT INTO notifications (student_id, message) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, message);
            ps.executeUpdate();
        }
    }
}