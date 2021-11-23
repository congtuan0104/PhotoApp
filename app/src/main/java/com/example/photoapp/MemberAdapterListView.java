package com.example.photoapp;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MemberAdapterListView extends BaseAdapter {
    ArrayList<TeamMember> listMember = new ArrayList<>();

    MemberAdapterListView(ArrayList<TeamMember> listMember) {
        this.listMember = listMember;
    }

    @Override
    public int getCount() {
        return listMember.size();
    }

    @Override
    public Object getItem(int position) {
        return listMember.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listMember.get(position).studentID.hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewMember;
        if (convertView == null) {
            viewMember = View.inflate(parent.getContext(), R.layout.member_layout, null);
        } else viewMember = convertView;

        TeamMember member = (TeamMember) getItem(position);
        ((TextView) viewMember.findViewById(R.id.studentID)).setText(member.studentID);
        ((TextView) viewMember.findViewById(R.id.studentName)).setText(member.name);

        return viewMember;
    }
}
