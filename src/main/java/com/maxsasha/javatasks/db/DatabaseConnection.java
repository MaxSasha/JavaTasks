package com.maxsasha.javatasks.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j
public class DatabaseConnection {
	@Getter private static Connection connection = null;
	private static final String url;
	private static final String user;
	private static final String pass;

	static {
		url = "jdbc:mysql://localhost/task1?useUnicode=true&serverTimezone=UTC";
		user = "insane";
		pass = "SMax2000";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(url, user, pass);
		} catch (ClassNotFoundException | SQLException ex) {
			log.error("Error with db connection: " + ex.getMessage());

		}
	}
}
