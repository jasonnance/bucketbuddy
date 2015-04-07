package com.cs495.bucketbuddy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {

    Button btnTeams,btnStartGame,btnAbout, btnDeleteDb ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseHelper dbHelper = new DatabaseHelper(this,null,null,1);
        btnTeams = (Button) findViewById(R.id.mainTeamBtn);
        btnStartGame = (Button) findViewById(R.id.mainStartBtn);
        btnAbout = (Button) findViewById(R.id.mainAboutBtn);
        btnDeleteDb = (Button) findViewById(R.id.mainDeleteDbBtn);

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
        btnDeleteDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spawnDeleteDatabaseDialog();
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

    private void spawnDeleteDatabaseDialog() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.deleteDb)
                .setMessage(R.string.reallyDeleteDb)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDatabase();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void deleteDatabase() {
        this.deleteDatabase("bucketbuddy.db");
    }

}
