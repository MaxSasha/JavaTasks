package com.maxsasha.javatasks.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.maxsasha.javatasks.api.dto.UserDto;
import com.maxsasha.javatasks.api.transformer.RequestMapper;
import com.maxsasha.javatasks.db.repository.UserRepository;
import com.maxsasha.javatasks.entity.User;

import lombok.extern.log4j.Log4j;

@Log4j
public class UserService {
	private final UserRepository dbActions = new UserRepository();
	private final RequestMapper requestMapper = new RequestMapper();
	
	public List<UserDto> getUsers() throws SQLException {
		List<UserDto> users = null;
		users = new ArrayList<UserDto>();
		users = dbActions.getUsers();
		return users;
	}

	public void createUser(String userInfo) throws SQLException, JsonParseException, JsonMappingException, IOException {

		UserDto userDto = requestMapper.convertToDto(userInfo);
		dbActions.insert(userDto);
	}

	public User editUser(String userInfo) throws SQLException, JsonParseException, JsonMappingException, IOException {
		UserDto userDto = requestMapper.convertToDto(userInfo);
		int userId = userDto.getId();
		if (dbActions.getUser(userId) != null) {
			dbActions.update(userDto);
			log.info("Update user id:" + userId);
		} else {
			throw new NullPointerException("User not found");
		}
		return dbActions.getUser(userId);
	}

	public void deleteUser(int userId) throws SQLException {
		if (dbActions.getUser(userId) != null) {
			dbActions.delete(userId);
			log.info("Delete user id:" + userId);
		} else {
			System.out.println("" + userId);
			throw new NullPointerException("User not found");
		}

	}
}
