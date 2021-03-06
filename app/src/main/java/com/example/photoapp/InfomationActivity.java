package com.example.photoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import csu.matos.fragment.InfoFragment;

public class InfomationActivity extends AppCompatActivity {
    FragmentTransaction ft;
    InfoFragment infoFragment;
    InfoFragment fragmentInfo;
    FrameLayout fragment_layout;

    ImageButton btnInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infomation_activity);

        ft = getSupportFragmentManager().beginTransaction();

        infoFragment = InfoFragment.newInstance(R.string.version);
        ft.replace(R.id.first_fragment, infoFragment);

        fragmentInfo = InfoFragment.newInstance(R.string.license_terms);
        ft.replace(R.id.second_fragment, fragmentInfo);

        fragmentInfo = InfoFragment.newInstance(R.string.policy);
        ft.replace(R.id.third_fragment, fragmentInfo);

        fragmentInfo = InfoFragment.newInstance(R.string.team_info);
        ft.replace(R.id.fourth_fragment, fragmentInfo);

        fragmentInfo = InfoFragment.newInstance(R.string.feedback);
        ft.replace(R.id.fifth_fragment, fragmentInfo);

        ft.commit();

        fragment_layout = (FrameLayout) findViewById(R.id.fourth_fragment);
        fragment_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyTeamInfoActivity.class);
                startActivity(intent);
            }
        });
    }
}