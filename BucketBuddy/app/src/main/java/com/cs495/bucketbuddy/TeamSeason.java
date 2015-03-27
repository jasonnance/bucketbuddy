package com.cs495.bucketbuddy;

import java.util.ArrayList;

/**
 * Extends Season, providing functionality unique to teams.
 */
public class TeamSeason extends Season {

    /**
     * Initializes a new TeamSeason with no games.
     */
    public TeamSeason() {
        games = new ArrayList<Game>();
    }

    /**
     * Adds a new TeamGame to the season.
     *
     * @param game the game to add
     */
    public void addGame(TeamGame game) {
        games.add((Game) game);
    }
}
