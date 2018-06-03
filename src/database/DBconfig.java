package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class DBconfig {
	public String driver = "com.mysql.jdbc.Driver";
    //URL指向要访问的数据库名my_data
	public String url = "jdbc:mysql://120.78.131.195:3306/library?useSSL=false";
    //MySQL配置时的用户名
	public String user = "LibraryAdmin";
    //MySQL配置时的密码
	public String password = "Database";
	
	@SuppressWarnings("finally")
	public Integer DBInnerUpdater(String sql,String[] strParams,Integer[] intParams) {
		Connection con;
		Integer ans = -1;
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url,user,password);
            if(con.isClosed()) 	return -1;
            PreparedStatement preState = con.prepareStatement(sql);
            int len1 = (strParams != null) ? strParams.length : 0;
            int len2 = (intParams != null) ? intParams.length : 0;
            for (int i = 0; i < len1; i++)
            	preState.setString(i+1, strParams[i]);
            for (int i = 0; i < len2; i++)
            	preState.setInt(i+len1+1, intParams[i]);
            ans = preState.executeUpdate();
            con.close();
        } catch(ClassNotFoundException e) {   
            e.printStackTrace();   
            } catch(SQLException e) {
            e.printStackTrace();  
            }catch (Exception e) {
            e.printStackTrace();
        }finally{
        	return ans;
        }
	}
	
	public String DBOuterUpdater(String sql, String[] strArr, Integer[] numArr) {
		Integer ans = DBInnerUpdater(sql,strArr,numArr);
        Map<String,Object> returnAns = new HashMap<>(); 
        Gson gson = new Gson();
		String resJSON = "";
		if (ans == -1)
		{
			returnAns.put("status", "failure");
        	returnAns.put("info", "The database can't be connected!");
    		resJSON = gson.toJson(returnAns);
    		return resJSON;
		}
		returnAns.put("status", "success");
        returnAns.put("data", ans);
		resJSON = gson.toJson(returnAns);
		return resJSON;
	}
	
	@SuppressWarnings("finally")
	public Integer DBInnerCounter(String sql,String[] strParams,Integer[] intParams) {
		Connection con;
        DBconfig DB = new DBconfig();
        Integer ans = -1;
        try {
            Class.forName(DB.driver);
            con = DriverManager.getConnection(DB.url,DB.user,DB.password);
            if(con.isClosed()) return -1;
            PreparedStatement preState = con.prepareStatement(sql);
            int len1 = (strParams != null) ? strParams.length : 0;
            int len2 = (intParams != null) ? intParams.length : 0;
            for (int i = 0; i < len1; i++)
            	preState.setString(i+1, strParams[i]);
            for (int i = 0; i < len2; i++)
            	preState.setInt(i+len1+1, intParams[i]);
            ResultSet res = preState.executeQuery();
            if(res.next()) ans = res.getInt(1);
            res.close();
            con.close();
        } catch(ClassNotFoundException e) {   
            e.printStackTrace();   
            } catch(SQLException e) {
            e.printStackTrace();  
            }catch (Exception e) {
            e.printStackTrace();
        }finally{
        	return ans;
        }
	}
}
