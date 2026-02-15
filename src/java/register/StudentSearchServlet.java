package register;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.json.JSONObject;
import database.DBConnection;

@WebServlet("/StudentSearchServlet")
public class StudentSearchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"registrar".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String studentId = request.getParameter("studentId");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String gradeLevel = request.getParameter("gradeLevel");
        String format = request.getParameter("format"); // optional, "full" for complete table

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            StringBuilder sql = new StringBuilder(
                "SELECT student_id, first_name, last_name, grade_level, section, email, registration_status, " +
                "dob, gender, phone, address, parent_name, parent_phone FROM students WHERE 1=1");

            if (studentId != null && !studentId.isEmpty())
                sql.append(" AND student_id LIKE ?");
            if (firstName != null && !firstName.isEmpty())
                sql.append(" AND first_name LIKE ?");
            if (lastName != null && !lastName.isEmpty())
                sql.append(" AND last_name LIKE ?");
            if (gradeLevel != null && !gradeLevel.isEmpty())
                sql.append(" AND grade_level = ?");

            sql.append(" ORDER BY grade_level, last_name, first_name");

            ps = conn.prepareStatement(sql.toString());

            int idx = 1;
            if (studentId != null && !studentId.isEmpty())
                ps.setString(idx++, "%" + studentId + "%");
            if (firstName != null && !firstName.isEmpty())
                ps.setString(idx++, "%" + firstName + "%");
            if (lastName != null && !lastName.isEmpty())
                ps.setString(idx++, "%" + lastName + "%");
            if (gradeLevel != null && !gradeLevel.isEmpty())
                ps.setInt(idx++, Integer.parseInt(gradeLevel));

            rs = ps.executeQuery();

            StringBuilder rowsHtml = new StringBuilder();
            boolean hasRows = false;

            while (rs.next()) {
                hasRows = true;
                String sid = rs.getString("student_id");
                String fname = escape(rs.getString("first_name"));
                String lname = escape(rs.getString("last_name"));
                int grade = rs.getInt("grade_level");
                String section = escape(rs.getString("section"));
                String email = escape(rs.getString("email"));
                String status = rs.getString("registration_status");
                String dob = rs.getDate("dob") != null ? rs.getDate("dob").toString() : "";
                String gender = escape(rs.getString("gender"));
                String phone = escape(rs.getString("phone"));
                String address = escape(rs.getString("address"));
                String parentName = escape(rs.getString("parent_name"));
                String parentPhone = escape(rs.getString("parent_phone"));

                JSONObject studentJson = new JSONObject();
                studentJson.put("student_id", sid);
                studentJson.put("first_name", fname);
                studentJson.put("last_name", lname);
                studentJson.put("grade_level", grade);
                studentJson.put("section", section);
                studentJson.put("email", email);
                studentJson.put("dob", dob);
                studentJson.put("gender", gender);
                studentJson.put("phone", phone);
                studentJson.put("address", address);
                studentJson.put("parent_name", parentName);
                studentJson.put("parent_phone", parentPhone);
                studentJson.put("registration_status", status);

                String jsonString = studentJson.toString();

                rowsHtml.append("<tr>");
                rowsHtml.append("<td>").append(sid).append("</td>");
                rowsHtml.append("<td>").append(fname).append(" ").append(lname).append("</td>");
                rowsHtml.append("<td>").append(grade).append("</td>");
                rowsHtml.append("<td>").append(section).append("</td>");
                rowsHtml.append("<td>").append(email).append("</td>");
                rowsHtml.append("<td><span class='status-badge status-").append(getStatusClass(status)).append("'>").append(status).append("</span></td>");
                rowsHtml.append("<td>");
                rowsHtml.append("<button class='btn btn-primary' onclick='showEditForm(").append(jsonString).append(")'>Edit</button> ");
                rowsHtml.append("<button class='btn btn-danger' onclick='deleteStudent(\"").append(sid).append("\")'>Delete</button>");
                rowsHtml.append("</td>");
                rowsHtml.append("</tr>");
            }

            if ("full".equals(format)) {
                // Return complete table (for any future use)
                out.println("<table class='data-table'>");
                out.println("<thead><tr><th>ID</th><th>Name</th><th>Grade</th><th>Section</th><th>Email</th><th>Status</th><th>Actions</th></tr></thead>");
                out.println("<tbody>");
                if (hasRows) {
                    out.println(rowsHtml.toString());
                } else {
                    out.println("<tr><td colspan='7' class='empty-state'>No students found</td></tr>");
                }
                out.println("</tbody></table>");
            } else {
                // Default: return only rows (no table wrapper)
                if (hasRows) {
                    out.println(rowsHtml.toString());
                }
                // If no rows, output nothing – the calling JavaScript will show empty state
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            if ("full".equals(format)) {
                out.println("<p class='error'>Database error</p>");
            } else {
                out.println("<tr><td colspan='7' class='error'>Database error</td></tr>");
            }
        } finally {
            DBConnection.closeConnection(conn, ps, rs);
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String getStatusClass(String status) {
        if (status == null) return "inactive";
        switch (status) {
            case "Active": return "active";
            case "Inactive": return "inactive";
            case "Graduated": return "graduated";
            case "Transferred": return "transferred";
            default: return "inactive";
        }
    }
}