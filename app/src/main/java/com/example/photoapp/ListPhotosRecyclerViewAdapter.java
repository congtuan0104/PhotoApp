package com.example.photoapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListPhotosRecyclerViewAdapter extends RecyclerView.Adapter<ListPhotosRecyclerViewAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<ListPhotos> mListPhotos;

    public ListPhotosRecyclerViewAdapter(Context mContext, ArrayList<ListPhotos> mListPhotos) {
        this.mContext = mContext;
        this.mListPhotos = mListPhotos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listphoto_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String strDate = mListPhotos.get(position).getDate();
        ArrayList<Photo> photos = mListPhotos.get(position).getPhotos();
        PhotoRecyclerViewAdapter mAdapter = new PhotoRecyclerViewAdapter(mContext, photos);
        holder.dateTxt.setText(strDate);
        holder.photoRecyclerView.setAdapter(mAdapter);
        holder.photoRecyclerView.setLayoutManager(new GridLayoutManager(mContext,3));
    }

    @Override
    public int getItemCount() {
        return mListPhotos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView dateTxt;
        private RecyclerView photoRecyclerView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTxt = (TextView) itemView.findViewById(R.id.dateTxt);
            photoRecyclerView =(RecyclerView) itemView.findViewById(R.id.photoRecyclerView);
        }
    }
}
