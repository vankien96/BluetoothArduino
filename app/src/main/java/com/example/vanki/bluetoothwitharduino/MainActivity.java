package com.example.vanki.bluetoothwitharduino;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button btnChoose,btnLocation,btnTime;
    TextView txtShow;

    public static BluetoothAdapter btAdapter;
    public static BluetoothSocket btSocket;
    public static OutputStream outStream;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    Boolean check = false;

    private static String address = "98:D3:31:FD:34:F1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Set up something");
        addControls();
        addEvents();


        txtShow.setText("Device is not connected");
        btnTime.setEnabled(false);
        btnLocation.setEnabled(false);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Check Status Connect Device
        if(!checkBluetoothOnOff()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
        if (checkPaired()){
            if(btSocket == null){
                try{
                    BluetoothDevice device = btAdapter.getRemoteDevice(address);
                    btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                    btAdapter.cancelDiscovery();
                    try{
                        btSocket.connect();
                        Log.d("My","Run OK");
                        changeStatus();
                    }catch(Exception ex){
                        Log.d("My","Run NOK");
                        ex.printStackTrace();
                    }
                    try {
                        outStream = btSocket.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    Log.d("Loi","KHong dc r");
                    Toast.makeText(MainActivity.this,"Error - Cannot connected to device",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
        if (check) {
            check = false;
            try {
                BluetoothDevice device = btAdapter.getRemoteDevice(address);
                btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                btSocket.connect();
                try {
                    outStream = btSocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                changeStatus();
            } catch (IOException e) {
                Log.d("Loi", "KHong dc r");
                Toast.makeText(MainActivity.this, "Error - Cannot connected to device", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop(){
        super.onStop();

    }

    private void changeStatus() {
        btnTime.setEnabled(true);
        btnLocation.setEnabled(true);
        btnChoose.setEnabled(false);
        txtShow.setText("Device is connected");
    }


    private void addEvents() {
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,SetTimeActivity.class);
                startActivity(i);
            }
        });
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkConnected();
            }
        });
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,SetLocationActivity.class);
                startActivity(i);
            }
        });
    }

    private void checkConnected() {
        if (checkPaired()){
            try{
                BluetoothDevice device = btAdapter.getRemoteDevice(address);
                btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                btSocket.connect();
                try {
                    outStream = btSocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                changeStatus();
            } catch (IOException e) {
                Log.d("Loi","KHong dc r");
                //Toast.makeText(MainActivity.this,"Error - Cannot connected to device",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }else{
            BluetoothDevice device = btAdapter.getRemoteDevice(address);
            device.createBond();
            check = true;
        }
    }

    private  boolean checkPaired(){
        Set<BluetoothDevice> paired = btAdapter.getBondedDevices();
        for(BluetoothDevice item : paired){
            if(item.getAddress().equals(address)){
                return true;
            }
        }
        return false;
    }

    private void addControls() {
        btnChoose = (Button) findViewById(R.id.btnChooseDevice);
        btnTime = (Button) findViewById(R.id.btnTime);
        btnLocation = (Button) findViewById(R.id.btnLocation);
        txtShow = (TextView) findViewById(R.id.txtShow);
    }


    private boolean checkBluetoothOnOff(){
        return btAdapter.isEnabled();
    }

    @Override
    public void onBackPressed() {
        System.exit(1);
    }
}
