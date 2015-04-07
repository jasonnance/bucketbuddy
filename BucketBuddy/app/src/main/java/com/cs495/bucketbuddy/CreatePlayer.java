package com.cs495.bucketbuddy;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Button;
import android.app.ActionBar;
import android.content.Intent;

public class CreatePlayer extends ActionBarActivity {

    private static Button createPlayerSubmitButton;
    private static EditText playerNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_player);
        playerNameInput = (EditText) findViewById(R.id.CreatePlayerNameInput);
        createPlayerSubmitButton = (Button) findViewById(R.id.CreatePlayerSubmitButton);
        createPlayerSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = getIntent().getExtras();
                long teamId = extras.getLong("teamId");
                Player newPlayer = createPlayer(teamId);
                Intent swap = new Intent(CreatePlayer.this,EditPlayer.class );
                swap.putExtra("playerId", newPlayer.getId());
                CreatePlayer.this.startActivity(swap);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_player, menu);
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

    private Player createPlayer(long teamId){
        String newPlayerName = playerNameInput.getText().toString();
        Player newPlayer = new Player();
        newPlayer.setAttr("playerName",newPlayerName);
        newPlayer.setTeamId(teamId);
        DatabaseHelper newPlayerDB = new DatabaseHelper(this,null,null,1);
        newPlayer = (Player) newPlayerDB.addStatEntity(newPlayer);
        Team updatedTeam = (Team) newPlayerDB.getStatEntity(teamId);
        updatedTeam.addPlayerId(newPlayer.getId());
        newPlayerDB.updateStatEntity(updatedTeam);
        return newPlayer;
    }

}
