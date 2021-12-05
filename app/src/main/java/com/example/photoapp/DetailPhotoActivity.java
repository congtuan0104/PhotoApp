package com.example.photoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

public class DetailPhotoActivity extends AppCompatActivity {
    private Photo mPhoto;
    private ImageView bigImg;

    ImageView detailMoreBtn;
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

        detailMoreBtn = findViewById(R.id.detailMore);
        detailMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDetailMore();
            }
        });
    }

    private void ShowDetailMore(){
        PopupMenu detailMenu = new PopupMenu(this, detailMoreBtn);
        detailMenu.getMenuInflater().inflate(R.menu.detail_more, detailMenu.getMenu());
        detailMenu.show();
    }
}