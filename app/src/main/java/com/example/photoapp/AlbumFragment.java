package com.example.photoapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class AlbumFragment extends Fragment {
    ArrayList<Photo> mPhotos = new ArrayList<>();
    ArrayList<Album> mAlbums = new ArrayList<>();
    RecyclerView mAlbumRecyclerView;
    AlbumRecyclerViewAdapter mAlbumAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_album,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAlbumRecyclerView = (RecyclerView)view.findViewById(R.id.albumRecyclerView);
        LoadAlbumTask loadAlbumTask = new LoadAlbumTask();
        loadAlbumTask.execute();
        mAlbumAdapter = new AlbumRecyclerViewAdapter(getContext(),mAlbums);
        mAlbumRecyclerView.setAdapter(mAlbumAdapter);
        mAlbumRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadAlbumTask loadAlbumTask = new LoadAlbumTask();
                loadAlbumTask.execute();
            }
        });
    }

    // Từ danh sách các hình ảnh chia thành danh sách của dánh sách các ảnh theo ngày tháng
    public ArrayList<Album> getListOfAlbum(ArrayList<Photo> photos) {
        if (photos.size() == 0) {
            return new ArrayList<Album>();
        }
        String currentAlbum = photos.get(0).getAlbumName();
        ArrayList<Album> listAlbums = new ArrayList<>();
        ArrayList<Photo> tempPhotos = new ArrayList<>();
        for (int i = 0; i < photos.size(); i++) {
            if (photos.get(i).getAlbumName().equals(currentAlbum)) {
                tempPhotos.add(photos.get(i));
            } else {
                listAlbums.add(new Album(currentAlbum, (ArrayList<Photo>) tempPhotos.clone()));
                Log.e("TAG", currentAlbum );
                currentAlbum = photos.get(i).getAlbumName();
                tempPhotos.clear();
                tempPhotos.add(photos.get(i));
            }
            if (i == photos.size() - 1) {
                listAlbums.add(new Album(currentAlbum, (ArrayList<Photo>) tempPhotos.clone()));
            }
        }
        return listAlbums;
    }
    //  Lấy các hình ảnh từ bộ nhớ thông qua MediaStore
    private void initAllPhotos() {
        mPhotos = new ArrayList<>();
        mAlbums = new ArrayList<>();
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };
        String sortOrder = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " ASC";
        try (Cursor cursor = getContext().getContentResolver().query(
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
            int dataCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int albumCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            while (cursor.moveToNext()) {
                Uri photoUri = Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        cursor.getString(idCol));
                final double[] latLong;
                String strDateTime = null;
                photoUri = MediaStore.setRequireOriginal(photoUri);
                InputStream stream = getContext().getContentResolver().openInputStream(photoUri);

                Date dateTaken = new Date();

                if (stream != null) {
                    ExifInterface exifInterface = new ExifInterface(stream);
                    double[] returnedLatLong = exifInterface.getLatLong();
                    latLong = returnedLatLong != null ? returnedLatLong : new double[2];
                    strDateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                    if (strDateTime == null) {
                        File file = new File(cursor.getString(dataCol));
                        dateTaken = new Date(file.lastModified());
                    } else {
                        dateTaken = dateFormater(strDateTime);
                    }

                    stream.close();
                } else {
                    latLong = new double[2];
                    strDateTime = "2015:1:1 0:0:0";
                }
                double latitude = latLong[0];
                double longitude = latLong[1];
                String position = convertLatAndLongToGeo(latitude, longitude);
                String name = cursor.getString(nameCol);
                String realPath = cursor.getString(dataCol);
                String album = cursor.getString(albumCol);
                mPhotos.add(new Photo(name,realPath, photoUri, dateTaken, position,album));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPhotos.sort((date1, date2) -> date2.getAlbumName().compareTo(date1.getAlbumName()));
        mAlbums = getListOfAlbum(mPhotos);
    }
    // Đổi chuỗi của này tháng thành dạng yyyy:MM:dd HH:mm:ss
    public Date dateFormater(String strDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        Date date = formatter.parse(strDate);
        return date;
    }
    // Từ kinh độ và vĩ độ đổi thành địa chỉ địa lí
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


    // AsyncTask này dùng để load ảnh và không ảnh hưởng tới UI
    private class LoadAlbumTask extends AsyncTask<Void, Void, Void> {
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
            mAlbumAdapter.setData(mAlbums);
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        LoadAlbumTask loadAlbumTask = new LoadAlbumTask();
        loadAlbumTask.execute();
    }
}
