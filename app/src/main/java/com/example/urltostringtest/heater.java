package com.example.urltostringtest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sdsmdg.harjot.crollerTest.Croller;
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener;
import com.suke.widget.SwitchButton;

import java.util.Objects;

public class heater extends AppCompatActivity {

    private TextView textView;
    private SwitchButton switchButton;
    private SeekBar seekBar;
    private Croller croller;

    public boolean heater = false;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    public static final String SWITCH = "switch";

    private int seekBarProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getDelegate().getSupportActionBar()).hide(); /* Hide action bar */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); /* Enable FULLSCREEN */
        setContentView(R.layout.activity_heater);


        com.suke.widget.SwitchButton switchButton = (com.suke.widget.SwitchButton) findViewById(R.id.heater_onoff);
        TextView textView = (TextView) findViewById(R.id.heatertemp);
        Croller croller = (Croller) findViewById(R.id.croller);
        croller.setMax(35);
        croller.setMin(1);
        croller.setStartOffset(45);


        croller.setOnCrollerChangeListener(new OnCrollerChangeListener() {
            @Override
            public void onProgressChanged(Croller croller, int progress) {
                if (progress>= 14) {
                    textView.setText(progress + "°C");
                }
                else {
                    textView.setText("Off");
                }
                SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putInt("croller",croller.getProgress());
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(Croller croller) {

            }

            @Override
            public void onStopTrackingTouch(Croller croller) {

            }
        });



        SharedPreferences sharedPreferences = getSharedPreferences("save",MODE_PRIVATE);
        switchButton.setChecked(sharedPreferences.getBoolean("value",false));

        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (switchButton.isChecked()) {
                    // when switch checked
                    //save prefs for switch
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", true);
                    editor.apply();
                    switchButton.setChecked(true);
                } else {
                    //when switch unchecked
                    //save prefs for switch
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", false);
                    editor.apply();
                    switchButton.setChecked(false);
                }
            }
        });


        ImageButton back = (ImageButton) findViewById(R.id.heater_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // LOAD KAI UPDATE DATA PREFERENCES
        seekBarProgress = sharedPreferences.getInt("croller",0);
        croller.setProgress(seekBarProgress);
        textView.setText(croller +"°C");

    }
}