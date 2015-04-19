package com.cs495.bucketbuddy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Rafael on 4/16/15.
 */
 class MyAdapter2 extends ArrayAdapter<String> {

    public MyAdapter2(Context context, String[] playersList) {

        super(context,R.layout.child_layout2, playersList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
                DatabaseHelper dbh = new DatabaseHelper(getContext(), null, null,1);
               // dbh.

            }
        });







        return customView;
    }
}
