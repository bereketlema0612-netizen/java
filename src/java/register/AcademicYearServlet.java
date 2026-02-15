package register;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import database.DBConnection;

@WebServlet("/AcademicYearServlet")
public class AcademicYearServlet extends HttpServlet {

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
            ps = conn.prepareStatement("SELECT year_name FROM academic_years ORDER BY year_name DESC");
            rs = ps.executeQuery();

            boolean hasAny = false;
            while (rs.next()) {
                hasAny = true;
                String year = rs.getString("year_name");
                out.println("<option value='" + year + "'>" + year + "</option>");
            }
            if (!hasAny) {
                out.println("<option value='' disabled>No academic years found</option>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("<option value='' disabled>Error loading years</option>");
        } finally {
            DBConnection.closeConnection(conn, ps, rs);
        }
    }
}