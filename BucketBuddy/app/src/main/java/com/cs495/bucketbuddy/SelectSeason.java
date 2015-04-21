package com.cs495.bucketbuddy;

import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;


public class SelectSeason extends ActionBarActivity {

    private Team team;
    private ArrayList<Player> players = new ArrayList<Player>();
    private DatabaseHelper dbHelper;
    private int seasonNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_season);
        dbHelper = new DatabaseHelper(this, null, null, 1);

        Bundle extras = getIntent().getExtras();

        team = (Team) dbHelper.getStatEntity(extras.getLong("teamId"));
        for (long playerId : team.getPlayerIds()) {
            players.add((Player) dbHelper.getStatEntity(playerId));
        }

        findViewById(R.id.btn_start_game).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });

        generateSpinner();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_season, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startGame() {
        if (seasonNumber == team.getSeasons().size()) {
            team.addSeason(new TeamSeason());
            for (Player player : players) {
                player.addSeason(new PlayerSeason());
            }
        }

        Intent swap = new Intent(SelectSeason.this, GameScreenActivity.class);
        team.getSeasons().get(seasonNumber).addGame(new TeamGame());
        dbHelper.updateStatEntity(team);
        for (Player player : players) {
            player.getSeasons().get(seasonNumber).addGame(new PlayerGame());
            dbHelper.updateStatEntity(player);
        }
        swap.putExtra("seasonNumber",seasonNumber);
        swap.putExtra("teamId", team.getId());
        this.startActivity(swap);
    }

    private void generateSpinner() {
        final int numSeasons = team.getSeasons().size();
        String[] choices = new String[numSeasons+1];
        for (int i = 0; i < numSeasons; i++) {
            choices[i] = getResources().getString(R.string.season) + " " + String.valueOf(i)
                + " (" + String.valueOf(team.getSeasons().get(i).getGames().size())
                + " " + getResources().getString(R.string.games) + ")";
        }
        choices[numSeasons] = getResources().getString(R.string.newSeason);

        Spinner spinner = (Spinner) findViewById(R.id.season_select_spinner);
        spinner.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
                choices));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                seasonNumber = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
