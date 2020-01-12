package com.teyenwu.myrun;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int  MY_PERMISSIONS_REQUEST_CAMERA = 0;
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";


    private static final int  REQUEST_IMAGE_CAPTURE = 3;
    private static final int  REQUEST_IMAGE_CROP = 4;
    private int selectedRadioButtonIndex = -1;

    private ImageView profileImageView;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private RadioGroup genderRadioGroup;
    private EditText classEditText;
    private EditText majorEditText;

    private Uri photoURI;
    String imageFilePath;
    private boolean isTakenFromCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button profileChangeButton = findViewById(R.id.profileChangeButton);
        profileChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfile();
            }
        });


        profileImageView = findViewById(R.id.profileImageView);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        classEditText = findViewById(R.id.classEditText);
        majorEditText = findViewById(R.id.majorEditText);

        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                selectedRadioButtonIndex = group.indexOfChild(radioButton);
            }
        });


        SharedPreferences local = this.getSharedPreferences("profile", 0);

        if(local.getBoolean("initialized", false)) {
            loadSnap();
            selectedRadioButtonIndex = local.getInt("gender", 0);
            if(selectedRadioButtonIndex != -1){
                ((RadioButton) genderRadioGroup.getChildAt(selectedRadioButtonIndex)).setChecked(true);
            }
            nameEditText.setText(local.getString("name", ""));
            emailEditText.setText(local.getString("email", ""));
            phoneEditText.setText(local.getString("phone", ""));
            classEditText.setText(local.getString("class", ""));
            majorEditText.setText(local.getString("major", ""));
        }

        if (savedInstanceState != null) {
            photoURI = savedInstanceState.getParcelable(URI_INSTANCE_STATE_KEY);
        }
    }

    private void launchCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            photoURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            takePictureIntent.putExtra("return-data", true);
            this.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            isTakenFromCamera = true;
        }
    }
    public void changeProfile(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA);
                //Create a file to store the image

//                grantUriPermission();
            }
        } else{
            launchCamera();
        }
    }

    public void save(View v) {
        SharedPreferences local = this.getSharedPreferences("profile", 0);
        SharedPreferences.Editor editor = local.edit();
        editor.putBoolean("initialized", true);
        editor.putString("name", nameEditText.getText().toString());

        editor.putString("email", emailEditText.getText().toString());
        editor.putString("phone", phoneEditText.getText().toString());
        editor.putInt("gender", selectedRadioButtonIndex);
        editor.putString("class", classEditText.getText().toString());
        editor.putString("major", majorEditText.getText().toString());
        editor.apply();
        saveSnap();
        finish();
    }

    public void cancel(View v) {
        // does something very interesting
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more_tab_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the image capture uri before the activity goes into background
        outState.putParcelable(URI_INSTANCE_STATE_KEY, photoURI);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
            Crop.of(photoURI, destination).asSquare().start(this);

        } else if (requestCode == Crop.REQUEST_CROP  && resultCode == RESULT_OK) {
            if (data != null) {
                Uri imageURI = Crop.getOutput(data);
                profileImageView.setImageBitmap(null);
                profileImageView.setImageURI(imageURI);
            }

            // Delete temporary image taken by camera after crop.
            if (isTakenFromCamera) {
                File f = new File(photoURI.getPath());
                if (f.exists())
                    f.delete();
                isTakenFromCamera = false;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                }else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)||shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            //Show an explanation to the user *asynchronously*
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage("This permission is important for the app.")
                                    .setTitle("Important permission required");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
                                    }

                                }
                            });
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
                        }else{
                            //Never ask again and handle your app without permission.
                        }
                    }
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    private void loadSnap() {


        // Load profile photo from internal storage
        try {
            FileInputStream fis = openFileInput(getString(R.string.profile_photo_file_name));
            Bitmap bmap = BitmapFactory.decodeStream(fis);
            profileImageView.setImageBitmap(bmap);
            fis.close();
        } catch (IOException e) {
            // Default profile photo if no photo saved before.
//            mImageView.setImageResource(R.drawable.default_profile);
        }
    }

    private void saveSnap() {

        // Commit all the changes into preference file
        // Save profile image into internal storage.
        profileImageView.buildDrawingCache();
        Bitmap bmap = profileImageView.getDrawingCache();
        try {
            FileOutputStream fos = openFileOutput(getString(R.string.profile_photo_file_name), MODE_PRIVATE);
            bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}

