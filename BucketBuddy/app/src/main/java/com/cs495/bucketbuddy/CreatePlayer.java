package com.cs495.bucketbuddy;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Button;

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
                createPlayer();
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

    private void createPlayer(){
        String newPlayerName = playerNameInput.getText().toString();
        Player newPlayer = new Player();
        newPlayer.setAttr("playerName",newPlayerName);
        newPlayer.setTeamId(0001);
        DatabaseHelper newPlayerDB = new DatabaseHelper(this,null,null,1);
    }

}
