package register;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import database.DBConnection;

@WebServlet("/StudentDeleteServlet")
public class StudentDeleteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"registrar".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String studentId = request.getParameter("studentId");
        if (studentId == null || studentId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Missing student ID");
            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Get user_id from students
            ps = conn.prepareStatement("SELECT user_id FROM students WHERE student_id=?");
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            int userId = 0;
            if (rs.next()) {
                userId = rs.getInt("user_id");
            }
            rs.close();
            ps.close();

            // Delete from students
            ps = conn.prepareStatement("DELETE FROM students WHERE student_id=?");
            ps.setString(1, studentId);
            ps.executeUpdate();
            ps.close();

            // Delete from users if user_id exists
            if (userId > 0) {
                ps = conn.prepareStatement("DELETE FROM users WHERE user_id=?");
                ps.setInt(1, userId);
                ps.executeUpdate();
                ps.close();
            }

            conn.commit();
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print("Student deleted successfully");

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("Database error: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn, ps, null);
        }
    }
}