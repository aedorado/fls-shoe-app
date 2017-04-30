package com.example.shaleya.myapplication;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.shaleya.myapplication.DataBaseHelper.LoginDataBaseAdapter;

//This is the first screen which is shown when we click on the application.. This stays for 3000 ms
//splash screen which redicrects to the mainacitivty after three seconds
public class Splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen); //This is an XML file in res directory, this shows where the laout for the screen is present.. Namely res/layout/splashscreen.xml
        int SPLASH_TIME_OUT = 3000;
        Intent i;// You can modify the timeout here
        setTitle("Find Shoes"); //Modify the title here
        LoginDataBaseAdapter loginDataBaseAdapter = new LoginDataBaseAdapter(Splashscreen.this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();
        String currentuser = loginDataBaseAdapter.getSinlgeEntry("currentuser");
        String user = " ";
        if(currentuser.equals("NOT EXIST")||currentuser.equals("-1")){
            i = new Intent(Splashscreen.this, Login.class);
        }else {
            i = new Intent(Splashscreen.this, MainActivity.class);
            user = currentuser;
            i.putExtra("USER", user);
        }
        final Intent intent = i;
        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
