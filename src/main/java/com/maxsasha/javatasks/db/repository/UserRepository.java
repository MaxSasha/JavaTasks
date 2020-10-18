package com.maxsasha.javatasks.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.maxsasha.javatasks.api.dto.UserDto;
import com.maxsasha.javatasks.api.transformer.UserTransformer;
import com.maxsasha.javatasks.db.*;
import com.maxsasha.javatasks.entity.*;

public class UserRepository {
    private static final String usersSql="SELECT * FROM users";
	private static final String userSql="SELECT * FROM users WHERE id = ?";
	private static final String insertUserSql="INSERT INTO users (name, email) Values (?, ?)";
	private static final String updateUserSql="UPDATE users SET name = ?, email = ? WHERE id = ?";
	private static final String deleteUserSql="DELETE FROM users WHERE id = ?";
	private final UserTransformer transformer = new UserTransformer();
	
	public ArrayList<UserDto> getUsers() throws SQLException {
		ArrayList<UserDto> listUsers = new ArrayList<UserDto>();
		Connection conn = DatabaseConnection.getConnection();
		Statement statement = conn.createStatement();
		ResultSet users = statement.executeQuery(usersSql);
		while (users.next()) {
			 User user = User.builder()
					.id(users.getInt(1))
					.name(users.getString(2))
					.email(users.getString(3))
					.build();
			 
			
			listUsers.add(transformer.toDto(user));
			
		}
		return listUsers;
	}

	public User getUser(int id) throws SQLException {
		User user = null;
		Connection conn = DatabaseConnection.getConnection();
		PreparedStatement preparedStatement = conn.prepareStatement(userSql);
		preparedStatement.setInt(1, id);
		ResultSet sqlResult = preparedStatement.executeQuery();
		if (sqlResult.next()) {
			user = User.builder()
					.id(sqlResult.getInt(1))
					.name(sqlResult.getString(2))
					.email(sqlResult.getString(3))
					.build();
		}
		return user;
	}

	public void insert(UserDto userDto) throws SQLException {
		Connection conn = DatabaseConnection.getConnection();
		User user = transformer.toEntity(userDto);
		PreparedStatement preparedStatement = conn.prepareStatement(insertUserSql);
		preparedStatement.setString(1, user.getName());
		preparedStatement.setString(2, user.getEmail());
		preparedStatement.executeUpdate();
	}

	public void update(UserDto userDto) throws SQLException {
		Connection conn = DatabaseConnection.getConnection();
		User user = transformer.toEntity(userDto);
		PreparedStatement preparedStatement = conn.prepareStatement(updateUserSql);
		preparedStatement.setString(1, user.getName());
		preparedStatement.setString(2, user.getEmail());
		preparedStatement.setInt(3, user.getId());
		preparedStatement.executeUpdate();
	}

	public void delete(int id) throws SQLException {
		Connection conn = DatabaseConnection.getConnection();
		PreparedStatement preparedStatement = conn.prepareStatement(deleteUserSql);
		preparedStatement.setInt(1, id);
		preparedStatement.executeUpdate();
	}
}
