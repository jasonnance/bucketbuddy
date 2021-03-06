package com.cs495.bucketbuddy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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


        for(int i = 0; i<parentList.size();i++){
        playersID.add(parentList.get(i).getPlayerIds());


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
        return playersID.get(groupPosition).get(childPosition);
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
            convertView = infalInflater.inflate(R.layout.parent_layout,
                    null);
        }
        ImageButton btEditTeam = (ImageButton) convertView.findViewById(R.id.btEdtitTeam);
        ImageButton btViewStats = (ImageButton) convertView.findViewById(R.id.btViewTeamStats);
        ImageButton btStartGame = (ImageButton) convertView.findViewById(R.id.btStartGame);
        ImageButton btDeleteTeam = (ImageButton) convertView.findViewById(R.id.btRemoveTeam);

        TextView txView = (TextView) convertView.findViewById(R.id.txTeamName);

        btEditTeam.setFocusable(false);
        btViewStats.setFocusable(false);
        btStartGame.setFocusable(false);
        btDeleteTeam.setFocusable(false);
        txView.setText(String.valueOf(parentList.get(groupPosition).getAttr("teamName").toString()));
        txView.setAllCaps(true);

        btDeleteTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.deleteTeam)
                        .setMessage(R.string.reallyDeleteTeam)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseHelper dbh = new DatabaseHelper(context, null, null,1);
                                Team mod = (Team)dbh.getStatEntity(parentList.get(groupPosition).getId());

                                ArrayList<Long> modPlayers = mod.getPlayerIds();

                                for (int i = 0; i < mod.getPlayerIds().size(); i++) {
                                    dbh.deleteStatEntity(modPlayers.get(i));
                                }

                                dbh.deleteStatEntity(parentList.get(groupPosition).getId());

                                Toast toast = Toast.makeText(context, "Team Deleted", Toast.LENGTH_SHORT);
                                toast.show();
                                Intent swap;
                                swap = new Intent(context,TeamListActivity.class );
                                swap.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(swap);

                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();



            }
        });

        btEditTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent swap;
                swap = new Intent(context,EditTeam.class );
                Long teamId= parentList.get(groupPosition).getId();
                swap.putExtra("teamId", teamId);
                context.startActivity(swap);

            }
        });
        btViewStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent swap;
                swap = new Intent(context,ViewEntityStatsTabbedActivity.class );
                Long teamId= parentList.get(groupPosition).getId();
                swap.putExtra("entityId", teamId);
                context.startActivity(swap);

            }
        });
        btStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent swap;
                swap = new Intent(context,SelectSeason.class );
                Long teamId= parentList.get(groupPosition).getId();
                swap.putExtra("teamId", teamId);
                context.startActivity(swap);

            }
        });



        return convertView;



    }

    @Override
    public View getChildView(final int groupPosition,final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        DatabaseHelper dbh = new DatabaseHelper(context, null , null, 1);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.child_layout,
                    null);
        }
        ImageButton btEditPlayer = (ImageButton) convertView.findViewById(R.id.btEditPlayer);
        ImageButton btViewStats = (ImageButton) convertView.findViewById(R.id.btViewPlayerStats);

        TextView txView = (TextView)convertView.findViewById(R.id.txPlayers);
        txView.setText(dbh.getStatEntity(playersID.get(groupPosition).get(childPosition)).getAttr("playerName").toString());
        convertView.setPadding(100,20,0,0);

        btEditPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent swap;
                swap = new Intent(context,EditPlayer.class );
                Long playerID = playersID.get(groupPosition).get(childPosition);
                swap.putExtra("playerId", playerID);
                String playerName = dbHelper.getStatEntity(playerID).getAttr("playerName").toString();
                swap.putExtra("playerName",playerName);

                Long teamId= parentList.get(groupPosition).getId();
                swap.putExtra("teamId", teamId);
                context.startActivity(swap);

            }
        });
        btViewStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent swap;
                swap = new Intent(context,ViewEntityStatsTabbedActivity.class );
                Long playerID= playersID.get(groupPosition).get(childPosition);
                swap.putExtra("entityId", playerID);

                context.startActivity(swap);

            }
        });
        return convertView;


    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
