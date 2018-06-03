package model;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import database.DBconfig;

public class RecordModel {
	Integer id;
	String lendTime;
	String returnTime;
	Integer giveback;
	Integer user_id;
	Integer book_id;
	
	public Integer addRecord(Integer book_id,Integer user_id)
	{
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lendTime = sdf.format(d);
		String [] strArr = new String [1];
		strArr[0] = lendTime;
		Integer [] intArr = new Integer[2];
		intArr[0] = book_id;
		intArr[1] = user_id;
		DBconfig DB = new DBconfig();
		return DB.DBInnerUpdater("Insert into record(lendTime,book_id,user_id,giveback) values(?,?,?,0)",strArr,intArr);
	}
	
	@SuppressWarnings("finally")
	private String DBSelector(String sql,String[] strParams,Integer [] intParams) {
		Connection con;
        DBconfig DB = new DBconfig();
        Map<String,Object> returnAns = new HashMap<>(); 
        Set<Object> resultSet = new HashSet<Object>(); 
        Gson gson = new Gson();
		String resJSON = "";
        try {
            Class.forName(DB.driver);
            con = DriverManager.getConnection(DB.url,DB.user,DB.password);
            if(con.isClosed())
            {
            	returnAns.put("status", "failure");
            	returnAns.put("info", "The database can't be connected!");
        		resJSON = gson.toJson(returnAns);
        		return resJSON;
            }
            PreparedStatement preState = con.prepareStatement(sql);
            int len1 = (strParams != null) ? strParams.length : 0;
            int len2 = (intParams != null) ? intParams.length : 0;
            for (int i = 0; i < len1; i++)
            	preState.setString(i+1, strParams[i]);
            for (int i = 0; i < len2; i++)
            	preState.setInt(i+len1+1, intParams[i]);
            ResultSet res = preState.executeQuery();
            boolean flag = false;
            BookModel B = new BookModel();
            String title,author;
            while(res.next()){
            	flag = true;
                lendTime = res.getString("lendTime");
                returnTime = res.getString("returnTime");
                user_id = res.getInt("user_id");
                book_id = res.getInt("book_id");
                title = B.getTitleById(book_id);
                author = B.getAuthorById(book_id);
                id = res.getInt("id");
                giveback = res.getInt("giveback");
                //Êä³ö½á¹û
                Map<String,Object> nowResult = new HashMap<>(); 
                nowResult.put("id", id);
                nowResult.put("giveback", giveback);
                nowResult.put("book_author", author);
                nowResult.put("book_title", title);
                nowResult.put("book_id",book_id);
                nowResult.put("user_id", user_id);
                nowResult.put("lendTime", lendTime);
                nowResult.put("returnTime", returnTime);
                resultSet.add(nowResult);
            }
            if (flag)
            {
            	returnAns.put("status", "success");            
            	returnAns.put("data", resultSet);
            }
            else 
            {
            	returnAns.put("status", "failure");            
            	returnAns.put("info", "No information found");
            }
            resJSON = gson.toJson(returnAns);
            res.close();
            con.close();
        } catch(ClassNotFoundException e) {   
        	returnAns.put("status", "failure");
        	returnAns.put("info", "Sorry,can`t find the Driver!");
    		resJSON = gson.toJson(returnAns);
            e.printStackTrace();   
            } catch(SQLException e) {
            e.printStackTrace();  
            }catch (Exception e) {
            e.printStackTrace();
        }finally{
        	return resJSON;
        }
	}
	
	public Integer endRecord(Integer book_id,Integer user_id)
	{
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String returnTime = sdf.format(d);
		String [] strArr = new String [1];
		strArr[0] = returnTime;
		Integer [] intArr = new Integer[2];
		intArr[0] = book_id;
		intArr[1] = user_id;
		DBconfig DB = new DBconfig();
		return DB.DBInnerUpdater("update record set returnTime = ?, giveback = 1 where book_id = ? and user_id = ? and giveback = 0",strArr,intArr);
	}
	
	public String showRecordById(Integer id)
	{
		Integer [] intArr = new Integer[1];
		intArr[0] = id;
		return DBSelector("select * from record where user_id = ?",null,intArr);
	}

}
