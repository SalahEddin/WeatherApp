/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Salah
 */
public class WebWeatherDebug extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet WebWeatherDebug</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet WebWeatherDebug at " + request.getContextPath() + "</h1>");
            
            // Connection
            Connection conn = null;
            try{
                Class.forName("org.sqlite.JDBC");
                String Path = getServletContext().getRealPath("/WEB-INF/");
                conn = DriverManager.getConnection("jdbc:sqlite:" + Path + "\\WeatherDB.sqlite");
                Statement stmt = conn.createStatement();
                String query = "SELECT * FROM LOCATION";
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    out.println("<p>");
                    out.println(rs.getString("NAME"));
                    out.println("</p>");
                }
            }
            catch(SQLException e){System.out.print(e.toString());}
            catch(Exception e){System.out.print(e.toString());}
            finally{
                try {
                    conn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(WebWeatherDebug.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            out.println(
            "<table style=\"width:100%\">\n" +
            "  <tr>\n" +
            "    <td>Jill</td>\n" +
            "    <td>Smith</td>		\n" +
            "    <td>50</td>\n" +
            "  </tr>\n" +
            "  <tr>\n" +
            "    <td>Eve</td>\n" +
            "    <td>Jackson</td>		\n" +
            "    <td>94</td>\n" +
            "  </tr>\n" +
            "</table>"
            );
            out.println("</body>");
            out.println("</html>");
        }
        
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        //processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
