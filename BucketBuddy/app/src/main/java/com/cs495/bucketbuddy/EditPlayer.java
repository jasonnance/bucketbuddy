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

public class EditPlayer extends ActionBarActivity {
    private static Button submitNumber;
    private static EditText playerNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_player);

        playerNumber = (EditText) findViewById(R.id.pNumberText);
        submitNumber = (Button) findViewById(R.id.editPlayerSubmit);
        submitNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = getIntent().getExtras();
                long playerId = extras.getLong("playerId");
                Player editedPlayer = editPlayer(playerId);
                Intent swap = new Intent(EditPlayer.this,EditTeam.class );
                swap.putExtra("playerId", editedPlayer.getId());
                EditPlayer.this.startActivity(swap);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_player, menu);
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

    private Player editPlayer(long playerId) {
        int playerNumberAttr = Integer.parseInt(playerNumber.getText().toString());
        DatabaseHelper dbHelper = new DatabaseHelper(null,null,null,1);
        Player player = (Player) dbHelper.getStatEntity(playerId);
        player.setAttr("playerNumber", playerNumberAttr);
        dbHelper.updateStatEntity(player);
        return player;
    }
}
