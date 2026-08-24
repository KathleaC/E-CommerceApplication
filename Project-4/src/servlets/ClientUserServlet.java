package servlets;

/* Name: Kathlea Corla
Course: CNT 4714 – Fall 2025 – Project Four
Assignment title: A Three-Tier Distributed Web-Based Application
Date: December 1, 2025
*/

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Servlet implementation class ClientUserServlet
 */

public class ClientUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
 
    public ClientUserServlet() {
        super();
    }
    
	// Add doGet for direct testing Method
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Client User Servlet</h1>");
        out.println("<p>This servlet only accepts POST requests from the client form.</p>");
        out.println("</body></html>");
    }

    // doPost Method
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String sqlCommand = request.getParameter("sqlCommand");
        HttpSession session = request.getSession();
        
        // Check if user is authenticated as client
        String username = (String) session.getAttribute("username");
        if(username == null || !"client".equals(username)) {
            response.sendRedirect("authentication.html");
            return;
        }
        
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String resultsHtml = "";
        
        try {
            // Load client properties file
            Properties props = new Properties();
            InputStream input = getServletContext().getResourceAsStream("/WEB-INF/conf/client.properties");
            
            if (input == null) {
                resultsHtml = "<p style='color: red;'>Error: Cannot find client.properties</p>";
                request.setAttribute("resultsHtml", resultsHtml);
                request.getRequestDispatcher("clientHome.jsp").forward(request, response);
                return;
            }
            
            props.load(input);
            
            // Load MySQL driver and establish connection
            Class.forName(props.getProperty("driver"));
            connection = DriverManager.getConnection(
                    props.getProperty("url"), 
                    props.getProperty("user"), 
                    props.getProperty("password"));
            
            statement = connection.createStatement();
            
            // Check if its a query (SELECT) or update (INSERT/UPDATE/DELETE)
            if(sqlCommand.trim().toUpperCase().startsWith("SELECT")) {
                // Its a query - execute and display results
                resultSet = statement.executeQuery(sqlCommand);
                resultsHtml = generateResultsHtml(resultSet, sqlCommand);
            } else {
                // Its an update command
                int rowsAffected = statement.executeUpdate(sqlCommand);
                resultsHtml = generateUpdateHtml(rowsAffected, sqlCommand);
            }
                    
        } catch(SQLException e) {
            resultsHtml = generateErrorHtml(e.getMessage(), sqlCommand);
        } catch(ClassNotFoundException e) {
            resultsHtml = generateErrorHtml("MySQL driver not found: " + e.getMessage(), sqlCommand);
        } catch (Exception e) {
            resultsHtml = generateErrorHtml("Error: " + e.getMessage(), sqlCommand);
        } finally {
            // Clean up resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        // Forward back to JSP with results
        request.setAttribute("resultsHtml", resultsHtml);
        request.getRequestDispatcher("clientHome.jsp").forward(request, response);
    }

    /* HELPER METHODS BEGINS HERE */
    
    // generateResultsHtml Method
	private String generateResultsHtml(ResultSet resultSet, String sqlCommand) throws SQLException {
	    StringBuilder html = new StringBuilder();
	    
	    html.append("<p><strong>Command executed:</strong> ").append(sqlCommand).append("</p>");
	    html.append("<hr>");
	    
        html.append("<p><strong>Execution Results: </strong></p>");
	    
	    ResultSetMetaData metaData = resultSet.getMetaData();
	    int columnCount = metaData.getColumnCount();
	    
	    html.append("<table border='1' style='border-collapse: collapse;'>");
	    html.append("<tr>");
	    for (int i = 1; i <= columnCount; i++) {
	        html.append("<th>").append(metaData.getColumnName(i)).append("</th>");
	    }
	    html.append("</tr>");
	    
	    int rowCount = 0;
	    while (resultSet.next()) {
	        html.append("<tr>");
	        for (int i = 1; i <= columnCount; i++) {
	            html.append("<td>").append(resultSet.getString(i)).append("</td>");
	        }
	        html.append("</tr>");
	        rowCount++;
	    }
	    html.append("</table>");
	    html.append("<p><strong>").append(rowCount).append(" row(s) returned</strong></p>");
	    
	    return html.toString();
	}
	
	// generateUpdateHtml Method
	private String generateUpdateHtml(int rowsAffected, String sqlCommand) {
	    return "<p><strong>Command executed:</strong> " + sqlCommand + "</p>" +
	           "<p><strong>Execution Results:</strong> The statement executed successfully.</p>" +
	           "<p><strong>" + rowsAffected + " row(s) affected.</strong></p>";
	}

	// generateErrorHtml Method
    private String generateErrorHtml(String errorMessage, String sqlCommand) {
        return "<p><strong>Command attempted:</strong> " + sqlCommand + "</p>" +
               "<p style='color: red;'><strong>Error executing the SQL statement:</strong> " + errorMessage + "</p>";
    }
	
	/* HELPER METHODS ENDS */

}