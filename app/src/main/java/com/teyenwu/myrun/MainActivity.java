package com.teyenwu.myrun;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
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
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int  MY_PERMISSIONS_REQUEST_CAMERA = 0;
    private static final int  MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 1;

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
            String profileImageURI = local.getString("profileImageURI", "");
            if (!profileImageURI.isEmpty()) {
                photoURI = Uri.parse(profileImageURI);
                setImageViewWithUri(profileImageView, photoURI);
            }
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
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void launchCamera(){
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            Toast.makeText(MainActivity.this, "Cannot access picture", Toast.LENGTH_SHORT).show();
        }
        if (photoFile != null) {
//            this.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                photoURI = FileProvider.getUriForFile(this,"com.teyenwu.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                this.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
    }
    public void changeProfile(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
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
        if(photoURI != null) {
            editor.putString("profileImageURI", photoURI.toString());
        }
        editor.apply();
        finish();
    }

    public void cancel(View v) {
        // does something very interesting
        finish();
    }

    public void setImageViewWithUri(ImageView imageView, Uri uri){
        Bitmap imageBitmap = null;
        try {
            imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
            imageView.setImageBitmap(imageBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Crop.of(photoURI, photoURI).asSquare().start(this);

        } else if (requestCode == Crop.REQUEST_CROP  && resultCode == RESULT_OK) {
            if (data != null) {
                setImageViewWithUri(profileImageView, photoURI);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to use the camera", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

}

