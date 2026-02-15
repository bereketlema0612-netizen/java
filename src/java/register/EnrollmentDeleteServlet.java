package register;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import database.DBConnection;

@WebServlet("/EnrollmentDeleteServlet")
public class EnrollmentDeleteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"registrar".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String enrollmentIdParam = request.getParameter("enrollmentId");
        if (enrollmentIdParam == null || enrollmentIdParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Missing enrollment ID");
            return;
        }

        int enrollmentId = Integer.parseInt(enrollmentIdParam);

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement("DELETE FROM enrollments WHERE enrollment_id=?");
            ps.setInt(1, enrollmentId);
            int deleted = ps.executeUpdate();

            if (deleted > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().print("Enrollment deleted successfully");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().print("Enrollment not found");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("Database error: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn, ps, null);
        }
    }
}