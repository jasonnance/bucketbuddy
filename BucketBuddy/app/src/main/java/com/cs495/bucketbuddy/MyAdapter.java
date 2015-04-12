package com.cs495.bucketbuddy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rafael on 4/9/15.
 */
public class MyAdapter extends BaseExpandableListAdapter {

    DatabaseHelper dbHelper;


    private Context context;
    ArrayList<Team> parentList;
    ArrayList <ArrayList<Long>> playersID = new ArrayList<ArrayList<Long>>() ;







    public MyAdapter(Context context){
        this.context = context;
        dbHelper = new DatabaseHelper(context,null,null,1);
        parentList= dbHelper.getAllTeams();
<<<<<<< Updated upstream
        for (int i=0; i < parentList.size(); i++) {
            playersID.add(parentList.get(i).getPlayerIds());
=======
        for(int i = 0; i<parentList.size();i++){
        playersID.add(parentList.get(i).getPlayerIds());


>>>>>>> Stashed changes
        }

    }

    @Override
    public int getGroupCount() {
        return parentList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return playersID.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.parent_layout,
                    null);
        }
        Button btEditTeam = (Button) convertView.findViewById(R.id.btEdtitTeam);
        Button btViewStats = (Button) convertView.findViewById(R.id.btViewTeamStats);
        TextView txView = (TextView) convertView.findViewById(R.id.txTeamName);
<<<<<<< Updated upstream
        txView.setText(String.valueOf(parentList.get(groupPosition).getAttr("teamName").toString()));
        return txView;
=======

        txView.setText(parentList.get(groupPosition).getAttr("teamName").toString());
        return convertView;
>>>>>>> Stashed changes
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        DatabaseHelper dbh = new DatabaseHelper(context, null , null, 1);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.child_layout,
                    null);
        }

        TextView txView = (TextView)convertView.findViewById(R.id.txPlayers);
        txView.setText(dbh.getStatEntity(playersID.get(groupPosition).get(childPosition)).getAttr("playerName").toString());


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
