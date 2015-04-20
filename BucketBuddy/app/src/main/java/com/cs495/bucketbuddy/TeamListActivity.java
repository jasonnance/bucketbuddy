package com.cs495.bucketbuddy;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Rafael on 3/30/15.
 */
public class TeamListActivity extends ActionBarActivity{
    ExpandableListView expList;


    @Override
    protected void onResume() {
        super.onResume();
        expList = (ExpandableListView) findViewById(R.id.exListTeam);
        expList.setAdapter(new MyAdapter(this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teamlist);

        Button createTeamButton = (Button) findViewById(R.id.spawnCreateTeam);
        createTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent swap;
                swap = new Intent(TeamListActivity.this,CreateTeam.class );
                TeamListActivity.this.startActivity(swap);
            }
        });

        expList = (ExpandableListView) findViewById(R.id.exListTeam);
        expList.setAdapter(new MyAdapter(this));



    }
}
