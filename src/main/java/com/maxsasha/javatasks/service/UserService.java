package com.maxsasha.javatasks.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.maxsasha.javatasks.api.dto.UserDto;
import com.maxsasha.javatasks.api.transformer.RequestMapper;
import com.maxsasha.javatasks.db.repository.UserRepository;
import com.maxsasha.javatasks.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserService {
	private final UserRepository dbActions = new UserRepository();
	private final RequestMapper requestMapper = new RequestMapper();

	public List<UserDto> getUsers() throws RuntimeException {
		List<UserDto> users = new ArrayList<UserDto>();
		users = dbActions.getUsers();
		return users;
	}

	public void createUser(HttpServletRequest request) throws RuntimeException {
		String info = getRequestInfo(request);
		UserDto userDto = requestMapper.convertToDto(info);
		dbActions.putUser(userDto);
	}

	public Optional<User> editUser(HttpServletRequest request) throws RuntimeException {
		String info = getRequestInfo(request);
		UserDto userDto = requestMapper.convertToDto(info);
		return dbActions.putUser(userDto);
	}

	public void deleteUser(HttpServletRequest request) throws RuntimeException {
		int userId = Integer.parseInt(Objects.requireNonNullElse((request.getParameter("id")), "0"));
		dbActions.delete(userId);
	}

	private String getRequestInfo(HttpServletRequest request) throws RuntimeException {
		try {
			return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		} catch (IOException ex) {
			log.error("Error with get request info", ex.getMessage());
			throw new RuntimeException(String.format("Error with get request info", ex.getMessage()));
		}
	}
}