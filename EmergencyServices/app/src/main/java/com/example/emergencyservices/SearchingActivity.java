package com.example.emergencyservices;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class SearchingActivity extends AppCompatActivity {

    public Handler mHandler;
    int request_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);

        String id = getIntent().getStringExtra("request_id");
        request_id = Integer.parseInt(id);
        GetServiceDetails(request_id);
        mHandler = new Handler();
        mHandler.postDelayed(m_Runnable,5000);

        Button cancel_button = (Button)findViewById(R.id.cancel_button_id);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelRequest(request_id);
            }
        });

    }

    private final Runnable m_Runnable = new Runnable()
    {
        public void run()
        {
            GetServiceDetails(request_id);
            SearchingActivity.this.mHandler.postDelayed(m_Runnable, 5000);
        }

    };

    public GetServiceDetailsConnect gc;
    public void GetServiceDetails(int id){
        gc = new GetServiceDetailsConnect(id);
        gc.execute();
    }

    public class GetServiceDetailsConnect extends AsyncTask<String,String,String> {
        private final int req_id;
        GetServiceDetailsConnect(int id){
            req_id = id;
        }

        @Override
        protected String doInBackground(String... params){
            try{
                String ip = "172.16.101.50";
                String serverURL="http://"+ip+":8080/EmergencyServicesBackend/webapi/resource/is_accepted/"+req_id;
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
                Log.d("SearchingActivity", "Waiting!");
            }
            else if(s.equals("invalid")){
                Log.e("SearchingActivity", "Searching for the requested service.");
                Toast.makeText(SearchingActivity.this,"Searching for the requested service.",Toast.LENGTH_LONG).show();
            }
            else if(s.equals("Exception")){
                Log.e("SearchingActivity", "SQL Exception");
                Toast.makeText(SearchingActivity.this,"Some error has occured at server",Toast.LENGTH_LONG).show();
            }
            else
            {
                if(s.equals("null")||s.equals("NULL")){
                    Toast.makeText(SearchingActivity.this,"Searching for the requested service.",Toast.LENGTH_LONG).show();
                }
                else{
                    Intent i = new Intent(SearchingActivity.this,Requests_serviceproviderDetailsActivity.class);
                    i.putExtra("Service_id",s);
                    startActivity(i);
                }
                Log.d("Main_serviceActivity", "Request Succesful");
            }
        }

        @Override
        protected void onCancelled() {
            gc = null;
        }
    }

    public CancelRequestConnect cc;
    public void CancelRequest(int id){
        cc = new CancelRequestConnect(id);
        cc.execute();
    }

    public class CancelRequestConnect extends AsyncTask<String,String,String> {
        private final int req_id;
        CancelRequestConnect(int id){
            req_id = id;
        }

        @Override
        protected String doInBackground(String... params){
            try{
                String ip = "172.16.101.50";
                String serverURL="http://"+ip+":8080/EmergencyServicesBackend/webapi/resource/cancel_request/"+req_id;
                URL url = new URL(serverURL);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("PUT");

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
                Log.d("SearchingActivity", "Waiting!");
            }
            else if(s.equals("invalid")){
                Log.e("SearchingActivity", "Cancel request cancelled.");
                Toast.makeText(SearchingActivity.this,"Cancel request cancelled.Try again.",Toast.LENGTH_LONG).show();
            }
            else if(s.equals("Exception")){
                Log.e("SearchingActivity", "SQL Exception");
                Toast.makeText(SearchingActivity.this,"Some error has occured at server",Toast.LENGTH_LONG).show();
            }
            else if(s.equals("valid")){
                Log.e("SearchingActivity","Cancel succesful");
                Toast.makeText(SearchingActivity.this,"Cancelation succesuful",Toast.LENGTH_LONG).show();
                Intent i = new Intent(SearchingActivity.this,MainActivity.class);
                startActivity(i);
            }
            else
            {
                Log.e("SearchingActivity", "Message from server"+s);
                Toast.makeText(SearchingActivity.this,"Some error has occured at server",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            gc = null;
        }
    }
}
