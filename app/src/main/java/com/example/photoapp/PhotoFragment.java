package com.example.photoapp;

import android.Manifest;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ActionMode;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PhotoFragment extends Fragment implements PhotoRecyclerViewAdapter.OnImageClickListener {
    private static final int DELETE_REQUEST_CODE = 13;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo,container,false);
    }

    private static final int CAMERA_REQUEST =12;
    private int STORAGE_PERMISSION_CODE = 1;

    public static ArrayList<Photo> mPhotos = new ArrayList<>();
    public static ArrayList<Photo> selectedPhotos = new ArrayList<>();
    ArrayList<ListPhotos> mListPhotos = new ArrayList<>();
    ArrayList<String> albumNames  = new ArrayList<>();

    RecyclerView mListPhotosRecyclerView;
    ListPhotosRecyclerViewAdapter mListPhotosAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    public static boolean multiSelectMode =false;
    private ActionMode actionMode;

    ActionMode.Callback actionModeCallBack = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelectMode = true;
            getActivity().getMenuInflater().inflate(R.menu.multi_select_menu,menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            if(item.getItemId()==R.id.delete){
                getSelectedImage();
                deleteMultiImage(selectedPhotos);

            }
            if(item.getItemId()==R.id.album){
                getSelectedImage();
                AlbumDialogFragment albumDialogFragment = new AlbumDialogFragment(albumNames,selectedPhotos);
                albumDialogFragment.show(getActivity().getSupportFragmentManager(),albumDialogFragment.getTag());
            }
            actionMode= null;
            multiSelectMode=false;
            LoadPhotoTask loadPhotoTask = new LoadPhotoTask();
            loadPhotoTask.execute();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelectMode = false;
            mListPhotosAdapter.notifyDataSetChanged();
            for(int i=0;i<mPhotos.size();i++){
                mPhotos.get(i).isSelected = false;
            }
            selectedPhotos.clear();
            actionMode = null;
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestStoragePermission();

        mListPhotosRecyclerView = (RecyclerView) view.findViewById(R.id.listphotosRecyclerView);
        mListPhotosAdapter = new ListPhotosRecyclerViewAdapter(getContext(), mListPhotos,this);
        mListPhotosRecyclerView.setAdapter(mListPhotosAdapter);
        mListPhotosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        // Kéo xuống để Reload lại danh sách ảnh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadPhotoTask loadPhotoTask = new LoadPhotoTask();
                loadPhotoTask.execute();
            }
        });


    }
    // Xin quyền cho ứng dụng
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

    // Kiểm tra kết quả sau khi xin quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                // Thành công
                LoadPhotoTask loadPhotoTask = new LoadPhotoTask();
                loadPhotoTask.execute();
            } else {
                // Thất bại
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Từ danh sách các hình ảnh chia thành danh sách của dánh sách các ảnh theo ngày tháng
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
    //  Lấy các hình ảnh từ bộ nhớ thông qua MediaStore
    private void initAllPhotos() {
        mPhotos = new ArrayList<>();
        mListPhotos = new ArrayList<>();
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " ASC";
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
                Photo photo = new Photo(name,realPath, photoUri, dateTaken, position,album);
                for(int i=0;i<mPhotos.size();i++){
                    if(mPhotos.get(i).getImgName().equals(name)){
                        mPhotos.remove(i);
                    }
                }
                for(int i=0;i<albumNames.size();i++){
                    if(albumNames.get(i).equals(album)){
                        albumNames.remove(i);
                    }
                }
                albumNames.add(album);
                mPhotos.add(photo);
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
//    public ArrayList<String> getListOfAlbumName(ArrayList<Photo> photos) {
//        if (photos.size() == 0) {
//            return new ArrayList<String>();
//        }
//        ArrayList<String> albumNames = new ArrayList<>();
//        for (int i = 0; i < photos.size(); i++) {
//            String albumName = photos.get(i).getAlbumName();
//            if(!albumNames.contains(albumName)){
//                albumNames.add(albumName);
//            }
//        }
//        return albumNames;
//    }
    // AsyncTask này dùng để load ảnh và không ảnh hưởng tới UI
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
    @Override
    public void onImageClick(int position) {
    }

    @Override
    public void onImageLongClick(int position) {
        if(actionMode==null){
            actionMode = getActivity().startActionMode(actionModeCallBack);
        }

    }
    void getSelectedImage(){
        for(int i=0;i<mPhotos.size();i++){
            if(mPhotos.get(i).isSelected){
                selectedPhotos.add(mPhotos.get(i));
            }
        }
    }
    public void deleteMultiImage(ArrayList<Photo> photos){
        ContentResolver contentResolver = getContext().getContentResolver();
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
                getActivity().startIntentSenderForResult(editPendingIntent.getIntentSender(), DELETE_REQUEST_CODE, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
        else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            try {
                for(int i=0;i<uriList.size();i++){
                    getContext().getContentResolver().delete(uriList.get(i),null,null);
                }
            }
            catch (SecurityException e){
                RecoverableSecurityException recoverableSecurityException = (RecoverableSecurityException) e;
                PendingIntent editPendingIntent = recoverableSecurityException.getUserAction().getActionIntent();
                try {
                    getActivity().startIntentSenderForResult(editPendingIntent.getIntentSender(), DELETE_REQUEST_CODE, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e1) {
                    e.printStackTrace();
                }
            }
        }
    }
}
