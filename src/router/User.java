package router;



import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.UserModel;

/**
 * Servlet implementation class User
 */
public class User extends HttpServlet {
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public User() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		if(pathInfo.equals("/getUser")) response.getWriter().append(getUser(request, response));
		else if (pathInfo.equals("/delUser")) response.getWriter().append(delUser(request, response));
		else if (pathInfo.equals("/getUserByToken")) response.getWriter().append(getUserByToken(request, response));
		else request.getRequestDispatcher("/page404.jsp").forward(request,response);
	}

	private String getUserByToken(HttpServletRequest request, HttpServletResponse response) {
		String token = request.getParameter("token");
		if (token == null) token = "";
		return new UserModel().getUserByToken(token);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String pathInfo = request.getPathInfo();
		//if (pathInfo.equals("/editUser")) response.getWriter().append(getUser(request, response));
		if (pathInfo.equals("/login")) response.getWriter().append(login(request, response));
		else if (pathInfo.equals("/register")) response.getWriter().append(register(request, response));
		else request.getRequestDispatcher("/page404.jsp").forward(request,response);
	}

	private String register(HttpServletRequest request, HttpServletResponse response) {

		String name = request.getParameter("name");
		if (name == null) name = "";
		String pass = request.getParameter("pass");
		if (pass == null) pass = "";
		String sex = request.getParameter("sex");
		if (sex == null) sex = "";
		String email = request.getParameter("email");
		if (email == null) email = "";
		UserModel A = new UserModel();
		return A.register(name, pass, sex, email);
	}

	private String login(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		String name = request.getParameter("name");
		String pass = request.getParameter("pass");
		UserModel A = new UserModel();
		if (name == null) name = "";
		if (pass == null) pass = "";
		return A.login(name,pass);
	}
	
	private String getUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		UserModel A = new UserModel();
		return A.getAllUser();
	}
	
	private String delUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		UserModel A = new UserModel();
		String id = request.getParameter("id");
		if (id == null) id = "0";
		return A.delUser(id);
	}
	
}
