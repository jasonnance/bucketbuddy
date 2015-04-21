package com.cs495.bucketbuddy;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Button;
import android.app.ActionBar;
import android.content.Intent;
import android.widget.TextView;

public class EditPlayer extends ActionBarActivity {
    private static Button submitNumber;
    private static EditText playerNumber;
    private static RadioGroup positionGroup;
    private static RadioButton playerPositionAttr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_player);
        String sPlayerName;


        //EditText editView = new EditText(this);
        //editView.setKeyListener(new DigitsKeyListener());
        playerNumber = (EditText) findViewById(R.id.pNumberText);
        playerNumber.setKeyListener(new DigitsKeyListener());

        positionGroup = (RadioGroup) findViewById(R.id.rgPosition);
        positionGroup.check(R.id.radioButton6);

        submitNumber = (Button) findViewById(R.id.editPlayerSubmit);
        final TextView playerName = (TextView) findViewById(R.id.PlayerName);
        Bundle extras = getIntent().getExtras();
        sPlayerName = extras.get("playerName").toString();
        playerName.setText(sPlayerName);
        submitNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedID = positionGroup.getCheckedRadioButtonId();
                playerPositionAttr = (RadioButton) findViewById(selectedID);
                Bundle extras = getIntent().getExtras();
                long playerId = extras.getLong("playerId");
                long teamId = extras.getLong("teamId");

                Player editedPlayer = editPlayer(playerId);
                Intent swap = new Intent(EditPlayer.this,EditTeam.class );
                swap.putExtra("playerId", editedPlayer.getId());
                swap.putExtra("teamId", teamId);
                EditPlayer.this.startActivity(swap);
            }
        });
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        );

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
        String playerPosition = (String) playerPositionAttr.getText();
        DatabaseHelper dbHelper = new DatabaseHelper(this,null,null,1);
        Player player = (Player) dbHelper.getStatEntity(playerId);
        player.setAttr("playerNumber", playerNumberAttr);
        player.setAttr("playerPosition", playerPosition);
        dbHelper.updateStatEntity(player);
        return player;
    }
}
