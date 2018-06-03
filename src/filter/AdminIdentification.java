package filter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import com.google.gson.Gson;

import database.DBconfig;

/**
 * Servlet Filter implementation class AdminIdentification
 */
@WebFilter("/AdminIdentification")
public class AdminIdentification implements Filter {

    /**
     * Default constructor. 
     */
    public AdminIdentification() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		// place your code here
		String token = request.getParameter("token");
		if (token != null && validate(token)) chain.doFilter(request, response); // pass the request along the filter chain
		else {
			Map<String,Object> returnAns = new HashMap<>(); 
			returnAns.put("status","failure");
			returnAns.put("info", "Access Denied");
			Gson gson = new Gson();
			String resJSON = gson.toJson(returnAns);
			response.getWriter().append(resJSON);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}
	
	private boolean validate(String token)
	{
		Connection con;
        //����������
        DBconfig DB = new DBconfig();
        //������ѯ�����
        try {
            //������������
            Class.forName(DB.driver);
            //1.getConnection()����������MySQL���ݿ⣡��
            con = DriverManager.getConnection(DB.url,DB.user,DB.password);
            if(con.isClosed())
            	return false;
            //2.����statement���������ִ��SQL��䣡��
            //Ҫִ�е�SQL���
            //3.ResultSet�࣬������Ż�ȡ�Ľ��������
            String sql = "select token from user where token = ? and admin >= 1";
            PreparedStatement preState = con.prepareStatement(sql);
            preState.setString(1, token);
            ResultSet res = preState.executeQuery();
            Boolean ans = false;
            if(res.next()) ans = true;
            res.close();
            con.close();
            return ans;
        } catch(ClassNotFoundException e) {   
            //���ݿ��������쳣����
            //System.out.println("Sorry,can`t find the Driver!");  
            e.printStackTrace();   
            } catch(SQLException e) {
            //���ݿ�����ʧ���쳣����
            e.printStackTrace();  
            }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        	return false;
        }
		return false;
	}

}
