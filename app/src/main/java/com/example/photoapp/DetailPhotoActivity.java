package com.example.photoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.widget.PopupMenu;

public class DetailPhotoActivity extends AppCompatActivity {
    private static final int DELETE_REQUEST_CODE = 12;
    private Photo mPhoto;
    private ImageView bigImg;
    private ImageView editPhotoBtn;
    private ImageView deletePhotoBtn;
    private ImageView favoritePhotoBtn;
    private ImageView sharePhotoBtn;

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
                // Mở màn hình Edit
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
                        //Xóa ảnh có hiển thi Dialog cho người dùng xác nhận
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
        favoritePhotoBtn = (ImageView) findViewById(R.id.favorite);
        favoritePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mPhoto.getImgUri());
                    saveImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        sharePhotoBtn = (ImageView) findViewById(R.id.share);
        sharePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG", mPhoto.getImgUri().toString() );
                shareImage();
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
                finish();
            } else {
                Toast.makeText(this, "Can't Delete", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void ShowDetailMore(){
        PopupMenu detailMenu = new PopupMenu(this, detailMoreBtn);
        detailMenu.getMenuInflater().inflate(R.menu.detail_more, detailMenu.getMenu());
        detailMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.detailDetail: {
                        Intent detaiInfolIntent = new Intent(DetailPhotoActivity.this,DetailPhotoInfomation.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable( "photo",mPhoto);
                        detaiInfolIntent.putExtras(bundle);
                        startActivity(detaiInfolIntent);
                        break;
                    }
                }
                return false;
            }
        });
        detailMenu.show();

    }

    private void saveImage(Bitmap bitmap){
        FileOutputStream fos;
        try{
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                ContentResolver resolver = getContentResolver();
                ContentValues  contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME,mPhoto.getImgName());
                contentValues.put(MediaStore.Images.Media.MIME_TYPE,"image/jpg");
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator+"Favorite");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
                fos = (FileOutputStream) resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                Objects.requireNonNull(fos);
                Toast.makeText(this, "Add To Favorite Success", Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    void shareImage(){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, mPhoto.getImgUri());
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, null));
    }

}