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
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textView.setText(String.valueOf(i)+"°C");
                saveData();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
                    editor.putInt("my_seekbar",seekBar.getProgress());
                    editor.apply();
                    switchButton.setChecked(true);
                } else {
                    //when switch unchecked
                    //save prefs for switch
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", false);
                    editor.putInt("my_seekbar",seekBar.getProgress());
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
        seekBarProgress = sharedPreferences.getInt("my_seekBar",0);
        seekBar.setProgress(seekBarProgress);
        textView.setText(String.valueOf(seekBarProgress)+"°C");

    }
    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("my_seekBar", seekBar.getProgress());
        editor.apply();
    }

}