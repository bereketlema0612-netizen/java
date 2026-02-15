package register;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import database.DBConnection;

@WebServlet("/StudentUpdateServlet")
public class StudentUpdateServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"registrar".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String studentId = request.getParameter("student_id");
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

        try {
            conn = DBConnection.getConnection();

            // Update students table
            ps = conn.prepareStatement(
                "UPDATE students SET first_name=?, last_name=?, grade_level=?, section=?, " +
                "dob=?, gender=?, email=?, phone=?, address=?, parent_name=?, parent_phone=?, " +
                "registration_status=? WHERE student_id=?");
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setInt(3, grade);
            ps.setString(4, section);
            ps.setDate(5, (dob != null && !dob.isEmpty()) ? Date.valueOf(dob) : null);
            ps.setString(6, gender);
            ps.setString(7, email);
            ps.setString(8, phone);
            ps.setString(9, address);
            ps.setString(10, parentName);
            ps.setString(11, parentPhone);
            ps.setString(12, status);
            ps.setString(13, studentId);
            ps.executeUpdate();

            // Optionally update email in users table
            ps = conn.prepareStatement("UPDATE users SET email=? WHERE username=?");
            ps.setString(1, email);
            ps.setString(2, studentId);
            ps.executeUpdate();

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print("Student updated successfully");

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("Database error: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn, ps, null);
        }
    }
}