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
import android.widget.Toast;

import com.example.shaleya.myapplication.DataBaseHelper.LoginDataBaseAdapter;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final EditText username = (EditText)findViewById(R.id.textView5);
        final EditText password = (EditText)findViewById(R.id.textView4);
        final EditText apassword = (EditText)findViewById(R.id.textView6);
        Button regbtn =  (Button)findViewById(R.id.button);
//        Log.d("hello", uname + " " + pwd + " " + apwd);
        LoginDataBaseAdapter loginDataBaseAdapter = new LoginDataBaseAdapter(Register.this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();
        final LoginDataBaseAdapter finalLoginDataBaseAdapter = loginDataBaseAdapter;
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = username.getText().toString();
                String pwd =   password.getText().toString();
                String apwd =  apassword.getText().toString();
                if(!pwd.equals(apwd)){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    builder.setMessage("Passwords do not match. Please try again...")
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
                else if(uname.equals("") || pwd.equals("")
                        || apwd.equals("")){

                    Toast.makeText(getApplicationContext(), "Field Vaccant",
                            Toast.LENGTH_LONG).show();
                    return ;
                }else if(uname.length() > 10){
                    Toast.makeText(getApplicationContext(), "username too long",
                            Toast.LENGTH_LONG).show();
                    return ;
                }
                else if(uname.equals("-1")){
                    Toast.makeText(getApplicationContext(), "not allowed",
                            Toast.LENGTH_LONG).show();
                    return ;
                }
                else {
                    String k = finalLoginDataBaseAdapter.getSinlgeEntry(uname);
                    if(k.equals("NOT EXIST")) {
                        finalLoginDataBaseAdapter.insertEntry(uname, pwd);
                        Toast.makeText(getApplicationContext(),
                                "Account Successfully Created ", Toast.LENGTH_LONG)
                                .show();
                        Intent i = new Intent(Register.this, Login.class);
                        startActivity(i);
                    }else{
                        final AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                        builder.setMessage("User already exists...")
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
            }
        });
    }
}
