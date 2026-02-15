package register;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import database.DBConnection;

@WebServlet("/StudentListServlet")
public class StudentListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"registrar".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT student_id, first_name, last_name FROM students WHERE registration_status='Active' ORDER BY student_id";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            boolean hasAny = false;
            while (rs.next()) {
                hasAny = true;
                String id = rs.getString("student_id");
                String name = rs.getString("first_name") + " " + rs.getString("last_name");
                out.println("<option value='" + id + "'>" + id + " - " + name + "</option>");
            }
            if (!hasAny) {
                out.println("<option value='' disabled>No active students found</option>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("<option value='' disabled>Error loading students</option>");
        } finally {
            DBConnection.closeConnection(conn, ps, rs);
        }
    }
}