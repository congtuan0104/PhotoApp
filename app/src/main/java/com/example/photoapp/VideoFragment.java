package com.example.photoapp;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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

public class VideoFragment extends Fragment {
    ArrayList<Video> mVideos  = new ArrayList<>();
    RecyclerView mListVideoRecyclerView;
    VideoRecyclerViewAdapter mVideoAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video,container,false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListVideoRecyclerView = (RecyclerView)view.findViewById(R.id.videoRecyclerView);

        LoadVideoTask loadPhotoTask = new LoadVideoTask();
        loadPhotoTask.execute();

        mVideoAdapter = new VideoRecyclerViewAdapter(getContext(),mVideos);
        mListVideoRecyclerView.setAdapter(mVideoAdapter);
        mListVideoRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadVideoTask loadPhotoTask = new LoadVideoTask();
                loadPhotoTask.execute();
            }
        });
    }
    // Load danh sách Video từ MediaStore
    private void initAllVideos() {
        mVideos = new ArrayList<>();
        String vidPath =null;
        String vidThumb = null;
        String vidName = null;
        String[] projection = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Thumbnails.DATA
        };
        try (Cursor cursor = getContext().getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null
        )) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            int thumbColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

            while (cursor.moveToNext()) {
                vidName = cursor.getString(nameColumn);
                vidPath = cursor.getString(dataColumn);
                vidThumb = cursor.getString(thumbColumn);
                Video video = new Video(vidName, vidPath, vidThumb);

                mVideos.add(video);
            }
        }
    }
    // AsyncTask để load Video không ảnh hương UI
    private class LoadVideoTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            initAllVideos();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            mVideoAdapter.setData(mVideos);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

}
