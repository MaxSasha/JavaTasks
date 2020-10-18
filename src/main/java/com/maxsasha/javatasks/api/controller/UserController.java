package com.maxsasha.javatasks.api.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.maxsasha.javatasks.api.dto.UserDto;
import com.maxsasha.javatasks.service.UserService;

import lombok.extern.log4j.Log4j;

@SuppressWarnings("serial")
@Log4j
@WebServlet("/users/*")
public class UserController extends javax.servlet.http.HttpServlet {

	private final UserService userService = new UserService();

	private void sendAsJson(HttpServletResponse response, Object obj) throws IOException {
		response.setContentType("application/json");
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(obj);
		PrintWriter out = response.getWriter();
		out.print(json);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			List<UserDto> users = userService.getUsers();
			sendAsJson(response, users);
		} catch (SQLException | IOException ex) {
			log.error("Exception on get user request. Exception message: " + ex.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			userService.createUser(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));
			sendAsJson(response, "Add successful");
		} catch (SQLException | IOException ex) {
			log.error("Exception on add user request. Exception message: " + ex.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			sendAsJson(response, userService
					.editUser(request.getReader().lines().collect(Collectors.joining(System.lineSeparator()))));
		} catch (SQLException | IOException ex) {
			log.error("Exception on update user request. Exception message: " + ex.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (NullPointerException ex) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			userService.deleteUser(Integer.parseInt(Objects.requireNonNullElse((request.getParameter("id")), "0")));
		} catch (SQLException ex) {
			log.info("Exception on delete user request. Exception message: " + ex.getMessage());
			response.sendError(HttpServletResponse.SC_NOT_FOUND);

		} catch (NullPointerException ex) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

}
