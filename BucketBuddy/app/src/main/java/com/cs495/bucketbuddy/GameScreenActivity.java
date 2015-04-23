package com.cs495.bucketbuddy;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Rafael on 3/30/15.
 */
public class GameScreenActivity extends ActionBarActivity {
    private Team team;
    private ArrayList<Player> players;
    private DatabaseHelper dbHelper;
    private Player attributedPlayer;
    private int curShotX;
    private int curShotY;
    private TextView teamScoreDisplay;
    private TextView oppScoreDisplay;
    private Button btnStats;
    private int selection;

    // Store the indices in the players list of the players currently on the floor
    private ArrayList<Integer> lineup = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_gamescreen);
        long teamId = getIntent().getExtras().getLong("teamId");

        // Load the team and players from the database
        dbHelper = new DatabaseHelper(this, null, null, 1);
        team = (Team) dbHelper.getStatEntity(teamId);
        players = new ArrayList<Player>();
        ArrayList<Long> playerIds = team.getPlayerIds();
        for (Long playerId : playerIds) {
            players.add((Player) dbHelper.getStatEntity(playerId));
        }
        MultiSpinner lineupSelector = (MultiSpinner) findViewById(R.id.lineup_multispinner);
        ArrayList<String> playerNames = new ArrayList<String>();
        for (Player player : players) {
            playerNames.add(player.toString());
        }
        lineupSelector.setItems(playerNames);
        lineupSelector.setMultiSpinnerListener(new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                lineup.clear();
                for (int i = 0; i < selected.length; i++) {
                    if (selected[i]) {
                        lineup.add(i);
                    }
                }
            }
        });

        findViewById(R.id.btn_rebound).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptStatCredit("rebounds");
            }
        });

        findViewById(R.id.btn_assist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptStatCredit("assists");
            }
        });

        findViewById(R.id.btn_steal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptStatCredit("steals");
            }
        });

        findViewById(R.id.btn_block).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptStatCredit("blocks");
            }
        });

        findViewById(R.id.btn_turnover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptStatCredit("turnovers");
            }
        });

        findViewById(R.id.btn_foul).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptStatCredit("fouls");
            }
        });

        findViewById(R.id.btn_end_game).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmEndGame();
            }
        });

        findViewById(R.id.btn_free_throw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptStatCredit("fta");
            }
        });

        findViewById(R.id.btn_opp_score).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOppScore();
            }
        });

        findViewById(R.id.court).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Figure out whether the touch lands in the (red) 2 point overlay
                // or the (green) 3 point overlay and call the appropriate method
                final int action = event.getAction();
                final int evX = (int) event.getX();
                final int evY = (int) event.getY();
                int touchColor = getTouchColor(R.id.court_overlay, evX, evY);
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        curShotX = evX;
                        curShotY = evY;
                        if (closeMatchColor(Color.RED, touchColor, 25)) {
                            // it's a 2 point attempt
                            promptStatCredit("2pa");
                        } else if (closeMatchColor(Color.GREEN, touchColor, 25)) {
                            // it's a 3 point attempt
                            promptStatCredit("3pa");
                        }
                        break;
                }
                return true;
            }
        });

        ((TextView) findViewById(R.id.txt_team_name)).setText((String) team.getAttr("teamName"));

        teamScoreDisplay = (TextView) findViewById(R.id.txt_team_score);
        oppScoreDisplay = (TextView) findViewById(R.id.txt_opponent_score);
        initializeStats();
        lineupSelector.performClick();


        btnStats = (Button)findViewById(R.id.btnStats);
        btnStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Select Changes");
        ExpandableListView expList;
        expList = new ExpandableListView(this);
        final MyAdapter3 myAdapter = new MyAdapter3(this,team,players);
        expList.setAdapter(myAdapter);
        builder.setView(expList);
        builder.setPositiveButton("Done",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                players = myAdapter.getChanges();
                commitChanges();


            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setLayout(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);

        dialog.show();



    }

    @Override
    public void onPause() {
        super.onPause();
        commitChanges();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gamescreen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    Determine the color of the given image at the given coordinates
     */
    private int getTouchColor(int hotspotId, int x, int y) {
        ImageView img = (ImageView) findViewById(hotspotId);

        img.setDrawingCacheEnabled(true);
        Bitmap hotspots = Bitmap.createBitmap(img.getDrawingCache());
        img.setDrawingCacheEnabled(false);
        return hotspots.getPixel(x, y);
    }

    /*
    Determine whether two colors match with the given tolerance
     */
    private boolean closeMatchColor(int color1, int color2, int tolerance) {
        if ((int) Math.abs(Color.red(color1) - Color.red(color2)) > tolerance)
            return false;
        if ((int) Math.abs(Color.green(color1) - Color.green(color2)) > tolerance)
            return false;
        if ((int) Math.abs(Color.blue(color1) - Color.blue(color2)) > tolerance)
            return false;
        return true;
    }

    /*
    Increase the other team's score by either 1, 2, or 3.  This change should be reflected
    as both a stat in the team's game and on the scoreboard.
     */
    private void addOppScore() {
        final CharSequence[] pointValues = new CharSequence[]{"1", "2", "3"};
        selection = 1;
        new AlertDialog.Builder(this)
                .setSingleChoiceItems(pointValues, 1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection = which;
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int newOppScore = (int) team.getGameStat("oppScore") +
                                Integer.parseInt((String) pointValues[selection]);
                        team.setGameStat("oppScore", newOppScore);
                        oppScoreDisplay.setText(String.valueOf(newOppScore));

                    }
                })
                .setTitle(R.string.set_point_value)
                .show();
    }

    /*
    Increment the team's score by the given amount.  We don't have to worry about
    saving it as a stat, since we can calculate it from the players' points.
     */
    private void addTeamScore(int amount) {
        int newTeamScore = Integer.parseInt((String) teamScoreDisplay.getText()) + amount;
        teamScoreDisplay.setText(String.valueOf(newTeamScore));
    }

    /*
    Increment the given stat for the current attributed player.
     */
    private void addStat(final String statName) {
        attributedPlayer.setGameStat(statName, (int) attributedPlayer.getGameStat(statName) + 1);
    }

    /*
    Increase the given stat by the given amount for the current attributed player.
     */
    private void addStat(final String statName, int amount) {
        attributedPlayer.setGameStat(statName, (int) attributedPlayer.getGameStat(statName) + amount);
    }

    /*
    Ask the user who should be given credit for the given stat.  For 2PA/3PA/FTA, this also
    entails finding out whether the shot was made or not.
     */
    private void promptStatCredit(final String statName) {
        CharSequence[] playerDisplay = new CharSequence[lineup.size()];

        for (int i = 0; i < lineup.size(); i++) {
            playerDisplay[i] = players.get(i).toString();
        }
        attributedPlayer = players.get(lineup.get(0));
        new AlertDialog.Builder(this)
                .setSingleChoiceItems(playerDisplay, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        attributedPlayer = players.get(lineup.get(which));
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addStat(statName);

                        // If it was a shot attempt, we have to find out whether it was made
                        if (statName.equals("2pa") || statName.equals("3pa") ||
                                statName.equals("fta")) {
                            promptStatMake(statName);
                        }
                        // Either a 2 point attempt or 3 point attempt also counts as a field goal
                        // attempt
                        if (statName.equals("2pa") || statName.equals("3pa")) {
                            addStat("fga");
                        }
                    }
                })
                .setTitle(R.string.credit_stat_title)
                .show();
    }

    /*
    Ask the user whether the given attempt (2PA, 3PA, FTA) resulted in a make.  Then add the
    corresponding make stat if necessary, as well as the points stat.
     */
    private void promptStatMake(final String statName) {
        new AlertDialog.Builder(this)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Add the shot coords if it was a field goal (i.e. not a free throw)
                        if (statName.equals("2pa") || statName.equals("3pa")) {
                            addStat("fgm");
                            addShot(true);
                        }
                        if (statName.equals("2pa")) {
                            addStat("2pm");
                            addStat("points", 2);
                            addTeamScore(2);
                        } else if (statName.equals("3pa")) {
                            addStat("3pm");
                            addStat("points", 3);
                            addTeamScore(3);
                        } else if (statName.equals("fta")) {
                            addStat("ftm");
                            addStat("points", 1);
                            addTeamScore(1);
                        }
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (statName.equals("2pa") || statName.equals("3pa")) {
                            addShot(false);

                        }
                    }
                })
                .setTitle(R.string.prompt_make)
                .show();
    }

    /*
     Add a shot to the attributed player's list of shot coords.
      */
    private void addShot(boolean made) {
        ArrayList<Shot> newShotCoords = (ArrayList<Shot>) attributedPlayer.getGameStat("shotCoords");
        newShotCoords.add(new Shot(curShotX, curShotY, made));
        attributedPlayer.setGameStat("shotCoords", newShotCoords);
    }

    /*
    Set each required stat for the team and all of its players to 0/empty
    so that we can just increment
    them as we go without worrying about any of them being null.
     */
    private void initializeStats() {
        // Start out each stat at 0
        for (int i = 0; i < Player.REQUIRED_STATS.length; i++) {
            for (Player player : players) {
                // Shot coords must be initialized as a list of pairs
                if (Player.REQUIRED_STATS[i].equals("shotCoords")) {
                    player.setGameStat(Player.REQUIRED_STATS[i],
                            new ArrayList<Shot>());
                }
                // Everything else is just an integer
                else {
                    player.setGameStat(Player.REQUIRED_STATS[i], 0);
                }
            }
        }
        for (int i = 0; i < Team.REQUIRED_STATS.length; i++) {
            // Shot coords are handled same as above
            if (Team.REQUIRED_STATS[i].equals("shotCoords")) {
                team.setGameStat(Team.REQUIRED_STATS[i],
                        new ArrayList<Shot>());
            } else {
                team.setGameStat(Team.REQUIRED_STATS[i], 0);
            }
        }
    }

    /*
    Save all changes made to team/player state to the database.
    Note: All team stats which are the sum of their player stats (i.e. everything except
    oppScore) are aggregated and added to the team's game here.
     */
    private void commitChanges() {
        int curStatValue;
        // Set the team stat to be the sum of all the player stats
        for (int i = 0; i < Player.REQUIRED_STATS.length; i++) {
            // We have to append all the shot coords lists together
            if (Player.REQUIRED_STATS[i].equals("shotCoords")) {
                ArrayList<Shot> teamShots = new ArrayList<Shot>();
                for (Player player : players) {
                    ArrayList<Shot> playerShots =
                            (ArrayList<Shot>) player.getGameStat(Player.REQUIRED_STATS[i]);
                    teamShots.addAll(playerShots);
                }
                team.setGameStat(Player.REQUIRED_STATS[i], teamShots);
            }
            // All other stats are just ints which need to be summed
            else {
                curStatValue = 0;
                for (Player player : players) {
                    curStatValue += (int) player.getGameStat(Player.REQUIRED_STATS[i]);
                }
                team.setGameStat(Player.REQUIRED_STATS[i], curStatValue);
            }
        }
        dbHelper.updateStatEntity(team);
        for (Player player : players) {
            dbHelper.updateStatEntity(player);
        }
    }

    /*
    Display a dialog box requesting confirmation for the user to actually end the game.
     */
    private void confirmEndGame() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.endGame)
                .setMessage(R.string.reallyEndGame)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        endGame();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    /*
    Send the user back to the team list after committing changes to end the game.
     */
    private void endGame() {
        commitChanges();
        Intent swap = new Intent(GameScreenActivity.this, TeamListActivity.class);
        GameScreenActivity.this.startActivity(swap);
    }
}