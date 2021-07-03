package com.example.urltostringtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getDelegate().getSupportActionBar()).hide(); /* Hide action bar */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); /* Enable FULLSCREEN */
        setContentView(R.layout.activity_settings);

        TextView textView = (TextView) findViewById(R.id.defaultip);
        final EditText editText = (EditText) findViewById(R.id.edittext_ip);

        // keeping the value of textview
        SharedPreferences sharedPreferences = getSharedPreferences("cameraIP", MODE_PRIVATE);
        String camIP = sharedPreferences.getString("value","");
        textView.setText(camIP);

        // when submit button is pressed ...
        Button Submit = (Button) findViewById(R.id.submit_IP);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = editText.getText().toString().trim();
                SharedPreferences sharedPref = getSharedPreferences("cameraIP", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("value", value);
                editor.apply();
                textView.setText(value);
                Toast.makeText(Settings.this, "Camera IP changed", Toast.LENGTH_SHORT).show();
            }
        });


        // when back button is pressed ...
        ImageButton back = (ImageButton) findViewById(R.id.settings_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {finish();}
        });

    }
}