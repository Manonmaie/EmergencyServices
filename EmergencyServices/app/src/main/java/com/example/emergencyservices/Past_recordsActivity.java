package com.example.emergencyservices;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Past_recordsActivity extends AppCompatActivity {

    private int num_requests;
    Button requestbtn;
    public Handler mHandler;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_records);

        mHandler = new Handler();
        mHandler.postDelayed(m_Runnable,10000);

        SharedPreferences sp= getSharedPreferences("login",MODE_PRIVATE);
        name = sp.getString("UserName_service",null);
        GetPastRequests();
    }

    private final Runnable m_Runnable = new Runnable()
    {
        public void run()

        {
            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.requests_id);
            if(((LinearLayout) linearLayout).getChildCount() > 0)
                ((LinearLayout) linearLayout).removeAllViews();
            GetPastRequests();
            Past_recordsActivity.this.mHandler.postDelayed(m_Runnable, 10000);
        }
    };

    public void addButton(String btnTxt, int btnNum, final int userid,final String address,final int req_id){
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.requests_id);
        requestbtn = new Button(this);
        requestbtn.setId(btnNum);
        requestbtn.setText(btnTxt);
        requestbtn.setTextSize(10);
        requestbtn.setBackgroundColor(Color.parseColor("#D55D01"));
//        requestbtn.setTextColor(Color.parseColor("#111111"));
//        requestbtn.setBackgroundColor(Color.parseColor("#B14E02"));
        linearLayout.addView(requestbtn);
        requestbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Past_recordsActivity.this,Past_user_detailsActivity.class);
                i.putExtra("Request_userid",userid);
                i.putExtra("Request_address",address);
                i.putExtra("Request_id",req_id);
                startActivity(i);
            }
        });
    }

    public GetPastRequestsConnect gc;
    public void GetPastRequests(){
        gc = new GetPastRequestsConnect();
        gc.execute();
    }

    public class GetPastRequestsConnect extends AsyncTask<String,String,String> {
        JSONArray requests_local,ids_local,req_ids;
        GetPastRequestsConnect(){
        }

        @Override
        protected String doInBackground(String... params){
//            JSONObject jsonObject = null;
            try{
                String ip = "172.16.101.50";
//                jsonObject = new JSONObject();
                String serverURL="http://"+ip+":8080/EmergencyServicesBackend/webapi/resource/get_past_requests/"+name;
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
                requests_local = jsonObj.getJSONArray("details");
                ids_local = jsonObj.getJSONArray("ids");
                req_ids = jsonObj.getJSONArray("request_ids");
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
                Log.d("Past_serviceActivity", "Some error has occurred at Server or connection error");
            }
            else if(s.equals("getClientRequests failed")){
                Log.e("Past_serviceActivity", "Some error has occured at Server");
                Toast.makeText(Past_recordsActivity.this,"Some error has occured at Server",Toast.LENGTH_LONG).show();
            }
            else
            {
                String txt = "",address_local="";
                int client_id=-1,req_id=-1;
                num_requests = requests_local.length();
                for(int i=0;i<num_requests;i++){
                    try {
                        txt = requests_local.getString(i);
                        address_local=txt.split(" : ")[1];
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        client_id = Integer.parseInt(ids_local.getString(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        req_id = Integer.parseInt(req_ids.getString(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    addButton(txt,i+1,client_id,address_local,req_id);
                }
//                Log.e("Main_serviceActivity", "Data from Server: " + s);
//                Toast.makeText(Main_seviceproviderActivity.this,"Some error has occured",Toast.LENGTH_LONG).show();
                Log.d("Past_serviceActivity", "Request Succesful");
            }
        }

        @Override
        protected void onCancelled() {
            gc = null;
        }
    }
}
