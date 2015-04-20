package com.cs495.bucketbuddy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rafael on 4/16/15.
 */
 class MyAdapter2 extends ArrayAdapter<String> {
    List<Long> playersId = new ArrayList<Long>();
    private long teamId;

    public MyAdapter2(Context context, String[] playersList,List<Long> playerId, long teamId) {

        super(context,R.layout.child_layout2, playersList);
        playersId = playerId;
        this.teamId = teamId;


    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageButton deleteButton;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        String player = getItem(position);
        View customView = inflater.inflate(R.layout.child_layout2,parent,false);
        deleteButton = (ImageButton)customView.findViewById(R.id.deleteEditTeam);
        TextView txView = (TextView) customView.findViewById(R.id.playeEditTeam);
        txView.setText(player);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.deletePlayer)
                        .setMessage(R.string.reallyDeletePlayer)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseHelper dbh = new DatabaseHelper(getContext(), null, null,1);
                                dbh.deleteStatEntity(playersId.get(position));

                                Toast toast = Toast.makeText(getContext(), "Player Deleted", Toast.LENGTH_SHORT);
                                toast.show();
                                Team mod = (Team)dbh.getStatEntity(teamId);
                                playersId.remove(position);
                                mod.deletePlayer(position);
                                dbh.updateStatEntity(mod);

                                Intent swap;

                                swap = new Intent(getContext(),EditTeam.class );
                                swap.putExtra("teamId", teamId);
                                getContext().startActivity(swap);
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();







            }
        });

        







        return customView;
    }
}
