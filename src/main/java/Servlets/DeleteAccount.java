package Servlets;

import Exceptions.MyException;
import Resources.AccessOperation;
import Resources.Account;
import Resources.Operation;
import Util.AccessControlCapabilities;
import Util.Authenticator;
import Util.Storage;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static Util.Constants.UNKNOWN_ERROR_MSG;

public class DeleteAccount extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		String user = request.getParameter("user");
		Account acc = null;
		try {
			acc = Authenticator.login(request, response);
			if (AccessControlCapabilities.checkPermission(acc.getCapabilities(), "WebApp.Users", AccessOperation.DELETE)) {
				Authenticator.delete_account(user);
				
				out.println("Account (" + user + ") was deleted");
				Storage.logOperation(acc.getUsername(), Operation.DELETE_ACCOUNT, true, "Deleted account: " + user);
			} else {
				out.println("Permission denied");
				Storage.logOperation(acc.getUsername(), Operation.DELETE_ACCOUNT, false, "Permission denied. Tried to delete account: " + user);
			}
		} catch (MyException e) {
			out.print(e.getHtmlMsg());
			assert acc != null;
			Storage.logOperation(acc.getUsername(), Operation.DELETE_ACCOUNT, false, "Tried to delete account: " + user);
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
			out.println(UNKNOWN_ERROR_MSG);
		} finally {
			RequestDispatcher rs = request.getRequestDispatcher("deleteAcc.jsp");
			rs.include(request, response);
		}
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		RequestDispatcher rs = request.getRequestDispatcher("deleteAcc.jsp");
		rs.include(request, response);
	}
}
