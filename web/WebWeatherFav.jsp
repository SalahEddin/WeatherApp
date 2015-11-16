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
<!DOCTYPE html>
<html>
    <head>
        <title>Home</title>
        <!-- Required meta tags always come first -->
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta http-equiv="x-ua-compatible" content="ie=edge">
        
        <!-- Bootstrap CSS -->
        <link rel="stylesheet" href="https://cdn.rawgit.com/twbs/bootstrap/v4-dev/dist/css/bootstrap.css">
    </head>
    <body>
        <h1>Hello World!</h1>
        <% 
            // Connection
            Connection conn = null;
            try{
                // DB Connection
                Class.forName("org.sqlite.JDBC");
                String Path = getServletContext().getRealPath("/WEB-INF/");
                conn = DriverManager.getConnection("jdbc:sqlite:" + Path + "\\WeatherDB.sqlite");
                Statement stmt = conn.createStatement();
                String query = "SELECT * FROM DATA,LOCATION WHERE ID = LOCATION_ID AND FAV = \"TRUE\"";
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {%>
                <div class="col-sm-4">
                    <div class="card">
                        <img class="card-img-top" data-src="holder.js/100%x180/?text=Image cap" alt="Card image cap">
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
                        <div class="card-block">
                            <a href="#" class="card-link">Card link</a>
                            <a href="#" class="card-link">Another link</a>
                        </div>
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
