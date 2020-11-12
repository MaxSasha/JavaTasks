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
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.dbutils.DbUtils;

import com.maxsasha.javatasks.api.util.PropertiesUtils;
import com.maxsasha.javatasks.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserRepository {
	private static final String SELECT_USERS_SQL = "SELECT * FROM users";
	private static final String SELECT_USER_BY_ID_SQL = "SELECT * FROM users WHERE id= ?";
	private static final String INSERT_USER_SQL = "INSERT INTO users (NAME, EMAIL) VALUES (?, ?)";
	private static final String UPDATE_USER_BY_ID_SQL = "UPDATE users SET name = ?, email = ? WHERE id = ?";
	private static final String DELETE_USER_BY_ID_SQL = "DELETE FROM users WHERE id = ?";
	private final String url;
	private final String userSql;
	private final String password;
	private final String dbDriver;

	public UserRepository() throws RuntimeException {
		try {
			Properties properties = PropertiesUtils.getProperties("application.property");
			url = properties.getProperty("url");
			userSql = properties.getProperty("user");
			password = properties.getProperty("pass");
			dbDriver = properties.getProperty("dbDriver");
			Class.forName(dbDriver);
		} catch (ClassNotFoundException | IOException ex) {
			log.error("Error with initialize data for connection: {}", ex.getMessage());
			throw new RuntimeException(String.format("Exception with initialize data for connection:{0}", ex.getMessage()));
		}
	}

	public List<User> findAll() throws RuntimeException {
		List<User> listUsers = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(url, userSql, password);
				Statement statement = connection.createStatement();
				ResultSet usersFromSql = statement.executeQuery(SELECT_USERS_SQL)) {
			while (usersFromSql.next()) {
				listUsers.add(extractFromResultSet(usersFromSql));
			}
		} catch (SQLException ex) {
			log.error(String.format("Error to get user. Exception message:{}", ex.getMessage()));
			throw new RuntimeException(String.format("Exception in getUsers. Exception message:{0}", ex.getMessage()));
		}
		return listUsers;
	}

	public Optional<User> findById(int id) throws RuntimeException {
		ResultSet sqlResult = null;
		Optional<User> user = Optional.empty();
		try (Connection connection = DriverManager.getConnection(url, userSql, password);
				PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID_SQL)) {
			preparedStatement.setInt(1, id);
			sqlResult = preparedStatement.executeQuery();
			if (sqlResult.next()) {
				user = Optional.ofNullable(extractFromResultSet(sqlResult));
			}
		} catch (SQLException ex) {
			log.error("Exception in getUser. Exception message:{}", ex.getMessage());
			throw new RuntimeException(String.format("Exception in getUser. Exception message:{0}", ex.getMessage()));
		} finally {
			DbUtils.closeQuietly(sqlResult);
		}
		return user;
	}

	public Optional<User> upsert(User user) throws RuntimeException {
		Optional<User> userOp = Optional.ofNullable(user);
		PreparedStatement preparedStatement = null;
		try (Connection connection = DriverManager.getConnection(url, userSql, password)) {
			boolean userExists = userOp.map(u -> findById(u.getId())).isPresent();
			if (userExists) {
				preparedStatement = connection.prepareStatement(UPDATE_USER_BY_ID_SQL);
				preparedStatement.setInt(3, userOp.get().getId());
			} else if (!userExists) {
				preparedStatement = connection.prepareStatement(INSERT_USER_SQL);
			}
			preparedStatement.setString(1, userOp.get().getName());
			preparedStatement.setString(2, userOp.get().getEmail());
			preparedStatement.executeUpdate();
		} catch (SQLException ex) {
			log.error("Exception in putUser. Exception message:{}", ex.getMessage());
			throw new RuntimeException(String.format("Exception in putUser. Exception message:{0}", ex.getMessage()));
		} finally {
			DbUtils.closeQuietly(preparedStatement);
		}
		return userOp;
	}

	public void deleteById(int id) throws RuntimeException {
		try (Connection connection = DriverManager.getConnection(url, userSql, password);
				PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_BY_ID_SQL)) {
			preparedStatement.setInt(1, id);
			preparedStatement.executeUpdate();
		} catch (SQLException ex) {
			log.error("Exception in deleteUser. Exception message:{}", ex.getMessage());
			throw new RuntimeException(String.format("Exception in putUser. Exception message:{0}", ex.getMessage()));
		}
	}

	private User extractFromResultSet(ResultSet sqlResult) throws SQLException {
		return User.builder()
				.id(sqlResult.getInt("id"))
				.name(sqlResult.getString("name"))
				.email(sqlResult.getString("email"))
				.build();
	}
}