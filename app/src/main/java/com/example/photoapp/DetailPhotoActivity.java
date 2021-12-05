package com.example.photoapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;

import java.io.File;

public class DetailPhotoActivity extends AppCompatActivity {
    private Photo mPhoto;
    private ImageView bigImg;
    private ImageView editPhotoBtn;
    private ImageView deletePhotoBtn;
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
                    Log.e("TAG", mPhoto.getRealPath());
                    if (photoFile.delete()) {
                        Toast.makeText(DetailPhotoActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        Toast.makeText(DetailPhotoActivity.this, "Can't Deleted", Toast.LENGTH_SHORT).show();
                    }
                }

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
    }
}