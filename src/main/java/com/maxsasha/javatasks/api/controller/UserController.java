package com.maxsasha.javatasks.api.controller;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.maxsasha.javatasks.api.dto.UserDto;
import com.maxsasha.javatasks.api.transformer.UserTransformer;
import com.maxsasha.javatasks.api.util.UserDtoUtil;
import com.maxsasha.javatasks.entity.User;
import com.maxsasha.javatasks.service.UserService;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("serial")
@Slf4j
@WebServlet("/users/*")
public class UserController extends javax.servlet.http.HttpServlet {

	private final UserService userService = new UserService();
	private final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

	private void sendAsJson(HttpServletResponse response, Object obj) throws IOException {
		response.setContentType("application/json");
		response.getWriter().print(ow.writeValueAsString(obj));
	}

	private String getRequestInfo(HttpServletRequest request) throws RuntimeException {
		try {
			return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		} catch (IOException ex) {
			log.error("Error with get request info", ex.getMessage());
			throw new RuntimeException(String.format("Error with get request info", ex.getMessage()));
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			List<UserDto> users = UserTransformer.transfrom(userService.getUsers());
			sendAsJson(response, users);
		} catch (RuntimeException ex) {
			log.error("Exception with get user. Exception message:{}", ex.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			String requestInfo = getRequestInfo(request);
			User userFromRequest = UserDtoUtil.fromJson(requestInfo);
			userService.createUser(userFromRequest);
			sendAsJson(response, "User successfully created");
			response.setStatus(201);
		} catch (RuntimeException ex) {
			log.error("Exception with create user. Exception message:{}", ex.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			String info = getRequestInfo(request);
			User userFromRequest = UserDtoUtil.fromJson(info);
			Optional<UserDto> user = Optional
					.ofNullable(UserTransformer.transform(userService.editUser(userFromRequest).get()));
			if (user.isPresent()) {
				sendAsJson(response, user.get());
				response.setStatus(200);
				log.info("Update user:{}", user.get());
			} else if (user.isEmpty()) {
				sendAsJson(response, "User successfully created");
				response.setStatus(201);
			}
		} catch (RuntimeException ex) {
			log.error("Exception with put user. Exception message:{}", ex.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			int userId = Integer.parseInt(Objects.requireNonNullElse((request.getParameter("id")), "0"));
			userService.deleteUser(userId);
		} catch (RuntimeException ex) {
			log.error("Exception with delete user. Exception message:{}", ex.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}