package com.example.photoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class DetailPhotoActivity extends AppCompatActivity {
    private Photo mPhoto;
    private ImageView bigImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_photo);

        Bundle bundle = getIntent().getExtras();
        if(bundle==null){
            return;
        }
        mPhoto = (Photo) bundle.getParcelable("object_photo");

        bigImg = (ImageView)findViewById(R.id.bigImg);
        bigImg.setImageURI(mPhoto.getImgUri());

    }
}