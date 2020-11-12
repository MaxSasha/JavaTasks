package com.maxsasha.javatasks.service;

import java.util.List;
import java.util.Optional;

import com.maxsasha.javatasks.db.repository.UserRepository;
import com.maxsasha.javatasks.entity.User;

public class UserService {
	private final UserRepository dbActions = new UserRepository();

	public List<User> getUsers() throws RuntimeException {
		return dbActions.findAll();
	}

	public void createUser(User user) throws RuntimeException {
		dbActions.upsert(user);
	}

	public Optional<User> editUser(User user) throws RuntimeException {
		return dbActions.upsert(user);
	}

	public void deleteUser(int userId) throws RuntimeException {
		dbActions.deleteById(userId);
	}
}