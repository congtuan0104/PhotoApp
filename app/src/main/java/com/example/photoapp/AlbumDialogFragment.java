package com.example.photoapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class AlbumDialogFragment extends BottomSheetDialogFragment {
    private ArrayList<String> albumNames;
    private ArrayList<Photo> selectedPhotos;

    public AlbumDialogFragment(ArrayList<String> albumNames, ArrayList<Photo> selectedPhotos) {
        this.albumNames = albumNames;
        this.selectedPhotos = selectedPhotos;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view  = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_sheet,null);
        bottomSheetDialog.setContentView(view);

        TextView addAlbum  = (TextView)view.findViewById(R.id.addNewAlbum);
        addAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Tên Album");

                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                final String[] albumName = {""};
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        albumName[0] = input.getText().toString();
                        for(int i=0;i<selectedPhotos.size();i++){
                            saveImage(selectedPhotos.get(i),albumName[0]);
                        }
                        Toast.makeText(getContext(), "Add To "+albumName[0]+" Success", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }

        });

        RecyclerView rcvAlbumName = view.findViewById(R.id.albumDialogRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvAlbumName.setLayoutManager(linearLayoutManager);

        AlbumDiaLogRecyclerViewAdapter adapter = new AlbumDiaLogRecyclerViewAdapter(albumNames, getContext(), new IClickListener() {
            @Override
            public void onClickItem(String albumName) {
                for(int i=0;i<selectedPhotos.size();i++){
                    saveImage(selectedPhotos.get(i),albumName);
                }
                Toast.makeText(getContext(), "Add To "+albumName+" Success", Toast.LENGTH_SHORT).show();
                getDialog().cancel();
            }
        });

        rcvAlbumName.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        rcvAlbumName.addItemDecoration(itemDecoration);
        return bottomSheetDialog;
    }

    private void saveImage(Photo photo, String folderName){
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photo.getImgUri());
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fos;
        try{
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                ContentResolver resolver = getContext().getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME,photo.getImgName());
                contentValues.put(MediaStore.Images.Media.MIME_TYPE,"image/jpg");
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator+folderName);
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
                fos = (FileOutputStream) resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                Objects.requireNonNull(fos);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
