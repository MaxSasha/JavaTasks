package Services;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import DTO.DatabaseActions;
import Entity.User;
import lombok.extern.log4j.Log4j;

@SuppressWarnings("serial")
@Log4j
@WebServlet("/users/*")
public class HttpServlet extends javax.servlet.http.HttpServlet {

	private final DatabaseActions dbActions = new DatabaseActions();

	private void sendAsJson(HttpServletResponse response, Object obj) throws IOException {
		response.setContentType("application/json");
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(obj);
		PrintWriter out = response.getWriter();
		out.print(json);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ArrayList<User> users = new ArrayList<User>();
		String pathInfo = request.getPathInfo();

		try {
			users = dbActions.getUsers();
		} catch (Exception ex) {
			log.error("Connection to database failed...  " + ex.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		if (pathInfo == null || pathInfo.equals("/")) {
			sendAsJson(response, users);
			return;
		}

		String[] splits = pathInfo.split("/");
		String userId = splits[1];

		try {
			User user = dbActions.getUser(userId);
			if (user == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			} else {
				sendAsJson(response, user);
			}
		} catch (SQLException ex) {
			log.error("Connection to database failed..." + ex.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ObjectMapper mapper = new ObjectMapper();

		String pathInfo = request.getPathInfo();

		if (pathInfo == null || pathInfo.equals("/")) {
			StringBuilder buffer = new StringBuilder();
			BufferedReader reader = request.getReader();
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			User user = mapper.readValue(buffer.toString(), User.class);
			try {
				dbActions.insert(user);
			} catch (SQLException ex) {
				log.error("Connection to database failed..." + ex.getMessage());
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			sendAsJson(response, "Add successful");
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user;
		ObjectMapper mapper = new ObjectMapper();
		String pathInfo = request.getPathInfo();
		if (pathInfo == null || pathInfo.equals("/")) {

			try {
				StringBuilder buffer = new StringBuilder();
				BufferedReader reader = request.getReader();
				String line;
				while ((line = reader.readLine()) != null) {
					buffer.append(line);
				}
				user = mapper.readValue(buffer.toString(), User.class);
				String userId = Integer.toString(1);
				if (dbActions.getUser(userId) != null) {
					dbActions.update(user);
					log.info("Update user id:" + userId);
					sendAsJson(response, dbActions.getUser(userId));
				} else {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
				}
			} catch (SQLException ex) {
				log.error("Connection to database failed..." + ex.getMessage());
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		if (pathInfo == null || pathInfo.equals("/")) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String[] splits = pathInfo.split("/");

		try {
			String userId = splits[1];
			if (dbActions.getUser(userId) != null) {
				dbActions.delete(userId);
				log.info("Delete user id:" + userId);
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}

		} catch (Exception ex) {
			log.error("Connection to database failed..." + ex.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

}
