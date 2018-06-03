package router;


import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.BookModel;

/**
 * Servlet implementation class Book
 */
public class Book extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Book() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		if(pathInfo.equals("/getBook")) response.getWriter().append(getBook(request, response));
		else if (pathInfo.equals("/delBook")) response.getWriter().append(delBook(request, response));
		else if (pathInfo.equals("/lendBook")) response.getWriter().append(lendBook(request,response));
		else if (pathInfo.equals("/returnBook")) response.getWriter().append(returnBook(request,response));
		else request.getRequestDispatcher("/page404.jsp").forward(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		if(pathInfo.equals("/addBook")) response.getWriter().append(addBook(request, response));
		else if (pathInfo.equals("/editBook")) response.getWriter().append(editBook(request,response));
		else request.getRequestDispatcher("/page404.jsp").forward(request,response);
	}
	
	private String getBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		return new BookModel().getBook();
	}
	
	private String delBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		if (id == null) id = "0";
		return new BookModel().delBook(id);
	}
	
	private String editBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String author = request.getParameter("author");
		String title = request.getParameter("title");
		String number = request.getParameter("number");
		String id = request.getParameter("id");
		if (author == null) author = "";
		if (title == null) title = "";
		if (number == null) number = "0";
		if (id == null) id = "0";
		return new BookModel().editBook(id,author,title,number);
	}
	
	private String addBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String author = request.getParameter("author");
		String title = request.getParameter("title");
		String number = request.getParameter("number");
		if (author == null) author = "";
		if (title == null) title = "";
		if (number == null) number = "0";
		return new BookModel().addBook(author, title, number);
	}
	
	private String lendBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		String token = request.getParameter("token");
		if (token == null) token = "";
		if (id == null) id = "0";
		return new BookModel().lendBook(id,token);
	}
	
	private String returnBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String book_id = request.getParameter("book_id");
		String user_id = request.getParameter("user_id");
		if (book_id == null) book_id = "";
		if (user_id == null) user_id = "0";
		return new BookModel().returnBook(book_id,user_id);
	}
}
