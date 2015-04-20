package com.cs495.bucketbuddy;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EditTeam extends ActionBarActivity {

    EditText teamNameChangeInput;
    DatabaseHelper dbHelper = new DatabaseHelper(this, null, null, 1);
    private Team curTeam;
    private long teamId;
    List<Long> playerId = new ArrayList<Long>();



    Context context;
    CharSequence text;// = "Hello toast!";
    int duration = Toast.LENGTH_LONG;
    String oldTeam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_team);
        Button createPlayerButton;
        Button dButton;
        Button changeTNButton;

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        changeTNButton = (Button) findViewById(R.id.changeTeamNameButton);

        List<String>playersName = new ArrayList<String>();

        //Button changeTeamNameButton;
        createPlayerButton = (Button) findViewById(R.id.spawnAddPlayer);
        dButton = (Button) findViewById(R.id.doneButton);

        teamNameChangeInput = (EditText) findViewById(R.id.changeTeamName);

        Bundle extras = getIntent().getExtras();
        teamId = extras.getLong("teamId");

        curTeam = (Team) dbHelper.getStatEntity(teamId);

        //oldTeamName = curTeam.getAttr("teamName").toString();
        oldTeam = curTeam.getAttr("teamName").toString();
        context  = getApplicationContext();

        changeTNButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               editTeamName();
            }
        });

        playerId = curTeam.getPlayerIds();

        String newTeamName = curTeam.getAttr("teamName").toString();
        String player;
        for(int i = 0 ; i<playerId.size();i++){
            player = dbHelper.getStatEntity(playerId.get(i)).getAttr("playerName").toString();
            playersName.add(player);


        }


        //String[] teamPlayers = (String[])playersName.toArray();
        String[] teamPlayers = null;
        if(playerId.size()!=0) {
            teamPlayers = new String[playersName.size()];
            for (int i = 0; i < playerId.size(); i++)
                teamPlayers[i] = playersName.get(i);
        }
        else{
            teamPlayers = new String[1];
            teamPlayers[0]="No Players";
        }

        teamNameChangeInput.setText(oldTeam);

        ListView playersList = (ListView)findViewById(R.id.playersListEditTeam);
        ListAdapter adapterList = new MyAdapter2(this,teamPlayers,playerId,teamId);
        playersList.setAdapter(adapterList);

        createPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent swap;
                swap = new Intent(EditTeam.this,CreatePlayer.class );
                swap.putExtra("teamId", teamId);
                EditTeam.this.startActivity(swap);
            }
        });

        dButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent swap;
                swap = new Intent(EditTeam.this,TeamListActivity.class );
                EditTeam.this.startActivity(swap);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_team, menu);
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

    private void editTeamName(){
        String newTeamName = teamNameChangeInput.getText().toString();
        curTeam.setAttr("teamName",newTeamName);
        DatabaseHelper newTeamDB = new DatabaseHelper(this,null,null,1);
        newTeamDB.updateStatEntity(curTeam);
        text = "Team name changed from \"" + oldTeam + "\" to \"" + newTeamName + "\".";
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
