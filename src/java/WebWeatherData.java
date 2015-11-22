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
import java.text.SimpleDateFormat;
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
public class WebWeatherData extends HttpServlet {
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
            // id of the location
            String locID = request.getParameter("id");
            /* Header imports Bootstrap */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>\n" +
                    "    <title>Details</title>\n" +
                    "    <meta charset=\"utf-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                    "    <meta http-equiv=\"x-ua-compatible\" content=\"ie=edge\">\n" +
                    "    <link rel=\"stylesheet\" href=\"https://cdn.rawgit.com/twbs/bootstrap/v4-dev/dist/css/bootstrap.css\">\n" +
                    "  </head>");
            // define reponsive page
            out.println("<body>"
                    + "<div class=\"container-fluid\">");
            out.println("<h1>Servlet WebWeatherData at " + locID + "</h1>");
            // Connection
            Connection conn = null;
            try{
                // DB Connection
                Class.forName("org.sqlite.JDBC");
                String Path = getServletContext().getRealPath("/WEB-INF/");
                conn = DriverManager.getConnection("jdbc:sqlite:" + Path + "\\WeatherDB.sqlite");
                Statement stmt = conn.createStatement();
                String query = "SELECT * FROM DATA,LOCATION WHERE ID = LOCATION_ID AND LOCATION_ID = "+locID;
                ResultSet rs = stmt.executeQuery(query);
                out.println(
                        "<table class=\"table table-striped\">\n"+
                                "<thead class=\"thead-inverse\">\n"+
                                "<tr>\n" +
                                "<th>Temprature</th>\n" +
                                "<th>Rain</th>\n" +
                                "<th>Clouds</th>\n" +
                                "<th>Wind</th>\n" +
                                "<th>Last Update</th>\n" +
                                "</tr>\n"+
                                "</thead>\n"+
                                "<tbody>"
                );
                while (rs.next()) {
                    out.println(
                            "  <tr>\n" +
                                    "<td>"+rs.getString("TEMP")+"</td>\n" +
                                    "<td>"+rs.getString("RAIN")+"</td>\n" +
                                    "<td>"+rs.getString("CLOUD")+"</td>\n" +
                                    "<td>"+rs.getString("WIND")+"</td>\n" +
                                    "<td>"+rs.getString("UPDATED")+"</td>\n" +
                                    /*"<td>"+new SimpleDateFormat("yyyy‐MM‐dd kk:mm:ss").format(rs.getDate("UPDATED"))+"</td>\n" +*/
                                    "  </tr>"
                    );
                }
                out.println("</tbody>\n"
                        + "</table>");
                // add to favourites
                out.println("<form name=\"favForm\" action=\"/WeatherApp/WebAddToFav\" method=\"POST\">\n" +
                        "<input type=\"hidden\" value=\""+locID+"\" name=\"add\" />\n"+
                        "<button class=\"btn btn-primary\" type=\"submit\">Add to favourites</button>\n"+
                        "</form>");
                // add new home page
                out.println("<form name=\"homeForm\" action=\"/WeatherApp/WebAddToFav\" method=\"POST\">\n" +
                        "<input type=\"hidden\" value=\""+locID+"\" name=\"home\" />\n"+
                        "<button class=\"btn btn-primary\" type=\"submit\">Set as Home</button>\n"+
                        "</form>");
                // list group for quick access to other locations
                query = "SELECT * FROM LOCATION";
                rs = stmt.executeQuery(query);
                out.println("<div class=\"clearfix\"></div>\n" +
                        "        <div class=\"col-sm-2\">\n" +
                        "            <h5>Check more locations...</h5>\n" +
                        "            <ul class=\"list-group\">");
                while (rs.next()) {
                    out.println("<a href=\"/WeatherApp/WebWeatherData?id="+rs.getString("ID")+"\" class=\"list-group-item\">"+rs.getString("Name")+"</a>");
                }
                out.println(
                        "</ul>\n" +
                                "        </div>");
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
                    "    <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js\"></script>\n" +
                            "    <script src=\"https://cdn.rawgit.com/twbs/bootstrap/v4-dev/dist/js/bootstrap.js\"></script>\n" +
                            "  </body>");
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
        processRequest(request, response);
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