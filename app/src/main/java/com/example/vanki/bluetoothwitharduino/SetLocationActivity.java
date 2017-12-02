package com.example.vanki.bluetoothwitharduino;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.FloatProperty;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;

public class SetLocationActivity extends Activity {
    Button btnCurrent, btnSend, btnBack;
    EditText txtLat, txtLon;


    private LocationManager locationManager;
    Location location;

    public static final String TAG = "MyApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set_location);
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtLat.getText().length() == 0 || txtLon.getText().length() == 0) {
                    Toast.makeText(SetLocationActivity.this, "Please enter your location", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        double lat = Double.parseDouble(txtLat.getText().toString());
                        double lon = Double.parseDouble(txtLon.getText().toString());
                        String dataSend = "l" + "?lat="+lat+"&long="+lon;
                        sendData(dataSend);
                    } catch (Exception ex) {
                        Toast.makeText(SetLocationActivity.this, "Please insert right Location", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        btnCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });
    }
    private void getCurrentLocation(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(SetLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SetLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            errorExit("Error","Permission not granted");
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null){
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            txtLat.setText(""+latitude);
            txtLon.setText(""+longitude);
        }else {
            //errorExit("error", "Location null");
            Log.d(TAG,"Location null");
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, locationListener);
    }
    private void addControls() {
        btnCurrent = (Button) findViewById(R.id.btnCurrent);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnSend = (Button) findViewById(R.id.btnSend);
        txtLat = (EditText) findViewById(R.id.txtLat);
        txtLon = (EditText) findViewById(R.id.txtLon);
    }


    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        Log.d(TAG, "...Send data: " + message + "...");
        Toast.makeText(SetLocationActivity.this,"Sending location : " + message,Toast.LENGTH_LONG).show();
        OutputStream outputStream = MainActivity.outStream;
        try {
            outputStream.write(msgBuffer);
        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            errorExit("Fatal Error", msg);
        }
    }
    private void errorExit(String title, String message) {
        Toast.makeText(getBaseContext(), title + " - " + message.substring(1), Toast.LENGTH_LONG).show();
        //finish();
    }
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            txtLat.setText(""+location.getLatitude());
            txtLon.setText(""+location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("Latitude","status");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("Latitude","enable");
        }

        @Override
        public void onProviderDisabled(String provider) {
            if(provider.equals("gps")){
                Toast.makeText(getApplicationContext(), "GPS is off", Toast.LENGTH_LONG).show();

                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
            Log.i("lm_disabled",provider);
            Log.d("Latitude","disable");
        }
    };
}
