package task_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@WebServlet("/users/*")
public class HttpServlet extends javax.servlet.http.HttpServlet {

	private static final long serialVersionUID = 1L;

	private void sendAsJson(HttpServletResponse response, Object obj) throws IOException {
		Gson _gson = new Gson();
		response.setContentType("application/json");
		String res = _gson.toJson(obj);
		PrintWriter out = response.getWriter();

		out.print(res);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ArrayList<User> users = new ArrayList<User>();
		String pathInfo = request.getPathInfo();
		try {
			users = DatabaseActions.getUsers();

		} catch (Exception ex) {
			System.out.println("Connection to database failed...  " + ex.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		if (pathInfo == null || pathInfo.equals("/")) {
			sendAsJson(response, users);
			return;
		}

		String[] splits = pathInfo.split("/");
		String userId = splits[1];

		try {
			User user = DatabaseActions.getUser(userId);
			if (user == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			} else {
				sendAsJson(response, user);
			}
		} catch (SQLException ex) {
			System.out.println("Connection to database failed..." + ex.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Gson _gson = new Gson();
		String pathInfo = request.getPathInfo();

		if (pathInfo == null || pathInfo.equals("/")) {
			StringBuilder buffer = new StringBuilder();
			BufferedReader reader = request.getReader();
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			User user = _gson.fromJson(buffer.toString(), User.class);
			try {
				DatabaseActions.insert(user);
			} catch (SQLException ex) {
				System.out.println("Connection to database failed... " + ex.getMessage());
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

		Gson _gson = new Gson();
		String pathInfo = request.getPathInfo();
		if (pathInfo == null || pathInfo.equals("/")) {

			try {
				StringBuilder buffer = new StringBuilder();
				BufferedReader reader = request.getReader();
				String line;
				while ((line = reader.readLine()) != null) {
					buffer.append(line);
				}
				user = _gson.fromJson(buffer.toString(), User.class);
				String userId = Integer.toString(user.getId());
				if (DatabaseActions.getUser(userId) != null) {
					DatabaseActions.update(user);
					sendAsJson(response, DatabaseActions.getUser(userId));
				} else {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
				}
			} catch (SQLException ex) {
				System.out.println("Connection to database failed..." + ex.getMessage());
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
			if (DatabaseActions.getUser(userId) != null) {
				DatabaseActions.delete(userId);
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}

		} catch (Exception ex) {
			System.out.println("Connection to database failed..." + ex.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

}
