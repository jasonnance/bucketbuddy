package com.cs495.bucketbuddy;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Player extends StatEntity as needed to represent
 * an individual basketball player.
 */
public class Player extends StatEntity {

    public enum Position {PointGuard, ShootingGuard, SmallForward, PowerForward, Center};
    private long teamId;

    /**
     * Constructs a blank player with no attributes,
     * seasons, or team, and a null id.
     */
    public Player() {
        attrs = new HashMap<String, Object>();
        seasons = new ArrayList<Season>();
    }

    /**
     * Returns the team for which the player plays.
     *
     * @return the player's current team
     */
    public long getTeamId() {
        return teamId;
    }

    /**
     * Sets the team for which the player plays.
     *
     * @param teamId the id of the player's new team
     */
    public void setTeamId(long teamId) { this.teamId = teamId; }

    /**
     * Adds a new PlayerSeason to the player's list
     * of seasons.
     *
     * @param season the season to add
     */
    public void addSeason(PlayerSeason season) {
        seasons.add((Season) season);
    }

    public static String abbreviatePosition(String position) {
        String result;
        if (position.equals("Point Guard")) {
            result = "PG";
        }
        else if (position.equals("Shooting Guard")) {
            result = "SG";
        }
        else if (position.equals("Small Forward")) {
            result = "SF";
        }
        else if (position.equals("Power Forward")) {
            result = "PF";
        }
        else if (position.equals("Center")) {
            result = "C";
        }
        else {
            result = "";
        }
        return result;
    }

    @Override
    public String toString() {
        return String.valueOf((int) getAttr("playerNumber")) + " " +
                getAttr("playerName") + " " +
                abbreviatePosition((String) getAttr("playerPosition"));
    }
}
