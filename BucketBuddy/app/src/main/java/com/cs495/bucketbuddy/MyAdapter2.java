package com.cs495.bucketbuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Rafael on 4/16/15.
 */
 class MyAdapter2 extends ArrayAdapter<String> {

    public MyAdapter2(Context context, String[] playersList) {

        super(context,R.layout.child_layout2, playersList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        String player = getItem(position);
        View customView = inflater.inflate(R.layout.child_layout2,parent,false);
        TextView txView = (TextView) customView.findViewById(R.id.playeEditTeam);
        txView.setText(player);


        return customView;
    }
}
