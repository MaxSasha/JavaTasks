package DTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import Entity.User;

public class DatabaseActions {
	
	public ArrayList<User> getUsers() throws SQLException {
		ArrayList<User> users = new ArrayList<User>();
		Connection conn = DatabaseConnection.getConnection();
		Statement statement = conn.createStatement();
		ResultSet result = statement.executeQuery("SELECT * FROM users");
		while (result.next()) {
			User user = User.builder()
					.id(result.getInt(1))
					.name(result.getString(2))
					.email(result.getString(3))
					.build();
			users.add(user);
		}
		return users;
	}

	public User getUser(String id) throws SQLException {
		User user = null;
		Connection conn = DatabaseConnection.getConnection();
		String sql = "SELECT * FROM users WHERE id = ?";
		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setString(1, id);
		ResultSet result = preparedStatement.executeQuery();
		if (result.next()) {
			user = User.builder()
					.id(result.getInt(1))
					.name(result.getString(2))
					.email(result.getString(3))
					.build();
		}
		return user;
	}

	public void insert(User user) throws SQLException {
		Connection conn = DatabaseConnection.getConnection();
		String sql = "INSERT INTO users (name, email) Values (?, ?)";
		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setString(1, user.getName());
		preparedStatement.setString(2, user.getEmail());
		preparedStatement.executeUpdate();
	}

	public void update(User user) throws SQLException {
		Connection conn = DatabaseConnection.getConnection();
		String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setString(1, user.getName());
		preparedStatement.setString(2, user.getEmail());
		preparedStatement.setInt(3, 1);
		preparedStatement.executeUpdate();
	}

	public void delete(String id) throws SQLException {
		Connection conn = DatabaseConnection.getConnection();
		String sql = "DELETE FROM users WHERE id = ?";
		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setString(1, id);
		preparedStatement.executeUpdate();
	}
}
