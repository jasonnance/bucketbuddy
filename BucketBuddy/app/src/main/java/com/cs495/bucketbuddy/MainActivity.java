package com.cs495.bucketbuddy;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {

    Button btnTeams,btnStartGame,btnAbout ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseHelper dbHelper = new DatabaseHelper(this,null,null,1);
        btnTeams = (Button) findViewById(R.id.mainTeamBtn);
        btnStartGame = (Button) findViewById(R.id.mainStartBtn);
        btnAbout = (Button) findViewById(R.id.mainAboutBtn);

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent swap;
                swap = new Intent(MainActivity.this,AboutActivity.class );
                MainActivity.this.startActivity(swap);
            }
        });

        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent swap;
                swap = new Intent(MainActivity.this,GameScreenActivity.class );
                MainActivity.this.startActivity(swap);
            }
        });
        btnTeams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent swap;
                swap = new Intent(MainActivity.this,TeamListActivity.class );
                MainActivity.this.startActivity(swap);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
