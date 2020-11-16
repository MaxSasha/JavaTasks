package com.maxsasha.javatasks.db.repository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.maxsasha.javatasks.api.util.PropertiesUtils;
import com.maxsasha.javatasks.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserRepository {
	private static final String SELECT_USERS_SQL = "SELECT * FROM users";
	private static final String DELETE_USER_BY_ID_SQL = "DELETE FROM users WHERE id = ?";
	private static final String INSERT_USER_SQL = "INSERT INTO users (name, email) VALUES (?, ?)";
	private static final String UPDATE_USER_BY_ID_SQL = "UPDATE users SET name = ?, email = ? WHERE id = ?";
	private final String url;
	private final String userSql;
	private final String password;

	public UserRepository() throws RuntimeException {
		try {
			Properties properties = PropertiesUtils.getProperties("application.property");
			url = properties.getProperty("url");
			userSql = properties.getProperty("user");
			password = properties.getProperty("pass");
			Class.forName(properties.getProperty("dbDriver"));
		} catch (ClassNotFoundException | IOException ex) {
			log.error("Error with initialize data for connection: {}", ex.getMessage());
			throw new RuntimeException(
					String.format("Exception with initialize data for connection:{0}", ex.getMessage()));
		}
	}

	public List<User> findAll() throws SQLException {
		List<User> listUsers = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(url, userSql, password);
				Statement statement = connection.createStatement()) {
			try (ResultSet usersFromSql = statement.executeQuery(SELECT_USERS_SQL)) {
				while (usersFromSql.next()) {
					listUsers.add(extractFromResultSet(usersFromSql));
				}
			}
		}
		return listUsers;
	}

	public void create(User user) throws SQLException {
		try (Connection connection = DriverManager.getConnection(url, userSql, password)) {
			try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL)) {
				prepareSqlQuery(preparedStatement, user).execute();
			}
		}
	}

	public User update(User user, String id) throws SQLException {
		try (Connection connection = DriverManager.getConnection(url, userSql, password)) {
			try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_BY_ID_SQL)) {
				preparedStatement.setString(3, id);
				prepareSqlQuery(preparedStatement, user).execute();
				return User.builder().id(Integer.parseInt(id)).name(user.getName()).email(user.getEmail()).build();
			}
		}
	}

	public void deleteById(String id) throws SQLException {
		try (Connection connection = DriverManager.getConnection(url, userSql, password);) {
			try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_BY_ID_SQL)) {
				preparedStatement.setString(1, id);
				preparedStatement.executeUpdate();
			}
		}
	}

	private User extractFromResultSet(ResultSet sqlResult) throws SQLException {
		return User.builder().id(sqlResult.getInt("id")).name(sqlResult.getString("name"))
				.email(sqlResult.getString("email")).build();
	}

	private PreparedStatement prepareSqlQuery(PreparedStatement preparedStatement, User user) throws SQLException {
		preparedStatement.setString(1, user.getName());
		preparedStatement.setString(2, user.getEmail());
		return preparedStatement;
	}
}