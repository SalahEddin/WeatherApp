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
public class WebWeatherRandomise extends HttpServlet {
    
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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            Connection conn = null;
            try{
                Class.forName("org.sqlite.JDBC");
                String Path = getServletContext().getRealPath("/WEB-INF/");
                conn = DriverManager.getConnection("jdbc:sqlite:" + Path + "\\WeatherDB.sqlite");
                Statement stmt = conn.createStatement();
                String query = "UPDATE DATA "
                        + "SET TEMP =   ABS(RANDOM() % 50)"
                        + "SET RAIN =   ABS(RANDOM() % 100)"
                        + "SET CLOUD =  ABS(RANDOM() % 100)"
                        + "SET WIND =   ABS(RANDOM() % 30)"
                        + "SET UPDATED = CURRENT_TIMESTAMP";
                stmt.executeQuery(query);
                response.sendRedirect("/WeatherApp/WebWeatherDebug");
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
        // processRequest(request, response);
        
        response.setContentType("text/plain");
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            String Path = getServletContext().getRealPath("/WEB-INF/");
            conn = DriverManager.getConnection("jdbc:sqlite:" + Path + "\\WeatherDB.sqlite");
            Statement stmt = conn.createStatement();
            String query = "UPDATE DATA "
                    + "SET TEMP =   ABS(RANDOM() % 50)-10"
                    + ", RAIN =   ABS(RANDOM() % 100)"
                    + ", CLOUD =  ABS(RANDOM() % 100)"
                    + ", WIND =   ABS(RANDOM() % 30)"
                    + ", UPDATED = CURRENT_TIMESTAMP";
            stmt.execute(query);
            response.sendRedirect("/WeatherApp/WebWeatherDebug");
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ignored) {
            }
        }
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
