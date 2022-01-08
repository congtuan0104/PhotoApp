package com.example.photoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlbumDiaLogRecyclerViewAdapter extends RecyclerView.Adapter<AlbumDiaLogRecyclerViewAdapter.MyViewHolder> {
    ArrayList<String> albumNames = new ArrayList<>();
    Context mContext;
    IClickListener listener;

    public AlbumDiaLogRecyclerViewAdapter(ArrayList<String> albumNames, Context mContext, IClickListener listener) {
        this.albumNames = albumNames;
        this.mContext = mContext;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlbumDiaLogRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_album_item, parent, false);
        return new AlbumDiaLogRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumDiaLogRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.albumName.setText(albumNames.get(position));
        holder.albumName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickItem(albumNames.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumNames.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView albumName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            albumName = (TextView)itemView.findViewById(R.id.albumName);
        }
    }
}
