package com.example.photoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
    private int STORAGE_PERMISSION_CODE = 1;
    ArrayList<Photo> mPhotos = new ArrayList<>();
    ArrayList<ListPhotos> mListPhotos =  new ArrayList<>();
    RecyclerView mListPhotosRecyclerView;
    SearchView searchView;
    ListPhotosRecyclerViewAdapter mListPhotosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestStoragePermission();

    }
    public ArrayList<ListPhotos> getListOfListPhoto(ArrayList<Photo> photos){
        if(photos.size() == 0){
            return new ArrayList<ListPhotos>();
        }
        String currentDate = photos.get(0).getStringDate();
        ArrayList<ListPhotos> listPhotos =  new ArrayList<>();
        ArrayList<Photo> tempPhotos = new ArrayList<>();
        for(int i =0 ;i<photos.size();i++){
            if(photos.get(i).getStringDate().equals(currentDate) ){
                tempPhotos.add(photos.get(i));
            }
            else{
                listPhotos.add(new ListPhotos(currentDate, (ArrayList<Photo>) tempPhotos.clone()));
                currentDate = photos.get(i).getStringDate();
                tempPhotos.clear();
                tempPhotos.add(photos.get(i));
            }
            if(i == photos.size()-1){
                listPhotos.add(new ListPhotos(currentDate, (ArrayList<Photo>) tempPhotos.clone()));
            }
        }
        return listPhotos;
    }
    private void initAllPhotos() {
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.RELATIVE_PATH,
                MediaStore.Images.Media.DATA,

        };
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " ASC";
        try (Cursor cursor = getApplicationContext().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null
        )) {
            // Cache column indices.
            int idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int titleCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
            int pathCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH);
            int dataCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            while (cursor.moveToNext()) {
                Uri photoUri = Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        cursor.getString(idCol));

                final double[] latLong;
                String strDateTime = null;
                // Get location data using the Exifinterface library.
                // Exception occurs if ACCESS_MEDIA_LOCATION permission isn't granted.
                photoUri = MediaStore.setRequireOriginal(photoUri);
                InputStream stream = getContentResolver().openInputStream(photoUri);

                Date dateTaken = new Date();

                if (stream != null) {
                    ExifInterface exifInterface = new ExifInterface(stream);
                    double[] returnedLatLong = exifInterface.getLatLong();

                    // If lat/long is null, fall back to the coordinates (0, 0).
                    latLong = returnedLatLong != null ? returnedLatLong : new double[2];

                    strDateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                    if(strDateTime == null){
                        File file = new File(cursor.getString(dataCol));
                        dateTaken = new Date(file.lastModified());
                    }
                    else{
                        dateTaken = dateFormater(strDateTime);
                    }

                    // Don't reuse the stream associated with
                    // the instance of "ExifInterface".
                    stream.close();
                } else {
                    // Failed to load the stream, so return the coordinates (0, 0).
                    latLong = new double[2];
                    strDateTime = "2015:1:1 0:0:0";
                }

                double latitude = latLong[0];
                double longitude = latLong[1];
                String position = convertLatAndLongToGeo(latitude,longitude);
                String name = cursor.getString(nameCol);

                mPhotos.add(new Photo(name, photoUri, dateTaken, position));

            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPhotos.sort((date1,date2)->date2.getDate().compareTo(date1.getDate()));
        mListPhotos = getListOfListPhoto(mPhotos);

    }

    private void requestStoragePermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_MEDIA_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            loadPhoto();
        } else {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_MEDIA_LOCATION};
            requestPermissions(permissions, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                loadPhoto();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
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

        }
        ;
        return super.onOptionsItemSelected(item);
    }

    public Date dateFormater(String strDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        Date date = formatter.parse(strDate);
        return date;
    }

    public String convertLatAndLongToGeo(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        String city = "";
        String states = "";
        String location = "";
        try {
            addresses = geocoder.getFromLocation(latitude,longitude,1);
            if(addresses.size()>=1) {
                states = addresses.get(0).getAdminArea();
                city = addresses.get(0).getLocality();
                location = city +", "+ states;
            }
            //address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  location;
    }
    public void loadPhoto(){
        initAllPhotos();
        mListPhotosRecyclerView = (RecyclerView) findViewById(R.id.listphotosRecyclerView);
        mListPhotosAdapter = new ListPhotosRecyclerViewAdapter(this, mListPhotos);
        mListPhotosRecyclerView.setAdapter(mListPhotosAdapter);
        mListPhotosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}