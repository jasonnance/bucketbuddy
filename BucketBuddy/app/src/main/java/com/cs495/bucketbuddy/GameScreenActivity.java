package com.cs495.bucketbuddy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import java.util.ArrayList;

/**
 * Created by Rafael on 3/30/15.
 */
public class GameScreenActivity extends ActionBarActivity {
    private Team team;
    private ArrayList<Player> players;
    private DatabaseHelper dbHelper;
    private Player attributedPlayer;

    // Store the indices in the players list of the players currently on the floor
    private ArrayList<Integer> lineup = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_gamescreen);
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

        findViewById(R.id.btn_rebound).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStat("rebounds");
            }
        });

        findViewById(R.id.btn_end_game).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endGame();
            }
        });
        initializeStats();
        lineupSelector.performClick();

    }

    @Override
    public void onPause() {
        super.onPause();
        commitChanges();
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

    private void addStat(final String statName) {
        CharSequence[] playerDisplay = new CharSequence[lineup.size()];

        for (int i = 0; i < lineup.size(); i++) {
            playerDisplay[i] = String.valueOf((int) players.get(i).getAttr("playerNumber")) + " " +
                    (String) players.get(i).getAttr("playerName") + " " +
                    Player.abbreviatePosition((String) players.get(i).getAttr("playerPosition"));
        }
        attributedPlayer = players.get(lineup.get(0));
        new AlertDialog.Builder(this)
                .setSingleChoiceItems(playerDisplay, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        attributedPlayer = players.get(lineup.get(which));
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        attributedPlayer.setGameStat(statName, (int) attributedPlayer.getGameStat(statName) + 1);
                    }
                })
                .setTitle(R.string.credit_stat_title)
                .show();
    }

    private void initializeStats() {

        // Start out each stat at 0
        for (int i = 0; i < StatEntity.REQUIRED_STATS.length; i++) {
            for (Player player : players) {
                player.setGameStat(StatEntity.REQUIRED_STATS[i], 0);
            }
            team.setGameStat(StatEntity.REQUIRED_STATS[i], 0);
        }
    }

    private void commitChanges() {

        // Set the team stat to be the sum of all the player stats
        int curStatValue;
        for (int i = 0; i < StatEntity.REQUIRED_STATS.length; i++) {
            curStatValue = 0;
            for (Player player : players) {
                curStatValue += (int) player.getGameStat(StatEntity.REQUIRED_STATS[i]);
            }
            team.setGameStat(StatEntity.REQUIRED_STATS[i], curStatValue);
        }


        dbHelper.updateStatEntity(team);
        for (Player player : players) {
            dbHelper.updateStatEntity(player);
        }
    }

    private void endGame() {
        commitChanges();
        Intent swap = new Intent(GameScreenActivity.this, TeamListActivity.class);
        GameScreenActivity.this.startActivity(swap);
    }
}