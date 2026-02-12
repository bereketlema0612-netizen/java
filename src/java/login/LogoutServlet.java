package login;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        String role = null;
        if (session != null) {
            // read role before invalidating
            Object r = session.getAttribute("role");
            if (r != null) {
                role = r.toString();
            }
            session.invalidate();
        }

        // after logout, send each role to its own first page
        if ("student".equals(role) || "teacher".equals(role)) {
            response.sendRedirect("Student_teacher_index.html");
        } else if ("registrar".equals(role)) {
            response.sendRedirect("registrar_login.html");
        } else if ("director".equals(role)) {
            response.sendRedirect("admin_login.html");
        } 
    }
}
