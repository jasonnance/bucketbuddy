package com.cs495.bucketbuddy;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Rafael on 3/30/15.
 */
public class GameScreenActivity extends ActionBarActivity {
    private Team team;
    private ArrayList<Player> players;
    private DatabaseHelper dbHelper;

    // Store the indices in the players list of the players currently on the floor
    private ArrayList<Integer> lineup = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamescreen);
        long teamId = getIntent().getExtras().getLong("teamId");

        // Load the team and players from the database
        dbHelper = new DatabaseHelper(this, null, null, 1);
        team = (Team) dbHelper.getStatEntity(teamId);
        players = new ArrayList<Player>();
        ArrayList<Long> playerIds = team.getPlayerIds();
        Log.d("spinnerDebug", "found " + String.valueOf(playerIds.size()) + " playerIds");
        for (Long playerId : playerIds) {
            players.add((Player) dbHelper.getStatEntity(playerId));
        }
        MultiSpinner lineupSelector = (MultiSpinner) findViewById(R.id.lineup_multispinner);
        ArrayList<String> playerNames = new ArrayList<String>();
        for (Player player : players) {
            playerNames.add((String) player.getAttr("playerName"));
        }
        lineupSelector.setItems(playerNames);
        lineupSelector.setMultiSpinnerListener(new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                lineup.clear();
                for (int i = 0; i < selected.length; i++) {
                    if (selected[i]) {
                        lineup.add(i);
                    }
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gamescreen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
