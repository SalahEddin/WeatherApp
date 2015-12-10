<%-- 
    Document   : WebWeatherFav
    Created on : Nov 16, 2015, 12:31:42 PM
    Author     : Salah
--%>

<%@page import="java.util.logging.Logger"%>
<%@page import="java.util.logging.Level"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.Connection"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%! 
static String getImg(String rain,String cloud){
    if(Integer.parseInt(cloud) < 50){ return "clear.jpg";}
    else if(Integer.parseInt(rain) < 50){ return "cloudy.jpg";}
    else { return "rainy.jpg";}
}
%>
<!DOCTYPE html>
<html>
    <head>
        <style>
            img{
                width:100%;
                height:auto;
              }
        </style>
        <title>Home</title>
        <!-- Required meta tags always come first -->
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta http-equiv="x-ua-compatible" content="ie=edge">
        
        <!-- Bootstrap CSS -->
        <link rel="stylesheet" href="https://cdn.rawgit.com/twbs/bootstrap/v4-dev/dist/css/bootstrap.css">
    </head>
    <body>
        <%
            Cookie[] cookies = null;
            // Get an array of Cookies associated with this domain
            cookies = request.getCookies();
            // Connection
            Connection conn = null;
            try{
                // DB Connection
                Class.forName("org.sqlite.JDBC");
                String Path = getServletContext().getRealPath("/WEB-INF/");
                conn = DriverManager.getConnection("jdbc:sqlite:" + Path + "\\WeatherDB.sqlite");
                Statement stmt = conn.createStatement();
                String query = "SELECT * FROM DATA,LOCATION WHERE ID = LOCATION_ID";
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    boolean isFav = false;
                    String resultId = rs.getString("ID");
                    
                    for (int i = 0; i < cookies.length; i++){
                    // cookie is favourite
                        if(cookies[i].getName().equals(resultId) && cookies[i].getValue().equals(resultId)) {
                            isFav = true;
                            break; // no need to traverse through the rest of the cookies
                        }
                     }
                    if(!isFav) continue; // not a favourite, go to next in result set
        %>
                <div class="col-sm-4">
                    <div class="card">
                        <img class="card-img-top" src="<%= getImg(rs.getString("RAIN"),rs.getString("CLOUD"))%>"/>
                        <div class="card-block">
                            <h4 class="card-title"><%= rs.getString("NAME")%></h4>
                            <p class="card-text"><%= rs.getString("DESC")%></p>
                        </div>
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item">Temperature: <%=rs.getString("TEMP")%></li>
                            <li class="list-group-item">Rain Chance: <%=rs.getString("RAIN")%></li>
                            <li class="list-group-item">Clouds: <%=rs.getString("CLOUD")%></li>
                            <li class="list-group-item">Wind: <%=rs.getString("WIND")%></li>
                        </ul>
                    </div>
                </div>
                <%
                }
            }
            catch(SQLException e){System.out.print(e.toString());}
            catch(Exception e){System.out.print(e.toString());}
            finally{
                try {
                    conn.close();
                } catch (SQLException ex) { }
            }

        %>       
        <!-- jQuery first, then Bootstrap JS. -->
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <script src="https://cdn.rawgit.com/twbs/bootstrap/v4-dev/dist/js/bootstrap.js"></script>
    </body>
</html>
