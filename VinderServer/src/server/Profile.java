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
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/VinderDB?user=vinderapp&password=password&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
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
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
