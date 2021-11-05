package com.example.photoapp;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PhotoRecyclerViewAdapter extends RecyclerView.Adapter<PhotoRecyclerViewAdapter.MyViewHolder> {
    ArrayList<Photo> photos;
    Context context;

    public PhotoRecyclerViewAdapter(Context context, ArrayList<Photo> photos) {
        this.context = context;
        this.photos = photos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String name = photos.get(position).getImgName();
        String imgPath = photos.get(position).getImgPath();
        //holder.photoTextView.setText(name);
        Glide.with(context)
                .load(imgPath)
                .centerCrop()
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView photoTextView;
        private ImageView img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            photoTextView = (TextView) itemView.findViewById(R.id.photoTextView);
            img = (ImageView) itemView.findViewById(R.id.photo);
        }
    }
}
