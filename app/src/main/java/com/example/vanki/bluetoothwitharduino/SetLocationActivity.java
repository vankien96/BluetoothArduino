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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.io.IOException;
import java.io.OutputStream;

public class SetLocationActivity extends Activity {
    Button btnSend, btnBack;
    TextView txtLocation;
    PlaceAutocompleteFragment autocompleteFragment;

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
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Lat: " + place.getLatLng().latitude + " long: " + place.getLatLng().longitude );
                location = new Location("new location");
                location.setLatitude(place.getLatLng().latitude);
                location.setLongitude(place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(location == null){
                    Toast.makeText(SetLocationActivity.this, "Please enter your location", Toast.LENGTH_LONG).show();
                }else{
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    String dataSend = "l" + "?lat="+lat+"&long="+lon;
                    sendData(dataSend);
                }
            }
        });
    }

    private void addControls() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnSend = (Button) findViewById(R.id.btnSend);
        txtLocation = (TextView) findViewById(R.id.txtLocation);
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
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
}
