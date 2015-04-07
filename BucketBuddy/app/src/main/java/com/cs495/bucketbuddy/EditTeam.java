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

public class EditTeam extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_team);
        Button createPlayerButton;
        Button dButton;
        createPlayerButton = (Button) findViewById(R.id.spawnAddPlayer);
        dButton = (Button) findViewById(R.id.doneButton);

        Bundle extras = getIntent().getExtras();

        createPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent swap;
                swap = new Intent(EditTeam.this,CreatePlayer.class );
                Bundle extras = getIntent().getExtras();
                long teamId = extras.getLong("teamId");
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
}
