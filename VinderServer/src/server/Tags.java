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
 * Servlet implementation class Tags
 */
@WebServlet("/Tags")
public class Tags extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn = null;
	private static Statement st = null;
	private static PreparedStatement ps = null;
	private static ResultSet rs = null;
	private static ResultSet rs2 = null;
	private static ResultSet rs3 = null;
	private static ResultSet rs4 = null;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Tags() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param name | String
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter("name");
		String num_param = request.getParameter("number");
		Integer number = 0;
		try {
			number = Integer.parseInt(num_param);
		} catch (NumberFormatException nfe) {
			// * Ignore
		}
		
		// * Set default JSON to failed login
     	String jsonStr = "{\"Error\": \"Fetching Tags Failed\"}";
     	
     	// * Send JSON Object as a response
		response.setContentType("application/json");
		
		// Get the PrintWriter object from response to write the required JSON object to the output stream      
		PrintWriter out = response.getWriter();
		
		boolean invalid_request = false;
		boolean select_all = false;
		if(name == null || name.length() == 0) {
			select_all = true;
		} else if(name != null && name.length() > 50) {
        	jsonStr = "{\"Error\": \"Fetching Tags Failed. Invalid name.\"}";
			invalid_request = true;
        }
		
		if(number == null || number <= 0) {
			number = 100;
		}
        
        // ! Returns JSON Object in a String format due to bad request
        if(invalid_request) {
    		out.print(jsonStr);
    		out.flush();
        } else {
        	try {
        		Class.forName("com.mysql.cj.jdbc.Driver");
        		// * Connection string for digitalocean db
        		//conn = DriverManager.getConnection("jdbc:mysql://doadmin:fxqax6g9ebsdwkna@db-mysql-nyc1-50156-do-user-7420753-0.a.db.ondigitalocean.com:25060/VinderDB?ssl-mode=REQUIRED");
        		// * Connection string for local db
    			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/VinderDB?user=vinderapp&password=password&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
    			if(select_all) {
    				ps = conn.prepareStatement("SELECT DISTINCT Name from Tags LIMIT " + number + ";");
        			rs = ps.executeQuery();
        			
        			// * Iterate over tags results and append
					jsonStr = "{\"tags\": [ ";
					while(rs.next()) {
						jsonStr += "\"" + rs.getString("Name") + "\",";
					}
					
        			// * Remove last comma after last post object and add bracket closing array of post objects and curly brace ending [jsonStr]
	    			jsonStr = jsonStr.substring(0, jsonStr.length() - 1) + "]}";
	        		
	        		// * Send Result
	        		out.print(jsonStr);
	        		out.flush();
    			} else {
    				ps = conn.prepareStatement("SELECT * from Tags WHERE Name='" + name.toLowerCase() + "' ORDER BY Tags.ID DESC;");
    				rs = ps.executeQuery();
    			
					// * Iterate over post results and append to [jsonStr]
					jsonStr = "{\"posts\": [ ";
					while(rs.next()) {
						// * Fetch Post with the [PostID] equal to the current post object
						ps = conn.prepareStatement("SELECT * from Posts where ID=" + rs.getInt("PostID") + " ORDER BY Posts.ID DESC;");
						rs2 = ps.executeQuery();
						
						// * Iterate over post (should only be one) and append to [jsonStr]
						if(rs2.next()) {
							
							// * Fetch all the tags related to the current post
							ps = conn.prepareStatement("SELECT * from VinderDB.Users WHERE UserName='" + rs2.getString("AuthorName") + "';");
			    			rs4 = ps.executeQuery();
			    			
			    			String avatar = "";
			    			if(rs4.next()) {
			    				avatar = rs4.getString("Avatar");
			    			}
			    			
							jsonStr += "{"
		    						+ "\"id\": \"" + rs2.getInt("ID") + "\","
		    						+ "\"date\": \"" + rs2.getString("Date") + "\","
		    						+ "\"AuthorName\": \"" + rs2.getString("AuthorName") + "\","
		    						+ "\"AuthorID\": \"" + rs2.getString("AuthorID") + "\","
		    						+ "\"Avatar\": \"" + avatar + "\","
		    						+ "\"Content\": \"" + rs2.getString("Content") + "\","
		    						+ "\"tags\": [ ";
							
							// * Fetch all the tags related to the current post
							ps = conn.prepareStatement("SELECT * from Tags WHERE PostID=" + rs2.getInt("ID") + " ORDER BY Tags.ID DESC;");
			    			rs3 = ps.executeQuery();
			    			
			    			while(rs3.next()) {
			    				jsonStr += "\"" + rs3.getString("Name") + "\",";
			    			}
			    			
			    			// * Remove last comma from inner tags array
							// * Append end of tags array bracket and post object curly brace
							jsonStr = jsonStr.substring(0, jsonStr.length() - 1) + "]},";
						}
    				
	    			}
	    			// * Remove last comma after last post object and add bracket closing array of post objects and curly brace ending [jsonStr]
	    			jsonStr = jsonStr.substring(0, jsonStr.length() - 1) + "]}";
	        		
	        		// * Send Result
	        		out.print(jsonStr);
	        		out.flush();
    			}
        		
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
