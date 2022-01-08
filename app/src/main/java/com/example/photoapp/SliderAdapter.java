package com.example.photoapp;

import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder> {

    ArrayList<Photo> photos = new ArrayList<>();

    public SliderAdapter(ArrayList<Photo> photos){

        this.photos = photos;

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slide_item,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {

        viewHolder.imageView.setImageURI(photos.get(position).getImgUri());
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    public class Holder extends  SliderViewAdapter.ViewHolder{

        ImageView imageView;

        public Holder(View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);

        }
    }

}