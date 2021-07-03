package com.example.urltostringtest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.harjot.crollerTest.Croller;
import com.suke.widget.SwitchButton;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.jsoup.internal.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    public double usage;
    Integer temparc;
    String modew;
    Handler fire_handler,rain_handler,rain_handler2;
    String[] LogTable;
    String fullString="";
    public TextView textview_heater;
    private TextView temperature,humid,fire,use;
    public boolean fire_notify = false;
    public boolean rain_notify = false;
    public boolean lock = false;
    public boolean heater = false;
    SharedPreferences sharedPreferencesbed,sharedPreferencesoff,sharedPreferencesbath,sharedPreferenceslr,sharedPreferencesgar,sharedPreferenceskit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getDelegate().getSupportActionBar()).hide(); /* Hide action bar */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); /* Enable FULLSCREEN */
        setContentView(R.layout.activity_main);

        // LOADING ALL THE PREFERENCES
        sharedPreferencesbed = getSharedPreferences("bedroom",MODE_PRIVATE);
        sharedPreferencesoff = getSharedPreferences("office",MODE_PRIVATE);
        sharedPreferencesbath = getSharedPreferences("bathroom",MODE_PRIVATE);
        sharedPreferenceslr = getSharedPreferences("livingroom",MODE_PRIVATE);
        sharedPreferencesgar = getSharedPreferences("garage",MODE_PRIVATE);
        sharedPreferenceskit = getSharedPreferences("kitchen",MODE_PRIVATE);

        // OPEN DIALOG LOADING SCREEN FOR 2 SECONDS
        final Dialog d=new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen); //Theme_Black_NoTitleBar_Fullscreen
        d.setContentView(R.layout.activity_loading);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        d.setCancelable(false);
        d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        d.show();
        d.getWindow().getDecorView().setSystemUiVisibility(MainActivity.this.getWindow().getDecorView().getSystemUiVisibility());
        d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        d.getWindow().setAttributes(lp);

        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            public void run() {
                d.dismiss();

            }
        }, 2000);

        // CALL OF TEST1 -> COMMUNICATE WITH  WEBSERVER //
        final Handler handler = new Handler();
        final int delay = 6000; // 6sec ananewsi dedomenwn apo webserver

        Test1();
        handler.postDelayed(new Runnable() {
            public void run() {
                Test1();
                handler.postDelayed(this, delay);
            }
        }, delay);


        // Orizw ta buttons kai ta textviews vash twn ID's //
        temperature = (TextView) findViewById(R.id.tempa);/* TEMERATURE*/
        humid = (TextView) findViewById(R.id.humi);/* HUMIDITY*/
        Button bathroom_b = findViewById(R.id.button1);/* BATHROOM*/
        Button lroom_b = findViewById(R.id.button2);/* LIVING ROOM*/
        Button kitchen_b = findViewById(R.id.button3);/* KITCHEN*/
        Button office_b = findViewById(R.id.button4);/* OFFICE*/
        Button bedroom_b = findViewById(R.id.button5);/* BEDROOM*/
        Button garage_b = findViewById(R.id.button6);/* GARAGE*/
        Button camera_b = findViewById(R.id.button7);/* CAMERA*/
        Button heater_b = findViewById(R.id.button8);/* HEATER*/



        ////// BUTTONS MAIN MENU //////
        ImageButton homelock = (ImageButton) findViewById(R.id.homelock);
        ImageButton settings = (ImageButton) findViewById(R.id.settings);

        // LOCK BUTTON
        homelock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lock) {
                    homelock.setImageResource(R.drawable.round_lock_black_48);
                    homelock.setBackgroundResource(R.drawable.lockbg);
                    lock = true;
                    Toast.makeText(MainActivity.this, "House Locked", Toast.LENGTH_SHORT).show();
                }else {
                    homelock.setImageResource(R.drawable.round_lock_open_black_48);
                    homelock.setBackgroundResource(R.drawable.unlockbg);
                    lock = false;
                    Toast.makeText(MainActivity.this, "House Unlocked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Settings
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Settings.class);
                startActivity(intent);
            }
        });

        // Camera
        camera_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,camera.class);
                startActivity(intent);
            }
        });

        // Heater
        heater_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,heater.class);
                startActivity(intent);
            }
        });

        // Patontas to koumpi Bathroom
        bathroom_b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                final Dialog d=new Dialog(MainActivity.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen); //Theme_Black_NoTitleBar_Fullscreen
                d.setContentView(R.layout.bathroom);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(d.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                d.setCancelable(false);
                d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                d.show();
                d.getWindow().getDecorView().setSystemUiVisibility(MainActivity.this.getWindow().getDecorView().getSystemUiVisibility());
                d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                d.getWindow().setAttributes(lp);
                TextView humi = (TextView) d.findViewById(R.id.humi_bath);
                TextView temp=(TextView) d.findViewById(R.id.temp_bath);
                SwitchButton lall=(SwitchButton) d.findViewById(R.id.lights_bath);
                SwitchButton waterheater=(SwitchButton) d.findViewById(R.id.waterheater);
                SwitchButton out=(SwitchButton) d.findViewById(R.id.outlet_bath);



                lall.setChecked(sharedPreferencesbath.getBoolean("lall",false));
                waterheater.setChecked(sharedPreferencesbath.getBoolean("waterheater",false));
                out.setChecked(sharedPreferencesbath.getBoolean("out",false));


                // checking if logtable has values.WEBSERVER IS UP/AVAILABLE
                if (LogTable.length>1) {
                    temp.setText("Temperature: " + LogTable[0] + "°C");
                    humi.setText("Humidity: " + LogTable[1] + "%");
                }
                final Handler handlerb = new Handler();
                handlerb.postDelayed(new Runnable() {
                    public void run() {
                        if (LogTable.length>1) {
                            temp.setText("Temperature: " + LogTable[0] + "°C");
                            humi.setText("Humidity: " + LogTable[1] + "%");
                        }
                        handlerb.postDelayed(this, 1000);
                    }
                }, 1000);


                // TO BACK PANW ARISTERA KLEINEI TO DIALOG
                @SuppressLint("WrongViewCast") ImageButton back = (ImageButton) d.findViewById(R.id.back);

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handlerb.removeCallbacksAndMessages(null);
                        SharedPreferences.Editor editor = getSharedPreferences("bathroom", MODE_PRIVATE).edit();
                        editor.putBoolean("lall", lall.isChecked());
                        editor.putBoolean("waterheater", waterheater.isChecked());
                        editor.putBoolean("out", out.isChecked());
                        editor.apply();
                        d.dismiss();
                    }
                });

                // TO DEFAULT BACK KLEINEI TO DIALOG
                d.setOnKeyListener(new Dialog.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface arg0, int keyCode,
                                         KeyEvent event) {
                        // TODO Auto-generated method stub
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            handlerb.removeCallbacksAndMessages(null);
                            SharedPreferences.Editor editor = getSharedPreferences("bath", MODE_PRIVATE).edit();
                            editor.putBoolean("lall", lall.isChecked());
                            editor.putBoolean("waterheater", waterheater.isChecked());
                            editor.putBoolean("out", out.isChecked());
                            editor.apply();
                            d.dismiss();
                        }
                        return true;
                    }
                });


            }
        });

        // Patontas to koumpi Living Room
        lroom_b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // NEW DIALOG
                final Dialog d=new Dialog(MainActivity.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen); //Theme_Black_NoTitleBar_Fullscreen
                d.setContentView(R.layout.living_room);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(d.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                d.setCancelable(false);
                d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                d.show();
                d.getWindow().getDecorView().setSystemUiVisibility(MainActivity.this.getWindow().getDecorView().getSystemUiVisibility());
                d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                d.getWindow().setAttributes(lp);

                TextView humi = (TextView) d.findViewById(R.id.humi_lr);
                TextView temp=(TextView) d.findViewById(R.id.temp_lr);
                SwitchButton lall=(SwitchButton) d.findViewById(R.id.lights_lr);
                SwitchButton l1=(SwitchButton) d.findViewById(R.id.lamp_lr);
                SwitchButton out=(SwitchButton) d.findViewById(R.id.outlet_lr);
                SwitchButton ac=(SwitchButton) d.findViewById(R.id.ac_lr);
                ImageButton heat = (ImageButton) d.findViewById(R.id.heat);
                ImageButton cold = (ImageButton) d.findViewById(R.id.cold);
                ImageButton up = (ImageButton) d.findViewById(R.id.up);
                ImageButton down = (ImageButton) d.findViewById(R.id.down);
                TextView tempac = (TextView) d.findViewById(R.id.tempac);


                temparc=sharedPreferenceslr.getInt("tempac",17);
                tempac.setText(""+temparc);

                up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (temparc<32){
                            tempac.setText(""+(temparc+1));
                            temparc+=1;
                        }
                    }
                });

                down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (temparc>14){
                            tempac.setText(""+(temparc-1));
                            temparc-=1;
                        }
                    }
                });

                modew=sharedPreferenceslr.getString("mode","cold");
                lall.setChecked(sharedPreferenceslr.getBoolean("lall",false));
                l1.setChecked(sharedPreferenceslr.getBoolean("l1",false));
                out.setChecked(sharedPreferenceslr.getBoolean("out",false));
                ac.setChecked(sharedPreferenceslr.getBoolean("ac",false));

                if(modew.equals("heat")){
                    heat.setBackgroundResource(R.drawable.buttonsbgpressed);
                    heat.setColorFilter(Color.argb(255, 255, 255, 255));
                }
                else{
                    cold.setBackgroundResource(R.drawable.buttonsbgpressed);
                    cold.setColorFilter(Color.argb(255, 255, 255, 255));
                }

                heat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(modew.equals("heat")){}
                        else{
                            cold.setBackgroundResource(R.color.mygrey2);
                            cold.setColorFilter(Color.argb(0, 0, 0, 0));
                            heat.setBackgroundResource(R.drawable.buttonsbgpressed);
                            heat.setColorFilter(Color.argb(255, 255, 255, 255));
                            modew="heat";
                        }
                    }
                });

                cold.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(modew.equals("cold")){}
                        else{
                            heat.setBackgroundResource(R.color.mygrey2);
                            heat.setColorFilter(Color.argb(0, 0, 0, 0));
                            cold.setBackgroundResource(R.drawable.buttonsbgpressed);
                            cold.setColorFilter(Color.argb(255, 255, 255, 255));
                            modew="cold";
                        }
                    }
                });

                // checking if logtable has values. IS WEBSERVER IS UP/AVAILABLE
                if (LogTable.length>1) {
                    temp.setText("Temperature: " + LogTable[0] + "°C");
                    humi.setText("Humidity: " + LogTable[1] + "%");
                }
                final Handler handlerb = new Handler();
                handlerb.postDelayed(new Runnable() {
                    public void run() {
                        if (LogTable.length>1) {
                            temp.setText("Temperature: " + LogTable[0] + "°C");
                            humi.setText("Humidity: " + LogTable[1] + "%");
                        }
                        handlerb.postDelayed(this, 1000);
                    }
                    }, 1000);




                // TO BACK PANW ARISTERA KLEINEI TO DIALOG
                @SuppressLint("WrongViewCast") ImageButton back = (ImageButton) d.findViewById(R.id.back);

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handlerb.removeCallbacksAndMessages(null);
                        SharedPreferences.Editor editor = getSharedPreferences("livingroom", MODE_PRIVATE).edit();
                        editor.putBoolean("lall", lall.isChecked());
                        editor.putBoolean("l1", l1.isChecked());
                        editor.putBoolean("out", out.isChecked());
                        editor.putBoolean("ac", ac.isChecked());
                        editor.putString("mode",modew);
                        editor.putInt("tempac",temparc);
                        editor.apply();
                        d.dismiss();
                    }
                });

                // TO DEFAULT BACK KLEINEI TO DIALOG
                d.setOnKeyListener(new Dialog.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface arg0, int keyCode,
                                         KeyEvent event) {
                        // TODO Auto-generated method stub
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            handlerb.removeCallbacksAndMessages(null);
                            SharedPreferences.Editor editor = getSharedPreferences("livingroom", MODE_PRIVATE).edit();
                            editor.putBoolean("lall", lall.isChecked());
                            editor.putBoolean("l1", l1.isChecked());
                            editor.putBoolean("out", out.isChecked());
                            editor.putBoolean("ac", ac.isChecked());
                            editor.putString("mode",modew);
                            editor.putInt("tempac",temparc);
                            editor.apply();
                            d.dismiss();
                        }
                        return true;
                    }
                });


            }
        });

        // Patontas to koumpi Kicthen
        kitchen_b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Dialog d=new Dialog(MainActivity.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen); //Theme_Black_NoTitleBar_Fullscreen
                d.setContentView(R.layout.kitchen);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(d.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                d.setCancelable(false);
                d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                d.show();
                d.getWindow().getDecorView().setSystemUiVisibility(MainActivity.this.getWindow().getDecorView().getSystemUiVisibility());
                d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                d.getWindow().setAttributes(lp);
                TextView humi = (TextView) d.findViewById(R.id.humi_kitchen);
                TextView temp=(TextView) d.findViewById(R.id.temp_kitchen);
                SwitchButton lall=(SwitchButton) d.findViewById(R.id.lights_kitchen);
                SwitchButton refr=(SwitchButton) d.findViewById(R.id.refrigerator);
                SwitchButton out=(SwitchButton) d.findViewById(R.id.outlet_kitchen);

                lall.setChecked(sharedPreferenceskit.getBoolean("lall",false));
                refr.setChecked(sharedPreferenceskit.getBoolean("refr",true));
                out.setChecked(sharedPreferenceskit.getBoolean("out",false));


                // checking if logtable has values. IS WEBSERVER IS UP/AVAILABLE
                if (LogTable.length>1) {
                    temp.setText("Temperature: " + LogTable[0] + "°C");
                    humi.setText("Humidity: " + LogTable[1] + "%");
                }

                final Handler handlerb = new Handler();
                handlerb.postDelayed(new Runnable() {
                    public void run() {

                        if (LogTable.length>1) {
                            temp.setText("Temperature: " + LogTable[0] + "°C");
                            humi.setText("Humidity: " + LogTable[1] + "%");
                        }
                        handlerb.postDelayed(this, 1000);
                    }
                }, 1000);



                // PATONTAS TO BACK PANW ARITERA KLEINEI TO DIALOG
                @SuppressLint("WrongViewCast") ImageButton back = (ImageButton) d.findViewById(R.id.back);

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handlerb.removeCallbacksAndMessages(null);
                        SharedPreferences.Editor editor = getSharedPreferences("kitchen", MODE_PRIVATE).edit();
                        editor.putBoolean("lall", lall.isChecked());
                        editor.putBoolean("refr", refr.isChecked());
                        editor.putBoolean("out", out.isChecked());
                        editor.apply();
                        d.dismiss();
                    }
                });

                // TO DEFAULT BACK KLEINEI TO DIALOG
                d.setOnKeyListener(new Dialog.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface arg0, int keyCode,
                                         KeyEvent event) {
                        // TODO Auto-generated method stub
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            handlerb.removeCallbacksAndMessages(null);
                            SharedPreferences.Editor editor = getSharedPreferences("kitchen", MODE_PRIVATE).edit();
                            editor.putBoolean("lall", lall.isChecked());
                            editor.putBoolean("door", refr.isChecked());
                            editor.putBoolean("out", out.isChecked());
                            editor.apply();
                            d.dismiss();
                        }
                        return true;
                    }
                });
            }
        });

        // Patontas to koumpi Office
        office_b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final Dialog d=new Dialog(MainActivity.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen); //Theme_Black_NoTitleBar_Fullscreen
                d.setContentView(R.layout.office);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(d.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                d.setCancelable(false);
                d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                d.show();
                d.getWindow().getDecorView().setSystemUiVisibility(MainActivity.this.getWindow().getDecorView().getSystemUiVisibility());
                d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                d.getWindow().setAttributes(lp);
                TextView humi = (TextView) d.findViewById(R.id.humi_off);
                TextView temp=(TextView) d.findViewById(R.id.temp_off);
                SwitchButton lall=(SwitchButton) d.findViewById(R.id.lights_office);
                SwitchButton l1=(SwitchButton) d.findViewById(R.id.lamp1_office);
                SwitchButton l2=(SwitchButton) d.findViewById(R.id.lamp2_office);
                SwitchButton out=(SwitchButton) d.findViewById(R.id.outlet_office);
                SwitchButton ac=(SwitchButton) d.findViewById(R.id.ac_office);
                ImageButton heat = (ImageButton) d.findViewById(R.id.heat);
                ImageButton cold = (ImageButton) d.findViewById(R.id.cold);

                ImageButton up = (ImageButton) d.findViewById(R.id.up);
                ImageButton down = (ImageButton) d.findViewById(R.id.down);
                TextView tempac = (TextView) d.findViewById(R.id.tempac);


                temparc=sharedPreferencesoff.getInt("tempac",17);
                tempac.setText(""+temparc);

                up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (temparc<30){
                            tempac.setText(""+(temparc+1));
                            temparc+=1;
                        }
                    }
                });

                down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (temparc>14){
                            tempac.setText(""+(temparc-1));
                            temparc-=1;
                        }
                    }
                });

                modew=sharedPreferencesoff.getString("mode","cold");
                lall.setChecked(sharedPreferencesoff.getBoolean("lall",false));
                l1.setChecked(sharedPreferencesoff.getBoolean("l1",false));
                l2.setChecked(sharedPreferencesoff.getBoolean("l2",false));
                out.setChecked(sharedPreferencesoff.getBoolean("out",false));
                ac.setChecked(sharedPreferencesoff.getBoolean("ac",false));

                if(modew.equals("heat")){
                    heat.setBackgroundResource(R.drawable.buttonsbgpressed);
                    heat.setColorFilter(Color.argb(255, 255, 255, 255));
                }
                else{
                    cold.setBackgroundResource(R.drawable.buttonsbgpressed);
                    cold.setColorFilter(Color.argb(255, 255, 255, 255));
                }

                heat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(modew.equals("heat")){}
                        else{
                            cold.setBackgroundResource(R.color.mygrey2);
                            cold.setColorFilter(Color.argb(0, 0, 0, 0));
                            heat.setBackgroundResource(R.drawable.buttonsbgpressed);
                            heat.setColorFilter(Color.argb(255, 255, 255, 255));
                            modew="heat";
                        }
                    }
                });

                cold.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(modew.equals("cold")){}
                        else{
                            heat.setBackgroundResource(R.color.mygrey2);
                            heat.setColorFilter(Color.argb(0, 0, 0, 0));
                            cold.setBackgroundResource(R.drawable.buttonsbgpressed);
                            cold.setColorFilter(Color.argb(255, 255, 255, 255));
                            modew="cold";
                        }
                    }
                });


                // checking if logtable has values. IS WEBSERVER IS UP/AVAILABLE
                if (LogTable.length>1) {
                    temp.setText("Temperature: " + LogTable[0] + "°C");
                    humi.setText("Humidity: " + LogTable[1] + "%");
                }
                final Handler handlerb = new Handler();
                handlerb.postDelayed(new Runnable() {
                    public void run() {
                        if (LogTable.length>1) {
                            temp.setText("Temperature: " + LogTable[0] + "°C");
                            humi.setText("Humidity: " + LogTable[1] + "%");
                        }
                        handlerb.postDelayed(this, 1000);
                    }
                }, 1000);


                // TO BACK PANW ARISTERA KLEINEI TO DIALOG
                @SuppressLint("WrongViewCast") ImageButton back = (ImageButton) d.findViewById(R.id.back);

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handlerb.removeCallbacksAndMessages(null);
                        SharedPreferences.Editor editor = getSharedPreferences("office", MODE_PRIVATE).edit();
                        editor.putBoolean("lall", lall.isChecked());
                        editor.putBoolean("l1", l1.isChecked());
                        editor.putBoolean("l2", l2.isChecked());
                        editor.putBoolean("out", out.isChecked());
                        editor.putBoolean("ac", ac.isChecked());
                        editor.putString("mode",modew);
                        editor.putInt("tempac",temparc);
                        editor.apply();
                        d.dismiss();
                    }
                });

                // TO DEFAULT BACK KLEINEI TO DIALOG
                d.setOnKeyListener(new Dialog.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface arg0, int keyCode,
                                         KeyEvent event) {
                        // TODO Auto-generated method stub
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            handlerb.removeCallbacksAndMessages(null);
                            SharedPreferences.Editor editor = getSharedPreferences("office", MODE_PRIVATE).edit();
                            editor.putBoolean("lall", lall.isChecked());
                            editor.putBoolean("l1", l1.isChecked());
                            editor.putBoolean("l2", l2.isChecked());
                            editor.putBoolean("out", out.isChecked());
                            editor.putBoolean("ac", ac.isChecked());
                            editor.putString("mode",modew);
                            editor.putInt("tempac",temparc);
                            editor.apply();
                            d.dismiss();
                        }
                        return true;
                    }
                });


            }
        });

        // Patontas to koumpi Bedroom
        bedroom_b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final Dialog d=new Dialog(MainActivity.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen); //Theme_Black_NoTitleBar_Fullscreen
                d.setContentView(R.layout.bedroom);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(d.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                d.setCancelable(false);
                d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                d.show();
                d.getWindow().getDecorView().setSystemUiVisibility(MainActivity.this.getWindow().getDecorView().getSystemUiVisibility());
                d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                d.getWindow().setAttributes(lp);
                TextView humi = (TextView) d.findViewById(R.id.humi_bedroom);
                TextView temp=(TextView) d.findViewById(R.id.temp_bedroom);
                SwitchButton lall=(SwitchButton) d.findViewById(R.id.lights_bedroom);
                SwitchButton l1=(SwitchButton) d.findViewById(R.id.lamp1_bedroom);
                SwitchButton l2=(SwitchButton) d.findViewById(R.id.lamp2_bedroom);
                SwitchButton out=(SwitchButton) d.findViewById(R.id.outlet_bedroom);
                SwitchButton ac=(SwitchButton) d.findViewById(R.id.ac_bedroom);
                ImageButton heat = (ImageButton) d.findViewById(R.id.heat);
                ImageButton cold = (ImageButton) d.findViewById(R.id.cold);

                ImageButton up = (ImageButton) d.findViewById(R.id.up);
                ImageButton down = (ImageButton) d.findViewById(R.id.down);
                TextView tempac = (TextView) d.findViewById(R.id.tempac);

                temparc=sharedPreferencesbed.getInt("tempac",17);
                tempac.setText(""+temparc);

                up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (temparc<30){
                            tempac.setText(""+(temparc+1));
                            temparc+=1;
                        }
                    }
                });

                down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (temparc>14){
                            tempac.setText(""+(temparc-1));
                            temparc-=1;
                        }
                    }
                });

                modew=sharedPreferencesbed.getString("mode","cold");
                lall.setChecked(sharedPreferencesbed.getBoolean("lall",false));
                l1.setChecked(sharedPreferencesbed.getBoolean("l1",false));
                l2.setChecked(sharedPreferencesbed.getBoolean("l2",false));
                out.setChecked(sharedPreferencesbed.getBoolean("out",false));
                ac.setChecked(sharedPreferencesbed.getBoolean("ac",false));

                if(modew.equals("heat")){
                    heat.setBackgroundResource(R.drawable.buttonsbgpressed);
                    heat.setColorFilter(Color.argb(255, 255, 255, 255));
                }
                else{
                    cold.setBackgroundResource(R.drawable.buttonsbgpressed);
                    cold.setColorFilter(Color.argb(255, 255, 255, 255));
                }

                heat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(modew.equals("heat")){}
                        else{
                            cold.setBackgroundResource(R.color.mygrey2);
                            cold.setColorFilter(Color.argb(0, 0, 0, 0));
                            heat.setBackgroundResource(R.drawable.buttonsbgpressed);
                            heat.setColorFilter(Color.argb(255, 255, 255, 255));
                            modew="heat";
                        }
                    }
                });

                cold.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(modew.equals("cold")){}
                        else{
                            heat.setBackgroundResource(R.color.mygrey2);
                            heat.setColorFilter(Color.argb(0, 0, 0, 0));
                            cold.setBackgroundResource(R.drawable.buttonsbgpressed);
                            cold.setColorFilter(Color.argb(255, 255, 255, 255));
                            modew="cold";
                        }
                    }
                });

                // checking if logtable has values. IS WEBSERVER IS UP/AVAILABLE
                if (LogTable.length>1) {
                    temp.setText("Temperature: " + LogTable[0] + "°C");
                    humi.setText("Humidity: " + LogTable[1] + "%");
                }

                final Handler handlerb = new Handler();
                handlerb.postDelayed(new Runnable() {
                    public void run() {

                        if (LogTable.length>1) {
                            temp.setText("Temperature: " + LogTable[0] + "°C");
                            humi.setText("Humidity: " + LogTable[1] + "%");
                        }
                        handlerb.postDelayed(this, 1000);
                    }
                }, 1000);



                // PATONTAS TO BACK PANW ARITERA KLEINEI TO DIALOG
                @SuppressLint("WrongViewCast") ImageButton back = (ImageButton) d.findViewById(R.id.back);

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handlerb.removeCallbacksAndMessages(null);
                        SharedPreferences.Editor editor = getSharedPreferences("bedroom", MODE_PRIVATE).edit();
                        editor.putBoolean("lall", lall.isChecked());
                        editor.putBoolean("l1", l1.isChecked());
                        editor.putBoolean("l2", l2.isChecked());
                        editor.putBoolean("out", out.isChecked());
                        editor.putBoolean("ac", ac.isChecked());
                        editor.putString("mode",modew);
                        editor.putInt("tempac",temparc);
                        editor.apply();
                        d.dismiss();
                    }
                });

                // TO DEFAULT BACK KLEINEI TO DIALOG
                d.setOnKeyListener(new Dialog.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface arg0, int keyCode,
                                         KeyEvent event) {
                        // TODO Auto-generated method stub
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            handlerb.removeCallbacksAndMessages(null);
                            SharedPreferences.Editor editor = getSharedPreferences("bedroom", MODE_PRIVATE).edit();
                            editor.putBoolean("lall", lall.isChecked());
                            editor.putBoolean("l1", l1.isChecked());
                            editor.putBoolean("l2", l2.isChecked());
                            editor.putBoolean("out", out.isChecked());
                            editor.putBoolean("ac", ac.isChecked());
                            editor.putString("mode",modew);
                            editor.putInt("tempac",temparc);
                            editor.apply();
                            d.dismiss();
                        }
                        return true;
                    }
                });
            }
        });

        // Patontas to koumpi Garage
        garage_b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Dialog d=new Dialog(MainActivity.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen); //Theme_Black_NoTitleBar_Fullscreen
                d.setContentView(R.layout.garage);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(d.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                d.setCancelable(false);
                d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                d.show();
                d.getWindow().getDecorView().setSystemUiVisibility(MainActivity.this.getWindow().getDecorView().getSystemUiVisibility());
                d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                d.getWindow().setAttributes(lp);
                TextView humi = (TextView) d.findViewById(R.id.humi_garage);
                TextView temp=(TextView) d.findViewById(R.id.temp_garage);
                SwitchButton lall=(SwitchButton) d.findViewById(R.id.lights_garage);
                SwitchButton door=(SwitchButton) d.findViewById(R.id.door_garage);
                SwitchButton out=(SwitchButton) d.findViewById(R.id.outlet_garage);

                lall.setChecked(sharedPreferencesgar.getBoolean("lall",false));
                door.setChecked(sharedPreferencesgar.getBoolean("door",false));
                out.setChecked(sharedPreferencesgar.getBoolean("out",false));


                // checking if logtable has values. IS WEBSERVER IS UP/AVAILABLE
                if (LogTable.length>1) {
                    temp.setText("Temperature: " + LogTable[0] + "°C");
                    humi.setText("Humidity: " + LogTable[1] + "%");
                }

                final Handler handlerb = new Handler();
                handlerb.postDelayed(new Runnable() {
                    public void run() {

                        if (LogTable.length>1) {
                            temp.setText("Temperature: " + LogTable[0] + "°C");
                            humi.setText("Humidity: " + LogTable[1] + "%");
                        }
                        handlerb.postDelayed(this, 1000);
                    }
                }, 1000);



                // PATONTAS TO BACK PANW ARITERA KLEINEI TO DIALOG
                @SuppressLint("WrongViewCast") ImageButton back = (ImageButton) d.findViewById(R.id.back);

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handlerb.removeCallbacksAndMessages(null);
                        SharedPreferences.Editor editor = getSharedPreferences("garage", MODE_PRIVATE).edit();
                        editor.putBoolean("lall", lall.isChecked());
                        editor.putBoolean("door", door.isChecked());
                        editor.putBoolean("out", out.isChecked());
                        editor.apply();
                        d.dismiss();
                    }
                });

                // TO DEFAULT BACK KLEINEI TO DIALOG
                d.setOnKeyListener(new Dialog.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface arg0, int keyCode,
                                         KeyEvent event) {
                        // TODO Auto-generated method stub
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            handlerb.removeCallbacksAndMessages(null);
                            SharedPreferences.Editor editor = getSharedPreferences("garage", MODE_PRIVATE).edit();
                            editor.putBoolean("lall", lall.isChecked());
                            editor.putBoolean("door", door.isChecked());
                            editor.putBoolean("out", out.isChecked());
                            editor.apply();
                            d.dismiss();
                        }
                        return true;
                    }
                });
            }
        });
    }

    public void Test1 (){




        new Thread(new Runnable() {
            public void run(){
                String urela1 = "http://192.168.2.40";
                URL url = null;
                try {
                    url = new URL(urela1);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                BufferedReader in1 = null;
                try {
                    in1 = new BufferedReader(
                            new InputStreamReader(
                                    url.openStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (in1 != null) {

                    try {
                        fullString = in1.readLine();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        in1.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread (new Runnable() {
                    public void run(){
                        // LogTable[0] -> temperature
                        // LogTable[1] -> humidity
                        // LogTable[2] -> fire sensor
                        // LogTable[3] -> rain sensor
                        // LogTable[4] -> motion sensor
                        LogTable = fullString.split(" ");
                        if (LogTable.length>1) {
                            //Jsontxt.setText(fullString);

                            temperature.setText("Temperature: "+LogTable[0]+"°C");

                            humid.setText("Humidity: "+LogTable[1]+"%");


                            // an o aisthitiras fwtias anixnefsei 0,1 tote stelnw notification warning. me to flag stamatw tin sinexomeni apostoli notifications.
                            if ((Integer.parseInt(LogTable[2]) == 0 || Integer.parseInt(LogTable[2]) == 1) && !fire_notify) {
                                fire_notify = true;
                                createNotification("Warning", "Fire!");
                                fire_handler = new Handler();
                                fire_handler.postDelayed(new Runnable() {
                                    public void run() {
                                        fire_notify = false;
                                        //Toast.makeText(MainActivity.this, "Mhdenisa fwtia", Toast.LENGTH_SHORT).show();
                                    }
                                }, 10000);

                            }
                            else if (Integer.parseInt(LogTable[2]) == 2) {
                                fire_notify = false;
                            }

                            // an o aisthitiras vroxis anixnefsei vroxoptosi stelnei ta antistixa notifications. Ana 3 wres stelnei notification(an vrexei)
                            if (Integer.parseInt(LogTable[3]) == 0 && !rain_notify) {
                                rain_notify = true;
                                createNotification("Weather Report", "Rain");
                                rain_handler = new Handler();
                                rain_handler.postDelayed(new Runnable() {
                                    public void run() {
                                        fire_notify = false;
                                        Toast.makeText(MainActivity.this, "Mhdenisa vroxi", Toast.LENGTH_SHORT).show();
                                    }
                                }, 10800000);

                            }
                            else if (Integer.parseInt(LogTable[3]) == 1 && !rain_notify ) {
                                rain_notify = true;
                                rain_handler2 = new Handler();
                                rain_handler2.postDelayed(new Runnable() {
                                    public void run() {
                                        fire_notify = false;
                                        //Toast.makeText(MainActivity.this, "Mhdenisa vroxi", Toast.LENGTH_SHORT).show();
                                    }
                                }, 10800000);
                                createNotification("Weather Report", "Light Rain");
                            }
                            else if (Integer.parseInt(LogTable[3]) == 2 ){
                                rain_notify = false; // epanafora tou flag se false otan stamatisei i vroxi wsta na mporw na to xrisimopoihsw amesws.
                                //.makeText(MainActivity.this, "Mhdenisa vroxi", Toast.LENGTH_SHORT).show();
                            }


                            /// PIR Motion sensor ///
                            /// If house is locked and movement detected ///
                            if ((Integer.parseInt(LogTable[4]) == 1) && (lock == true)){
                                createNotification("Security", "Movement detected");
                                Intent intent = new Intent(MainActivity.this,camera.class);
                                startActivity(intent);
                            }


                        }

                    }});


            }
        }).start();



    }


    //TODO NOTIFICATION AREA************
    private NotificationManager notifManager;
    public void createNotification(String aMessage, String bMessage) {
        final int NOTIFY_ID = 1002;

        // There are hardcoding only for show it's just strings
        String name = "my_package_channel";
        String id = "my_package_channel_1"; // The user-visible name of the channel.
        String description = "my_package_first_channel"; // The user-visible description of the channel.


        if (bMessage.equals("Movement detected")){
            Intent intent;
            PendingIntent pendingIntent;
            NotificationCompat.Builder builder;
            if (notifManager == null) {
                notifManager =
                        (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = notifManager.getNotificationChannel(id);
                if (mChannel == null) {
                    mChannel = new NotificationChannel(id, name, importance);
                    mChannel.setDescription(description);
                    mChannel.enableVibration(true);
                    mChannel.setLightColor(Color.GREEN);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    notifManager.createNotificationChannel(mChannel);
                }
                builder = new NotificationCompat.Builder(this, id);

                intent = new Intent(this, camera.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

                builder.setContentTitle(aMessage)  // required
                        .setSmallIcon(R.drawable.notification) // required
                        .setContentText(bMessage)  // required
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setTicker(aMessage)
                        .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            } else {

                builder = new NotificationCompat.Builder(this);

                intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

                builder.setContentTitle(aMessage)
                        .setSmallIcon(R.drawable.notification) // required
                        .setContentText(bMessage)  // required
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setTicker(aMessage)
                        .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                        .setPriority(Notification.PRIORITY_HIGH);
            } // else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Notification notification = builder.build();
            notifManager.notify(NOTIFY_ID, notification);

        }else {
            Intent intent;
            PendingIntent pendingIntent;
            NotificationCompat.Builder builder;
            if (notifManager == null) {
                notifManager =
                        (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = notifManager.getNotificationChannel(id);
                if (mChannel == null) {
                    mChannel = new NotificationChannel(id, name, importance);
                    mChannel.setDescription(description);
                    mChannel.enableVibration(true);
                    mChannel.setLightColor(Color.GREEN);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    notifManager.createNotificationChannel(mChannel);
                }
                builder = new NotificationCompat.Builder(this, id);

                intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

                builder.setContentTitle(aMessage)  // required
                        .setSmallIcon(R.drawable.notification) // required
                        .setContentText(bMessage)  // required
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setTicker(aMessage)
                        .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            } else {

                builder = new NotificationCompat.Builder(this);

                intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

                builder.setContentTitle(aMessage)
                        .setSmallIcon(R.drawable.notification) // required
                        .setContentText(bMessage)  // required
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setTicker(aMessage)
                        .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                        .setPriority(Notification.PRIORITY_HIGH);
            } // else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Notification notification = builder.build();
            notifManager.notify(NOTIFY_ID, notification);

        }

    }

    //TODO ON BACK DIALOG POPUP
    @Override
    public void onBackPressed(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("MyHome");
        builder.setMessage("Are you sure you want to exit?");
        builder.setCancelable(true);
        builder.setNegativeButton("Stay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
    }

    /*//TODO ONPAUSE
    @Override
    protected void onPause() {
        super.onPause();
        fire_handler.removeCallbacksAndMessages(null);
        rain_handler.removeCallbacksAndMessages(null);
        rain_handler2.removeCallbacksAndMessages(null);
    }*/

}




