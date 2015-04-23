package com.cs495.bucketbuddy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Rafael on 4/9/15.
 */
public class MyAdapter3 extends BaseExpandableListAdapter {

    DatabaseHelper dbHelper;


    private Context context;
    Team team;
    String[] attributes;
    private ArrayList<Player> players;


    public MyAdapter3(Context context, Team team,ArrayList<Player> players ){
        this.context = context;
        dbHelper = new DatabaseHelper(context,null,null,1);
        this.team = team;
        this.players = players;
        attributes = new String[] {"rebounds","assists",
                "ftm","fta","fouls","turnovers","blocks","steals","2pm","2pa","3pm","3pa"};



    }

    public ArrayList<Player> getChanges(){
        return this.players;
    }
    @Override
    public int getGroupCount() {
        return players.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return attributes.length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return attributes[childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.parent_layout2,
                    null);
        }
        TextView playerName = (TextView) convertView.findViewById(R.id.txStatsPlayerName);
        playerName.setText(players.get(groupPosition).getAttr("playerName").toString());
        playerName.setAllCaps(true);


        return convertView;




    }

    @Override
    public View getChildView(final int groupPosition,final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {


        DatabaseHelper dbh = new DatabaseHelper(context, null , null, 1);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.child_layout3,
                        null);
            }

            TextView playerStats = (TextView) convertView.findViewById(R.id.StatsPlayer);
            playerStats.setText(attributes[childPosition]);
            playerStats.setAllCaps(true);
            final TextView stats = (TextView) convertView.findViewById(R.id.statsAttribute);
            stats.setText(players.get(groupPosition).getGameStat(attributes[childPosition]).toString());
            Button plusButton = (Button) convertView.findViewById(R.id.plusButton);
            Button minusButton = (Button) convertView.findViewById(R.id.minusButton);




            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int oldvalue = (Integer) players.get(groupPosition).getGameStat(attributes[childPosition]);
                    oldvalue++;
                    if (attributes[childPosition].equals("2pm"))
                    {
                        int oldv = (Integer) players.get(groupPosition).getGameStat("points");
                        int oldv2 = (Integer) players.get(groupPosition).getGameStat("fgm");

                        players.get(groupPosition).setGameStat("points", oldv+2);
                        players.get(groupPosition).setGameStat("fgm", oldv2+1);


                    }
                    if (attributes[childPosition].equals("3pm"))
                    {
                        int oldv = (Integer) players.get(groupPosition).getGameStat("points");
                        int oldv2 = (Integer) players.get(groupPosition).getGameStat("fgm");

                        players.get(groupPosition).setGameStat("points", oldv+3);
                        players.get(groupPosition).setGameStat("fgm", oldv2+1);



                    }

                    if (attributes[childPosition].equals("ftm"))
                    {
                        int oldv = (Integer) players.get(groupPosition).getGameStat("points");
                        players.get(groupPosition).setGameStat("points", oldv+1);


                    }
                    if (attributes[childPosition].equals("2pa"))
                    {
                        int oldv = (Integer) players.get(groupPosition).getGameStat("fga");
                        players.get(groupPosition).setGameStat("fga", oldv+1);

                    }

                    if (attributes[childPosition].equals("3pa"))
                    {
                        int oldv = (Integer) players.get(groupPosition).getGameStat("fga");
                        players.get(groupPosition).setGameStat("fga", oldv+1);

                    }


                    players.get(groupPosition).setGameStat(attributes[childPosition], oldvalue);

                    stats.setText(Integer.toString(oldvalue));


                }
            });

            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int oldvalue = (Integer) players.get(groupPosition).getGameStat(attributes[childPosition]);
                    if (oldvalue == 0)
                        return;

                    if (attributes[childPosition].equals("2pm"))
                    {
                        int oldv = (Integer) players.get(groupPosition).getGameStat("points");
                        int oldv2 = (Integer) players.get(groupPosition).getGameStat("fgm");

                        players.get(groupPosition).setGameStat("points", oldv-2);
                        players.get(groupPosition).setGameStat("fgm", oldv2-1);


                    }
                    if (attributes[childPosition].equals("3pm"))
                    {
                        int oldv = (Integer) players.get(groupPosition).getGameStat("points");
                        int oldv2 = (Integer) players.get(groupPosition).getGameStat("fgm");

                        players.get(groupPosition).setGameStat("points", oldv-3);
                        players.get(groupPosition).setGameStat("fgm", oldv2-1);



                    }

                    if (attributes[childPosition].equals("ftm"))
                    {
                        int oldv = (Integer) players.get(groupPosition).getGameStat("points");
                        players.get(groupPosition).setGameStat("points", oldv-1);


                    }
                    if (attributes[childPosition].equals("2pa"))
                    {
                        int oldv = (Integer) players.get(groupPosition).getGameStat("fga");
                        players.get(groupPosition).setGameStat("fga", oldv-1);

                    }

                    if (attributes[childPosition].equals("3pa"))
                    {
                        int oldv = (Integer) players.get(groupPosition).getGameStat("fga");
                        players.get(groupPosition).setGameStat("fga", oldv-1);

                    }

                        oldvalue--;
                    players.get(groupPosition).setGameStat(attributes[childPosition], oldvalue);

                    stats.setText(Integer.toString(oldvalue));


                }
            });




        return convertView;


    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
