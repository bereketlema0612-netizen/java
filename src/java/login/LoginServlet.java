package login;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import database.DBConnection;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role     = request.getParameter("role");   // student, teacher, registrar, director

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();

            String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                // success
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                session.setAttribute("role", role);

                if ("student".equals(role)) {
                    response.sendRedirect("dashboard_student.html");
                } else if ("teacher".equals(role)) {
                    response.sendRedirect("dashboard_teacher.html");
                } else if ("registrar".equals(role)) {
                    response.sendRedirect("registrardashboard.html");
                } else if ("admin".equals(role)) {
                    response.sendRedirect("admindashboard.html");
                } 
                return;
            }

            // invalid login → back to right page
            if ("student".equals(role) || "teacher".equals(role)) {
                response.sendRedirect("Student_teacher_index.html?error=1");
            } else if ("registrar".equals(role)) {
                response.sendRedirect("registrar_login.html?error=1");
            } else if ("admin".equals(role)) {
                response.sendRedirect("admin_login.html?error=1");
            }
            

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("Student_teacher_index.html?error=2");
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
