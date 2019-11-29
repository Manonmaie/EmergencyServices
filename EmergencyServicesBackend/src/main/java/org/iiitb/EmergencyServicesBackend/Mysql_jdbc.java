package org.iiitb.EmergencyServicesBackend;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Mysql_jdbc {
	Statement statement;
	ResultSet rs;
	Connection conn = null;
	String sql = null;
	
	public Mysql_jdbc() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver found");
		}
		catch(ClassNotFoundException e) {
			System.out.println("Driver not found"+e);
		}
		
		String dbname = "Emergencydb";
		String url = "jdbc:mysql://localhost:3306/"+dbname+"?verifyServerCertificate=false&useSSL=false";
		final String user = "root";
		final String pass = "Man1Tan2*";
		
		try {
			conn = (Connection) DriverManager.getConnection(url,user,pass);
			System.out.println("Connection established to database");
		}
		catch(SQLException e) {
			System.out.println("SQL Exception: "+e);
		}
	}
	
	public String Signup(String uname,String password,long phno,String bgroup) {
		String res = null;
		Statement stmt = null;
		String sql;
		if(uname.trim().equals("")||password.trim().equals("")) {
			res = "Name or Password cannot be empty";
			return res; 
		}
		try{
			stmt = (Statement) conn.createStatement();
		    sql = "insert into users_client values (0,\""+uname+"\",\""+password+"\",\""+phno+"\",\""+bgroup+"\")";
		    stmt.executeUpdate(sql);
		    res = "valid";
		}
		catch (SQLException ex) {
		  res = "This name already exixts! Try a new name :)";
		  ex.printStackTrace();
		  System.out.println("SQLException: " + ex.getMessage());
		  System.out.println("SQLState: " + ex.getSQLState());
		  System.out.println("VendorError: " + ex.getErrorCode());
		}
		return res;
	}
	public String Signup_serviceprovider(String uname,String password,long phno,String address,String service_type) {
		String res = null;
		Statement stmt = null;
		String sql;
		if(uname.trim().equals("")||password.trim().equals("")||address.trim().equals("")||phno==0) {
			res = "All fields are compulsory";
			return res; 
		}
		try{
			stmt = (Statement) conn.createStatement();
		    sql = "insert into users_serviceprovider values (0,\""+uname+"\",\""+password+"\",\""+phno+"\",\""+address+"\",\""+service_type+"\")";
		    stmt.executeUpdate(sql);
		    res = "valid";
		}
		catch (SQLException ex) {
		  res = "This name already exixts! Try a new name :)";
		  ex.printStackTrace();
		  System.out.println("SQLException: " + ex.getMessage());
		  System.out.println("SQLState: " + ex.getSQLState());
		  System.out.println("VendorError: " + ex.getErrorCode());
		}
		return res;
	}
	
	public String Login(String uname,String password) {
		String res = null;
		Statement stmt = null;
		String sql=null;
		if(uname.trim().equals("")||password.trim().equals("")) {
			res = "Name or Password cannot be empty";
			return res; 
		}
		try{
			stmt = (Statement) conn.createStatement();
		   // sql = "insert into users_client values (0,\""+uname+"\",\""+password+"\",\""+phno+"\",\""+bgroup+"\")";
		    sql="select * from users_client where name=\""+uname+"\" and password=\""+password+"\""; 
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()) {
				res = "Valid";
			}
			else {
				res = "Invalid Credentials. Please try again!!";
			}
		}
		catch (SQLException ex) {
		  res = "Some Sql exception while client login !!";
		  ex.printStackTrace();
		  System.out.println("SQLException: " + ex.getMessage());
		  System.out.println("SQLState: " + ex.getSQLState());
		  System.out.println("VendorError: " + ex.getErrorCode());
		}
		  System.out.println(res);
		return res;
	}
	
	public String Login_serviceprovider(String uname,String password) {
		String res = null;
		Statement stmt = null;
		String sql=null;
		if(uname.trim().equals("")||password.trim().equals("")) {
			res = "Name or Password cannot be empty";
			return res; 
		}
		try{
			stmt = (Statement) conn.createStatement();
		   // sql = "insert into users_client values (0,\""+uname+"\",\""+password+"\",\""+phno+"\",\""+bgroup+"\")";
		    sql="select * from users_serviceprovider where name=\""+uname+"\" and password=\""+password+"\""; 
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()) {
				res = "Valid";
			}
			else {
				res = "Invalid Credentials. Please try again!!";
			}
		}
		catch (SQLException ex) {
		  res = "Some Sql exception while client login !!";
		  ex.printStackTrace();
		  System.out.println("SQLException: " + ex.getMessage());
		  System.out.println("SQLState: " + ex.getSQLState());
		  System.out.println("VendorError: " + ex.getErrorCode());
		}
		  System.out.println(res);
		return res;
	}
	
	public String GetService(String uname,String type,double latitude,double longitude,String address) {
		String res = null;
		Statement stmt = null,stmt1=null,stmt2=null;
		String sql=null,sql1=null,sql2=null;
		if(uname.trim().equals("") || ((!type.trim().equals("Hospital") && !type.trim().equals("Fire")))) {
			res = "username or type is null";
			return res; 
		}
		if(address.trim().equals("Address Not Found")||address.trim().equals("")) {
			res = "Address not found";
			return res; 
		}
		try{
			stmt = (Statement) conn.createStatement();
			stmt1 = (Statement) conn.createStatement();
			stmt2 = (Statement) conn.createStatement();
			int client_id;
			sql1 = "select users_client_id from users_client where name=\""+uname+"\"";
			ResultSet rs = stmt.executeQuery(sql1);
			if(rs.next()) {
				client_id = rs.getInt("users_client_id");
				System.out.println(client_id);
				System.out.println(type);
				System.out.println(latitude);
				System.out.println(longitude);
				System.out.println(address);
				sql = "insert into requests(client_id,serviceprovider_id,service_type,latitude,longitude,address) values (\""+client_id+"\",null,\""+type+"\",\""+latitude+"\",\""+longitude+"\",\""+address+"\")";
				stmt1.executeUpdate(sql);
				sql2 = "select requests_id from requests  where requests_id=(select max(requests_id) from (select * from requests where client_id=\""+client_id+"\") as T1)";
				ResultSet rs1 = stmt2.executeQuery(sql2);
				if(rs1.next()) {
					Integer request_id;
					request_id = rs1.getInt("requests_id");
					res = request_id.toString();
				}
				else {
					res = "No requests of you";
				}
			}
			else {
				res = "No username found";
				return res;
			}   
		}
		catch (SQLException ex) {
		  res = "Some Sql exception while getting service!!";
		  ex.printStackTrace();
		  System.out.println("SQLException: " + ex.getMessage());
		  System.out.println("SQLState: " + ex.getSQLState());
		  System.out.println("VendorError: " + ex.getErrorCode());
		}
		System.out.println(res);
		return res;
	}
	
	
	public ArrayList<ArrayList<String>> GetClientRequests(String type) {
		ArrayList<String> requests = new ArrayList<String>();
		ArrayList<String> requests_id = new ArrayList<String>();
		Statement stmt = null;
		String sql=null;
		try{
			stmt = (Statement) conn.createStatement();
			sql = "select requests_id,name,address from users_client INNER JOIN requests on users_client.users_client_id=requests.client_id where service_type=\""+type+"\" and status='Pending'";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				String req = "";
				req = rs.getString("name")+" : "+rs.getString("address");
				requests.add(req);
				Integer req_id = rs.getInt("requests_id");
				requests_id.add(req_id.toString());
			} 
		}
		catch (SQLException ex) {
		  ex.printStackTrace();
		  System.out.println("SQLException: " + ex.getMessage());
		  System.out.println("SQLState: " + ex.getSQLState());
		  System.out.println("VendorError: " + ex.getErrorCode());
		}
		ArrayList<String> client_ids_requests = new ArrayList<String>();
		Statement stmt1 = null;
		String sql1=null;
		try{
			stmt1 = (Statement) conn.createStatement();
			sql1 = "select client_id from requests where service_type=\""+type+"\" and status='Pending'";
			ResultSet rs = stmt.executeQuery(sql1);
			while(rs.next()) {
				Integer req;
				req = rs.getInt("client_id");
				client_ids_requests.add(req.toString());
			} 
		}
		catch (SQLException ex) {
		  ex.printStackTrace();
		  System.out.println("SQLException: " + ex.getMessage());
		  System.out.println("SQLState: " + ex.getSQLState());
		  System.out.println("VendorError: " + ex.getErrorCode());
		}
		ArrayList<ArrayList<String>> ret=new ArrayList<ArrayList<String>>();
		ret.add(client_ids_requests);
		ret.add(requests);
		ret.add(requests_id);
		return ret;
	}
	
	public ArrayList<ArrayList<String>> GetPastRequests(String uname) {
		
		ArrayList<String> requests = new ArrayList<String>();
		ArrayList<String> requests_id = new ArrayList<String>();
		ArrayList<ArrayList<String>> ret=new ArrayList<ArrayList<String>>();
		Statement stmt = null,stmt0=null;
		String sql=null,sql0=null;
		try{
			stmt0 = (Statement) conn.createStatement();
			sql0 = "select users_serviceprovider_id from users_serviceprovider where name=\""+uname+"\"";
		    ResultSet rs0= stmt0.executeQuery(sql0);
		    int id;
		    if(rs0.next())
		    {
		    	id=rs0.getInt("users_serviceprovider_id");
				stmt = (Statement) conn.createStatement();
				sql = "select requests_id,name,address from users_client INNER JOIN requests on users_client.users_client_id=requests.client_id where serviceprovider_id=\""+id+"\" and status='Completed'";
				ResultSet rs = stmt.executeQuery(sql);
				while(rs.next()) {
					String req = "";
					req = rs.getString("name")+" : "+rs.getString("address");
					requests.add(req);
					Integer req_id = rs.getInt("requests_id");
					requests_id.add(req_id.toString());
				} 
				ArrayList<String> client_ids_requests = new ArrayList<String>();
				Statement stmt1 = null;
				String sql1=null;
				stmt1 = (Statement) conn.createStatement();
				sql1 = "select client_id from requests where serviceprovider_id=\""+id+"\" and status='Completed'";
				rs = stmt.executeQuery(sql1);
				while(rs.next()) {
					Integer req;
					req = rs.getInt("client_id");
					client_ids_requests.add(req.toString());
				}
				ret.add(client_ids_requests);
				ret.add(requests);
				ret.add(requests_id);
		    }
		    else {
		    	System.out.println("id doesn't exist");
		    }
		}
		catch (SQLException ex) {
		  ex.printStackTrace();
		  System.out.println("SQLException: " + ex.getMessage());
		  System.out.println("SQLState: " + ex.getSQLState());
		  System.out.println("VendorError: " + ex.getErrorCode());
		}
		return ret;
	}
	
	public String GetClientDetails(int id){
		String res = null;
		Statement stmt = null;
		String sql;
		if(id==-1) {
			res = "user_id is becoming -1";
			return res; 
		}
		try{
			stmt = (Statement) conn.createStatement();
			sql = "select name,phonenumber from users_client where users_client_id = \""+id+"\"";
		    ResultSet rs= stmt.executeQuery(sql);
		    String name,phno;
		    if(rs.next())
		    {
		    	name=rs.getString("name");
		    	phno=rs.getBigDecimal("phonenumber").toString();
		    	JSONObject jo=new JSONObject();
		    	jo.put("name",name);
		    	jo.put("phno",phno);
		    	res=jo.toString();
		    }
		    else
		    {
		    	res="invalid";
		    }
		}
		catch (SQLException ex) {
		  res = "Exception";
		  ex.printStackTrace();
		  System.out.println("SQLException: " + ex.getMessage());
		  System.out.println("SQLState: " + ex.getSQLState());
		  System.out.println("VendorError: " + ex.getErrorCode());
		} catch (JSONException e) {
			res="Exception";
			e.printStackTrace();
		}
		return res;
//		return "";
	}
	
	public String AcceptRequest(int req_id,String service_name){
		String res = null;
		Statement stmt = null,stmt1=null;
		String sql,sql1;
		if(req_id==-1) {
			res = "request_id is becoming -1";
			return res; 
		}
		try{
			stmt = (Statement) conn.createStatement();
			sql = "select users_serviceprovider_id from users_serviceprovider where name=\""+service_name+"\"";
		    ResultSet rs= stmt.executeQuery(sql);
		    int id;
		    if(rs.next())
		    {
		    	id=rs.getInt("users_serviceprovider_id");
		    	try {
		    		stmt1 = (Statement) conn.createStatement();
					sql1 = "update requests set serviceprovider_id=\""+id+"\",status='Completed' where requests_id=\""+req_id+"\"";
				    stmt1.executeUpdate(sql1);
				    res = "valid";
		    	}
		    	catch (SQLException ex) {
		  		  res = "SQL Exception";
		  		  ex.printStackTrace();
		  		  System.out.println("SQLException: " + ex.getMessage());
		  		  System.out.println("SQLState: " + ex.getSQLState());
		  		  System.out.println("VendorError: " + ex.getErrorCode());
		  		}
		    }
		    else{
		    	res="invalid";
		    }
		}
		catch (SQLException ex) {
		  res = "SQL Exception";
		  ex.printStackTrace();
		  System.out.println("SQLException: " + ex.getMessage());
		  System.out.println("SQLState: " + ex.getSQLState());
		  System.out.println("VendorError: " + ex.getErrorCode());
		}
		return res;
	}
	
	public String IsAccepted(int req_id) {
		String res = null;
		Statement stmt = null;
		String sql;
		try{
			stmt = (Statement) conn.createStatement();
		    sql = "select serviceprovider_id from requests where requests_id=\""+req_id+"\" and status='Completed'";
		    ResultSet rs = stmt.executeQuery(sql);
		    if(rs.next()) {
		    	Integer r;
		    	r = rs.getInt("serviceprovider_id");
		    	res = r.toString();
		    }
		    else {
		    	res = "invalid";
		    }
		}
		catch (SQLException ex) {
		  res = "Exception";
		  ex.printStackTrace();
		  System.out.println("SQLException: " + ex.getMessage());
		  System.out.println("SQLState: " + ex.getSQLState());
		  System.out.println("VendorError: " + ex.getErrorCode());
		}
		return res;
	}
	
	public String GetServiceDetails(int id){
		String res = null;
		Statement stmt = null;
		String sql;
		if(id==-1) {
			res = "user_id is becoming -1";
			return res; 
		}
		try{
			stmt = (Statement) conn.createStatement();
			sql = "select name,phno,address from users_serviceprovider where users_serviceprovider_id = \""+id+"\"";
		    ResultSet rs= stmt.executeQuery(sql);
		    String name,phno,address;
		    if(rs.next())
		    {
		    	name=rs.getString("name");
		    	phno=rs.getBigDecimal("phno").toString();
		    	address = rs.getString("address");
		    	JSONObject jo=new JSONObject();
		    	jo.put("name",name);
		    	jo.put("phno",phno);
		    	jo.put("address", address);
		    	res=jo.toString();
		    }
		    else
		    {
		    	res="invalid";
		    }
		}
		catch (SQLException ex) {
		  res = "Exception";
		  ex.printStackTrace();
		  System.out.println("SQLException: " + ex.getMessage());
		  System.out.println("SQLState: " + ex.getSQLState());
		  System.out.println("VendorError: " + ex.getErrorCode());
		} catch (JSONException e) {
			res="Exception";
			e.printStackTrace();
		}
		return res;
	}
	
	public String GetType(String name) {
		String res = null;
		Statement stmt = null;
		String sql;
		try{
			System.out.println("Name = "+name);
			stmt = (Statement) conn.createStatement();
		    sql = "select service_type from users_serviceprovider where name=\""+name+"\"";
		    ResultSet rs = stmt.executeQuery(sql);
		    if(rs.next()) {
		    	res = rs.getString("service_type");
		    }
		    else {
		    	res = "invalid";
		    }
		}
		catch (SQLException ex) {
		  res = "SQL Exception";
		  ex.printStackTrace();
		  System.out.println("SQLException: " + ex.getMessage());
		  System.out.println("SQLState: " + ex.getSQLState());
		  System.out.println("VendorError: " + ex.getErrorCode());
		}
		return res;
	}
	
	public String CancelRequest(int req_id) {
		String res = null;
		Statement stmt = null;
		String sql;
		try{
			stmt = (Statement) conn.createStatement();
		    sql = "update requests set status='Cancelled' where requests_id=\""+req_id+"\"";
		    stmt.executeUpdate(sql);
		    res = "valid";
		}
		catch (SQLException ex) {
		  res = "SQL Exception";
		  ex.printStackTrace();
		  System.out.println("SQLException: " + ex.getMessage());
		  System.out.println("SQLState: " + ex.getSQLState());
		  System.out.println("VendorError: " + ex.getErrorCode());
		}
		return res;
	}
}