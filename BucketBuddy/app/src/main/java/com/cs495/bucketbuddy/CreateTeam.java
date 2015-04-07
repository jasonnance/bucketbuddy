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

public class CreateTeam extends ActionBarActivity {

   //private static Button createTeamSubmitButton;
    private static EditText teamNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);
        Button createTeamButton = (Button) findViewById(R.id.CreateTeamSubmitButton);
        teamNameInput = (EditText) findViewById(R.id.CreateTeamNameInput);
        createTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Team newTeam = createTeam();
                Intent swap;
                swap = new Intent(CreateTeam.this,EditTeam.class );
                swap.putExtra("teamId", newTeam.getId());
                CreateTeam.this.startActivity(swap);
            }
        });
        //createTeamSubmitButton = (Button) findViewById(R.id.CreateTeamSubmitButton);
//        createTeamSubmitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_team, menu);
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

    private Team createTeam(){
        String newTeamName = teamNameInput.getText().toString();
        Team newTeam = new Team();
        newTeam.setAttr("teamName",newTeamName);
        DatabaseHelper newTeamDB = new DatabaseHelper(this,null,null,1);
        newTeam = (Team) newTeamDB.addStatEntity(newTeam);
        return newTeam;
    }

}
