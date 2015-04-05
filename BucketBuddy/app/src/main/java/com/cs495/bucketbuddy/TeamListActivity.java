package com.cs495.bucketbuddy;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

        TableLayout tableLayout = (TableLayout) findViewById(R.id.teamsListTable);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        tableLayout.setLayoutParams(params);

        TableRow.LayoutParams trparams = new TableRow.LayoutParams(android.widget.TableRow.LayoutParams.WRAP_CONTENT,
                android.widget.TableRow.LayoutParams.WRAP_CONTENT);

        DatabaseHelper dbHelper = new DatabaseHelper(this, null, null, 1);
        ArrayList<Team> allTeams = dbHelper.getAllTeams();

        for (Team team : allTeams) {
            TextView teamName = new TextView(this);
            teamName.setText((String) team.getAttr("name"));
            teamName.setLayoutParams(trparams);

            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(trparams);
            tableRow.addView(teamName);
            tableLayout.addView(tableRow);
        }
    }
}
