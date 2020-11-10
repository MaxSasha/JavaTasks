package com.maxsasha.javatasks.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.maxsasha.javatasks.api.util.PropertiesUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseConnection {
	private Connection connection;
	private final String dbDriver = "com.mysql.jdbc.Driver";

	public Connection getConnection() {
		try {
			Properties properties = PropertiesUtils.getProperties("application.property");
			String url = properties.getProperty("url");
			String user = properties.getProperty("user");
			String password = properties.getProperty("pass");
			Class.forName(dbDriver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException | IOException | SQLException ex) {
			log.error("Error with creating db connection : ", ex.getMessage());
			throw new RuntimeException(ex);
		}
		return connection;
	}

	public void closeConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException ex) {
			log.error("Can`t close connection. Exception message:", ex.getMessage());
			throw new RuntimeException(
					String.format("Exception with close connection. Exception message:{}", ex.getMessage()));
		}
	}
}