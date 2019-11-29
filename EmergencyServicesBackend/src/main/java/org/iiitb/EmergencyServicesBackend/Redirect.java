package org.iiitb.EmergencyServicesBackend;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import com.google.gson.Gson;
//import com.google.gson.JsonObject;

/**
 * Root resource (exposed at "resource" path)
 */
@Path("resource")
public class Redirect {

    /*
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
	
    @Path("test")
	@GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!!";
    }
    
	  @Path("signup")
	  @POST
	  //@GET
	  @Produces(MediaType.TEXT_PLAIN)
	  @Consumes(MediaType.TEXT_PLAIN)
	  public String Signup(String text) {
		  String uname,password,bgroup;
		  long phno;
		  uname = null;
		  password = null;
		  phno = 0;
		  bgroup = null;
		  
		  try {
			  JSONObject jo = new JSONObject(text);
			  uname = jo.getString("Name");
			  password = jo.getString("Password");
			  phno = jo.getLong("PhNo");
			  bgroup = jo.getString("BloodGroup");
		  }
		  catch(JSONException e){
			  e.printStackTrace();
		  }
		  
		  Mysql_jdbc db_conn = new Mysql_jdbc();
		  return db_conn.Signup(uname,password,phno,bgroup);
	//	  return db_conn.Signup("B","a",123,"A+");
		  
	  }
	  @Path("signup_serviceprovider")
	  @POST
	  @Produces(MediaType.TEXT_PLAIN)
	  @Consumes(MediaType.TEXT_PLAIN)
	  public String Signup_serviceprovider(String text) {
		  String uname,password,address,service_type;
		  long phno;
		  uname = null;
		  password = null;
		  phno = 0;
		  address = null;
		  service_type=null;
		  try {
			  JSONObject jo = new JSONObject(text);
			  uname = jo.getString("Name");
			  password = jo.getString("Password");
			  phno = jo.getLong("PhNo");
			  address = jo.getString("Address");
			  service_type=jo.getString("Service_type");
		  }
		  catch(JSONException e){
			  e.printStackTrace();
		  }
		  
		  Mysql_jdbc db_conn = new Mysql_jdbc();
		  return db_conn.Signup_serviceprovider(uname,password,phno,address,service_type);
//		  return db_conn.Signup("B","a",123,"A+");
		  
	  }

	  @Path("login")
	  @POST
	 // @GET
	  @Produces(MediaType.TEXT_PLAIN)
	  @Consumes(MediaType.TEXT_PLAIN)
	  public String login(String text) {
		  String uname,password;
		  uname = null;
		  password = null;
		  
		  try {
			  JSONObject jo = new JSONObject(text);
			  uname = jo.getString("Name");
			  password = jo.getString("Password");
		  }
		  catch(JSONException e){
			  e.printStackTrace();
		  }
		  
		  Mysql_jdbc db_conn = new Mysql_jdbc();
		  return db_conn.Login(uname,password);
//		  return db_conn.Login("Brsg","a");
		  
	  }
	  
	  @Path("login_serviceprovider")
	  @POST
	 // @GET
	  @Produces(MediaType.TEXT_PLAIN)
	  @Consumes(MediaType.TEXT_PLAIN)
	  public String login_serviceprovider(String text) {
		  String uname,password;
		  uname = null;
		  password = null;
		  
		  try {
			  JSONObject jo = new JSONObject(text);
			  uname = jo.getString("Name");
			  password = jo.getString("Password");
		  }
		  catch(JSONException e){
			  e.printStackTrace();
		  }
		  
		  Mysql_jdbc db_conn = new Mysql_jdbc();
		  return db_conn.Login_serviceprovider(uname,password);
//		  return db_conn.Login_serviceprovider("Brsg","a");
		  
	  }
	  @Path("get_service")
	  @POST
	  @Produces(MediaType.TEXT_PLAIN)
	  @Consumes(MediaType.TEXT_PLAIN)
	  public String getService(String text) {
		  String uname,type,address;
		  double latitude,longitude;
		  uname = null;
		  type = null;
		  address = null;
		  latitude = 0;
		  longitude = 0;
		  
		  try {
			  JSONObject jo = new JSONObject(text);
			  uname = jo.getString("Name");
			  type = jo.getString("Type");
			  latitude = jo.getDouble("Latitude");
			  longitude = jo.getDouble("Longitude");
			  address = jo.getString("Address");
		  }
		  catch(JSONException e){
			  e.printStackTrace();
		  }
		  
		  Mysql_jdbc db_conn = new Mysql_jdbc();
		  return db_conn.GetService(uname,type,latitude,longitude,address);
	//	  return db_conn.Signup("B","a",123,"A+");
		  
	  }
	  

	  
	  @Path("get_client_requests/{type}")
	  @GET
	  @Produces(MediaType.APPLICATION_JSON)
	  public String getClientRequests(@PathParam("type") String type) {
		  ArrayList<ArrayList<String>> lst = new ArrayList<ArrayList<String>>();
		  JSONObject j = new JSONObject();
		  Mysql_jdbc db_conn = new Mysql_jdbc();
		  lst = db_conn.GetClientRequests(type);
		  JSONArray ids = new JSONArray(lst.get(0));
		  try {
			j.put("ids",ids);
		  } catch (JSONException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
			  return "getClientRequests failed";
		  }
		  JSONArray details = new JSONArray(lst.get(1));
		  try {
			j.put("details", details);
		  } catch (JSONException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
			  return "getClientRequests failed";
		  }
		  JSONArray req_ids = new JSONArray(lst.get(2));
		  try {
			j.put("request_ids",req_ids);
		  } catch (JSONException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
			  return "getClientRequests failed";
		  }
		  System.out.println(j);
		  return j.toString();
	  }
	  
	  @Path("get_past_requests/{uname}")
	  @GET
	  @Produces(MediaType.APPLICATION_JSON)
	  public String getPastRequests(@PathParam("uname") String uname) {
		  ArrayList<ArrayList<String>> lst = new ArrayList<ArrayList<String>>();
		  JSONObject j = new JSONObject();
		  Mysql_jdbc db_conn = new Mysql_jdbc();
		  lst = db_conn.GetPastRequests(uname);
		  JSONArray ids = new JSONArray(lst.get(0));
		  try {
			j.put("ids",ids);
		  } catch (JSONException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
			  return "getClientRequests failed";
		  }
		  JSONArray details = new JSONArray(lst.get(1));
		  try {
			j.put("details", details);
		  } catch (JSONException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
			  return "getClientRequests failed";
		  }
		  JSONArray req_ids = new JSONArray(lst.get(2));
		  try {
			j.put("request_ids",req_ids);
		  } catch (JSONException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
			  return "getClientRequests failed";
		  }
		  System.out.println(j);
		  return j.toString();
	  }
	  
	  @Path("get_clientdetails/{uid}")
	  @GET
	  @Produces(MediaType.APPLICATION_JSON)
	  public String getClientDetails(@PathParam("uid") int uid) {
		  Mysql_jdbc db_conn = new Mysql_jdbc();
		  return db_conn.GetClientDetails(uid);
	  }
	  
	  @Path("get_type/{uname}")
	  @GET
	  @Produces(MediaType.TEXT_PLAIN)
	  public String getType(@PathParam("uname") String uname) {
		  Mysql_jdbc db_conn = new Mysql_jdbc();
		  return db_conn.GetType(uname);
	  }
	  
	  @Path("get_servicedetails/{uid}")
	  @GET
	  @Produces(MediaType.APPLICATION_JSON)
	  public String getServiceDetails(@PathParam("uid") int uid) {
		  Mysql_jdbc db_conn = new Mysql_jdbc();
		  return db_conn.GetServiceDetails(uid);
	  }
	  
	  @Path("is_accepted/{req_id}")
	  @GET
	  @Produces(MediaType.TEXT_PLAIN)
	  public String isAccepted(@PathParam("req_id") int req_id) {
		  Mysql_jdbc db_conn = new Mysql_jdbc();
		  return db_conn.IsAccepted(req_id);
	  }
	  
	  @Path("cancel_request/{req_id}")
	  @PUT
	  @Produces(MediaType.TEXT_PLAIN)
	  public String cancelRequest(@PathParam("req_id") int req_id) {
		  Mysql_jdbc db_conn = new Mysql_jdbc();
		  return db_conn.CancelRequest(req_id);
	  }
	  
	  @Path("accept_request/{req_id}/{serviceprovider_name}")
	  @PUT
	  @Produces(MediaType.TEXT_PLAIN)
	  public String acceptRequest(@PathParam("req_id") int req_id,@PathParam("serviceprovider_name") String name) {
		  Mysql_jdbc db_conn = new Mysql_jdbc();
		  return db_conn.AcceptRequest(req_id,name);
	  }
}
