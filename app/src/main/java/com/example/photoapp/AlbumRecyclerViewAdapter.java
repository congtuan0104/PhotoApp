package com.example.photoapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlbumRecyclerViewAdapter extends RecyclerView.Adapter<AlbumRecyclerViewAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<Album> mListAlbum;
    public AlbumRecyclerViewAdapter(Context mContext, ArrayList<Album> mListAlbum) {
        this.mContext = mContext;
        this.mListAlbum = mListAlbum;
    }
    public void setData(ArrayList<Album> mListAlbum){
        this.mListAlbum = mListAlbum;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        return new AlbumRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String albumName = mListAlbum.get(position).getAlbumName();
        ArrayList<Photo> photos = mListAlbum.get(position).getPhotos();
        PhotoRecyclerViewAdapter mAdapter = new PhotoRecyclerViewAdapter(mContext, photos);
        holder.albumName.setText(albumName);
        holder.albumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,AlbumPhotoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("list_photo",mListAlbum.get(position).getPhotos());
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListAlbum.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView albumName;
        private ImageView albumBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            albumName = (TextView) itemView.findViewById(R.id.albumName);
            albumBtn =(ImageView) itemView.findViewById(R.id.album);
        }
    }
}
