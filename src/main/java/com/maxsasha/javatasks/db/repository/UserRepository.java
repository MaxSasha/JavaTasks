package com.maxsasha.javatasks.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.maxsasha.javatasks.api.dto.UserDto;
import com.maxsasha.javatasks.api.transformer.UserTransformer;
import com.maxsasha.javatasks.db.DatabaseConnection;
import com.maxsasha.javatasks.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserRepository {
	private static final String usersSql = "SELECT * FROM users";
	private static final String userSql = "SELECT * FROM users WHERE id= ?";
	private static final String insertUserSql = "INSERT INTO users (NAME, EMAIL) VALUES (?, ?)";
	private static final String updateUserSql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
	private static final String deleteUserSql = "DELETE FROM users WHERE id = ?";
	private final UserTransformer transformer = new UserTransformer();
	private final DatabaseConnection dbConnection = new DatabaseConnection();

	public List<UserDto> getUsers() throws RuntimeException {
		List<UserDto> listUsers = new ArrayList<UserDto>();
		Connection conn = null;
		try {
			conn = dbConnection.getConnection();
			Statement statement = conn.createStatement();
			ResultSet users = statement.executeQuery(usersSql);
			while (users.next()) {
				User user = User.builder()
						.id(users.getInt("id"))
						.name(users.getString("name"))
						.email(users.getString("email"))
						.build();
				listUsers.add(transformer.transform(user));
			}
		} catch (SQLException ex) {
			log.error(String.format("Error to get user. Exception message:{}", ex.getMessage()));
			throw new RuntimeException(String.format("Exception in getUsers. Exception message:{}", ex.getMessage()),
					ex);
		} finally {
			try {
				conn.close();
			} catch (SQLException ex) {
				log.error("Can`t close connection. Exception message:{}", ex.getMessage());
				throw new RuntimeException(
						String.format("Exception with close connection. Exception message:{}", ex.getMessage()));
			}
		}
		return listUsers;
	}

	public Optional<User> getUser(int id) throws RuntimeException {
		User user = null;
		Connection conn = null;
		try {
			conn = dbConnection.getConnection();
			PreparedStatement preparedStatement = conn.prepareStatement(userSql);
			preparedStatement.setInt(1, id);
			ResultSet sqlResult = preparedStatement.executeQuery();
			if (sqlResult.next()) {
				user = User.builder()
						.id(sqlResult.getInt("id"))
						.name(sqlResult.getString("name"))
						.email(sqlResult.getString("email"))
						.build();
			}
		} catch (SQLException ex) {
			log.error("Exception in geUser. Exception message:{}", ex.getMessage());
			throw new RuntimeException(String.format("Exception in getUser. Exception message:{}", ex.getMessage()));
		} finally {
			try {
				conn.close();
			} catch (SQLException ex) {
				log.error(String.format("Can`t close connection. Exception message:{}", ex.getMessage()));
				throw new RuntimeException(
						String.format("Exception with close connection. Exception message:{}", ex.getMessage()));
			}
		}
		return Optional.ofNullable(user);
	}

	public Optional<User> putUser(UserDto userDto) throws RuntimeException {
		Optional<User> user = Optional.ofNullable(transformer.transform(userDto));
		Optional<User> checkExsistUser;
		Connection conn = null;
		try {
			conn = dbConnection.getConnection();
			PreparedStatement preparedStatement = null;
			checkExsistUser = getUser(user.get().getId() != null ? user.get().getId() : 0);
			if (checkExsistUser.isPresent()) {
				preparedStatement = conn.prepareStatement(updateUserSql);
				preparedStatement.setInt(3, user.get().getId());
				checkExsistUser = user;
			} else if (!checkExsistUser.isPresent()) {
				preparedStatement = conn.prepareStatement(insertUserSql);
			}
			preparedStatement.setString(1, user.get().getName());
			preparedStatement.setString(2, user.get().getEmail());
			preparedStatement.executeUpdate();
		} catch (SQLException ex) {
			log.error("Exception in putUser. Exception message:{}", ex.getMessage());
			throw new RuntimeException(String.format("Exception in putUser. Exception message:{}", ex.getMessage()));
		} finally {
			try {
				conn.close();
			} catch (SQLException ex) {
				log.error(String.format("Can`t close connection. Exception message:{}", ex.getMessage()));
				throw new RuntimeException(
						String.format("Exception with close connection. Exception message:{}", ex.getMessage()));
			}
		}
		return checkExsistUser;
	}

	public void delete(int id) throws RuntimeException {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = dbConnection.getConnection();
			preparedStatement = conn.prepareStatement(deleteUserSql);
			preparedStatement.setInt(1, id);
			preparedStatement.executeUpdate();
		} catch (SQLException ex) {
			log.error("Exception in deleteUser. Exception message:{}", ex.getMessage());
			throw new RuntimeException(String.format("Exception in putUser. Exception message:{}", ex.getMessage()));
		} finally {
			try {
				conn.close();
			} catch (SQLException ex) {
				log.error(String.format("Can`t close connection. Exception message:{0}", ex.getMessage()));
				throw new RuntimeException(
						String.format("Exception with close connection. Exception message:{}", ex.getMessage()));
			}
		}
	}
}