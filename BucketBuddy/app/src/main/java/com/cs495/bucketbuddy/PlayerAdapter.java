package com.cs495.bucketbuddy;


import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PlayerAdapter extends ArrayAdapter<Player>{

    private Context context;
    private int layoutResourceId;
    private ArrayList<Player> players;

    public PlayerAdapter (Context context, int layoutResourceId, ArrayList<Player> players) {
        super(context, layoutResourceId, players);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.players = players;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent);

        if (v == null) {
            v = new TextView(context);
        }
        v.setText((String) players.get(position).getAttr("playerName"));
        return v;
    }

    @Override
    public Player getItem(int position) {
        return players.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(layoutResourceId, null);
        }
        TextView lbl = new TextView(getContext());
        lbl.setText((String) players.get(position).getAttr("playerName"));
        return convertView;
    }

}
