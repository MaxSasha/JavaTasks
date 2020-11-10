package com.maxsasha.javatasks.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.maxsasha.javatasks.db.DatabaseConnection;
import com.maxsasha.javatasks.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserRepository {
	private static final String SELECT_USERS_SQL = "SELECT * FROM users";
	private static final String SELECT_USER_BY_ID_SQL = "SELECT * FROM users WHERE id= ?";
	private static final String INSERT_USER_SQL = "INSERT INTO users (NAME, EMAIL) VALUES (?, ?)";
	private static final String UPDATE_USER_BY_ID_SQL = "UPDATE users SET name = ?, email = ? WHERE id = ?";
	private static final String DELETE_USER_BY_ID_SQL = "DELETE FROM users WHERE id = ?";
	private final DatabaseConnection dbConnection = new DatabaseConnection();

	public List<User> findAll() throws RuntimeException {
		List<User> listUsers = new ArrayList<>();
		Connection conn = null;
		try {
			conn = dbConnection.getConnection();
			Statement statement = conn.createStatement();
			ResultSet usersFromSql = statement.executeQuery(SELECT_USERS_SQL);
			while (usersFromSql.next()) {
				listUsers.add(extractFromResultSet(usersFromSql));
			}
		} catch (SQLException ex) {
			log.error(String.format("Error to get user. Exception message:{}", ex.getMessage()));
			throw new RuntimeException(String.format("Exception in getUsers. Exception message:{}", ex.getMessage()),
					ex);
		} finally {
			dbConnection.closeConnection(conn);
		}
		return listUsers;
	}

	public Optional<User> findById(int id) throws RuntimeException {
		User user = null;
		Connection conn = null;
		try {
			conn = dbConnection.getConnection();
			PreparedStatement preparedStatement = conn.prepareStatement(SELECT_USER_BY_ID_SQL);
			preparedStatement.setInt(1, id);
			ResultSet sqlResult = preparedStatement.executeQuery();
			if (sqlResult.next()) {
				user = extractFromResultSet(sqlResult);
			}
		} catch (SQLException ex) {
			log.error("Exception in getUser. Exception message:{}", ex.getMessage());
			throw new RuntimeException(String.format("Exception in getUser. Exception message:{}", ex.getMessage()));
		} finally {
			dbConnection.closeConnection(conn);
		}
		return Optional.ofNullable(user);
	}

	public Optional<User> upsert(User user) throws RuntimeException {
		Optional<User> userOp = Optional.ofNullable(user);
		Connection conn = null;
		try {
			conn = dbConnection.getConnection();
			PreparedStatement preparedStatement = null;
			boolean userExists = userOp.map(u -> findById(u.getId())).isPresent();
			if (userExists) {
				preparedStatement = conn.prepareStatement(UPDATE_USER_BY_ID_SQL);
				preparedStatement.setInt(3, userOp.get().getId());
			} else if (!userExists) {
				preparedStatement = conn.prepareStatement(INSERT_USER_SQL);
			}
			preparedStatement.setString(1, userOp.get().getName());
			preparedStatement.setString(2, userOp.get().getEmail());
			preparedStatement.executeUpdate();
		} catch (SQLException ex) {
			log.error("Exception in putUser. Exception message:{}", ex.getMessage());
			throw new RuntimeException(String.format("Exception in putUser. Exception message:{}", ex.getMessage()));
		} finally {
			dbConnection.closeConnection(conn);
		}
		return userOp;
	}

	public void deleteById(int id) throws RuntimeException {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = dbConnection.getConnection();
			preparedStatement = conn.prepareStatement(DELETE_USER_BY_ID_SQL);
			preparedStatement.setInt(1, id);
			preparedStatement.executeUpdate();
		} catch (SQLException ex) {
			log.error("Exception in deleteUser. Exception message:{}", ex.getMessage());
			throw new RuntimeException(String.format("Exception in putUser. Exception message:{}", ex.getMessage()));
		} finally {
			dbConnection.closeConnection(conn);
		}
	}

	private User extractFromResultSet(ResultSet sqlResult) throws SQLException {
		return User.builder().id(sqlResult.getInt("id")).name(sqlResult.getString("name"))
				.email(sqlResult.getString("email")).build();
	}
}