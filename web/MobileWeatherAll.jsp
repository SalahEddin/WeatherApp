<%-- 
    Document   : MobileWeatherAll
    Created on : Dec 2, 2015, 11:16:35 PM
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
        <title>All Weather</title>
        <!-- Required meta tags always come first -->
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta http-equiv="x-ua-compatible" content="ie=edge">
        <!-- Bootstrap CSS -->
        <link rel="stylesheet" href="https://cdn.rawgit.com/twbs/bootstrap/v4-dev/dist/css/bootstrap.css">
    </head>
    <body>
        <h1>All Weather Locations</h1>
        <%
            // Connection
            Connection conn = null;
            try{
                // DB Connection
                Class.forName("org.sqlite.JDBC");
                String Path = getServletContext().getRealPath("/WEB-INF/");
                conn = DriverManager.getConnection("jdbc:sqlite:" + Path + "\\WeatherDB.sqlite");
                Statement stmt = conn.createStatement();
                String query = "SELECT * FROM LOCATION";
                ResultSet rs = stmt.executeQuery(query);
                %>
                <table class= table table-striped>
                    <thead class="thead-inverse">
                        <th>Lat/ Lng</th> <!-- combined to fit on a mobile screen -->
                        <th>Name</th>
                        <th>Description</th>
                        <th>More details</th>
                    </thead>
                    <tbody>
                <%
                while (rs.next()) {
        %>
                        <tr>
                            <td><%= rs.getString("LAT") %>,<%= rs.getString("LNG")%></td>
                            <td><%= rs.getString("NAME")%></td>
                            <td><%= rs.getString("DESC")%></td>
                            <td>
                                <form name="form<%= rs.getString("ID")%>" action="/WeatherApp/MobileWeatherData" method=POST>
                                    <input type=hidden value="<%= rs.getString("ID")%>" name="id" />
                                    <button class="btn btn-primary" type="submit">details</button>
                                </form>
                            </td>
                        </tr>             
        <%
                }%>
                    </tbody>
                </table>
                                    <%
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
