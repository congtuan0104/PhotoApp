package com.example.photoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyTeamInfoActivity extends AppCompatActivity {
    ArrayList<TeamMember> listMember;
    MemberAdapterListView memberAdapterListView;
    ListView listViewMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myteaminfo);

        listMember = new ArrayList<>();
        listMember.add(new TeamMember("Trịnh Quyền Đế", "19120192"));
        listMember.add(new TeamMember("Phan Công Tuấn", "19120418"));
        listMember.add(new TeamMember("Trần Minh Bảo", "19120457"));
        listMember.add(new TeamMember("Trần Thái Bảo", "19120458"));
        listMember.add(new TeamMember("Trần Vũ Việt Cường", "19120465"));

        memberAdapterListView = new MemberAdapterListView(listMember);

        listViewMember = (ListView) findViewById(R.id.list_member);
        listViewMember.setAdapter(memberAdapterListView);

        listViewMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TeamMember member = (TeamMember) memberAdapterListView.getItem(position);
                Toast.makeText(MyTeamInfoActivity.this, member.name, Toast.LENGTH_LONG).show();
            }
        });
    }
}