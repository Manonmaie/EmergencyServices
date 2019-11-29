package com.example.emergencyservices;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Past_user_detailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_user_details);
        int request_user_id = getIntent().getIntExtra("Request_userid",-1);
        String request_address = getIntent().getStringExtra("Request_address");
        final int request_id = getIntent().getIntExtra("Request_id",-1);
        GetRequestsDetails(request_address,request_user_id);

        SharedPreferences sp;
        sp = getSharedPreferences("login",MODE_PRIVATE);
        final String serviceprovider_name = sp.getString("UserName_service",null);
    }

    public GetRequestsDetailsConnect gc;
    public void GetRequestsDetails(String address,int uid){
        gc = new GetRequestsDetailsConnect(address,uid);
        gc.execute();
    }

    public class GetRequestsDetailsConnect extends AsyncTask<String,String,String> {
        String address,name_local,phno_local;
        int user_id;
        GetRequestsDetailsConnect(String a,int uid){
            address=a;
            user_id=uid;
        }

        @Override
        protected String doInBackground(String... params){
            try{

                String ip = "172.16.101.50";
//                jsonObject = new JSONObject();
                String serverURL="http://"+ip+":8080/EmergencyServicesBackend/webapi/resource/get_clientdetails/"+user_id;
                URL url = new URL(serverURL);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");

                connection.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");

                InputStream is = connection.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(is));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null)
                {
                    total.append(line);
                }
                is.close();
                line = total.toString();
                line = line.trim();
                System.out.println("line = "+line);
                JSONObject jsonObj = new JSONObject(line);
                name_local = jsonObj.getString("name");
                phno_local = jsonObj.getString("phno");
                return line;
            }
            catch(MalformedURLException e){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s==null){
                Log.d("Past_detailsActivity", "Some error has occurred at Server or connection error");
            }
            else if(s.equals("invalid") || s.equals("user_id is becoming -1")){
                Log.e("Past_detailsActivity", "Request cancelled");
                Toast.makeText(Past_user_detailsActivity.this,"Request cancelled",Toast.LENGTH_LONG).show();
            }
            else if(s.equals("Exception")){
                Log.e("Past_detailsActivity", "Some error has occurred at Server");
                Toast.makeText(Past_user_detailsActivity.this,"Some error has occurred at Server",Toast.LENGTH_LONG).show();
            }
            else
            {
                TextView nameText = (TextView) findViewById(R.id.name_serviceprovider_id);
                nameText.append("   "+name_local);
                TextView addressText = (TextView) findViewById(R.id.address_id);
                addressText.append(address);
                TextView phnoText = (TextView) findViewById(R.id.phno_serviceprovider_id);
                phnoText.append("   "+phno_local);
                Log.d("Past_detailsActivity", "Request Succesful");
            }
        }

        @Override
        protected void onCancelled() {
            gc = null;
        }
    }
}
