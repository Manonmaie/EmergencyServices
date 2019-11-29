package com.example.emergencyservices;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import android.util.Log;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.content.Intent;

import org.json.JSONObject;

public class Signup_serviceproviderActivity extends AppCompatActivity implements OnItemSelectedListener{

    EditText name,phno,password,address;
    ProgressBar pb;
    Button signup_button,go_login;
    Spinner service_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_serviceprovider);

        // Spinner element
        name = (EditText)findViewById(R.id.signup_name_id);
        phno = (EditText)findViewById(R.id.signup_phno_id);
        password = (EditText)findViewById(R.id.signup_pass_id);
        address = (EditText) findViewById(R.id.signup_address_id);
        pb = (ProgressBar)findViewById(R.id.signup_progressbar_id);
        signup_button = findViewById(R.id.signup_button_id);
        go_login = findViewById(R.id.signup_login_id);
        service_type = (Spinner) findViewById(R.id.signup_type_id);


        pb.setVisibility(View.GONE);

        service_type.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
//        categories.add("Service Type");
        categories.add("Hospital");
        categories.add("Fire Station");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        service_type.setAdapter(dataAdapter);


        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Signup();
//                openMainActivity();
            }
        });

        go_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginActivity();
            }
        });

    }

    public SignupConnect sc;
    public void Signup(){
        String username_local = name.getText().toString();
        String password_local = password.getText().toString();
        String phno_local = phno.getText().toString();
        String address_local = address.getText().toString();
        String service_type_local=service_type.getSelectedItem().toString();
        if(phno_local.length()!=10){
            Toast.makeText(Signup_serviceproviderActivity.this,"Enter valid mobile number",Toast.LENGTH_LONG).show();
        }

        else{
            sc = new SignupConnect(username_local,password_local,phno_local,address_local,service_type_local);
            sc.execute();
        }
    }

    public class SignupConnect extends AsyncTask<String,String,String> {
        private final String name_local,password_local,phno_local,address_local,service_type_local;
        SignupConnect(String n,String p,String ph,String a,String s){
            name_local = n;
            password_local = p;
            phno_local = ph;
            address_local = a;
            service_type_local=s;
        }

        @Override
        protected String doInBackground(String... params){
            String res = null;
            JSONObject jsonObject = null;
            try{
                String ip = "172.16.101.50";
                jsonObject = new JSONObject();
                jsonObject.put("Name", name_local);
                jsonObject.put("Password", password_local);
                jsonObject.put("PhNo",phno_local);
                jsonObject.put("Address",address_local);
                jsonObject.put("Service_type",service_type_local);
                String serverURL="http://"+ip+":8080/EmergencyServicesBackend/webapi/resource/signup_serviceprovider";
                URL url = new URL(serverURL);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                //connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                os.write(jsonObject.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = connection.getResponseCode();

                Log.d("Code", "ResponseCode: " + responseCode);

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
                Log.d("Signup_serviceActivity","Data from the Server: " + line);
                res = line;
                return res;
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

            if(s == null)
            {
                Log.d("Signup_serviceActivity", "Some error has occurred at Server");
            }
            else if(s.equals("valid"))
            {
                Log.d("Signup_serviceActivity", "User in Authentic Display the MainActivity");
                Intent intent = new Intent(Signup_serviceproviderActivity.this, MainActivity.class);
                Bundle basket= new Bundle();
                basket.putString("UserName",name_local);
                intent.putExtras(basket);
                startActivity(intent);
                finish();
            }
            else if(s.equals("All fields are compulsory"))
            {
                Log.e("SignupActivity", "All fields are compulsory");
                Toast.makeText(Signup_serviceproviderActivity.this,"All fields are compulsory",Toast.LENGTH_LONG).show();
            }
            else if(s.equals("This name already exixts! Try a new name :)"))
            {
                Log.e("SignupActivity", "This name already exixts! Try a new name :)" );
                Toast.makeText(Signup_serviceproviderActivity.this,"This name already exixts! Try a new name :)",Toast.LENGTH_LONG).show();
            }
            else
            {
                Log.e("SignupActivity", "Data from Server: " + s);
                Toast.makeText(Signup_serviceproviderActivity.this,"Invalid Signup",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            sc = null;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    public void openMainActivity(){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }

    public void openLoginActivity(){
        Intent i = new Intent(this,LoginActivity.class);
        startActivity(i);
    }

}