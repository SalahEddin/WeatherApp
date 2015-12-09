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
            out.println("<head>\n" +
                    "    <title>Location Selector</title>\n" +
                    "    <!-- Required meta tags always come first -->\n" +
                    "    <meta charset=\"utf-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                    "    <meta http-equiv=\"x-ua-compatible\" content=\"ie=edge\">\n" +
                    "\n" +
                    "    <!-- Bootstrap CSS -->\n" +
                    "    <link rel=\"stylesheet\" href=\"https://cdn.rawgit.com/twbs/bootstrap/v4-dev/dist/css/bootstrap.css\">\n" +
                    "  </head>");
            out.println("<body>"
                    + "<div class=\"container-fluid\">");            
            // Connection
            Connection conn = null;
            try{
                Class.forName("org.sqlite.JDBC");
                String Path = getServletContext().getRealPath("/WEB-INF/");
                conn = DriverManager.getConnection("jdbc:sqlite:" + Path + "\\WeatherDB.sqlite");
                Statement stmt = conn.createStatement();
                String query = "SELECT * FROM LOCATION";
                ResultSet rs = stmt.executeQuery(query);
                out.println(
                        "<table class=\"table table-striped\">\n"+
                                "<thead class=\"thead-inverse\">\n"+
                                "<tr>\n" +
                                "<th>Latitude</th>\n" +
                                "<th>Longitude</th>\n" +
                                "<th>Name</th>\n" +
                                "<th>Description</th>\n" +
                                "<th>More details</th>\n" +
                                "</tr>\n"+
                                "</thead>\n"+
                                "<tbody>"
                );
                while (rs.next()) {
                    out.println(
                            "  <tr>\n" +
                                    "<td>"+rs.getString("LAT")+"</td>\n" +
                                    "<td>"+rs.getString("LNG")+"</td>\n" +
                                    "<td>"+rs.getString("NAME")+"</td>\n" +
                                    "<td>"+rs.getString("DESC")+"</td>\n" +
                                    "<td>\n" +
                                    "<form name=\"form"+rs.getString("ID")+"\" action=\"/WeatherApp/WebWeatherData\" method=\"POST\">\n" +
                                    "<input type=\"hidden\" value=\""+rs.getString("ID")+"\" name=\"id\" />\n"+
                                    "<button class=\"btn btn-primary\" type=\"submit\" value="+rs.getString("ID")+">details</button>\n"+
                                    "</form>\n" +
                                    "</td>\n"+
                                    "  </tr>"
                    );
                }
                out.println("</tbody>\n"
                        + "</table>");
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
            out.println("<form action=\"/WeatherApp/WebWeatherRandomise\" method=\"POST\" class=\"form-inline navbar-form pull-right\">\n" +
"                           <button class=\"btn btn-secondary\" type=\"submit\">Randomise Weather Values</button>\n" +
"                       </form>");
            out.println("</div>\n"
                    + "<!-- jQuery first, then Bootstrap JS. -->\n" +
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
