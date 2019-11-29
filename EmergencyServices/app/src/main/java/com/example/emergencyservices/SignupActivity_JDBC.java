package com.example.emergencyservices;

import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import android.os.StrictMode;
import android.annotation.SuppressLint;
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
import android.util.Log;
import java.sql.DriverManager;

public class SignupActivity_JDBC extends AppCompatActivity implements OnItemSelectedListener{

    EditText name,phno,password;
    Spinner bloodgrp;
    ProgressBar pb;
    Button signup_button,go_login;

    Connection conn;
    String un,pass,db,ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = (EditText)findViewById(R.id.signup_name_id);
        phno = (EditText)findViewById(R.id.signup_phno_id);
        password = (EditText)findViewById(R.id.signup_pass_id);
        bloodgrp = (Spinner) findViewById(R.id.signup_bloodgrp_id);
        pb = (ProgressBar)findViewById(R.id.signup_progressbar_id);
        signup_button = findViewById(R.id.signup_button_id);
        go_login = findViewById(R.id.signup_login_id);

        pb.setVisibility(View.GONE);
        ip="localhost";
        //ip = "172.16.145.218";
//        ip = "127.0.0.1";
        db = "Emergencydb";
        un = "root";
        pass = "Man1Tan2*";

        // Spinner click listener
        bloodgrp.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Blood Group");
        categories.add("A+");
        categories.add("A-");
        categories.add("B+");
        categories.add("B-");
        categories.add("O+");
        categories.add("O-");
        categories.add("AB+");
        categories.add("AB-");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        bloodgrp.setAdapter(dataAdapter);


//        signup_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openMainActivity();
//            }
//        });
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                CheckSignin checksignin = new CheckSignin();
                checksignin.execute("");
            }
        });

        go_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginActivity();
            }
        });
    }

    public class CheckSignin extends AsyncTask<String,String,String>{
        String z = "";
        Boolean is_success = false;
        String username_local,password_local,phno_local,bloodgrp_local;

        @Override
        protected void onPreExecute(){
            username_local = name.getText().toString();
            password_local = password.getText().toString();
            phno_local = phno.getText().toString();
            bloodgrp_local = bloodgrp.getSelectedItem().toString();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params){
            if(username_local.trim().equals("")||password_local.trim().equals("")){
                z = "Please enter Name or Password";
            }
            else{
                try{
                    conn =  connectionclass(un,pass,db,ip);
                    if(conn == null){
                        z = "Connection lost (Check Your Internet)";
                    }
                    else{
                        String query = "INSERT INTO users_client VALUES (0,"+username_local+","+password_local+","+phno_local+","+bloodgrp_local+")";
                        Statement stmt = conn.createStatement();
                        try{
                            stmt.executeUpdate(query);
                        }
                        catch (SQLException e){
                            z = "Name already exists";
                            return z;
                        }
                    }
                }
                catch(Exception ex){
                    is_success = false;
                    z = ex.getMessage();
                }
            }
            return z;
        }

        @SuppressLint("NewApi")
        public Connection connectionclass(String user, String password, String database, String server)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Connection connection = null;
            String DB_URL,ConnectionURL;
            try
            {
                Class.forName("com.mysql.jdbc.Driver");
                DB_URL = "jdbc:mysql://"+server+":3306/"+database+"?verifyServerCertificate=false&useSSL=false";
//                DB_URL = "jdbc:mysql://192.168.43.183:3306/workflowdb?verifyServerCertificate=false&useSSL=false";
                System.out.println(DB_URL);
                connection = (Connection)DriverManager.getConnection(DB_URL,user,password);

            }
            catch (SQLException se)
            {
                se.printStackTrace();
                //Log.e("error here 1 : ", se.getMessage());
            }
            catch (ClassNotFoundException e)
            {
                Log.e("error here 2 : ", e.getMessage());
            }
            catch (Exception e)
            {
                Log.e("error here 3 : ", e.getMessage());
            }
            return connection;
        }

        @Override
        protected void onPostExecute(String r){
            pb.setVisibility(View.GONE);
            Toast.makeText(SignupActivity_JDBC.this,r,Toast.LENGTH_SHORT).show();
            if(is_success){
                Toast.makeText(SignupActivity_JDBC.this,"Signup Successful",Toast.LENGTH_LONG).show();
            }
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
