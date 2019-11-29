package com.example.emergencyservices;

import androidx.appcompat.app.AppCompatActivity;

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

public class Requests_serviceproviderDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_serviceprovider_details);

        String id = getIntent().getStringExtra("Service_id");
        int service_id = Integer.parseInt(id);
        GetServiceRequestsDetails(service_id);
    }

    public GetServiceRequestsDetailsConnect gc;
    public void GetServiceRequestsDetails(int uid){
        gc = new GetServiceRequestsDetailsConnect(uid);
        gc.execute();
    }

    public class GetServiceRequestsDetailsConnect extends AsyncTask<String,String,String> {
        String address_local,name_local,phno_local;
        int user_id;
        GetServiceRequestsDetailsConnect(int uid){
            user_id=uid;
        }

        @Override
        protected String doInBackground(String... params){
            try{

                String ip = "172.16.101.50";
//                jsonObject = new JSONObject();
                String serverURL="http://"+ip+":8080/EmergencyServicesBackend/webapi/resource/get_servicedetails/"+user_id;
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
                address_local = jsonObj.getString("address");
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
                Log.d("Request_detailsActivity", "Some error has occurred at Server or connection error");
            }
            else if(s.equals("invalid") || s.equals("user_id is becoming -1")){
                Log.e("Request_detailsActivity", "Request cancelled");
                Toast.makeText(Requests_serviceproviderDetailsActivity.this,"Request cancelled",Toast.LENGTH_LONG).show();
            }
            else if(s.equals("Exception")){
                Log.e("Request_detailsActivity", "Some error has occurred at Server");
                Toast.makeText(Requests_serviceproviderDetailsActivity.this,"Some error has occurred at Server",Toast.LENGTH_LONG).show();
            }
            else
            {
                TextView nameText = (TextView) findViewById(R.id.name_serviceprovider_id);
                nameText.append("   "+name_local);
                TextView addressText = (TextView) findViewById(R.id.address_id);
                addressText.append(address_local);
                TextView phnoText = (TextView) findViewById(R.id.phno_serviceprovider_id);
                phnoText.append("   "+phno_local);
                Log.d("Request_detailsActivity", "Request Succesful");
            }
        }

        @Override
        protected void onCancelled() {
            gc = null;
        }
    }
}
