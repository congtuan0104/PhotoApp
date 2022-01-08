package com.example.photoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class SlideShowActivity extends AppCompatActivity {

    SliderView sliderView;
    ArrayList<Photo> mPhotos= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show);

        sliderView = (SliderView) findViewById(R.id.imageSlider);
        Bundle bundle = getIntent().getExtras();
        if(bundle==null){
            return;
        }
        mPhotos =  bundle.getParcelableArrayList("list_photo");
        SliderAdapter sliderAdapter = new SliderAdapter(mPhotos);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();
    }
}