package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

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
			ps = conn.prepareStatement("SELECT * from Users where UserName='" + username + "' AND Password='" + password + "';");
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
        
        // * Remove password from request to prevent MITM attacks
        request.removeAttribute("password");
        
        // * Set default JSON to failed login
     	String jsonStr = "{\"Error\": \"Sign-up Failed\"}";
     	
     	// * Send JSON Object as a response
		response.setContentType("application/json");
		
		// Get the PrintWriter object from response to write the required JSON object to the output stream      
		PrintWriter out = response.getWriter();
		
		boolean invalid_request = false;
		if(username.length() == 0 || username.length() > 50) {
			jsonStr = "{\"Error\": \"Sign-up Failed. Invalid username.\"}";
			invalid_request = true;
        }
        if(password.length() < 6 || password.length() > 50) {
        	jsonStr = "{\"Error\": \"Sign-up Failed. Invalid password.\"}";
			invalid_request = true;
        }
        if(email.length() < 6 || email.length() > 50) {
        	jsonStr = "{\"Error\": \"Sign-up Failed. Invalid email.\"}";
			invalid_request = true;
        }
        if(bio.length() == 0 || bio.length() > 200) {
        	jsonStr = "{\"Error\": \"Sign-up Failed. Invalid biography.\"}";
			invalid_request = true;
        }
        if(name.length() == 0 || name.length() > 50) {
        	jsonStr = "{\"Error\": \"Sign-up Failed. Invalid name.\"}";
			invalid_request = true;
        }
        if(avatar.length() == 0 || avatar.length() > 100) {
        	jsonStr = "{\"Error\": \"Sign-up Failed. Invalid avatar url.\"}";
			invalid_request = true;
        }
        
        // ! Returns JSON Object in a String format due to bad request
        if(invalid_request) {
    		out.print(jsonStr);
    		out.flush();
        }
        
        try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/VinderDB?user=vinderapp&password=password&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
			ps = conn.prepareStatement("SELECT * from Users where UserName='" + username + "' OR Email='" + email + "';");
			rs = ps.executeQuery();
			if(rs.next()) {
				// * Automatically log user in if information is correct
				String queried_username = rs.getString("UserName");
				String queried_password = rs.getString("Password");
				String queried_email = rs.getString("Email");
				if(queried_username.equals(username) && queried_password.equals(password) && queried_email.equals(email)) {
					// * User successfully logged in
					jsonStr = "{"
							+ "\"username\": \"" + rs.getString("UserName") + "\","
							+ "\"email\": \"" + rs.getString("Email") + "\","
							+ "\"bio\": \"" + rs.getString("Bio") + "\","
							+ "\"avatar\": \"" + rs.getString("Avatar") + "\","
							+ "\"name\": \"" + rs.getString("Name") + "\","
							+ "\"date\": \"" + rs.getString("Date") + "\""
							+ "}";				
				} else {
					jsonStr = "{\"Error\": \"Sign-up Failed. User exists and password was incorrect for successful login.\"}";
				}
			} else {
				Calendar calendar=Calendar.getInstance();
		        String date = calendar.getTime().toString();
				ps = conn.prepareStatement("INSERT INTO Users (Date, Name, UserName, Email, Password, Bio, Avatar) VALUES ('" + date + "', '" + name + "', '" + username + "', '" + email + "', '" + password + "', '" + bio + "', '" + avatar + "' );");
				ps.executeUpdate();
				
				jsonStr = "{"
						+ "\"username\": \"" + username + "\","
						+ "\"email\": \"" + email + "\","
						+ "\"bio\": \"" + bio + "\","
						+ "\"avatar\": \"" + avatar + "\","
						+ "\"name\": \"" + name + "\","
						+ "\"date\": \"" + date + "\""
						+ "}";	
			}
			
			// * Send Result
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

}
