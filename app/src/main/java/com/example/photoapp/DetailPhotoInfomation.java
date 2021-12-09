package com.example.photoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class DetailPhotoInfomation extends AppCompatActivity {
    private Photo mPhoto;
    private TextView date;
    private TextView path;
    private TextView locate;
    private TextView albumName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_photo_infomation);

        Bundle bundle = getIntent().getExtras();
        if(bundle==null){
            return;
        }
        mPhoto = (Photo) bundle.getParcelable("photo");
        date = (TextView) findViewById(R.id.date);
        path = (TextView) findViewById(R.id.path);
        locate = (TextView) findViewById(R.id.locate);
        albumName = (TextView) findViewById(R.id.albumName);

        date.append(mPhoto.getStringDate());
        path.append(mPhoto.getRealPath());
        locate.append(mPhoto.getGeoLocation());
        albumName.append(mPhoto.getAlbumName());
    }
}