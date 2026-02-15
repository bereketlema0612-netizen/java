package register;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import database.DBConnection;

@WebServlet("/CertificateServlet")
public class CertificateServlet extends HttpServlet {

    private static final String[] SUBJECTS = {
        "biology", "english", "mathematics", "chemistry",
        "physics", "art", "history", "geography", "civics"
    };

    private static class Student {
        String id;
        String firstName;
        String lastName;
        int gradeLevel;
        String section;
    }

    private static class TranscriptRow {
        String subject;
        String firstScore = "-";
        String firstGrade = "-";
        String secondScore = "-";
        String secondGrade = "-";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"registrar".equals(session.getAttribute("role"))) {
            response.sendRedirect("registrar_login.html");
            return;
        }

        String type = request.getParameter("type");

        if ("search".equals(type)) {
            handleStudentSearch(request, response);
            return;
        }

        String studentId = request.getParameter("studentId");
        if ((studentId == null || studentId.isEmpty()) && !"summary".equals(type)) {
            response.getWriter().println("Missing student ID");
            return;
        }

        if ("summary".equals(type)) {
            generatePdfCertificate(type, null, response);
        } else {
            Connection conn = null;
            try {
                conn = DBConnection.getConnection();

                String studentSql =
                        "SELECT first_name, last_name, grade_level, section FROM students WHERE student_id=?";
                PreparedStatement ps = conn.prepareStatement(studentSql);
                ps.setString(1, studentId);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    response.getWriter().println("Student not found.");
                    return;
                }
                Student s = new Student();
                s.id = studentId;
                s.firstName = rs.getString("first_name");
                s.lastName = rs.getString("last_name");
                s.gradeLevel = rs.getInt("grade_level");
                s.section = rs.getString("section");
                rs.close();
                ps.close();

                // completion: only Grade 12 and <= 4 F grades
                if ("completion".equals(type)) {
                    if (s.gradeLevel != 12) {
                        response.getWriter().println("Completion certificate is only for Grade 12 students.");
                        return;
                    }
                    int fCount = countFGradesForStudent(s);
                    if (fCount > 4) {
                        response.getWriter().println(
                            "Student is not eligible for completion certificate (F grades: " + fCount + ").");
                        return;
                    }
                }

                generatePdfCertificate(type, s, response);

            } catch (SQLException e) {
                e.printStackTrace();
                response.getWriter().println("Database error: " + e.getMessage());
            } finally {
                DBConnection.closeConnection(conn, null, null);
            }
        }
    }

    private void handleStudentSearch(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String studentId = request.getParameter("studentId");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String gradeLevel = request.getParameter("gradeLevel");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            StringBuilder sql = new StringBuilder(
                "SELECT student_id, first_name, last_name, grade_level, section FROM students WHERE 1=1");
            if (studentId != null && !studentId.isEmpty())
                sql.append(" AND student_id LIKE ?");
            if (firstName != null && !firstName.isEmpty())
                sql.append(" AND first_name LIKE ?");
            if (lastName != null && !lastName.isEmpty())
                sql.append(" AND last_name LIKE ?");
            if (gradeLevel != null && !gradeLevel.isEmpty())
                sql.append(" AND grade_level = ?");
            sql.append(" ORDER BY last_name, first_name");

            ps = conn.prepareStatement(sql.toString());
            int idx = 1;
            if (studentId != null && !studentId.isEmpty())
                ps.setString(idx++, "%" + studentId + "%");
            if (firstName != null && !firstName.isEmpty())
                ps.setString(idx++, "%" + firstName + "%");
            if (lastName != null && !lastName.isEmpty())
                ps.setString(idx++, "%" + lastName + "%");
            if (gradeLevel != null && !gradeLevel.isEmpty())
                ps.setInt(idx++, Integer.parseInt(gradeLevel));

            rs = ps.executeQuery();

            out.println("<table class='student-results-table'>");
            out.println("<tr><th>ID</th><th>Name</th><th>Grade</th><th>Section</th><th>Action</th></tr>");
            while (rs.next()) {
                String sid = rs.getString("student_id");
                String fname = rs.getString("first_name");
                String lname = rs.getString("last_name");
                int grade = rs.getInt("grade_level");
                String section = rs.getString("section");
                out.println("<tr>");
                out.println("<td>" + sid + "</td>");
                out.println("<td>" + fname + " " + lname + "</td>");
                out.println("<td>" + grade + "</td>");
                out.println("<td>" + section + "</td>");
                out.println("<td><button class='generate-btn' onclick='generateCertificate(\"" + sid + "\")'>Generate</button></td>");
                out.println("</tr>");
            }
            out.println("</table>");
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<p class='error'>Database error</p>");
        } finally {
            DBConnection.closeConnection(conn, ps, rs);
        }
    }

    // count F grades across subjects for current grade DB
    private int countFGradesForStudent(Student s) throws SQLException {
        int fCount = 0;

        String gradeDb = "grade" + s.gradeLevel + "_db";
        String gradeDbUrl = "jdbc:mysql://localhost:3306/" + gradeDb;
        String dbUser = "root";
        String dbPass = "";

        try (Connection gradeConn = DriverManager.getConnection(gradeDbUrl, dbUser, dbPass)) {
            for (String subject : SUBJECTS) {
                String sql = "SELECT grade_letter FROM " + subject +
                             " WHERE student_id=? AND academic_year='2025-2026'";
                try (PreparedStatement ps = gradeConn.prepareStatement(sql)) {
                    ps.setString(1, s.id);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String gradeLetter = rs.getString("grade_letter");
                            if ("F".equalsIgnoreCase(gradeLetter)) {
                                fCount++;
                            }
                        }
                    }
                }
            }
        }
        return fCount;
    }

    // load transcript rows for current grade
    private java.util.List<TranscriptRow> loadTranscriptForStudent(Student s) throws SQLException {
        java.util.List<TranscriptRow> rows = new java.util.ArrayList<>();

        String gradeDb = "grade" + s.gradeLevel + "_db";
        String gradeDbUrl = "jdbc:mysql://localhost:3306/" + gradeDb;
        String dbUser = "root";
        String dbPass = "";

        try (Connection gradeConn = DriverManager.getConnection(gradeDbUrl, dbUser, dbPass)) {
            for (String subject : SUBJECTS) {
                TranscriptRow row = new TranscriptRow();
                row.subject = subject.substring(0,1).toUpperCase() + subject.substring(1);

                String sql = "SELECT semester, total_score, grade_letter FROM " + subject +
                             " WHERE student_id=? AND academic_year='2025-2026'";
                try (PreparedStatement ps = gradeConn.prepareStatement(sql)) {
                    ps.setString(1, s.id);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String sem = rs.getString("semester");
                            double score = rs.getDouble("total_score");
                            String letter = rs.getString("grade_letter");
                            if ("First".equalsIgnoreCase(sem)) {
                                row.firstScore = String.valueOf(score);
                                row.firstGrade = letter;
                            } else if ("Second".equalsIgnoreCase(sem)) {
                                row.secondScore = String.valueOf(score);
                                row.secondGrade = letter;
                            }
                        }
                    }
                }
                rows.add(row);
            }
        }
        return rows;
    }

    // modern certificate PDF with transcript table when type=transcript
    private void generatePdfCertificate(String type, Student s, HttpServletResponse response) throws IOException {
        String title = getCertificateTitle(type);
        String today = new SimpleDateFormat("MMMM dd, yyyy").format(new Date());
        String schoolName = "Bense Secondary High School";

        String mainLine;
        if ("enrollment".equals(type)) {
            mainLine = "This certifies that the above student is officially enrolled for the current academic year.";
        } else if ("completion".equals(type)) {
            mainLine = "This certifies that the above student has successfully completed Grade 12 and is eligible for graduation.";
        } else if ("transcript".equals(type)) {
            mainLine = "The following table shows the academic record for the current grade.";
        } else if ("summary".equals(type)) {
            mainLine = "Summary of active student registrations by grade level.";
        } else {
            mainLine = "This document is issued by Bense Secondary High School.";
        }

        java.util.List<TranscriptRow> transcriptRows = java.util.Collections.emptyList();
        if ("transcript".equals(type) && s != null) {
            try {
                transcriptRows = loadTranscriptForStudent(s);
            } catch (SQLException e) {
                throw new IOException("Error loading transcript: " + e.getMessage(), e);
            }
        }

        String fullName = (s != null) ? (s.firstName + " " + s.lastName) : "";
        int centerX = 297;

        StringBuilder content = new StringBuilder();

        // Light background
        content.append("0.90 0.94 1 rg\n");
        content.append("0 0 595 842 re f\n");
        content.append("0 0 0 RG 1 w\n");

        // Outer border
        content.append("0.25 0.35 0.70 RG 2 w\n");
        content.append("20 20 555 802 re S\n");

        // Left and right accents
        content.append("0.06 0.18 0.40 rg\n");
        content.append("20 842 m 20 600 l 120 842 l f\n");
        content.append("0.95 0.80 0.25 rg\n");
        content.append("20 600 m 20 842 l 80 842 l f\n");

        content.append("0.06 0.18 0.40 rg\n");
        content.append("575 842 m 475 842 l 575 600 l f\n");
        content.append("0.95 0.80 0.25 rg\n");
        content.append("575 600 m 515 842 l 575 842 l f\n");

        content.append("0 0 0 rg\n0 0 0 RG 1 w\n");

        // Header text
        content.append("BT\n/F1 26 Tf\n");
        content.append(centerX - 140).append(" 760 Td\n(")
               .append(escapePdf("CERTIFICATE")).append(") Tj\nET\n");

        content.append("BT\n/F1 14 Tf\n");
        content.append(centerX - 120).append(" 735 Td\n(")
               .append(escapePdf("OF " + title.toUpperCase())).append(") Tj\nET\n");

        content.append("BT\n/F1 12 Tf\n");
        content.append(centerX - 140).append(" 710 Td\n(")
               .append(escapePdf("Issued by " + schoolName)).append(") Tj\nET\n");

        content.append("0.6 0.6 0.6 RG 1 w\n");
        content.append("110 700 m 485 700 l S\n");
        content.append("0 0 0 RG\n");

        int y = 675;

        if (!"summary".equals(type) && s != null) {
            content.append("BT\n/F1 11 Tf\n");
            content.append(centerX - 160).append(" ").append(y).append(" Td\n(")
                   .append(escapePdf("This certificate is proudly presented to")).append(") Tj\nET\n");
            y -= 28;

            content.append("BT\n/F1 20 Tf\n");
            content.append(centerX - 150).append(" ").append(y).append(" Td\n(")
                   .append(escapePdf(fullName)).append(") Tj\nET\n");
            y -= 10;

            content.append("0.6 0.6 0.6 RG 1 w\n");
            content.append("150 ").append(y - 4).append(" m 445 ").append(y - 4).append(" l S\n");
            content.append("0 0 0 RG\n");
            y -= 35;

            content.append("BT\n/F1 11 Tf\n");
            content.append(120).append(" ").append(y).append(" Td\n(")
                   .append(escapePdf("Student ID: " + s.id)).append(") Tj\nET\n");
            y -= 18;

            content.append("BT\n/F1 11 Tf\n");
            content.append(120).append(" ").append(y).append(" Td\n(")
                   .append(escapePdf("Grade: " + s.gradeLevel + "   Section: " + s.section)).append(") Tj\nET\n");
            y -= 30;
        }

        // Main line
        content.append("BT\n/F1 11 Tf\n");
        content.append(80).append(" ").append(y).append(" Td\n(")
               .append(escapePdf(mainLine)).append(") Tj\nET\n");
        y -= 30;

        // Transcript table
        if ("transcript".equals(type) && s != null) {
            int tableTop = y;
            int leftX = 60;
            int colSubject = leftX;
            int colFirst = leftX + 170;
            int colSecond = leftX + 290;
            int colTotal = leftX + 410;

            // header row
            content.append("0.85 0.90 1 rg\n0.4 0.5 0.9 RG 1 w\n");
            content.append(leftX).append(" ").append(tableTop - 5)
                   .append(" ").append(475).append(" 22 re B\n");
            content.append("0 0 0 rg\n");

            int textY = tableTop + 2;

            content.append("BT\n/F1 10 Tf\n");
            content.append(colSubject).append(" ").append(textY).append(" Td\n(Subject) Tj\nET\n");

            content.append("BT\n/F1 10 Tf\n");
            content.append(colFirst).append(" ").append(textY).append(" Td\n(First Sem) Tj\nET\n");

            content.append("BT\n/F1 10 Tf\n");
            content.append(colSecond).append(" ").append(textY).append(" Td\n(Second Sem) Tj\nET\n");

            content.append("BT\n/F1 10 Tf\n");
            content.append(colTotal).append(" ").append(textY).append(" Td\n(Total) Tj\nET\n");

            int rowY = tableTop - 20;

            for (TranscriptRow row : transcriptRows) {
                if (rowY < 120) break;

                String totalText = "-";
                try {
                    double f = "-".equals(row.firstScore) ? 0 : Double.parseDouble(row.firstScore);
                    double s2 = "-".equals(row.secondScore) ? 0 : Double.parseDouble(row.secondScore);
                    if (!"-".equals(row.firstScore) || !"-".equals(row.secondScore)) {
                        totalText = String.valueOf(f + s2);
                    }
                } catch (NumberFormatException ignored) {}

                String firstCombined = row.firstScore + " (" + row.firstGrade + ")";
                String secondCombined = row.secondScore + " (" + row.secondGrade + ")";

                content.append("0.9 0.9 0.95 rg\n0.8 0.8 0.9 RG 0.5 w\n");
                content.append(leftX).append(" ").append(rowY - 3)
                       .append(" ").append(475).append(" 18 re B\n");
                content.append("0 0 0 rg\n");

                int ty = rowY;

                content.append("BT\n/F1 9 Tf\n");
                content.append(colSubject).append(" ").append(ty).append(" Td\n(")
                       .append(escapePdf(row.subject)).append(") Tj\nET\n");

                content.append("BT\n/F1 9 Tf\n");
                content.append(colFirst).append(" ").append(ty).append(" Td\n(")
                       .append(escapePdf(firstCombined)).append(") Tj\nET\n");

                content.append("BT\n/F1 9 Tf\n");
                content.append(colSecond).append(" ").append(ty).append(" Td\n(")
                       .append(escapePdf(secondCombined)).append(") Tj\nET\n");

                content.append("BT\n/F1 9 Tf\n");
                content.append(colTotal).append(" ").append(ty).append(" Td\n(")
                       .append(escapePdf(totalText)).append(") Tj\nET\n");

                rowY -= 20;
            }

            y = rowY - 20;
        }

        // Date and signatures
        content.append("BT\n/F1 10 Tf\n");
        content.append(80).append(" 120 Td\n(")
               .append(escapePdf("Date: " + today)).append(") Tj\nET\n");

        content.append("BT\n/F1 10 Tf\n");
        content.append(120).append(" 80 Td\n(Signature) Tj\nET\n");
        content.append("BT\n/F1 10 Tf\n");
        content.append(380).append(" 80 Td\n(Signature) Tj\nET\n");

        content.append("0.2 0.2 0.2 RG 1 w\n");
        content.append("80 95 m 240 95 l S\n");
        content.append("340 95 m 500 95 l S\n");
        content.append("0 0 0 RG\n");

        byte[] contentBytes = content.toString().getBytes("UTF-8");
        int contentLength = contentBytes.length;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write("%PDF-1.4\n".getBytes("UTF-8"));

        int obj1 = baos.size();
        baos.write("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n".getBytes("UTF-8"));

        int obj2 = baos.size();
        baos.write("2 0 obj\n<< /Type /Pages /Count 1 /Kids [3 0 R] >>\nendobj\n".getBytes("UTF-8"));

        int obj3 = baos.size();
        baos.write("3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>\nendobj\n".getBytes("UTF-8"));

        int obj4 = baos.size();
        baos.write("4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Times-Roman >>\nendobj\n".getBytes("UTF-8"));

        int obj5 = baos.size();
        baos.write(("5 0 obj\n<< /Length " + contentLength + " >>\nstream\n").getBytes("UTF-8"));
        baos.write(contentBytes);
        baos.write("\nendstream\nendobj\n".getBytes("UTF-8"));

        int xrefPos = baos.size();
        StringBuilder xref = new StringBuilder();
        xref.append("xref\n0 6\n");
        xref.append("0000000000 65535 f \n");
        xref.append(String.format("%010d 00000 n \n", obj1));
        xref.append(String.format("%010d 00000 n \n", obj2));
        xref.append(String.format("%010d 00000 n \n", obj3));
        xref.append(String.format("%010d 00000 n \n", obj4));
        xref.append(String.format("%010d 00000 n \n", obj5));
        baos.write(xref.toString().getBytes("UTF-8"));

        String trailer = "trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n" +
                xrefPos + "\n%%EOF";
        baos.write(trailer.getBytes("UTF-8"));

        byte[] pdfBytes = baos.toByteArray();

        response.setContentType("application/pdf");
        String fileName = type + (s != null ? "_" + s.id : "_summary") + ".pdf";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentLength(pdfBytes.length);

        OutputStream out = response.getOutputStream();
        out.write(pdfBytes);
        out.flush();
    }

    private String getCertificateTitle(String type) {
        switch (type) {
            case "enrollment": return "Enrollment Certificate";
            case "completion": return "Completion Certificate";
            case "transcript": return "Academic Transcript";
            case "summary": return "Summary Report";
            default: return "Certificate";
        }
    }

    private String escapePdf(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }
}
