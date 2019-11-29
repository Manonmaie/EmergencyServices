package com.example.emergencyservices;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class LoginActivity extends AppCompatActivity {

    Button login_button,login_as_diffuser,go_signup;
    SharedPreferences sp;
    EditText name,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_button = (Button) findViewById(R.id.login_button_id);
        name=(EditText) findViewById(R.id.login_name_id);
        password=(EditText) findViewById(R.id.login_pass_id);
        sp = getSharedPreferences("login",MODE_PRIVATE);

        if(sp.getBoolean("logged",false)){
            goToMainActivity();
        }
        else if(sp.getBoolean("logged_service",false)){
            goToMain_serviceproviderActivity();
        }

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
                sp.edit().putBoolean("logged",true).apply();
                sp.edit().putString("UserName",name.getText().toString()).apply();
            }
        });

        go_signup = findViewById(R.id.login_signup_id);

        go_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignupActivity();
            }
        });

        login_as_diffuser = findViewById(R.id.login_serviceprovider_button_id);

        login_as_diffuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginActivity();
            }
        });

    }

    public LoginConnect lc;
    public void Login(){
        String username_local = name.getText().toString();
        String password_local = password.getText().toString();
        lc = new LoginConnect(username_local,password_local);
        lc.execute();
    }

    public class LoginConnect extends AsyncTask<String,String,String> {
        private final String name_local,password_local;
        LoginConnect(String n,String p){
            name_local = n;
            password_local = p;
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
                String serverURL="http://"+ip+":8080/EmergencyServicesBackend/webapi/resource/login";
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
                Log.d("LoginActivity","Data from the Server: " + line);
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
                Log.d("LoginActivity", "Some error has occurred at Server");
                Toast.makeText(LoginActivity.this,"Invalid login",Toast.LENGTH_LONG).show();

            }
            else if(s.equals("Valid"))
            {
                Log.d("LoginActivity", "Client succefully logged in, Display MainActivity");
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Bundle basket= new Bundle();
                basket.putString("UserName",name_local);
                intent.putExtras(basket);
                startActivity(intent);
                finish();
            }
            else if(s.equals("Invalid Credentials. Please try again!!"))
            {
                Log.e("LoginActivity", "Invalid Credentials. Try again!!");
                Toast.makeText(LoginActivity.this,"Invalid Credentials. Try again!!",Toast.LENGTH_LONG).show();
            }
            else if(s.equals("Name or Password cannot be empty"))
            {
                Log.e("LoginActivity", "Name or Password cannot be empty");
                Toast.makeText(LoginActivity.this,"Name or Password cannot be empty",Toast.LENGTH_LONG).show();
            }
            else
            {
                Log.e("LoginActivity", "Data from Server: " + s);
                Toast.makeText(LoginActivity.this,"Invalid login",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            lc = null;
        }
    }

    public void goToMainActivity(){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }
    public void goToMain_serviceproviderActivity(){
        Intent i = new Intent(this,Main_seviceproviderActivity.class);
        startActivity(i);
    }
    public void openSignupActivity(){
        Intent i = new Intent(this,User_typeActivity.class);
        startActivity(i);
    }

    public void openLoginActivity(){
        Intent i = new Intent(this,Login_serviceproviderActivity.class);
        startActivity(i);
    }
}