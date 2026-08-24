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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Servlet implementation class DataEntryUserServlet
 */
public class DataEntryUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public DataEntryUserServlet() {
        super();
    }

    // doGet Method
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>DataEntry User Servlet</h1>");
        out.println("<p>This servlet only accepts POST requests from the DataEntry form.</p>");
        out.println("</body></html>");
	}

	// doPost Method
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        
        // Check if user is authenticated as dataentry
        if(username == null || !"dataentry".equals(username)) {
            response.sendRedirect("authentication.html");
            return;
        }
        
        String formType = request.getParameter("formType");
        String resultsHtml = "";
        
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        
        try {
            // Load dataentry properties file
            Properties props = new Properties();
            InputStream input = getServletContext().getResourceAsStream("/WEB-INF/conf/dataentry.properties");
            
            if (input == null) {
                resultsHtml = "<p style='color: red;'>Error: Cannot find dataentry.properties</p>";
                request.setAttribute("resultsHtml", resultsHtml);
                request.getRequestDispatcher("dataEntryHome.jsp").forward(request, response);
                return;
            }
            
            props.load(input);
            
            // Load MySQL driver and establish connection
            Class.forName(props.getProperty("driver"));
            connection = DriverManager.getConnection(
                    props.getProperty("url"), 
                    props.getProperty("user"), 
                    props.getProperty("password"));
            
            // Process based on which form was submitted
            switch (formType) {
                case "suppliers":
                    resultsHtml = insertSupplier(request, connection);
                    break;
                case "parts":
                    resultsHtml = insertPart(request, connection);
                    break;
                case "jobs":
                    resultsHtml = insertJob(request, connection);
                    break;
                case "shipments":
                    resultsHtml = insertShipment(request, connection);
                    break;
                default:
                    resultsHtml = "<p style='color: red;'>Error: Unknown form type</p>";
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
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        // Forward back to JSP with results
        request.setAttribute("resultsHtml", resultsHtml);
        request.getRequestDispatcher("dataEntryHome.jsp").forward(request, response);
	}
	
	// insertSupplier Method
	private String insertSupplier(HttpServletRequest request, Connection connection) throws SQLException {
        String snum = request.getParameter("snum");
        String sname = request.getParameter("sname");
        int status = Integer.parseInt(request.getParameter("status"));
        String city = request.getParameter("city");
        
        String sql = "INSERT INTO suppliers (snum, sname, status, city) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, snum);
        pstmt.setString(2, sname);
        pstmt.setInt(3, status);
        pstmt.setString(4, city);
        
        int rowsAffected = pstmt.executeUpdate();
        pstmt.close();
        
        return "<p style='color: green;'>Supplier record inserted successfully!</p>" +
               "<p>Supplier: " + sname + " (" + snum + ")</p>" +
               "<p>Status: " + status + ", City: " + city + "</p>" +
               "<p>" + rowsAffected + " row(s) affected.</p>";
    }
	
	// insertPart Method
    private String insertPart(HttpServletRequest request, Connection connection) throws SQLException {
        String pnum = request.getParameter("pnum");
        String pname = request.getParameter("pname");
        String color = request.getParameter("color");
        double weight = Double.parseDouble(request.getParameter("weight"));
        String city = request.getParameter("city");
        
        String sql = "INSERT INTO parts (pnum, pname, color, weight, city) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, pnum);
        pstmt.setString(2, pname);
        pstmt.setString(3, color);
        pstmt.setDouble(4, weight);
        pstmt.setString(5, city);
        
        int rowsAffected = pstmt.executeUpdate();
        pstmt.close();
        
        return "<p style='color: green;'>Part record inserted successfully!</p>" +
               "<p>Part: " + pname + " (" + pnum + ")</p>" +
               "<p>Color: " + color + ", Weight: " + weight + ", City: " + city + "</p>" +
               "<p>" + rowsAffected + " row(s) affected.</p>";
    }
    
    // insertJob Method
    private String insertJob(HttpServletRequest request, Connection connection) throws SQLException {
        String jnum = request.getParameter("jnum");
        String jname = request.getParameter("jname");
        int numworkers = Integer.parseInt(request.getParameter("numworkers"));
        String city = request.getParameter("city");
        
        String sql = "INSERT INTO jobs (jnum, jname, numworkers, city) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, jnum);
        pstmt.setString(2, jname);
        pstmt.setInt(3, numworkers);
        pstmt.setString(4, city);
        
        int rowsAffected = pstmt.executeUpdate();
        pstmt.close();
        
        return "<p style='color: green;'>Job record inserted successfully!</p>" +
               "<p>Job: " + jname + " (" + jnum + ")</p>" +
               "<p>Workers: " + numworkers + ", City: " + city + "</p>" +
               "<p>" + rowsAffected + " row(s) affected.</p>";
    }
    
    // insertShipment Method
    private String insertShipment(HttpServletRequest request, Connection connection) throws SQLException {
        String snum = request.getParameter("snum");
        String pnum = request.getParameter("pnum");
        String jnum = request.getParameter("jnum");
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        
        String sql = "INSERT INTO shipments (snum, pnum, jnum, quantity) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, snum);
        pstmt.setString(2, pnum);
        pstmt.setString(3, jnum);
        pstmt.setInt(4, quantity);
        
        int rowsAffected = pstmt.executeUpdate();
        pstmt.close();
        
        StringBuilder result = new StringBuilder();
        result.append("<p style='color: green;'>Shipment record inserted successfully!</p>")
              .append("<p>Shipment: Supplier ").append(snum).append(" → Part ").append(pnum).append(" → Job ").append(jnum).append("</p>")
              .append("<p>Quantity: ").append(quantity).append("</p>")
              .append("<p>").append(rowsAffected).append(" row(s) affected.</p>");
        
        // Business logic check for data entry users too
        if (quantity >= 100) {
            
            // Update supplier status for all suppliers with any shipment >= 100
            int suppliersUpdated = updateSupplierStatus(connection);
            
            result.append("<p style='color: blue;'><strong>Business Logic Detected! - Updating Supplier Status</strong></p>");
            result.append("<p style='color: blue;'>Business Logic updated ").append(suppliersUpdated).append(" supplier status marks.</p>");
        } else {
        	result.append("<p style='color: green;'><strong>Business Logic Not Triggered!</strong></p>");
        }
        
        return result.toString();
    }

    // updateSupplierStatus method for all suppliers with any shipment >= 100 (business Logic)
    private int updateSupplierStatus(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        
        // Update status for all suppliers who have any shipment with quantity >= 100
        String updateSql = "UPDATE suppliers SET status = status + 5 " +
                          "WHERE snum IN (SELECT DISTINCT snum FROM shipments WHERE quantity >= 100)";
        
        int rowsAffected = stmt.executeUpdate(updateSql);
        stmt.close();
        
        return rowsAffected;
    }


}
