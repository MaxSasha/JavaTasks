import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	static Connection con=null;
	
	static
    { 
        String url = "jdbc:mysql://localhost/task1?useUnicode=true&serverTimezone=UTC"; 
        String user = "insane"; 
        String pass = "SMax2000"; 
        try { 
            Class.forName("com.mysql.jdbc.Driver"); 
            con = DriverManager.getConnection(url, user, pass); 
        } 
        catch (ClassNotFoundException | SQLException e) { 
            e.printStackTrace(); 
        } 
    } 
	public static Connection getConnection()
    {
        return con;
    }

   
}
