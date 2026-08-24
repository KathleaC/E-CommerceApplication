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
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Servlet implementation class AccountantUserServlet
 */
public class AccountantUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public AccountantUserServlet() {
        super();
    }
    
    // goGet Method
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Accountant User Servlet</h1>");
        out.println("<p>This servlet only accepts POST requests from the Accountant form.</p>");
        out.println("</body></html>");
	}

	// doPost Method
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        
        // Check if user is authenticated as theaccountant
        if(username == null || !"theaccountant".equals(username)) {
            response.sendRedirect("authentication.html");
            return;
        }
        
        String reportType = request.getParameter("reportType");
        String resultsHtml = "";
        
        Connection connection = null;
        CallableStatement callableStatement = null;
        ResultSet resultSet = null;
        
        try {
            // Load accountant properties file
            Properties props = new Properties();
            InputStream input = getServletContext().getResourceAsStream("/WEB-INF/conf/accountant.properties");
            
            if (input == null) {
                resultsHtml = "<p style='color: red;'>Error: Cannot find accountant.properties</p>";
                request.setAttribute("resultsHtml", resultsHtml);
                request.getRequestDispatcher("accountantHome.jsp").forward(request, response);
                return;
            }
            
            props.load(input);
            
            // Load MySQL driver and establish connection
            Class.forName(props.getProperty("driver"));
            connection = DriverManager.getConnection(
                    props.getProperty("url"), 
                    props.getProperty("user"), 
                    props.getProperty("password"));
            
            // Process based on which report was selected
            switch (reportType) {
                case "max_status":
                    resultsHtml = getMaxSupplierStatus(connection);
                    break;
                case "total_weight":
                    resultsHtml = getTotalPartsWeight(connection);
                    break;
                case "total_shipments":
                    resultsHtml = getTotalShipments(connection);
                    break;
                case "max_workers":
                    resultsHtml = getJobWithMostWorkers(connection);
                    break;
                case "supplier_status":
                    resultsHtml = getAllSupplierStatus(connection);
                    break;
                default:
                    resultsHtml = "<p style='color: red;'>Error: Unknown report type</p>";
            }
                    
        } catch(SQLException e) {
            resultsHtml = "<p style='color: red;'>Database Error: " + e.getMessage() + "</p>";
        } catch(ClassNotFoundException e) {
            resultsHtml = "<p style='color: red;'>MySQL driver not found: " + e.getMessage() + "</p>";
        } catch (Exception e) {
            resultsHtml = "<p style='color: red;'>Error: " + e.getMessage() + "</p>";
        } finally {
            // Clean up resources
            try {
                if (resultSet != null) resultSet.close();
                if (callableStatement != null) callableStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        // Forward back to JSP with results
        request.setAttribute("resultsHtml", resultsHtml);
        request.getRequestDispatcher("accountantHome.jsp").forward(request, response);
	}
	
	// getMaxSupplierStatus Method
	private String getMaxSupplierStatus(Connection connection) throws SQLException {
	    CallableStatement callableStatement = null;
	    ResultSet resultSet = null;
	    
	    try {
	        String sql = "{call Get_The_Maximum_Status_Of_All_Suppliers()}";
	        callableStatement = connection.prepareCall(sql);
	        resultSet = callableStatement.executeQuery();
	        
	        StringBuilder html = new StringBuilder();
	        html.append("<h4>Maximum Status Value Of All Suppliers</h4>");
	        
	        if (resultSet.next()) {
	            int maxStatus = resultSet.getInt("Maximum_Status_Of_All_Suppliers");
	            html.append("<p><strong>Maximum Status:</strong> ").append(maxStatus).append("</p>");
	        }
	        
	        return html.toString();
	    } finally {
	        if (resultSet != null) resultSet.close();
	        if (callableStatement != null) callableStatement.close();
	    }
	}

	// getTotalPartsWeight Method
	private String getTotalPartsWeight(Connection connection) throws SQLException {
	    CallableStatement callableStatement = null;
	    ResultSet resultSet = null;
	    
	    try {
	        String sql = "{call Get_The_Sum_Of_All_Parts_Weights()}";
	        callableStatement = connection.prepareCall(sql);
	        resultSet = callableStatement.executeQuery();
	        
	        StringBuilder html = new StringBuilder();
	        html.append("<h4>Total Weight Of All Parts</h4>");
	        
	        if (resultSet.next()) {
	            double totalWeight = resultSet.getDouble("Sum_Of_All_Part_Weights");
	            html.append("<p><strong>Total Weight:</strong> ").append(totalWeight).append("</p>");
	        }
	        
	        return html.toString();
	    } finally {
	        if (resultSet != null) resultSet.close();
	        if (callableStatement != null) callableStatement.close();
	    }
	}

	// getTotalShipments Method
	private String getTotalShipments(Connection connection) throws SQLException {
	    CallableStatement callableStatement = null;
	    ResultSet resultSet = null;
	    
	    try {
	        String sql = "{call Get_The_Total_Number_Of_Shipments()}";
	        callableStatement = connection.prepareCall(sql);
	        resultSet = callableStatement.executeQuery();
	        
	        StringBuilder html = new StringBuilder();
	        html.append("<h4>Total Number of Shipments</h4>");
	        
	        if (resultSet.next()) {
	            int totalShipments = resultSet.getInt("The_Total_Number_Of_Shipments");
	            html.append("<p><strong>Total Shipments:</strong> ").append(totalShipments).append("</p>");
	        }
	        
	        return html.toString();
	    } finally {
	        if (resultSet != null) resultSet.close();
	        if (callableStatement != null) callableStatement.close();
	    }
	}
	
	// getJobWithMostWorkers Method
	private String getJobWithMostWorkers(Connection connection) throws SQLException {
	    CallableStatement callableStatement = null;
	    ResultSet resultSet = null;
	    
	    try {
	        String sql = "{call Get_The_Name_Of_The_Job_With_The_Most_Workers()}";
	        callableStatement = connection.prepareCall(sql);
	        resultSet = callableStatement.executeQuery();
	        
	        StringBuilder html = new StringBuilder();
	        html.append("<h4>Job With The Most Workers</h4>");
	        
	        ResultSetMetaData metaData = resultSet.getMetaData();
	        int columnCount = metaData.getColumnCount();
	        
	        html.append("<table border='1' style='border-collapse: collapse;'>");
	        html.append("<tr>");
	        for (int i = 1; i <= columnCount; i++) {
	            html.append("<th>").append(metaData.getColumnName(i)).append("</th>");
	        }
	        html.append("</tr>");
	        
	        while (resultSet.next()) {
	            html.append("<tr>");
	            for (int i = 1; i <= columnCount; i++) {
	                html.append("<td>").append(resultSet.getString(i)).append("</td>");
	            }
	            html.append("</tr>");
	        }
	        html.append("</table>");
	        
	        return html.toString();
	    } finally {
	        if (resultSet != null) resultSet.close();
	        if (callableStatement != null) callableStatement.close();
	    }
	}
	
	// getAllSupplierStatus Method
	private String getAllSupplierStatus(Connection connection) throws SQLException {
	    CallableStatement callableStatement = null;
	    ResultSet resultSet = null;
	    
	    try {
	        String sql = "{call List_The_Name_And_Status_Of_All_Suppliers()}";
	        callableStatement = connection.prepareCall(sql);
	        resultSet = callableStatement.executeQuery();
	        
	        StringBuilder html = new StringBuilder();
	        html.append("<h4>Name And Status Of Every Supplier</h4>");
	        
	        ResultSetMetaData metaData = resultSet.getMetaData();
	        int columnCount = metaData.getColumnCount();
	        
	        html.append("<table border='1' style='border-collapse: collapse;'>");
	        html.append("<tr>");
	        for (int i = 1; i <= columnCount; i++) {
	            html.append("<th>").append(metaData.getColumnName(i)).append("</th>");
	        }
	        html.append("</tr>");
	        
	        while (resultSet.next()) {
	            html.append("<tr>");
	            for (int i = 1; i <= columnCount; i++) {
	                html.append("<td>").append(resultSet.getString(i)).append("</td>");
	            }
	            html.append("</tr>");
	        }
	        html.append("</table>");
	        
	        return html.toString();
	    } finally {
	        if (resultSet != null) resultSet.close();
	        if (callableStatement != null) callableStatement.close();
	    }
	}
	

}
