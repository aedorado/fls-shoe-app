package com.example.shaleya.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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
        filePath = intent.getStringExtra("filePath");
        setTitle("Results");
        new UploadFileToServer().execute();

    }


    //openurl is method for button ("View on amazon"), it redirect us to amazon website
    public void openUrl(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.amazon.co.uk"));
        startActivity(browserIntent);
    }


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
            new DownloadImageTask(im1)
                    .execute(p.get(0).url);
            Log.d("hello", p.get(0).url);
            new DownloadImageTask(im2)
                    .execute(p.get(1).url);
            new DownloadImageTask(im3)
                    .execute(p.get(2).url);
            new DownloadImageTask(im4)
                    .execute(p.get(3).url);
            new DownloadImageTask(im5)
                    .execute(p.get(4).url);
            im1.setBackgroundColor(getResources().getColor(R.color.white));
            im2.setBackgroundColor(getResources().getColor(R.color.white));
            im3.setBackgroundColor(getResources().getColor(R.color.white));
            im4.setBackgroundColor(getResources().getColor(R.color.white));
            im5.setBackgroundColor(getResources().getColor(R.color.white));
            super.onPostExecute(s);
        }

        //upload file is the main method for uplaoding shoes to the server
        //server address is : https://fls-shoe-app.herokuapp.com/test
        private String uploadFile() {
            Log.d("hello","uploading file to server");
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://fls-shoe-app.herokuapp.com/test");
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
                JSONObject json = new JSONObject(result);
                Iterator<String> keys = json.keys();
                while(keys.hasNext()){
                    String el = keys.next();
                    String val = json.get(el).toString();
                    Float value = Float.parseFloat(val);
                    p.add(new Pair(el, value));
                }
                Collections.sort(p, new Comparator<Pair>() {
                    @Override
                    public int compare(Pair o1, Pair o2) {
                        return o1.dist.compareTo(o2.dist);
                    }
                });
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return responseString;
        }
}}
