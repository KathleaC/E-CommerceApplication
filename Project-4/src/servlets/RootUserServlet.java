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
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Servlet implementation class RootUserServlet
 */

public class RootUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    public RootUserServlet() {
        super();
    }
    
    // doGet for direct testing Method
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Root User Servlet</h1>");
        out.println("<p>This servlet only accepts POST requests from the root form.</p>");
        out.println("</body></html>");
    }

    // doPost Method
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String sqlCommand = request.getParameter("sqlCommand");
        HttpSession session = request.getSession();
        
        // Check if user is authenticated as root
        String username = (String) session.getAttribute("username");
        if(username == null || !"root".equals(username)) {
            response.sendRedirect("authentication.html");
            return;
        }
        
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String resultsHtml = "";
        boolean businessLogicTriggered = false;
        int suppliersUpdated = 0;
        
        try {
            // Load root properties file
            Properties props = new Properties();
            InputStream input = getServletContext().getResourceAsStream("/WEB-INF/conf/root.properties");
            
            if (input == null) {
                resultsHtml = "<p style='color: red;'>Error: Cannot find root.properties</p>";
                request.setAttribute("resultsHtml", resultsHtml);
                request.getRequestDispatcher("rootHome.jsp").forward(request, response);
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
            String upperCommand = sqlCommand.trim().toUpperCase();
            if(upperCommand.startsWith("SELECT")) {
                // It's a query - execute and display results
                resultSet = statement.executeQuery(sqlCommand);
                resultsHtml = generateResultsHtml(resultSet, sqlCommand);
            } // In doPost method - replace the entire else block (starting around line 72):
            else {
            	// It's an update command
            	boolean affectsShipments = checkIfAffectsShipments(sqlCommand);
            	int rowsAffected = 0;
            
            	if(affectsShipments) {
            		// Check if this is an INSERT with quantity >= 100 OR any UPDATE that affects quantity
            		boolean shouldCheckBusinessLogic = shouldTriggerBusinessLogicCheck(sqlCommand);
                
            		// Execute the original command
            		rowsAffected = statement.executeUpdate(sqlCommand);
            		resultsHtml = generateUpdateHtml(rowsAffected, sqlCommand);
                
            		if (shouldCheckBusinessLogic) {
            			businessLogicTriggered = true;
                    
            			// Update supplier status for all suppliers with any shipment >= 100
            			suppliersUpdated = updateSupplierStatus(connection);
                    
            			// Add business logic message to results
            			resultsHtml += "<p style='color: blue;'><strong>Business Logic Detected! - Updating Supplier Status</strong></p>";
                    
            			if (suppliersUpdated > 0) {
            				resultsHtml += "<p style='color: blue;'>Business Logic updated " + suppliersUpdated + " supplier status marks.</p>";
            			} else {
            				resultsHtml += "<p style='color: blue;'>Business Logic updated 0 supplier status marks.</p>";
            			}
            		} else {
            			resultsHtml += "<p style='color: green;'><strong>Business Logic Not Triggered!</strong></p>";
            		}
            	} else {
            		// Command doesn't affect shipments table
            		rowsAffected = statement.executeUpdate(sqlCommand);
            		resultsHtml = generateUpdateHtml(rowsAffected, sqlCommand);
            		resultsHtml += "<p style='color: green;'><strong>Business Logic Not Triggered!</strong></p>";
            	}
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
        request.getRequestDispatcher("rootHome.jsp").forward(request, response);
    }
    
    /* HELPER METHODS BEGINS HERE */

    private boolean shouldTriggerBusinessLogicCheck(String sqlCommand) {
        String upperCommand = sqlCommand.toUpperCase().trim();
        
        // INSERT with quantity >= 100
        if (upperCommand.startsWith("INSERT") && upperCommand.contains("SHIPMENTS")) {
            int quantity = extractQuantityFromInsert(sqlCommand);
            return (quantity >= 100);
        }
        
        // ANY UPDATE that affects quantity in shipments
        if (upperCommand.startsWith("UPDATE") && upperCommand.contains("SHIPMENTS") && upperCommand.contains("QUANTITY")) {
            return true;
        }
        
        return false;
    }
    
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
        String upperCommand = sqlCommand.trim().toUpperCase();
        String resultText = "";
        
        if (upperCommand.startsWith("INSERT")) {
            resultText = "<p><strong>Execution Results:</strong> The statement executed successfully.</p>" +
                         "<p><strong>" + rowsAffected + " row(s) affected.</strong></p>";
        } else if (upperCommand.startsWith("UPDATE")) {
            resultText = "<p><strong>Execution Results</strong> The statement executed successfully.</p>" +
                         "<p><strong>A total of " + rowsAffected + " row(s) were updated.</strong></p>";
        } else if (upperCommand.startsWith("DELETE")) {
            resultText = "<p><strong>Execution Results</strong> The statement executed successfully.</p>" +
                         "<p><strong>" + rowsAffected + " row(s) affected.</strong></p>";
        } else {
            resultText = "<p><strong>Execution Results</strong> The statement executed successfully.</p>" +
                         "<p><strong>" + rowsAffected + " row(s) affected.</strong></p>";
        }
        
        return "<p><strong>Command executed:</strong> " + sqlCommand + "</p>" + resultText;
    }

    // generateErrorHtml Method
    private String generateErrorHtml(String errorMessage, String sqlCommand) {
        return "<p><strong>Command attempted:</strong> " + sqlCommand + "</p>" +
               "<p style='color: red;'><strong>Error executing the SQL statement:</strong> " + errorMessage + "</p>";
    }
    
    // checkIfAffectsShipments Method
    private boolean checkIfAffectsShipments(String sqlCommand) {
        String upperCommand = sqlCommand.toUpperCase();
        return upperCommand.contains("SHIPMENTS") && 
               (upperCommand.contains("INSERT") || upperCommand.contains("UPDATE") || upperCommand.contains("DELETE"));
    }

    // Helper method to extract quantity from INSERT statement
    private int extractQuantityFromInsert(String sqlCommand) {
        try {
            // Multiple patterns to handle different SQL formats
            Pattern[] patterns = {
                Pattern.compile("VALUES\\s*\\([^,]+,[^,]+,[^,]+,\\s*(\\d+)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("\\)\\s*VALUES?\\s*\\([^,]+,[^,]+,[^,]+,\\s*(\\d+)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("VALUES\\s*\\([^,]+, [^,]+, [^,]+, (\\d+)\\)", Pattern.CASE_INSENSITIVE)
            };
            
            for (int i = 0; i < patterns.length; i++) {
                Matcher matcher = patterns[i].matcher(sqlCommand);
                if (matcher.find()) {
                    int quantity = Integer.parseInt(matcher.group(1));
                    return quantity;
                }
            }
        } catch (Exception e) {
            System.out.println("Error parsing quantity from INSERT: " + e.getMessage());
        }
        return 0; // Default to no trigger
    }

    // updateSupplierStatus Method - NON-BONUS VERSION (affects ALL suppliers with ANY shipment >= 100)
    private int updateSupplierStatus(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
    
        // Update status for all suppliers who have any shipment with quantity >= 100
        String updateSql = "UPDATE suppliers SET status = status + 5 " +
                    "WHERE snum IN (SELECT DISTINCT snum FROM shipments WHERE quantity >= 100)";
    
        int rowsAffected = stmt.executeUpdate(updateSql);
        stmt.close();
    
        return rowsAffected;
    }
    
    /* HELPER METHODS ENDS */
}