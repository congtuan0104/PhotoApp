package com.example.photoapp;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
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

    private ArrayList<Video> mVideos;
    private Context mContext;

    public VideoRecyclerViewAdapter(Context mContext, ArrayList<Video> mVideos) {
        this.mContext = mContext;
        this.mVideos = mVideos;
    }
    public void setData(ArrayList<Video> mVideos){
        this.mVideos = mVideos;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public VideoRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new VideoRecyclerViewAdapter.MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String vidName = mVideos.get(position).getVidName();
        String vidPath = mVideos.get(position).getVidPath();
        String vidThumb = mVideos.get(position).getVidThumb();
        Glide.with(mContext)
                .load(vidThumb)
                .centerCrop()
                .into(holder.vidThumb);
        holder.playVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,PlayVideoActivity.class);
                intent.putExtra("video_path",vidPath);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView vidThumb;
        private ImageView playVideoBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            vidThumb = (ImageView) itemView.findViewById(R.id.imgVideo);
            playVideoBtn = (ImageView) itemView.findViewById(R.id.playVideoBtn);
        }
    }
}
