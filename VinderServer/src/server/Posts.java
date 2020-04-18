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
 * Servlet implementation class Posts
 */
@WebServlet("/Posts")
public class Posts extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn = null;
	private static Statement st = null;
	private static PreparedStatement ps = null;
	private static ResultSet rs = null;
	private static ResultSet rs2 = null;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Posts() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param number | integer
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String num_param = request.getParameter("number");
		Integer number = 0;
		try {
			number = Integer.parseInt(num_param);
		} catch (NumberFormatException nfe) {
			System.out.println("Number '" + number + "' could not be parsed into an integer.");
			System.out.println("Returning 100 most recent posts to client.");
		}
		
		if(number == null || number <= 0) {
			number = 100;
		}
		
		// * Set default JSON to failed login
     	String jsonStr = "{\"Error\": \"Fetching Most Recent Posts Failed\"}";
     	
     	// * Send JSON Object as a response
		response.setContentType("application/json");
		
		// Get the PrintWriter object from response to write the required JSON object to the output stream      
		PrintWriter out = response.getWriter();
		
		try {
    		Class.forName("com.mysql.cj.jdbc.Driver");
    		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/VinderDB?user=vinderapp&password=password&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
			ps = conn.prepareStatement("SELECT * from Posts ORDER BY 'ID' DESC LIMIT " + number + ";");
			rs = ps.executeQuery();
			
			// * Iterate over post results and append to [jsonStr]
			jsonStr = "{\"posts\": [";
			while(rs.next()) {
				jsonStr += "{"
						+ "\"id\": \"" + rs.getInt("ID") + "\","
						+ "\"date\": \"" + rs.getString("Date") + "\","
						+ "\"AuthorName\": \"" + rs.getString("AuthorName") + "\","
						+ "\"AuthorID\": \"" + rs.getString("AuthorID") + "\","
						+ "\"Content\": \"" + rs.getString("Content") + "\","
						+ "\"tags\": ["; 
				
				// * Fetch Tags that contain the [PostID] equal to the current post object
				ps = conn.prepareStatement("SELECT * from Tags where PostID='" + rs.getInt("ID") + "';");
				rs2 = ps.executeQuery();
				
				// * Iterate over tags and append to [jsonStr]
				while(rs2.next()) {
					jsonStr += "\"" + rs2.getString("Name") + "\",";
				}
				
				// * Append end of tags array bracket and end of post object curly brace
				jsonStr += "]},";
			}
			// * Remove last comma after last post object and add bracket closing array of post objects and curly brace ending [jsonStr]
			jsonStr = jsonStr.substring(0, jsonStr.length() - 1) + "]}";
    		
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Forward to get request
		doGet(request, response);
	}

}
