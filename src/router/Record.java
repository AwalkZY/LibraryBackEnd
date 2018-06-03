package router;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import database.DBconfig;
import model.RecordModel;
import model.UserModel;

/**
 * Servlet implementation class Record
 */
public class Record extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Record() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		if(pathInfo.equals("/getRecord")) response.getWriter().append(getRecord(request, response));
		else if(pathInfo.equals("/getMyRecord")) response.getWriter().append(getMyRecord(request, response));
		else request.getRequestDispatcher("/page404.jsp").forward(request,response);
	}

	private String getRecord(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		String id = request.getParameter("id");
		if (id == null) id = "0";
		RecordModel R = new RecordModel();
		return R.showRecordById(Integer.valueOf(id));
	}
	
	private String getMyRecord(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
        Map<String,Object> returnAns = new HashMap<>(); 
        Gson gson = new Gson();
		String resJSON = "";
		String token = request.getParameter("token");
		if (token == null) token = "";
		String [] strArr = new String[1];
		strArr[0] = token;
		int ans = new DBconfig().DBInnerCounter("select count(id) from user where token = ?",strArr,null);
		if (ans <= 0) {
	       	returnAns.put("status", "failure");
	       	returnAns.put("info", "Sorry, access denied!");
	    	resJSON = gson.toJson(returnAns);
	    	return resJSON;
		}
		RecordModel R = new RecordModel();
		return R.showRecordById(new UserModel().tokenIdentification(token));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
