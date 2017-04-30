package com.example.shaleya.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shaleya.myapplication.DataBaseHelper.LoginDataBaseAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import at.markushi.ui.CircleButton;

import static android.content.ContentValues.TAG;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;


//activity where camerapreview is shown to capture pictures
//also you can upload picture from gallery
public class MainActivity extends Activity {
    public Camera mCamera;
    private CameraPreview mPreview;
    public File pictureFile;
    public String user;
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

    //This method will setup the first screen of the main class
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("hello", " came into main activity ");
        Display display = getWindowManager().getDefaultDisplay();
        Intent intent = getIntent();
        user = intent.getStringExtra("USER");
        Log.d("hello", "user is :" + user);
        TextView username = (TextView)findViewById(R.id.username);
        username.setText("Hello " + user + "!" );
        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(90);
        mPreview = new CameraPreview(this, mCamera);//Camera preview is being requested here
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        CircleButton captureButton = (CircleButton) findViewById(R.id.circleButton); // this is the circular button at the bottom of the screen used to click picture from camera
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCamera.takePicture(null, null, mPicture);
                        CustomDialogClass cdd=new CustomDialogClass(MainActivity.this); //When user clicks the picture, show the dialog, refer CustomDialogClass in this file itself
                        cdd.show();
                    }
                }
        );
    }
    //callback method for capturing picture
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE); //Function to get the media output from the camera, defined below
            if (pictureFile == null){
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close(); // Saving the media file in a stream to use it further
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };


    //get captures picture real path, if not exist then save the picture according to timestamp
    public static File getOutputMediaFile(int type){
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
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
    }

    public void logout(View view) {
        LoginDataBaseAdapter loginDataBaseAdapter = new LoginDataBaseAdapter(MainActivity.this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();
        loginDataBaseAdapter.updateEntry("currentuser", "-1");
        Log.d("hello", "entry updated");
        Intent i = new Intent(MainActivity.this, Login.class);
        startActivity(i);
    }


    //path returned from previous method after selecting images from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 2) {
            Uri imageUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(byteArray);
                fos.close();
                Toast.makeText(this, "Image saved to some location", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
            Intent i = new Intent(MainActivity.this, Results.class);
            i.putExtra("filePath",pictureFile.getPath());
            i.putExtra("USER", user);
            Log.d("hello", pictureFile.getPath());
            if(isNetworkAvailable()) {
                startActivityForResult(i,1);
            }else{
                Toast.makeText(MainActivity.this, "Internet is not available...", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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


        // All the setup of the screen always start from Oncreate.
        //Since this is a private class to display the custom dialog, this method would set up that part of the screen
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE); //Theme requested for the screen
            setContentView(R.layout.custom_diaog);
            yes = (Button) findViewById(R.id.btn_yes); // These buttons are defined in the corresponding XML res/layout/custom_dialog
            no = (Button) findViewById(R.id.btn_no);
            yes.setOnClickListener(this); // Setting up On click listeners
            no.setOnClickListener(this);

        }


        //dialog click methods, if yes then go to next activity (results.java) else remain on current activity
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:
                    Intent i = new Intent(MainActivity.this, Results.class);
                    i.putExtra("filePath",pictureFile.getPath()); // Putting the file path in the intent as extra data, this will be used where the intent is targeted i.e. result.class
                    Log.d("filePath", pictureFile.getPath());
                    if(isNetworkAvailable()) {
                        startActivity(i);
                    }else{
                        Toast.makeText(MainActivity.this, "Internet is not available...", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btn_no:
                    mCamera.startPreview(); // If the user clicks No, dismiss the dialog and start camera again.
                    dismiss();
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }
}
