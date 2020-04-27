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
 * Servlet implementation class Profile
 */
@WebServlet("/Profile")
public class Profile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn = null;
	private static Statement st = null;
	private static PreparedStatement ps = null;
	private static ResultSet rs = null;
	
    /**
     * @see HttpServlet#HttpServlet()
     * @param username | String
     */
    public Profile() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		
		// * Set default JSON to failed profile fetch
     	String jsonStr = "{\"Error\": \"Profile Fetch Failed. Could not find user with provided username.\"}";
     			
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
    		// * Connection string for digitalocean db
    		conn = DriverManager.getConnection("jdbc:mysql://doadmin:fxqax6g9ebsdwkna@db-mysql-nyc1-50156-do-user-7420753-0.a.db.ondigitalocean.com:25060/VinderDB?ssl-mode=REQUIRED");
    		// * Connection string for local db
			//conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/VinderDB?user=vinderapp&password=password&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
			ps = conn.prepareStatement("SELECT * from Users where UserName='" + username + "';");
			rs = ps.executeQuery();
			if(rs.next()) {
				// * User successfully found
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
		} catch (Exception e) {
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
	 * @param username | String
	 * @param password | String
	 * @param email | String
	 * @param bio | String
	 * @param name | String
	 * @param avatar | String
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
     	String jsonStr = "{\"Error\": \"Updating Profile Failed.\"}";
     	
     	// * Send JSON Object as a response
		response.setContentType("application/json");
		
		// Get the PrintWriter object from response to write the required JSON object to the output stream      
		PrintWriter out = response.getWriter();
		
		boolean invalid_request = false;
		if(username == null || username.length() == 0 || username.length() > 50) {
			jsonStr = "{\"Error\": \"Updating Profile Failed. Invalid username.\"}";
			invalid_request = true;
        }
        if(password == null || password.length() < 6 || password.length() > 50) {
        	jsonStr = "{\"Error\": \"Updating Profile Failed. Invalid password.\"}";
			invalid_request = true;
        }
        if(email == null || email.length() < 6 || email.length() > 50) {
        	jsonStr = "{\"Error\": \"Updating Profile Failed. Invalid email.\"}";
			invalid_request = true;
        }
        if(bio == null || bio.length() == 0 || bio.length() > 200) {
        	jsonStr = "{\"Error\": \"Updating Profile Failed. Invalid biography.\"}";
			invalid_request = true;
        }
        if(name == null || name.length() == 0 || name.length() > 50) {
        	jsonStr = "{\"Error\": \"Updating Profile Failed. Invalid name.\"}";
			invalid_request = true;
        }
        if(avatar == null || avatar.length() == 0 || avatar.length() > 100) {
        	jsonStr = "{\"Error\": \"Updating Profile Failed. Invalid avatar url.\"}";
			invalid_request = true;
        }
        
        // ! Returns JSON Object in a String format due to bad request
        if(invalid_request) {
    		out.print(jsonStr);
    		out.flush();
        } else {
        	try {
        		Class.forName("com.mysql.cj.jdbc.Driver");
        		// * Connection string for digitalocean db
        		conn = DriverManager.getConnection("jdbc:mysql://doadmin:fxqax6g9ebsdwkna@db-mysql-nyc1-50156-do-user-7420753-0.a.db.ondigitalocean.com:25060/VinderDB?ssl-mode=REQUIRED");
        		// * Connection string for local db
    			//conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/VinderDB?user=vinderapp&password=password&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        		ps = conn.prepareStatement("SELECT * from Users where (UserName='" + username + "' AND Email<>'" + email + "') OR (UserName<>'" + username + "' AND Email='" + email + "') OR (UserName='" + username + "' AND Email='" + email + "');");
        		rs = ps.executeQuery();
        		
        		// * Only update profile if user exists
        		if(rs.next()) {
        			String queried_username = rs.getString("UserName");
        			String queried_password = rs.getString("Password");
        			String queried_email = rs.getString("Email");
        			if((queried_username.equals(username) && queried_password.equals(password)) || (queried_password.equals(password) && queried_email.equals(email))) {
        				// * User credentials verified
        				// * Create new date for update
        				Calendar calendar=Calendar.getInstance();
            			String date = calendar.getTime().toString();
            			
            			// * Update User
        				ps = conn.prepareStatement("UPDATE Users SET Password='" + password + "', Email='" + email + "', Bio='" + bio + "', Avatar='" + avatar + "', Name='" + name + "', Date='" + date + "' WHERE (UserName='" + username + "' AND Email<>'" + email + "') OR (UserName<>'" + username + "' AND Email='" + email + "') OR (UserName='" + username + "' AND Email='" + email + "');");
                		if(ps.executeUpdate() != 0) {
                			jsonStr = "{"
                					+ "\"username\": \"" + queried_username + "\","
                					+ "\"email\": \"" + email + "\","
                					+ "\"bio\": \"" + bio + "\","
                					+ "\"avatar\": \"" + avatar + "\","
                					+ "\"name\": \"" + name + "\","
                					+ "\"date\": \"" + date + "\""
                					+ "}";
                		}
                		
                		
        			} else {
        				jsonStr = "{\"Error\": \"Sign-up Failed. User exists and password was incorrect for successful login.\"}";
        			}
        		}
        		
        		// * Send Result
        		out.print(jsonStr);
        		out.flush();
        		
        	} catch (SQLException sqle) {
        		System.out.println ("SQLException: " + sqle.getMessage());
        	} catch (Exception e) {
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

}
