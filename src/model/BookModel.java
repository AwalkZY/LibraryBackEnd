package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import database.DBconfig;

public class BookModel {
	String author;
	String title;
	Integer number;
	Integer id;
	
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
            while(res.next()){
            	flag = true;
                author = res.getString("author");
                title = res.getString("title");
                number = res.getInt("number");
                id = res.getInt("id");
                //Êä³ö½á¹û
                Map<String,Object> nowResult = new HashMap<>(); 
                nowResult.put("id", id);
                nowResult.put("number", number);
                nowResult.put("title", title);
                nowResult.put("author", author);
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
	
	public String addBook(String author,String title,String number)
	{
		Map<String,Object> returnAns = new HashMap<>(); 
        Gson gson = new Gson();
		String resJSON = "";
		Pattern validation;
		Matcher match;
		validation = Pattern.compile("^(?!_)(?!.*?_$)['. a-zA-Z0-9_\\u4e00-\\u9fa5]+$");
		match = validation.matcher(author);
		if (!match.matches())
		{
	        returnAns.put("status", "failure");
	        returnAns.put("info", "Sorry, the format of author is invalid!");
	    	resJSON = gson.toJson(returnAns);
	    	return resJSON;
		}
		String [] strArr = new String[2];
		strArr[0] = author;
		strArr[1] = title;
		DBconfig DB = new DBconfig();
		Integer occupied = DB.DBInnerCounter("Select count(author) from book where author = ? and title = ?",strArr,null);
		if (occupied > 0) 
		{
	        returnAns.put("status", "failure");
	        returnAns.put("info", "Sorry, this title and author have been occupied!");
	    	resJSON = gson.toJson(returnAns);
	    	return resJSON;
		}
		match = validation.matcher(title);
		if (!match.matches())
		{
	       	returnAns.put("status", "failure");
	       	returnAns.put("info", "Sorry, the format of title is invalid!");
	    	resJSON = gson.toJson(returnAns);
	    	return resJSON;
		}
		validation = Pattern.compile("[0-9]+");
		match = validation.matcher(number);
		if (!match.matches())
		{
	       	returnAns.put("status", "failure");
	       	returnAns.put("info", "Sorry, the format of number is invalid!");
	    	resJSON = gson.toJson(returnAns);
	    	return resJSON;
		}
		strArr = new String[2];
		Integer [] numArr = new Integer[1];
		strArr[0] = author;
		strArr[1] = title;
		numArr[0] = Integer.valueOf(number);
		return DB.DBOuterUpdater("Insert into book(author,title,number) values(?,?,?)",strArr,numArr);
	}

	public String editBook(String id, String author, String title, String number) {
		Map<String,Object> returnAns = new HashMap<>(); 
        Gson gson = new Gson();
		String resJSON = "";
		Pattern validation;
		Matcher match;
		validation = Pattern.compile("^(?!_)(?!.*?_$)['. a-zA-Z0-9_\\u4e00-\\u9fa5]+$");
		match = validation.matcher(author);
		if (!match.matches())
		{
	        returnAns.put("status", "failure");
	        returnAns.put("info", "Sorry, the format of author is invalid!");
	    	resJSON = gson.toJson(returnAns);
	    	return resJSON;
		}
		match = validation.matcher(title);
		if (!match.matches())
		{
	       	returnAns.put("status", "failure");
	       	returnAns.put("info", "Sorry, the format of title is invalid!");
	    	resJSON = gson.toJson(returnAns);
	    	return resJSON;
		}
		validation = Pattern.compile("[0-9]+");
		match = validation.matcher(number);
		if (!match.matches())
		{
	       	returnAns.put("status", "failure");
	       	returnAns.put("info", "Sorry, the format of number is invalid!");
	    	resJSON = gson.toJson(returnAns);
	    	return resJSON;
		}
		match = validation.matcher(id);
		if (!match.matches())
		{
	       	returnAns.put("status", "failure");
	       	returnAns.put("info", "Sorry, the format of number is invalid!");
	    	resJSON = gson.toJson(returnAns);
	    	return resJSON;
		}
		String [] strArr = new String[2];
		strArr[0] = author;
		strArr[1] = title;
		Integer [] numArr = new Integer[1];
		numArr[0] = Integer.valueOf(id);
		DBconfig DB = new DBconfig(); 
		Integer occupied = DB.DBInnerCounter("Select count(author) from book where author = ? and title = ? and id != ?",strArr,null);
		if (occupied > 0) 
		{
	        returnAns.put("status", "failure");
	        returnAns.put("info", "Sorry, this title and author have been occupied!");
	    	resJSON = gson.toJson(returnAns);
	    	return resJSON;
		}
		strArr = new String[2];
		numArr = new Integer[2];
		strArr[0] = author;
		strArr[1] = title;
		numArr[0] = Integer.valueOf(number);
		numArr[1] = Integer.valueOf(id);
		return DB.DBOuterUpdater("update book set author = ? , title = ? , number = ? where id = ?",strArr,numArr);
	}
	
	
	public String getBook()
	{
		return DBSelector("select * from book ORDER BY id",null,null);
	}
	
	public String findByAuthor(String author)
	{
		String [] strArr = new String[1];
		strArr[0] = author;
		return DBSelector("select * from book where author = ?",strArr,null);
	}
	
	public String findByTitle(String title)
	{
		String [] strArr = new String[1];
		strArr[0] = title;
		return DBSelector("select * from book where title = ?",strArr,null);
	}
	
	@SuppressWarnings("finally")
	public String DBSelectorByPK(String sql,Integer id)
	{
		Connection con;
        String ans = null;
        DBconfig DB = new DBconfig();
        try {
            Class.forName(DB.driver);
            con = DriverManager.getConnection(DB.url,DB.user,DB.password);
            if(con.isClosed()) return null;
            PreparedStatement preState = con.prepareStatement(sql);
            preState.setInt(1, id);
            ResultSet res = preState.executeQuery();
            if(res.next()) 	ans = res.getString(1);
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
	public String getAuthorById(Integer id)
	{
		return DBSelectorByPK("select author from book where id = ?",id);
	}
	
	public String getTitleById(Integer id)
	{
		return DBSelectorByPK("select title from book where id = ?",id);
	}
	
	public String delBook(String id)
	{
		Pattern validation;
		Matcher match;
        Map<String,Object> returnAns = new HashMap<>(); 
        Gson gson = new Gson();
		String resJSON = "";
		validation = Pattern.compile("[0-9]+");
		match = validation.matcher(id);
		if (!match.matches())
		{
	       	returnAns.put("status", "failure");
	       	returnAns.put("info", "Sorry, the format of id is invalid!");
	    	resJSON = gson.toJson(returnAns);
	    	return resJSON;
		}
		Integer [] numArr = new Integer[1];
		numArr[0] = Integer.valueOf(id);
		DBconfig DB = new DBconfig();
		int record = DB.DBInnerCounter("select count(id) from record where book_id = ?",null,numArr);
		if (record > 0)
		{
	       	returnAns.put("status", "failure");
	       	returnAns.put("info", "Sorry, this book has been lent!");
	    	resJSON = gson.toJson(returnAns);
	    	return resJSON;
		}
		return DB.DBOuterUpdater("delete from book where id = ?",null,numArr);
	}

	public String lendBook(String id,String token) {
		Pattern validation;
		Matcher match;
        Map<String,Object> returnAns = new HashMap<>(); 
        Gson gson = new Gson();
		String resJSON = "";
		validation = Pattern.compile("[0-9]+");
		match = validation.matcher(id);
		if (!match.matches())
		{
	       	returnAns.put("status", "failure");
	       	returnAns.put("info", "Sorry, the format of id is invalid!");
	    	resJSON = gson.toJson(returnAns);
	    	return resJSON;
		}
		Integer [] numArr = new Integer[1];
		numArr[0] = Integer.valueOf(id);
		DBconfig DB = new DBconfig();
		int book = DB.DBInnerCounter("Select count(id) from book where id = ? and number != 0",null, numArr);
		if (book <= 0) 
		{
	       	returnAns.put("status", "failure");
	       	if (book == 0)
	       		returnAns.put("info", "Sorry, these books have been lent out!");
	       	else 
	       		returnAns.put("info", "Sorry, There is no such a book!");
	    	resJSON = gson.toJson(returnAns);
	    	return resJSON;
		}
		int user_id = new UserModel().tokenIdentification(token);
		numArr = new Integer[2];
		numArr[0] = user_id;
		numArr[1] = Integer.valueOf(id);
		int record = DB.DBInnerCounter("select count(id) from record where user_id = ? and book_id = ? and giveback = 0",null,numArr);
		if (record > 0)
		{
	       	returnAns.put("status", "failure");
	       	returnAns.put("info", "Sorry, You have lent this book and it hasn't been returned!");
	    	resJSON = gson.toJson(returnAns);
	    	return resJSON;
		}
		RecordModel B = new RecordModel();
		if (B.addRecord(Integer.valueOf(id), user_id) != 1)
		{
	       	returnAns.put("status", "failure");
	       	returnAns.put("info", "Sorry, Failed to add this piece of record!");
	    	resJSON = gson.toJson(returnAns);
	    	return resJSON;
		}
		numArr = new Integer[1];
		numArr[0] = Integer.valueOf(id);
		return DB.DBOuterUpdater("Update book set number = number-1 where id = ?",null,numArr);
	}

	public String returnBook(String book_id, String user_id) {   
		Pattern validation;
		Matcher match;
        Map<String,Object> returnAns = new HashMap<>(); 
        Gson gson = new Gson();
		String resJSON = "";
		validation = Pattern.compile("[0-9]+");
		match = validation.matcher(book_id);
		if (!match.matches())
		{
	       	returnAns.put("status", "failure");
	       	returnAns.put("info", "Sorry, the format of book_id is invalid!");
	    	resJSON = gson.toJson(returnAns);
	    	return resJSON;
		}
		match = validation.matcher(user_id);
		if (!match.matches())
		{
	       	returnAns.put("status", "failure");
	       	returnAns.put("info", "Sorry, the format of user_id is invalid!");
	    	resJSON = gson.toJson(returnAns);
	    	return resJSON;
		}
		DBconfig DB = new DBconfig();
		Integer [] numArr = new Integer[2];
		numArr[0] = Integer.valueOf(user_id);
		numArr[1] = Integer.valueOf(book_id);
		int record = DB.DBInnerCounter("select count(id) from record where user_id = ? and book_id = ? and giveback = 0",null,numArr);
		if (record <= 0)
		{
	       	returnAns.put("status", "failure");
	       	returnAns.put("info", "Sorry, This user didn't lend such a book!");
	    	resJSON = gson.toJson(returnAns);
	    	return resJSON;
		}
		RecordModel B = new RecordModel();
		if (B.endRecord(numArr[1], numArr[0]) != 1)
		{
	       	returnAns.put("status", "failure");
	       	returnAns.put("info", "Sorry, Failed to return this book!");
	    	resJSON = gson.toJson(returnAns);
	    	return resJSON;
		}
		numArr = new Integer[1];
		numArr[0] = Integer.valueOf(book_id);
		return DB.DBOuterUpdater("Update book set number = number+1 where id = ?",null,numArr);
	}
}
