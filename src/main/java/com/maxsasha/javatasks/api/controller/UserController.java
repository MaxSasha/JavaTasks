package com.maxsasha.javatasks.api.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.maxsasha.javatasks.api.dto.UserDto;
import com.maxsasha.javatasks.service.UserService;
import com.maxsasha.javatasks.entity.User;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("serial")
@Slf4j
@WebServlet("/users/*")
public class UserController extends javax.servlet.http.HttpServlet {

	private final UserService userService = new UserService();
	private final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

	private void sendAsJson(HttpServletResponse response, Object obj) throws IOException {
		response.setContentType("application/json");
		String json = ow.writeValueAsString(obj);
		PrintWriter out = response.getWriter();
		out.print(json);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			List<UserDto> users = userService.getUsers();
			sendAsJson(response, users);
		} catch (RuntimeException ex) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			userService.createUser(request);
			sendAsJson(response, "User successfully created");
			response.setStatus(201);
		} catch (RuntimeException ex) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			Optional<User> user = userService.editUser(request);
			if (user.isPresent()) {
				sendAsJson(response, user.get());
				response.setStatus(200);
				log.info("Update user:{}", user.get());
			} else if (!user.isPresent()) {
				sendAsJson(response, "User successfully created");
				response.setStatus(201);
			} else if (user.isEmpty()) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} catch (RuntimeException ex) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			userService.deleteUser(request);
		} catch (RuntimeException ex) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}