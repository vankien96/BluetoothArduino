package com.example.vanki.bluetoothwitharduino;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

public class SetTimeActivity extends Activity {
    Button btnBack,btnSend;
    //TextView txtTime;
    String timeSend = "";
    TimePicker timePicker;
    public static final String TAG = "MyApp";
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_set_time);
        addControls();
        addEvents();
        xuLyTimePicker();
        timeSend = "t"+sdf.format(calendar.getTime());
    }

    private void xuLyTimePicker() {
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calendar.set(Calendar.MINUTE,minute);
                //txtTime.setText("Your time :"+ sdf.format(calendar.getTime()));
                timeSend = "t"+sdf.format(calendar.getTime());
            }
        });
    }


    private void addEvents() {
        //event for btn Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //event for btnSend
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData(timeSend);
            }
        });
    }

    private void addControls() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnSend = (Button) findViewById(R.id.btnSend);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
    }


    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        Log.d(TAG, "...Send data: " + message + "...");
        Toast.makeText(SetTimeActivity.this,"Sending time : "+sdf.format(calendar.getTime()),Toast.LENGTH_LONG).show();
        OutputStream outputStream = MainActivity.outStream;
        try {
            outputStream.write(msgBuffer);
        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            errorExit("Fatal Error", msg);
        }
    }
    private void errorExit(String title, String message) {
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }
}

