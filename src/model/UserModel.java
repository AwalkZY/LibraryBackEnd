package model;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import com.google.gson.Gson;

import database.DBconfig;

public class UserModel {
    String name = null;
    String sex = null;
    Integer id = null;
    String token = null;
    String email = null;
    Integer admin = null;
    
	@SuppressWarnings("finally")
	private String DBSelector(String sql,String[] params,int choice) {
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
            if (params != null) 
            	for (int i = 0; i < params.length; i++)
            		preState.setString(i+1, params[i]);
            ResultSet res = preState.executeQuery();
            boolean flag = false;
            while(res.next()){
            	flag = true;
                id = res.getInt("id");
                name = res.getString("name");
                sex = res.getString("sex");
                if (choice == 1)token = res.getString("token");
                email = res.getString("email");
                admin = res.getInt("admin");
                //Êä³ö½á¹û
                Map<String,Object> nowResult = new HashMap<>(); 
                nowResult.put("id", id);
                nowResult.put("name", name);
                nowResult.put("sex", sex);
                if (choice == 1) nowResult.put("token", token);
                nowResult.put("email", email);
                nowResult.put("admin",admin);
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
            	returnAns.put("info", "Sorry, the username or password is wrong!");
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
	
	
	@SuppressWarnings("finally")
	public Integer tokenIdentification(String token) {
		Connection con;
        DBconfig DB = new DBconfig();
		Integer ans = -1;
        try {
            Class.forName(DB.driver);
            con = DriverManager.getConnection(DB.url,DB.user,DB.password);
            if(con.isClosed()) 	return -1;
            PreparedStatement preState = con.prepareStatement("Select id from user where token = ?");
            preState.setString(1, token);
            ResultSet res = preState.executeQuery();
            if(res.next()) ans = res.getInt(1);
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
	
	public String getAllUser() {
        return DBSelector("select * from user ORDER BY id",null,0);
    }
	
	public String getUserByToken(String token) {
		String [] strArr = new String[1];
		strArr[0] = token;
		return DBSelector("select * from user where token = ?",strArr,1);
	}
	
	public String delUser(String id) {
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
		Integer[] intArr = new Integer[1];
		intArr[0] = Integer.valueOf(id);
		DBconfig DB = new DBconfig();
		int record = DB.DBInnerCounter("select count(id) from record where user_id = ? and giveback = 0",null,intArr);
		if (record > 0)
		{
	       	returnAns.put("status", "failure");
	       	returnAns.put("info", "Sorry, this user hasn't return all books he/she lent!");
	    	resJSON = gson.toJson(returnAns);
	    	return resJSON;
		}
		DB.DBInnerUpdater("delete from record where user_id = ?", null, intArr);
		return DB.DBOuterUpdater("delete from user where id = ? and admin != 2",null,intArr);
	}
	
	public String login(String user,String pass)
	{
		String [] strArr = new String[2];
		strArr[0] = user;
		strArr[1] = SHA1(pass);
		return DBSelector("select * from user where name = ? and pass = ?",strArr,1);
	}
	
	public String register(String name,String pass,String sex,String email)
	{
		Map<String,Object> returnAns = new HashMap<>(); 
        Gson gson = new Gson();
		String resJSON = "";
		Pattern validation;
		Matcher match;
		validation = Pattern.compile("[A-Za-z0-9_]{6,16}");
		match = validation.matcher(name);
		if (!match.matches())
		{
        	returnAns.put("status", "failure");
        	returnAns.put("info", "Sorry, the format of name is invalid!");
    		resJSON = gson.toJson(returnAns);
    		return resJSON;
		}
		String [] strArr = new String[1];
		strArr[0] = name;
		DBconfig DB = new DBconfig();
		Integer occupied = DB.DBInnerCounter("Select count(name) from user where name = ?",strArr, null);
		if (occupied > 0) 
		{
        	returnAns.put("status", "failure");
        	returnAns.put("info", "Sorry, this user name has been occupied!");
    		resJSON = gson.toJson(returnAns);
    		return resJSON;
		}
		validation = Pattern.compile("[A-Za-z0-9_.]{6,16}");
		match = validation.matcher(pass);
		if (!match.matches())
		{
        	returnAns.put("status", "failure");
        	returnAns.put("info", "Sorry, the format of password is invalid!");
    		resJSON = gson.toJson(returnAns);
    		return resJSON;
		}
		validation = Pattern.compile("[MF]{1,1}");
		match = validation.matcher(sex);
		if (!match.matches())
		{
        	returnAns.put("status", "failure");
        	returnAns.put("info", "Sorry, the format of sex is invalid!");
    		resJSON = gson.toJson(returnAns);
    		return resJSON;
		}
		validation = Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
		match = validation.matcher(email);
		if (!match.matches())
		{
        	returnAns.put("status", "failure");
        	returnAns.put("info", "Sorry, the format of email is invalid!");
    		resJSON = gson.toJson(returnAns);
    		return resJSON;
		}
		String token = DigestUtils.md5Hex(email + name + "salt");
		strArr = new String[5];
		strArr[0] = name;
		strArr[1] = SHA1(pass);
		strArr[2] = sex;
		strArr[3] = email;
		strArr[4] = token;
		return DB.DBOuterUpdater("Insert into user(name,pass,sex,email,token,admin) values(?,?,?,?,?,0)",strArr, null);
	}
	
    private String SHA1(String src){
    	src += "De!S.m_o1n?d";
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA");
            digest.update(src.getBytes());
            return Hex.encodeHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
