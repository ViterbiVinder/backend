package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Post
 */
@WebServlet("/Post")
public class Post extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn = null;
	private static Statement st = null;
	private static PreparedStatement ps = null;
	private static ResultSet rs = null;
	private static ResultSet rs2 = null;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Post() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param username
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		
		// * Set default JSON to failed login
     	String jsonStr = "{\"Error\": \"Fetching Posts Failed\"}";
     	
     	// * Send JSON Object as a response
		response.setContentType("application/json");
		
		// Get the PrintWriter object from response to write the required JSON object to the output stream      
		PrintWriter out = response.getWriter();
		
		boolean invalid_request = false;
		if(username == null || username.length() == 0 || username.length() > 50) {
			jsonStr = "{\"Error\": \"Fetching Posts Failed. Invalid username.\"}";
			invalid_request = true;
        }
        
        // ! Returns JSON Object in a String format due to bad request
        if(invalid_request) {
    		out.print(jsonStr);
    		out.flush();
        } else {
        	try {
        		Class.forName("com.mysql.cj.jdbc.Driver");
        		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/VinderDB?user=vinderapp&password=password&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        		ps = conn.prepareStatement("SELECT * from Users where UserName='" + username + "';");
        		rs = ps.executeQuery();
        		if(rs.next()) {
        			Integer queried_id = rs.getInt("ID");
        			
        			System.out.println(username);
        			System.out.println(queried_id);
        			// * Insert new Post
        			Calendar calendar=Calendar.getInstance();
        			String date = calendar.getTime().toString();
        			ps = conn.prepareStatement("SELECT * from Posts where AuthorID='" + queried_id + "' AND AuthorName='" + username + "';");
        			rs = ps.executeQuery();
        			
        			// * Iterate over post results and append to [jsonStr]
        			jsonStr = "{\"posts\": [ ";
        			while(rs.next()) {
        				jsonStr += "{"
        						+ "\"id\": \"" + rs.getInt("ID") + "\","
        						+ "\"date\": \"" + rs.getString("Date") + "\","
        						+ "\"AuthorName\": \"" + username + "\","
        						+ "\"AuthorID\": \"" + queried_id + "\","
        						+ "\"Content\": \"" + rs.getString("Content") + "\","
        						+ "\"tags\": [ "; 
        				
        				// * Fetch Tags that contain the [PostID] equal to the current post object
        				ps = conn.prepareStatement("SELECT * from Tags where PostID='" + rs.getInt("ID") + "';");
        				rs2 = ps.executeQuery();
        				
        				// * Iterate over tags and append to [jsonStr]
        				while(rs2.next()) {
        					jsonStr += "\"" + rs2.getString("Name") + "\",";
        				}
        				
        				// * Append end of tags array bracket and end of post object curly brace
        				jsonStr = jsonStr.substring(0, jsonStr.length() - 1) + "]},";
        			}
        			// * Remove last comma after last post object and add bracket closing array of post objects and curly brace ending [jsonStr]
        			jsonStr = jsonStr.substring(0, jsonStr.length() - 1) + "]}";
        		} else {
        			jsonStr = "{\"Error\": \"Fetching Posts Failed. User does not exist.\"}";
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * @param username | String
	 * @param content | String
	 * @param tags | Array[String]
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
        String content = request.getParameter("content");
		String[] tags = request.getParameterValues("tags");
        
        // * Set default JSON to failed login
     	String jsonStr = "{\"Error\": \"Creating Post Failed\"}";
     	
     	// * Send JSON Object as a response
		response.setContentType("application/json");
		
		// Get the PrintWriter object from response to write the required JSON object to the output stream      
		PrintWriter out = response.getWriter();
		
		boolean invalid_request = false;
		if(username == null || username.length() == 0 || username.length() > 50) {
			jsonStr = "{\"Error\": \"Creating Post Failed. Invalid username.\"}";
			invalid_request = true;
        }
        if(content == null || content.length() == 0 || content.length() > 200) {
        	jsonStr = "{\"Error\": \"Creating Post Failed. Invalid content.\"}";
			invalid_request = true;
        }
        if(tags == null || tags.length > 50) {
        	jsonStr = "{\"Error\": \"Creating Post Failed. Too many tags.\"}";
			invalid_request = true;
        }
        
        // ! Returns JSON Object in a String format due to bad request
        if(invalid_request) {
    		out.print(jsonStr);
    		out.flush();
        } else {
        	try {
        		Class.forName("com.mysql.cj.jdbc.Driver");
        		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/VinderDB?user=vinderapp&password=password&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        		ps = conn.prepareStatement("SELECT * from Users where UserName='" + username + "';");
        		rs = ps.executeQuery();
        		if(rs.next()) {
        			Integer queried_id = rs.getInt("ID");
        			
        			System.out.println(username);
        			System.out.println(queried_id);
        			// * Insert new Post
        			Calendar calendar=Calendar.getInstance();
        			String date = calendar.getTime().toString();
        			ps = conn.prepareStatement("INSERT INTO Posts (Date, AuthorName, AuthorID, Content) VALUES ('" + date + "', '" + username + "', '" + queried_id + "', '" + content + "' );", Statement.RETURN_GENERATED_KEYS);
        			ps.executeUpdate();
        			
        			// * Get Post ID from query
        			ResultSet result_set = ps.getGeneratedKeys();
        			int generatedKey = 0;
        			if (result_set.next()) {
        				generatedKey = result_set.getInt(1);
        			}
        			
        			// * Create tags with link to post ID
        			for(String tag : tags) {
        				ps = conn.prepareStatement("INSERT INTO Tags (Name, PostID) VALUES ('" + tag + "', '" + generatedKey + "' );");
        				ps.executeUpdate();
        			}
        			
        			jsonStr = "{"
        					+ "\"username\": \"" + username + "\","
        					+ "\"content\": \"" + content + "\","
        					+ "\"id\": \"" + generatedKey + "\","
        					+ "\"date\": \"" + date + "\","
        					+ "\"author_id\": \"" + queried_id + "\""
        					+ "}";
        		} else {
        			jsonStr = "{\"Error\": \"Creating Post Failed. User does not exist.\"}";
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

}
