package com.example.photoapp;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<VideoRecyclerViewAdapter.MyViewHolder> {

    ArrayList<Video> videos;
    Context context;

    public VideoRecyclerViewAdapter(Context context, ArrayList<Video> videos) {
        this.context = context;
        this.videos = videos;
    }

    @NonNull
    @Override
    public VideoRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new VideoRecyclerViewAdapter.MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String name = videos.get(position).getVidName();
        String vidPath = videos.get(position).getVidPath();
//        Glide.with(context)
//                .load(vidPath)
//                .centerCrop()
//                .into(holder.vid);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView videoTextView;
        private VideoView vid;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            videoTextView = (TextView) itemView.findViewById(R.id.videoTextView);
            vid = (VideoView) itemView.findViewById(R.id.video);
        }
    }
}
