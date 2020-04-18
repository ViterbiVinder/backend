package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Auth
 */
@WebServlet("/Auth")
public class Auth extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn = null;
	private static Statement st = null;
	private static PreparedStatement ps = null;
	private static ResultSet rs = null;
    
	/**
     * @see HttpServlet#HttpServlet()
     */
    public Auth() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param username
	 * @param password
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // * Only require username and password to log a user in or get their profile
		String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // * Remove password from request to prevent MITM attacks
        request.removeAttribute("password");
        
        // * Set default JSON to failed login
     	String jsonStr = "{\"Error\": \"Login Failed\"}";
     			
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/VinderDB?user=vinderapp&password=password&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
			ps = conn.prepareStatement("SELECT * from Users where username='" + username + "' AND password='" + password + "';");
			rs = ps.executeQuery();
			if(rs.next()) {
				// * User successfully logged in
				jsonStr = "{"
						+ "\"username\": \"" + rs.getString("UserName") + "\","
						+ "\"email\": \"" + rs.getString("Email") + "\","
						+ "\"bio\": \"" + rs.getString("Bio") + "\","
						+ "\"avatar\": \"" + rs.getString("Avatar") + "\","
						+ "\"name\": \"" + rs.getString("Name") + "\","
						+ "\"date\": \"" + rs.getString("Date") + "\""
						+ "}";
			}
			// * Send JSON Object as a response
			response.setContentType("application/json");
			
			// Get the PrintWriter object from response to write the required JSON object to the output stream      
			PrintWriter out = response.getWriter();
			
			// Returns JSON Object in a String format
			out.print(jsonStr);
			out.flush();
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @param username
	 * @param password
	 * @param email
	 * @param bio
	 * @param name
	 * @param avatar
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String bio = request.getParameter("bio");
        String name = request.getParameter("name");
        String avatar = request.getParameter("avatar");
        
		if(username.length() == 0 || username.length() > 50) {
        	response.sendRedirect("/SalEats/login-sign-up.html?auth_success=login-failed");
        }
        if(password.length() < 6 || password.length() > 50) {
        	response.sendRedirect("/SalEats/login-sign-up.html?auth_success=login-failed");
        }
	}

}
