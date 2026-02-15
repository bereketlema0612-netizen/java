package register;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import database.DBConnection;

@WebServlet("/StudentAddServlet")
public class StudentAddServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"registrar".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String firstName = request.getParameter("first_name");
        String lastName = request.getParameter("last_name");
        int grade = Integer.parseInt(request.getParameter("grade_level"));
        String section = request.getParameter("section");
        String dob = request.getParameter("dob");
        String gender = request.getParameter("gender");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String parentName = request.getParameter("parent_name");
        String parentPhone = request.getParameter("parent_phone");
        String status = request.getParameter("registration_status");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Generate student_id: STU + grade + next 3-digit number
            String prefix = "STU" + grade;
            ps = conn.prepareStatement("SELECT student_id FROM students WHERE student_id LIKE ? ORDER BY student_id DESC LIMIT 1");
            ps.setString(1, prefix + "%");
            rs = ps.executeQuery();
            int nextNum = 1;
            if (rs.next()) {
                String lastId = rs.getString("student_id");
                String numPart = lastId.substring(prefix.length()); // e.g., "001"
                nextNum = Integer.parseInt(numPart) + 1;
            }
            String studentId = String.format("%s%03d", prefix, nextNum);
            rs.close(); ps.close();

            // Insert into users
            ps = conn.prepareStatement("INSERT INTO users (username, password, role, email) VALUES (?, ?, 'student', ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, studentId);
            ps.setString(2, "default123"); // TODO: hash this password
            ps.setString(3, email);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            int userId = 0;
            if (rs.next()) userId = rs.getInt(1);
            rs.close(); ps.close();

            // Insert into students
            ps = conn.prepareStatement(
                "INSERT INTO students (student_id, user_id, first_name, last_name, grade_level, section, " +
                "dob, gender, email, phone, address, parent_name, parent_phone, registration_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, studentId);
            ps.setInt(2, userId);
            ps.setString(3, firstName);
            ps.setString(4, lastName);
            ps.setInt(5, grade);
            ps.setString(6, section);
            ps.setDate(7, (dob != null && !dob.isEmpty()) ? Date.valueOf(dob) : null);
            ps.setString(8, gender);
            ps.setString(9, email);
            ps.setString(10, phone);
            ps.setString(11, address);
            ps.setString(12, parentName);
            ps.setString(13, parentPhone);
            ps.setString(14, status != null ? status : "Active");
            ps.executeUpdate();

            conn.commit();

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print("Student added successfully");

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("Database error: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn, ps, rs);
        }
    }
}