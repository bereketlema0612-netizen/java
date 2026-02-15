package register;

import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import database.DBConnection;

@WebServlet("/studentNotifications")
public class NotificationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"student".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String studentId = (String) session.getAttribute("username");
        String type = request.getParameter("type");
        if (type == null) type = "unread";

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();

            if ("history".equals(type)) {
                // Fetch all notifications from last 7 days
                String sql = "SELECT notification_id, message, created_at, is_read FROM notifications " +
                             "WHERE student_id = ? AND created_at >= NOW() - INTERVAL 7 DAY " +
                             "ORDER BY created_at DESC";
                ps = conn.prepareStatement(sql);
                ps.setString(1, studentId);
                rs = ps.executeQuery();

                out.println("<div class='dropdown-content'>");
                boolean hasAny = false;
                while (rs.next()) {
                    hasAny = true;
                    int id = rs.getInt("notification_id");
                    String msg = rs.getString("message");
                    Timestamp ts = rs.getTimestamp("created_at");
                    int isRead = rs.getInt("is_read");
                    String colorClass = getMessageClass(msg);
                    out.println("<div class='dropdown-item'>");
                    out.println("<div class='msg " + colorClass + "'>" + escape(msg) + "</div>");
                    out.println("<div class='time'>" + ts + "</div>");
                    out.println("</div>");
                }
                if (!hasAny) {
                    out.println("<div class='dropdown-empty'>No notifications in the last 7 days.</div>");
                }
                out.println("</div>");
            } else {
                // Fetch unread notifications only (for main section)
                String sql = "SELECT notification_id, message, created_at FROM notifications " +
                             "WHERE student_id = ? AND is_read = 0 ORDER BY created_at DESC";
                ps = conn.prepareStatement(sql);
                ps.setString(1, studentId);
                rs = ps.executeQuery();

                List<Map<String, Object>> unread = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> notif = new HashMap<>();
                    notif.put("id", rs.getInt("notification_id"));
                    notif.put("message", rs.getString("message"));
                    notif.put("created_at", rs.getTimestamp("created_at"));
                    unread.add(notif);
                }
                rs.close();
                ps.close();

                if (unread.isEmpty()) {
                    out.println("<div class='empty-notifications'>No new notifications.</div>");
                } else {
                    out.println("<div class='notification-list'>");
                    for (Map<String, Object> notif : unread) {
                        int id = (int) notif.get("id");
                        String msg = (String) notif.get("message");
                        Timestamp ts = (Timestamp) notif.get("created_at");
                        String colorClass = getMessageClass(msg);
                        out.println("<div class='notification-item' data-id='" + id + "'>");
                        out.println("<div class='notification-content'>");
                        out.println("<div class='notification-message " + colorClass + "'>" + escape(msg) + "</div>");
                        out.println("<div class='notification-time'>" + ts + "</div>");
                        out.println("</div>");
                        out.println("<button class='dismiss-btn' onclick='dismissNotification(" + id + ", this)'>✕</button>");
                        out.println("</div>");
                    }
                    out.println("</div>");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("<p class='error'>Could not load notifications.</p>");
        } finally {
            DBConnection.closeConnection(conn, ps, rs);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"student".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int notificationId = Integer.parseInt(idParam);

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE notifications SET is_read = 1 WHERE notification_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, notificationId);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().print("Dismissed");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().print("Notification not found");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("Database error");
        } finally {
            DBConnection.closeConnection(conn, ps, null);
        }
    }

    private String getMessageClass(String message) {
        if (message == null) return "";
        String lower = message.toLowerCase();
        if (lower.contains("congrat")) {
            return "success";
        } else if (lower.contains("fail") || lower.contains("not promoted")) {
            return "failure";
        }
        return "";
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
}