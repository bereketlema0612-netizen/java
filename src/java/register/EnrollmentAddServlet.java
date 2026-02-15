package register;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import database.DBConnection;

@WebServlet("/EnrollmentAddServlet")
public class EnrollmentAddServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"registrar".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String studentId = request.getParameter("student_id");
        String academicYear = request.getParameter("academic_year");
        int gradeLevel = Integer.parseInt(request.getParameter("grade_level"));
        String section = request.getParameter("section");
        String enrollmentType = request.getParameter("enrollment_type");
        String enrollmentDate = request.getParameter("enrollment_date");
        String status = request.getParameter("status");

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();

            String sql = "INSERT INTO enrollments (student_id, academic_year, grade_level, section, enrollment_type, enrollment_date, status) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, studentId);
            ps.setString(2, academicYear);
            ps.setInt(3, gradeLevel);
            ps.setString(4, section);
            ps.setString(5, enrollmentType);
            ps.setDate(6, Date.valueOf(enrollmentDate));
            ps.setString(7, status);
            ps.executeUpdate();

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print("Enrollment added successfully");

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("Database error: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn, ps, null);
        }
    }
}