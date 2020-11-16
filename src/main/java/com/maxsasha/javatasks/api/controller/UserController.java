package com.maxsasha.javatasks.api.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
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
	private final ObjectWriter objWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

	private void sendAsJson(HttpServletResponse response, Object obj) throws IOException {
		response.setContentType("application/json");
		response.getWriter().print(objWriter.writeValueAsString(obj));
	}

	private String getRequestInfo(HttpServletRequest request) throws RuntimeException {
		try {
			return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		} catch (IOException ex) {
			log.error("Error with get request info {}", ex.getMessage());
			throw new RuntimeException(String.format("Error with get request info {0}", ex.getMessage()));
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			List<UserDto> users = UserTransformer.transfrom(userService.getUsers());
			sendAsJson(response, users);
		} catch (RuntimeException | SQLException | IOException ex) {
			log.error("Exception with get user. Exception message:{}", ex.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			String requestInfo = getRequestInfo(request);
			User userFromRequest = UserDtoUtil.fromJson(requestInfo);
			userService.createUser(userFromRequest);
			response.setStatus(201);
		} catch (RuntimeException | SQLException ex) {
			log.error("Exception with create user. Exception message:{}", ex.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			String info = getRequestInfo(request);
			User userFromRequest = UserDtoUtil.fromJson(info);
			String id = Objects.requireNonNullElse((request.getParameter("id")), "0");
			UserDto user = UserTransformer.transform(userService.editUser(userFromRequest, id));
			sendAsJson(response, user);
			response.setStatus(200);
		} catch (RuntimeException | SQLException | IOException ex) {
			log.error("Exception with put user. Exception message:{}", ex.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			String id = Objects.requireNonNullElse((request.getParameter("id")), "0");
			userService.deleteUser(id);
		} catch (RuntimeException | SQLException ex) {
			log.error("Exception with delete user. Exception message:{}", ex.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}