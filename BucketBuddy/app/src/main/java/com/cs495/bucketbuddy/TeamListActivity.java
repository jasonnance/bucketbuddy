package com.cs495.bucketbuddy;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Rafael on 3/30/15.
 */
public class TeamListActivity extends ActionBarActivity{
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

        TableLayout tableLayout = (TableLayout) findViewById(R.id.teamsListTable);
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT);

        TableRow.LayoutParams trparams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);

        DatabaseHelper dbHelper = new DatabaseHelper(this, null, null, 1);
        ArrayList<Team> allTeams = dbHelper.getAllTeams();

        for (Team team : allTeams) {
            TextView teamName = new TextView(this);
            teamName.setText((String) team.getAttr("teamName"));
            teamName.setLayoutParams(trparams);

            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(trparams);
            tableRow.addView(teamName);
            tableLayout.addView(tableRow, params);
        }
    }
}
