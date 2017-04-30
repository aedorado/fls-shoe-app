package com.example.shaleya.myapplication;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shaleya.myapplication.DataBaseHelper.LoginDataBaseAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;


//results shoes the resulting similar shoes on linearlayout in decreasing similarity order

public class Results extends AppCompatActivity {
    public String filePath;
    private ProgressDialog mProgressDialog;
    ImageView im1,im2,im3,im4,im5;
    public String user = "";
    Button[] button = new Button[5];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("hello"," starting resultss ");
        setContentView(R.layout.activity_results);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressNumberFormat(null);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setProgressPercentFormat(null);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        Intent intent = getIntent();
        im1 = (ImageView)findViewById(R.id.shoe1pic);
        im2 = (ImageView)findViewById(R.id.shoe2pic);
        im3 = (ImageView)findViewById(R.id.shoe3pic);
        im4 = (ImageView)findViewById(R.id.shoe4pic);
        im5 = (ImageView)findViewById(R.id.shoe5pic);
        button[0] = (Button)findViewById(R.id.shoe1link);
        button[1] = (Button)findViewById(R.id.shoe2link);
        button[2] = (Button)findViewById(R.id.shoe3link);
        button[3] = (Button)findViewById(R.id.shoe4link);
        button[4] = (Button)findViewById(R.id.shoe5link);
        filePath = intent.getStringExtra("filePath");
        user = intent.getStringExtra("USER");
        setTitle("Results");
        new UploadFileToServer().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_results, menu);
        MenuItem menuItem = menu.findItem(R.id.action_username);
        menuItem.setTitle(user);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                LoginDataBaseAdapter loginDataBaseAdapter = new LoginDataBaseAdapter(Results.this);
                loginDataBaseAdapter = loginDataBaseAdapter.open();
                loginDataBaseAdapter.updateEntry("currentuser", "-1");
                Log.d("hello", "entry updated");
                Intent i = new Intent(Results.this, Login.class);
                startActivity(i);
                return true;
        }
        return false;
    }
    //openurl is method for button ("View on amazon"), it redirect us to amazon website

    //download image task is async task which downloads the image give the url of image
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }


        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    //this method upload the image to python flask server
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        ArrayList<Pair> p;
        @Override
        protected void onPreExecute() {
            p = new ArrayList<Pair>();
            mProgressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }


        //after uploading save the result and set the imageview with corresponding similar shoes
        @Override
        protected void onPostExecute(String s) {
            mProgressDialog.dismiss();
            if(p.size()==1){
                Log.d("hello", "no shoes found ");
                AlertDialog.Builder builder = new AlertDialog.Builder(Results.this);
                builder.setMessage("No shoe image found in the picture. Please try again...")
                        .setTitle("Alert");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("hello", "go back to main activity ");
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }else {
                Log.d("hello", "shoes found");
                new DownloadImageTask(im1)
                        .execute(p.get(0).imageurl);
                Log.d("hello", p.get(0).imageurl);
                new DownloadImageTask(im2)
                        .execute(p.get(1).imageurl);
                new DownloadImageTask(im3)
                        .execute(p.get(2).imageurl);
                new DownloadImageTask(im4)
                        .execute(p.get(3).imageurl);
                new DownloadImageTask(im5)
                        .execute(p.get(4).imageurl);
                im1.setBackgroundColor(getResources().getColor(R.color.white));
                im2.setBackgroundColor(getResources().getColor(R.color.white));
                im3.setBackgroundColor(getResources().getColor(R.color.white));
                im4.setBackgroundColor(getResources().getColor(R.color.white));
                im5.setBackgroundColor(getResources().getColor(R.color.white));
                for(int i=0; i<5; i++){
                    final int finalI = i;
                    button[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = p.get(finalI).shoplink;
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            startActivity(intent);
                        }
                    });
                }
            }
            super.onPostExecute(s);
        }

        //upload file is the main method for uplaoding shoes to the server
        //server address is : https://fls-shoe-app.herokuapp.com/test
        private String uploadFile() {
            Log.d("hello","uploading file to server");
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://fls-shoe-app.herokuapp.com/test");
            Log.d("hello", "everything is fine");
            try {
                MultipartEntity entity = new MultipartEntity();
                ExifInterface newIntef = new ExifInterface(filePath);
                newIntef.setAttribute(ExifInterface.TAG_ORIENTATION,String.valueOf(2));
                File file = new File(filePath);
                entity.addPart("file", new FileBody(file));
                httppost.setEntity(entity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                String result = EntityUtils.toString(r_entity);
                Log.d("hello", result);
                JSONObject json = new JSONObject(result);
                Iterator<String> keys = json.keys();
                while(keys.hasNext()){
                    String el = keys.next();
                    String val = json.get(el).toString();
                    Log.d("hello", el + " and " + val);
                    if(val.equals("NO SHOE FOUND IN IMAGE")){
                        p.add(new Pair("No shoe image found", "nothing ", Float.parseFloat("0.00")));
                        break;
                    }
                    Float value = Float.parseFloat(val);
                    p.add(new Pair(el.split(";;")[0], el.split(";;")[1], value));
                }
                Collections.sort(p, new Comparator<Pair>() {
                    @Override
                    public int compare(Pair o1, Pair o2) {
                        return o1.dist.compareTo(o2.dist);
                    }
                });
            } catch (ClientProtocolException e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Results.this);
                builder.setMessage("Something went wrong ...")
                        .setTitle("Alert");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("hello", "go back to main activity ");
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(Results.this);
                builder.setMessage("Something went wrong ...")
                        .setTitle("Alert");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("hello", "go back to main activity ");
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } catch (JSONException e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Results.this);
                builder.setMessage("Something went wrong ...")
                        .setTitle("Alert");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("hello", "go back to main activity ");
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                e.printStackTrace();
            }
            return responseString;
        }
}}
