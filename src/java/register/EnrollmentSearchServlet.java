package register;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.json.JSONObject;
import database.DBConnection;

@WebServlet("/EnrollmentSearchServlet")
public class EnrollmentSearchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"registrar".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String studentId = request.getParameter("studentId");
        String grade = request.getParameter("grade");
        String academicYear = request.getParameter("academicYear");
        String status = request.getParameter("status");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            StringBuilder sql = new StringBuilder(
                "SELECT e.enrollment_id, e.student_id, e.academic_year, e.grade_level, e.section, " +
                "e.enrollment_type, e.enrollment_date, e.status, s.first_name, s.last_name " +
                "FROM enrollments e JOIN students s ON e.student_id = s.student_id WHERE 1=1");

            if (studentId != null && !studentId.isEmpty())
                sql.append(" AND e.student_id LIKE ?");
            if (grade != null && !grade.isEmpty())
                sql.append(" AND e.grade_level = ?");
            if (academicYear != null && !academicYear.isEmpty())
                sql.append(" AND e.academic_year = ?");
            if (status != null && !status.isEmpty())
                sql.append(" AND e.status = ?");

            sql.append(" ORDER BY e.enrollment_date DESC");

            ps = conn.prepareStatement(sql.toString());

            int idx = 1;
            if (studentId != null && !studentId.isEmpty())
                ps.setString(idx++, "%" + studentId + "%");
            if (grade != null && !grade.isEmpty())
                ps.setInt(idx++, Integer.parseInt(grade));
            if (academicYear != null && !academicYear.isEmpty())
                ps.setString(idx++, academicYear);
            if (status != null && !status.isEmpty())
                ps.setString(idx++, status);

            rs = ps.executeQuery();

            while (rs.next()) {
                int enrollmentId = rs.getInt("enrollment_id");
                String sid = rs.getString("student_id");
                String studentName = rs.getString("first_name") + " " + rs.getString("last_name");
                String year = rs.getString("academic_year");
                int gradeLevel = rs.getInt("grade_level");
                String section = rs.getString("section");
                String type = rs.getString("enrollment_type");
                String date = rs.getDate("enrollment_date") != null ? rs.getDate("enrollment_date").toString() : "";
                String stat = rs.getString("status");

                // Build JSON for edit function
                JSONObject enrollmentJson = new JSONObject();
                enrollmentJson.put("enrollment_id", enrollmentId);
                enrollmentJson.put("student_id", sid);
                enrollmentJson.put("academic_year", year);
                enrollmentJson.put("grade_level", gradeLevel);
                enrollmentJson.put("section", section);
                enrollmentJson.put("enrollment_type", type);
                enrollmentJson.put("enrollment_date", date);
                enrollmentJson.put("status", stat);

                String jsonString = enrollmentJson.toString();

                out.println("<tr>");
                out.println("<td>" + enrollmentId + "</td>");
                out.println("<td>" + sid + "</td>");
                out.println("<td>" + studentName + "</td>");
                out.println("<td>" + gradeLevel + "</td>");
                out.println("<td>" + section + "</td>");
                out.println("<td>" + year + "</td>");
                out.println("<td>" + type + "</td>");
                out.println("<td>" + date + "</td>");
                out.println("<td><span class='status-badge status-" + getStatusClass(stat) + "'>" + stat + "</span></td>");
                out.println("<td>");
                out.println("<button class='btn btn-primary' onclick='showEditEnrollment(" + jsonString + ")'>Edit</button>");
                out.println("<button class='btn btn-danger' onclick='deleteEnrollment(" + enrollmentId + ")'>Delete</button>");
                out.println("</td>");
                out.println("</tr>");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("<tr><td colspan='10' class='error'>Database error</td></tr>");
        } finally {
            DBConnection.closeConnection(conn, ps, rs);
        }
    }

    private String getStatusClass(String status) {
        if (status == null) return "inactive";
        switch (status) {
            case "Active": return "active";
            case "Pending": return "pending";
            case "Withdrawn": return "inactive";
            default: return "inactive";
        }
    }
}