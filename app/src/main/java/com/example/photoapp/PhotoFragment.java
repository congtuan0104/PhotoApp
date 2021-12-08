package com.example.photoapp;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PhotoFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo,container,false);
    }

    private static final int CAMERA_REQUEST =12;
    private int STORAGE_PERMISSION_CODE = 1;

    ArrayList<Photo> mPhotos = new ArrayList<>();
    ArrayList<ListPhotos> mListPhotos = new ArrayList<>();
    RecyclerView mListPhotosRecyclerView;
    ListPhotosRecyclerViewAdapter mListPhotosAdapter;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestStoragePermission();

        mListPhotosRecyclerView = (RecyclerView) view.findViewById(R.id.listphotosRecyclerView);
        mListPhotosAdapter = new ListPhotosRecyclerViewAdapter(getContext(), mListPhotos);
        mListPhotosRecyclerView.setAdapter(mListPhotosAdapter);
        mListPhotosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadPhotoTask loadPhotoTask = new LoadPhotoTask();
                loadPhotoTask.execute();
            }
        });


    }

    private void requestStoragePermission() {
        if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && getContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && getContext().checkSelfPermission(Manifest.permission.ACCESS_MEDIA_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LoadPhotoTask loadPhotoTask = new LoadPhotoTask();
            loadPhotoTask.execute();
        } else {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_MEDIA_LOCATION};
            requestPermissions(permissions, STORAGE_PERMISSION_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                LoadPhotoTask loadPhotoTask = new LoadPhotoTask();
                loadPhotoTask.execute();
            } else {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    String currentCameraPhotoPath;
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.putExtra("PhotoPath",currentCameraPhotoPath);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    public ArrayList<ListPhotos> getListOfListPhoto(ArrayList<Photo> photos) {
        if (photos.size() == 0) {
            return new ArrayList<ListPhotos>();
        }
        String currentDate = photos.get(0).getStringDate();
        ArrayList<ListPhotos> listPhotos = new ArrayList<>();
        ArrayList<Photo> tempPhotos = new ArrayList<>();
        for (int i = 0; i < photos.size(); i++) {
            if (photos.get(i).getStringDate().equals(currentDate)) {
                tempPhotos.add(photos.get(i));
            } else {
                listPhotos.add(new ListPhotos(currentDate, (ArrayList<Photo>) tempPhotos.clone()));
                currentDate = photos.get(i).getStringDate();
                tempPhotos.clear();
                tempPhotos.add(photos.get(i));
            }
            if (i == photos.size() - 1) {
                listPhotos.add(new ListPhotos(currentDate, (ArrayList<Photo>) tempPhotos.clone()));
            }
        }
        return listPhotos;
    }

    private void initAllPhotos() {
        mPhotos = new ArrayList<>();
        mListPhotos = new ArrayList<>();
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.RELATIVE_PATH,
                MediaStore.Images.Media.DATA,
        };
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " ASC";
        try (Cursor cursor = getContext().getApplicationContext().getContentResolver().query(
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
                InputStream stream = getContext().getContentResolver().openInputStream(photoUri);

                Date dateTaken = new Date();

                if (stream != null) {
                    ExifInterface exifInterface = new ExifInterface(stream);
                    double[] returnedLatLong = exifInterface.getLatLong();

                    // If lat/long is null, fall back to the coordinates (0, 0).
                    latLong = returnedLatLong != null ? returnedLatLong : new double[2];

                    strDateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                    if (strDateTime == null) {
                        File file = new File(cursor.getString(dataCol));
                        dateTaken = new Date(file.lastModified());
                    } else {
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
                String position = convertLatAndLongToGeo(latitude, longitude);
                String name = cursor.getString(nameCol);
                String realPath = cursor.getString(dataCol);
                mPhotos.add(new Photo(name,realPath, photoUri, dateTaken, position));

            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPhotos.sort((date1, date2) -> date2.getDate().compareTo(date1.getDate()));
        mListPhotos = getListOfListPhoto(mPhotos);
    }

    public Date dateFormater(String strDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        Date date = formatter.parse(strDate);
        return date;
    }

    public String convertLatAndLongToGeo(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        String city = "";
        String states = "";
        String location = "";
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() >= 1) {
                states = addresses.get(0).getAdminArea();
                city = addresses.get(0).getLocality();
                location = city + ", " + states;
            }
            //address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return location;
    }


    private class LoadPhotoTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            initAllPhotos();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            mListPhotosAdapter.setData(mListPhotos);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadPhotoTask loadPhotoTask = new LoadPhotoTask();
        loadPhotoTask.execute();
    }
}
