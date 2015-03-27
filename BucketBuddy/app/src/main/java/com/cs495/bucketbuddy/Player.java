package com.cs495.bucketbuddy;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Player extends StatEntity as needed to represent
 * an individual basketball player.
 */
public class Player extends StatEntity {

    private Team team;

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
    public Team getTeam() {
        return team;
    }

    /**
     * Sets the team for which the player plays.
     *
     * @param team the player's new team
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * Adds a new PlayerSeason to the player's list
     * of seasons.
     *
     * @param season the season to add
     */
    public void addSeason(PlayerSeason season) {
        seasons.add((Season) season);
    }
}
