package DTO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.extern.log4j.Log4j;

@Log4j
public class DatabaseConnection {
	private static Connection con = null;

	static {
		String url = "jdbc:mysql://localhost/task1?useUnicode=true&serverTimezone=UTC";
		String user = "insane";
		String pass = "SMax2000";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, user, pass);
		} catch (ClassNotFoundException | SQLException ex) {
			log.error("Error with db connection: " + ex.getMessage());

		}
	}

	public static Connection getConnection() {
		return con;
	}
}
