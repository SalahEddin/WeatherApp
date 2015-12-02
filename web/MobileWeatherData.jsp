<%-- 
    Document   : MobileWeatherData
    Created on : Dec 2, 2015, 11:42:10 PM
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
        <style>
            img{
                width:100%;
                height:auto;
              }
        </style>
        <title>Detailed Weather</title>
        <!-- Required meta tags always come first -->
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta http-equiv="x-ua-compatible" content="ie=edge">
        <!-- Bootstrap CSS -->
        <link rel="stylesheet" href="https://cdn.rawgit.com/twbs/bootstrap/v4-dev/dist/css/bootstrap.css">
    </head>
    <body>
        <h1>Detailed Weather</h1>
        <%
            String locId = request.getParameter("id");
            // Connection
            Connection conn = null;
            try{
                // DB Connection
                Class.forName("org.sqlite.JDBC");
                String Path = getServletContext().getRealPath("/WEB-INF/");
                conn = DriverManager.getConnection("jdbc:sqlite:" + Path + "\\WeatherDB.sqlite");
                Statement stmt = conn.createStatement();
                String query = "SELECT * FROM DATA,LOCATION WHERE ID = LOCATION_ID AND LOCATION_ID = "+locId;
                ResultSet rs = stmt.executeQuery(query);
                %>
                <table class= table table-striped>
                    <thead class="thead-inverse">
                        <th>Temperature</th>
                        <th>Rain</th>
                        <th>Cloud</th>
                        <th>Wind</th>
                        <th>Last Update</th>
                    </thead>
                    <tbody>
                <%
                while (rs.next()) {
        %>
                    <tr>
                            <td><%= rs.getString("TEMP") %></td>
                            <td><%= rs.getString("RAIN")%></td>
                            <td><%= rs.getString("CLOUD")%></td>
                            <td><%= rs.getString("WIND")%></td>
                            <td><%= rs.getString("UPDATED")%></td>
                    </tr>
        <%      } %>
                    </tbody>
                </table>
                <!-- Add to Favourites -->
                <div class="col-sm-4">
                    <form name="favForm" action="/WeatherApp/WebAddToFav" method="POST">
                        <input type="hidden" value="<%= locId %>" name="add">
                        <button class="btn btn-secondary" type="submit">Add to Favourites</button>
                    </form>
                </div>
                <!-- Set Homepage -->
                <div class="col-sm-4">
                    <form name="homeForm" action="/WeatherApp/WebAddToFav" method="POST">
                        <input type="hidden" value="<%= locId %>" name="add">
                        <button class="btn btn-secondary" type="submit">Set Homepage</button>
                    </form>
                </div>
        <%
                // list group for quick access to other locations
                query = "SELECT * FROM LOCATION";
                rs = stmt.executeQuery(query);
                %>
                <div class="clearfix"></div>
                <div class="col-sm-4">
                    <h5>Check more locations...</h5>
                    <ul class="list-group">
                        <% while (rs.next()) { %>
                        <a class="list-group-item" href="/WeatherApp/MobileWeatherData?id=<%= rs.getString("ID") %>> <%= rs.getString("ID") %></a>
                        <%}%>
                    </ul>
                </div>
                <%
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
%>
    </body>
</html>
