package servlets;

/* Name: Kathlea Corla
Course: CNT 4714 – Fall 2025 – Project Four
Assignment title: A Three-Tier Distributed Web-Based Application
Date: December 1, 2025
*/

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class AuthenticateUserServlet
 */

public class AuthenticateUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	String username = request.getParameter("username");
    	String password = request.getParameter("password");
    	
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	ResultSet resultSet = null;
    	
    	try {
    		// Load properties file
    		Properties props = new Properties();
    		InputStream input = getServletContext().getResourceAsStream("/WEB-INF/conf/systemapp.properties");
    		
    		if(input == null) {
    			 System.out.println("ERROR: Cannot find systemapp.properties");
                 response.sendRedirect("errorpage.html");
                 return;
    		}
    		
    		props.load(input);
    		
    		//Load MySQL driver
    		Class.forName(props.getProperty("driver"));
    		System.out.println("MySQL driver loaded");
    		
    		// Establish Connection
    		connection = DriverManager.getConnection(
    				props.getProperty("url"),
    				props.getProperty("user"),
    				props.getProperty("password")
    		);
    		
            // Validating Credentials
            String sql = "SELECT * FROM usercredentials WHERE login_username = ? AND login_password = ?";
            
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                System.out.println("LOGIN SUCCESS for: " + username);
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                
                // Redirect based on user type
                if ("root".equals(username)) {
                    response.sendRedirect("rootHome.jsp");
                } else if ("client".equals(username)) {
                    response.sendRedirect("clientHome.jsp");
                } else if ("dataentry".equals(username)) {
                    response.sendRedirect("dataEntryHome.jsp");
                } else if ("theaccountant".equals(username)) {
                    response.sendRedirect("accountantHome.jsp");
                } else {
                    response.sendRedirect("errorpage.html");
                }
            } else {
                System.out.println("LOGIN FAILED for: " + username);
                response.sendRedirect("errorpage.html");
            }
    				
    	} catch (ClassNotFoundException e) {
            System.out.println("ERROR: MySQL driver not found");
            e.printStackTrace();
            response.sendRedirect("errorpage.html");
    	} catch (SQLException e) {
    		System.out.println("ERROR: Database error");
            e.printStackTrace();
            response.sendRedirect("errorpage.html");
    	} catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("errorpage.html");
    	}finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    
    
    }
}