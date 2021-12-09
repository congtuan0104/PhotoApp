package com.example.photoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class AlbumPhotoActivity extends AppCompatActivity {
    ArrayList<Photo> mAlbumPhotos = new ArrayList<>();
    RecyclerView listAlbumPhotoRecyclerView;
    PhotoRecyclerViewAdapter mPhotoAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_photo);

        Bundle bundle = getIntent().getExtras();
        if(bundle==null){
            return;
        }
        mAlbumPhotos =  bundle.getParcelableArrayList("list_photo");

        listAlbumPhotoRecyclerView = (RecyclerView) findViewById(R.id.photoRecyclerView);
        mPhotoAdapter=  new PhotoRecyclerViewAdapter(this,mAlbumPhotos);
        listAlbumPhotoRecyclerView.setAdapter(mPhotoAdapter);
        listAlbumPhotoRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
    }
}