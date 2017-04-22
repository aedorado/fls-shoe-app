package com.example.naveen.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import at.markushi.ui.CircleButton;

import static android.content.ContentValues.TAG;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;


//activity where camerapreview is shown to capture pictures
//also you can upload picture from gallery
public class MainActivity extends Activity {
    public Camera mCamera;
    private CameraPreview mPreview;
    public File pictureFile;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    //get real path of image from uri
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            Log.d("hello", "cursor is : " + cursor);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            Log.d("hello", " column_index is :" + column_index);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    //check whether network is available or not
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Display display = getWindowManager().getDefaultDisplay();
        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(90);
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        CircleButton captureButton = (CircleButton) findViewById(R.id.circleButton);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCamera.takePicture(null, null, mPicture);
                        CustomDialogClass cdd=new CustomDialogClass(MainActivity.this);
                        cdd.show();
                    }
                }
        );
    }
    //callback method for capturing picture
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };


    //get captures picture real path, if not exist then save the picture according to timestamp
    private static File getOutputMediaFile(int type){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
        return mediaFile;
    }


    //if camera exists the create and return camera instance
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        }
        catch (Exception e){
        }
        return c;
    }

    //method for picking images from gallery
    public void pickGallery(View view) {
        if (Build.VERSION.SDK_INT <19){
            Intent intent = new Intent();
            intent.setType("image/jpeg");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture "),1);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/jpeg");
            startActivityForResult(intent, 2);
        }
    }


    //path returned from previous method after selecting images from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d("hello", " requestcode :" + String.valueOf(requestCode));
    Log.d("hello", " requestcode :" + String.valueOf(resultCode));
        if(requestCode == 1){
            if(resultCode == -1){
                Uri result = data.getData();
                Log.d("hello", "get data hahaha");
                Log.d("hello", result.toString());
                String realPath = getRealPathFromURI(this, result);
                Log.d("hello", realPath + " hahaha ");
                Intent i = new Intent(MainActivity.this, Results.class);
                i.putExtra("filePath",realPath);
                Log.d("hello", realPath);
                if(isNetworkAvailable()) {
                    startActivity(i);
                }else{
                    Toast.makeText(this, "Internet is not available...", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if(requestCode == 2){
            Uri result = data.getData();
            Log.d("hello", result.toString());
        }
    }


    //dialog of confirmation
    public class CustomDialogClass extends Dialog implements
            android.view.View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button yes, no;

        public CustomDialogClass(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_diaog);
            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);

        }


        //dialog click methods, if yes then go to next activity (results.java) else remain on current activity
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:
                    Intent i = new Intent(MainActivity.this, Results.class);
                    i.putExtra("filePath",pictureFile.getPath());
                    Log.d("hello", pictureFile.getPath());
                    if(isNetworkAvailable()) {
                        startActivity(i);
                    }else{
                        Toast.makeText(MainActivity.this, "Internet is not available...", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btn_no:
                    mCamera.startPreview();
                    dismiss();
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }
}
