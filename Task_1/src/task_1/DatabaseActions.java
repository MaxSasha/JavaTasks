package task_1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseActions {

	public static ArrayList<User> getUsers() throws SQLException {
		ArrayList<User> users = new ArrayList<User>();
		Connection conn = DatabaseConnection.getConnection();
		Statement statement = conn.createStatement();
		ResultSet result = statement.executeQuery("SELECT * FROM users");
		while (result.next()) {
			int id = result.getInt(1);
			String name = result.getString(2);
			String email = result.getString(3);
			User user = new User(id, name, email);
			users.add(user);
		}
		return users;
	}

	public static User getUser(String id) throws SQLException {
		User user = null;
		Connection conn = DatabaseConnection.getConnection();
		String sql = "SELECT * FROM users WHERE id = ?";
		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setString(1, id);
		ResultSet result = preparedStatement.executeQuery();
		if (result.next()) {
			int userId = result.getInt(1);
			String name = result.getString(2);
			String email = result.getString(3);
			user = new User(userId, name, email);
		}
		return user;
	}

	public static void insert(User user) throws SQLException {
		Connection conn = DatabaseConnection.getConnection();
		String sql = "INSERT INTO users (name, email) Values (?, ?)";
		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setString(1, user.getName());
		preparedStatement.setString(2, user.getEmail());
		preparedStatement.executeUpdate();
	}

	public static void update(User user) throws SQLException {
		Connection conn = DatabaseConnection.getConnection();
		String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setString(1, user.getName());
		preparedStatement.setString(2, user.getEmail());
		preparedStatement.setInt(3, user.getId());
		preparedStatement.executeUpdate();
	}

	public static void delete(String id) throws SQLException {
		Connection conn = DatabaseConnection.getConnection();
		String sql = "DELETE FROM users WHERE id = ?";
		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		preparedStatement.setString(1, id);
		preparedStatement.executeUpdate();
	}
}
