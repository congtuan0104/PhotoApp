package com.example.photoapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.widget.PopupMenu;

public class DetailPhotoActivity extends AppCompatActivity {
    private static final int DELETE_REQUEST_CODE = 12;
    private Photo mPhoto;
    private ImageView bigImg;
    private ImageView editPhotoBtn;
    private ImageView deletePhotoBtn;

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
        Glide.with(this)
                .load(mPhoto.getImgUri())
                .into(bigImg);
        editPhotoBtn = (ImageView)findViewById(R.id.editPhotoBtn);

        editPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dsPhotoEditorIntent = new Intent(DetailPhotoActivity.this, DsPhotoEditorActivity.class);
                dsPhotoEditorIntent.setData(mPhoto.getImgUri());
                dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY,"Photo Direction");
                startActivityForResult(dsPhotoEditorIntent,200);
            }
        });
        deletePhotoBtn =(ImageView) findViewById(R.id.deletePhotoBtn);
        deletePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File photoFile = new File(mPhoto.getRealPath());
                if (photoFile.exists()) {
                    if (photoFile.delete()) {
                        Toast.makeText(DetailPhotoActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        ContentResolver contentResolver = DetailPhotoActivity.this.getContentResolver();
                        ArrayList<Uri> uriList = new ArrayList<>();
                        uriList.add(mPhoto.getImgUri());

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                            PendingIntent editPendingIntent = MediaStore.createDeleteRequest(contentResolver, uriList);
                            try {
                                startIntentSenderForResult(editPendingIntent.getIntentSender(), DELETE_REQUEST_CODE, null, 0, 0, 0);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                }
            }
        });
        detailMoreBtn = findViewById(R.id.detailMore);
        detailMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDetailMore();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode ==RESULT_OK){
            switch (requestCode){
                case 200:
                    Uri outputUri = data.getData();
                    bigImg.setImageURI(outputUri);
                    break;
            }
        }
        if (requestCode == DELETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                finish();;
            } else {
                Toast.makeText(this, "Can't Delete", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void ShowDetailMore(){
        PopupMenu detailMenu = new PopupMenu(this, detailMoreBtn);
        detailMenu.getMenuInflater().inflate(R.menu.detail_more, detailMenu.getMenu());
        detailMenu.show();
    }
}