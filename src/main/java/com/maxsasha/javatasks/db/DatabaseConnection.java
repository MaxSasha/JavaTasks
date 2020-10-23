package com.maxsasha.javatasks.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.extern.log4j.Log4j;

@Log4j
public class DatabaseConnection {
	private static Connection connection;
	private static final String url = "jdbc:mysql://localhost/task1?useUnicode=true&serverTimezone=UTC";;
	private static final String user = "insane";
	private static final String pass = "SMax2000";

	public Connection getConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(url, user, pass);
		} catch (ClassNotFoundException | SQLException ex) {
			log.error(String.format("Error with creating db connection : ", ex.getMessage()),ex);
			throw new RuntimeException(ex);
		}
		return connection;
	}
}