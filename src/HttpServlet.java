

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;



/**
 * Servlet implementation class HttpServlet
 */
@WebServlet("/users/*")
public class HttpServlet extends javax.servlet.http.HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection conn;
	ArrayList<User> users;
	private static Gson _gson = null;
	static {
		_gson = new Gson();
	}

    /**
     * @see javax.servlet.http.HttpServlet#javax.servlet.http.HttpServlet()
     */
    public HttpServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    private void sendAsJson(HttpServletResponse response,  Object obj) throws IOException {
    		
    		response.setContentType("application/json");
    		
    		String res = _gson.toJson(obj);
    		     
    		PrintWriter out = response.getWriter();
    		  
    		out.print(res);
    		out.flush();
    	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		/*response.setContentType("text/html");
		PrintWriter writer = response.getWriter();*/
		
		String pathInfo = request.getPathInfo();
		try {
			users = getUsers();
			}
		catch(Exception ex){
			System.out.println("Connection to database failed...");
			System.out.println(ex);
		}
		if(pathInfo == null || pathInfo.equals("/")){
            	/*for(User item : users)
            	{
            		writer.println("Id:"+item.getId()+"  Name:"+item.getName()+"  Email:"+item.getEmail());
            	}*/
            	sendAsJson(response, users);
            	return;
		}

		String[] splits = pathInfo.split("/");
		if(splits.length != 2) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String userId = splits[1];
		try {
			User user = getUser(userId);
			if(user==null)
			{
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			else
			{
				sendAsJson(response, user);
            	return;
			}
		}
		catch(SQLException ex){
			System.out.println(""+ex);
		}
		return;
	}
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		User user;
		String pathInfo = request.getPathInfo();
		
		
		if(pathInfo == null || pathInfo.equals("/")){
			StringBuilder buffer = new StringBuilder();
		    BufferedReader reader = request.getReader();
		    String line;
		    while ((line = reader.readLine()) != null) {
		        buffer.append(line);
		    }
		    String playload = buffer.toString();	
            user = _gson.fromJson(playload, User.class);
            insert(user);
            
            sendAsJson(response, user);
            return;
		}
		else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
	}
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user;
		String pathInfo = request.getPathInfo();
		
		if(pathInfo == null || pathInfo.equals("/")){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
		}
		
		String[] splits = pathInfo.split("/");
		
		if(splits.length!=2) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
		}
		
		String userId = splits[1];
		
		try {
			
			user = getUser(userId);
			if(user==null)
			{
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			else
			{
				StringBuilder buffer = new StringBuilder();
			    BufferedReader reader = request.getReader();
			    String line;
			    while ((line = reader.readLine()) != null) {
			        buffer.append(line);
			    }
			    
			    String playload = buffer.toString();
			    playload= playload.substring(0, playload.length()-1);
			    playload+=(",\"id\":\""+userId+"\"}");
			    System.out.println(""+playload);
		        user = _gson.fromJson(playload, User.class);
		        
		        update(user);
		        sendAsJson(response, getUsers());
		        return;
			}
		}
		catch(SQLException ex){
			System.out.println(""+ex);
		}
	}
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String pathInfo = request.getPathInfo();
		System.out.println(""+pathInfo);
		
		if(pathInfo == null || pathInfo.equals("/")){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            	return;
		}

		String[] splits = pathInfo.split("/");
		if(splits.length != 2) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String userId = splits[1];
		System.out.println(userId);
		try {
			User user = getUser(userId);
			if(user==null)
			{
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			else
			{
				System.out.println("DELETE");
				int result = delete(userId);
				sendAsJson(response, getUsers());
            	return;
			}
		}
		catch(SQLException ex){
			System.out.println(""+ex);
		}
		return;
		
	}
	
	public ArrayList<User> getUsers()
	{
		users = new ArrayList<User>();
		try{
        	Connection conn = DatabaseConnection.getConnection();
        	Statement statement = conn.createStatement();
        	ResultSet result = statement.executeQuery("SELECT * FROM users");
        	while(result.next())
        	{
        		int id = result.getInt(1);
                String name = result.getString(2);
                String email = result.getString(3);
                User user = new User(id, name, email);
                users.add(user);
        	}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return users;
	}
	public User getUser(String id) throws SQLException
	{
		User user=null;
		try 
		{
			Connection conn = DatabaseConnection.getConnection();
			String sql = "SELECT * FROM users WHERE id = ?";
			
				PreparedStatement preparedStatement = conn.prepareStatement(sql);
				preparedStatement.setString(1, id);
				ResultSet result = preparedStatement.executeQuery();
				if(result.next()){
					int userId = result.getInt(1);
					String name = result.getString(2);
					String email = result.getString(3);
					user = new User(userId, name, email);
				}	
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		return user;
	}
	public int insert(User user) 
	{
            try
            {
            	Connection conn = DatabaseConnection.getConnection();
                String sql = "INSERT INTO users (name, email) Values (?, ?)";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getEmail());
                      
                return  preparedStatement.executeUpdate();
            }
            catch(Exception ex){
                System.out.println(ex);
            }
        return 0;
    }
	public int update(User user)
	{
		try
        {
			Connection conn = DatabaseConnection.getConnection();
			String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setInt(3, user.getId());
            return  preparedStatement.executeUpdate();
        }
        catch(Exception ex){
            System.out.println(ex);
        }
		return 0;
	}
	public int delete(String id)
	{
		try
        {
			Connection conn = DatabaseConnection.getConnection();
			String sql = "DELETE FROM users WHERE id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, id);
                  
            return  preparedStatement.executeUpdate();
        }
        catch(Exception ex){
            System.out.println(ex);
        }
		return 0;
	}
 	

}
