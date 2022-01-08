package com.example.photoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.PrecomputedText;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST =11;
    private static final int DELETE_REQUEST_CODE = 13;
    private int STORAGE_PERMISSION_CODE = 1;
    private TabLayout mTablayout;
    private ViewPager2 mViewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    FloatingActionButton fabCamera;

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("my_pin_pref", MODE_PRIVATE);
        SharedPreferences security = getSharedPreferences("com.example.app", MODE_PRIVATE);
        boolean isSecurity = security.getBoolean("securityKey",true);
        if(prefs.contains("pin") && isSecurity==true){
            Intent passIntent = new Intent(getApplicationContext(), PassAuthentication.class);
            startActivity(passIntent);
        }

        mTablayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager2) findViewById(R.id.view_pager);
        myViewPagerAdapter = new MyViewPagerAdapter(this);
        mViewPager.setAdapter(myViewPagerAdapter);

        new TabLayoutMediator(mTablayout, mViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0: {
                        tab.setText("Photo");
                        break;
                    }
                    case 1: {
                        tab.setText("Video");
                        break;
                    }
                    case 2: {
                        tab.setText("Album");
                        break;
                    }
                }
            }
        }).attach();
        fabCamera = (FloatingActionButton)findViewById(R.id.fabCamera);
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    capturePhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    String currentCameraPhotoPath;
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.e("TAG", storageDir.toString() );
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentCameraPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void capturePhoto() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.putExtra("PhotoPath",currentCameraPhotoPath);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            try {
                MediaStore.Images.Media.insertImage(this.getContentResolver(), currentCameraPhotoPath,
                        "Title", null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == DELETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.Q) {
                    deleteMultiImage(PhotoFragment.selectedPhotos);
                }
            } else {
                Toast.makeText(this, "Can't Delete", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem = menu.findItem(R.id.search);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.find_hint));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings: {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.about: {
                Intent intent2 = new Intent(getApplicationContext(), InfomationActivity.class);
                startActivity(intent2);
                break;
            }
            case R.id.slideshow:{
                Intent intent = new Intent(this,SlideShowActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("list_photo",PhotoFragment.mPhotos);
                intent.putExtras(bundle);
                startActivity(intent);
            }

        }
        ;
        return super.onOptionsItemSelected(item);
    }


    public void deleteMultiImage(ArrayList<Photo> photos){
        ContentResolver contentResolver = getContentResolver();
        ArrayList<Uri> uriList = new ArrayList<Uri>();
        for(int i=0;i<photos.size();i++){
            File photoFile = new File(photos.get(i).getRealPath());
            if (photoFile.exists()) {
                //Xóa ảnh có hiển thi Dialog cho người dùng xác nhận
                uriList.add(photos.get(i).getImgUri());
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            PendingIntent editPendingIntent = MediaStore.createDeleteRequest(contentResolver, uriList);
            try {
                startIntentSenderForResult(editPendingIntent.getIntentSender(), DELETE_REQUEST_CODE, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
        else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            try {
                for(int i=0;i<uriList.size();i++){
                    getContentResolver().delete(uriList.get(i),null,null);
                }
            }
            catch (SecurityException e){
                RecoverableSecurityException recoverableSecurityException = (RecoverableSecurityException) e;
                PendingIntent editPendingIntent = recoverableSecurityException.getUserAction().getActionIntent();
                try {
                    startIntentSenderForResult(editPendingIntent.getIntentSender(), DELETE_REQUEST_CODE, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e1) {
                    e.printStackTrace();
                }
            }
        }
    }
}