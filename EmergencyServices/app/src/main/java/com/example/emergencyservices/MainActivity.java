package com.example.emergencyservices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.location.Address;
import android.location.Geocoder;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import static android.location.LocationManager.*;

public class MainActivity extends AppCompatActivity {
    private Button hospital_button;
    private Button fire_button;
    private ImageButton profile_button;
    private LocationManager lm;
    private LocationListener ll;
    private Location curr_loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {    // first method which it loads
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hospital_button = findViewById(R.id.hospital_id);
        fire_button = findViewById(R.id.fire_id);

            lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            ll = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d("Latitude_Longitude ","Data from the Latitude_Longitude : " + location.getLatitude());
                    System.out.println("\nLatitude_Longitude " + location.getLatitude() + " " + location.getLongitude());
                    curr_loc = location;
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {
                    //What to do if GPS is off
                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(i);
                }
            };

        configureButton();

        profile_button = findViewById(R.id.profile_page_id);
//        profile_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openProfileActivity();
//            }
//        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
                return;
        }
    }


    private void configureButton() {
        hospital_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET},
                                10);
                        return;
                    }
                }
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, ll);
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);

                if (curr_loc != null){
                    getService("Hospital");
                }
                else
                    System.out.println("Location fetching is slow");

            }
        });

        fire_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                                ,10);
                        return;
                    }
                }
                lm.requestLocationUpdates(GPS_PROVIDER, 1000, 0, ll);
                if (curr_loc != null){
                    getService("Fire");
                }
                else
                    System.out.println("Location fetching is slow");
            }
        });
    }



    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);

//            add = add + "," + obj.getCountryName();
//            add = add + "," + obj.getCountryCode();
//            add = add + "," + obj.getAdminArea();
//            add = add + "," + obj.getPostalCode();
//            add = add + "," + obj.getSubAdminArea();
//            add = add + "," + obj.getLocality();
//            add = add + "," + obj.getSubThoroughfare();

//            getAdminArea(): returns the state acronym ("CA", for California)
//            getCountryCode(): returns the country ISO code ("JP", for Japan)
//            getCountryName(): returns country name ("Spain", for... Spain)
//            getFeatureName(): returns the name of the location, if any ("Louvre", for the museum)
//            getLocality(): returns the city name ("London")
//            getPostalCode(): returns the postal code ("94110", in the US)
//            getPremises(): ???
//            getSubAdminArea(): ???
//            getSubLocality(): ???
//            getSubThoroughfare(): ???
//            getThoroughfare(): returns the street and building number ("1600 Amphitheater Parkway")


            Log.d("IGA", "Address" + add);
            System.out.println("Address => "+add);
            return add;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Address Not Found";
    }

    public MainActivity.MainConnect mc;
    public void getService(String type){
        SharedPreferences sp;
        sp = getSharedPreferences("login",MODE_PRIVATE);
        System.out.println(curr_loc.getLatitude());
        System.out.println(curr_loc.getLongitude());
        String latitude_local = String.format("%f",curr_loc.getLatitude());
        String longitude_local = String.format("%f",curr_loc.getLongitude());
        String username_local = sp.getString("UserName",null);
        String address_local = getAddress(Double.parseDouble(latitude_local),Double.parseDouble(longitude_local));
        System.out.println("Entered!!");
        if(username_local==null){
            Toast.makeText(MainActivity.this,"Please login!",Toast.LENGTH_LONG).show();
            gotoLogin();
        }
        else{
            mc = new MainActivity.MainConnect(latitude_local,longitude_local,username_local,type,address_local);
            mc.execute();
        }
    }

    public class MainConnect extends AsyncTask<String,String,String> {
        private String latitude_local,longitude_local,username_local,type,address_local;
        MainConnect(String lat,String lon,String un,String t,String a){
            latitude_local = lat;
            longitude_local = lon;
            username_local = un;
            type = t;
            address_local = a;
        }

        @Override
        protected String doInBackground(String... params){

            String res = null;
            JSONObject jsonObject = null;
            try{
                System.out.println("Entered background!!");
                String ip = "172.16.101.50";
//                192.168.43.183 - Manonmaie
//                192.168.43.105 - Soumya
//                172.16.145.218 - Milan

                jsonObject = new JSONObject();
                jsonObject.put("Latitude", latitude_local);
                jsonObject.put("Longitude", longitude_local);
                jsonObject.put("Name",username_local);
                jsonObject.put("Type",type);
                jsonObject.put("Address",address_local);
                String serverURL="http://"+ip+":8080/EmergencyServicesBackend/webapi/resource/get_service";
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
                Log.d("MainActivity","Data from the Server: " + line);
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
                Log.d("MainActivity", "Some error has occurred at Server");
            }
            else if(s.equals("username or type is null"))
            {
                Log.e("MainActivity", "username or type is null");
                Toast.makeText(MainActivity.this,"username or type is null",Toast.LENGTH_LONG).show();
            }
            else if(s.equals("No username found"))
            {
                Log.e("MainActivity", "No username found" );
                Toast.makeText(MainActivity.this,"No username found",Toast.LENGTH_LONG).show();
            }
            else if(s.equals("Address not found")){
                Log.e("MainActivity", "Address not found" );
                Toast.makeText(MainActivity.this,"Address not found",Toast.LENGTH_LONG).show();
            }
            else if(s.equals("No requests of you")){
                Log.e("MainActivity", "No requests of you" );
                Toast.makeText(MainActivity.this,"Some error occured. Try again!!",Toast.LENGTH_LONG).show();
            }
            else
            {
                Log.d("MainActivity", "Request sent");
                Intent intent = new Intent(MainActivity.this, SearchingActivity.class);
                intent.putExtra("request_id",s);
//                Bundle basket= new Bundle();
//                basket.putString("UserName",username_local);
//                intent.putExtras(basket);
                startActivity(intent);
//                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mc = null;
        }
    }

    public void gotoLogin(){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    public void openProfileActivity(){
        Intent intent = new Intent(this,Profile.class);
        startActivity(intent);
    }
}
