package com.maxsasha.javatasks.service;

import java.sql.SQLException;
import java.util.List;

import com.maxsasha.javatasks.db.repository.UserRepository;
import com.maxsasha.javatasks.entity.User;

public class UserService {
	private final UserRepository userRepository = new UserRepository();

	public List<User> getUsers() throws RuntimeException, SQLException {
		return userRepository.findAll();
	}

	public void createUser(User user) throws RuntimeException, SQLException {
		userRepository.create(user);
	}

	public User editUser(User user, String id) throws RuntimeException, SQLException {
		return userRepository.update(user, id);
	}

	public void deleteUser(String id) throws RuntimeException, SQLException {
		userRepository.deleteById(id);
	}
}