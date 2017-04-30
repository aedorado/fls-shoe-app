package com.example.shaleya.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.shaleya.myapplication.DataBaseHelper.LoginDataBaseAdapter;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText username = (EditText)findViewById(R.id.textView2);
        final EditText password = (EditText)findViewById(R.id.textView);
        final Button loginbtn =  (Button)findViewById(R.id.button2);
        Button regbtn =  (Button)findViewById(R.id.button3);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = username.getText().toString();
                String pwd = password.getText().toString();
                LoginDataBaseAdapter loginDataBaseAdapter = new LoginDataBaseAdapter(Login.this);
                loginDataBaseAdapter = loginDataBaseAdapter.open();
                String k = loginDataBaseAdapter.getSinlgeEntry(uname);
                if(pwd.equals(k)){
                    String currentuser = loginDataBaseAdapter.getSinlgeEntry("currentuser");
                    if(currentuser.equals("NOT EXIST")){
                        loginDataBaseAdapter.insertEntry("currentuser", uname);
                        Log.d("hello", " inserted entry ");
                    }else{
                        loginDataBaseAdapter.updateEntry("currentuser", uname);
                        Log.d("hello", " updated entry ");
                    }
                    Intent i = new Intent(Login.this, MainActivity.class);
                    i.putExtra("USER", uname);
                    startActivity(i);
                }else{
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                    builder.setMessage("Either password or Username is wrong")
                            .setTitle("Alert");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
            }
        });
    }
}
