package com.example.emergencyservices;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class User_typeActivity extends AppCompatActivity {

    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);

        submit = (Button) findViewById(R.id.type_button_id);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(onRadioButtonClicked(v).equals("user"))
//                    goToUserSignup();
//                else if(onRadioButtonClicked(v).equals("service"))
//                    goToServiceSignup();
//                else
//                    Toast.makeText(User_typeActivity.this,"Select account type",Toast.LENGTH_LONG).show();
                onRadioButtonClicked();
            }
        });
    }

    public void onRadioButtonClicked() {

        RadioGroup rg = (RadioGroup) findViewById(R.id.type_group_id);
        int checked = rg.getCheckedRadioButtonId();

        if(checked==-1)
            Toast.makeText(User_typeActivity.this,"Select account type",Toast.LENGTH_LONG).show();
        else{
//            RadioButton selected = (RadioButton)rg.findViewById(checked);
            if(checked==R.id.type_user_id)
                goToUserSignup();
            else if(checked==R.id.type_service_id)
                goToServiceSignup();
            else
                Toast.makeText(User_typeActivity.this,"Select account type",Toast.LENGTH_LONG).show();
        }
    }

    public void goToUserSignup(){
        Intent i = new Intent(this,SignupActivity.class);
        startActivity(i);
    }
    public void goToServiceSignup(){
        Intent i = new Intent(this,Signup_serviceproviderActivity.class);
        startActivity(i);
    }
}
