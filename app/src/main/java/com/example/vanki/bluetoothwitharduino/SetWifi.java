package com.example.vanki.bluetoothwitharduino;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;

public class SetWifi extends AppCompatActivity {

    Button btnSend,btnBack;
    EditText txtName,txtPass;
    String stringSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_wifi);
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
                if(txtPass.getText().equals("") || txtName.getText().equals("")){
                    Toast.makeText(SetWifi.this,"Please enter name and pass wifi",Toast.LENGTH_LONG).show();
                }else{
                    String user = txtName.getText().toString();
                    String pass = txtPass.getText().toString();
                    if(pass.length() < 8){
                        Toast.makeText(SetWifi.this,"Password is more than 8 character",Toast.LENGTH_LONG).show();
                    }else{
                        stringSend = "w"+user+"-"+pass;
                        sendData(stringSend);
                    }

                }
            }
        });
    }

    private void addControls() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnSend = (Button) findViewById(R.id.btnSend);
        txtName = (EditText) findViewById(R.id.txtName);
        txtPass = (EditText) findViewById(R.id.txtPass);
    }
    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        Log.d("MY APP: ", "...Send data: " + message + "...");
        Toast.makeText(SetWifi.this,"Sending time : "+message,Toast.LENGTH_LONG).show();
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
