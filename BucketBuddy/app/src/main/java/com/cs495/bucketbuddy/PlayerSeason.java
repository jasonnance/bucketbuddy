package com.cs495.bucketbuddy;

import java.util.ArrayList;

/**
 * Extends Season, providing functionality specific
 * to players.
 */
public class PlayerSeason extends Season {

    /**
     * Initializes a new PlayerSeason with no games.
     */
    public PlayerSeason() {
        games = new ArrayList<Game>();
    }

    /**
     * Adds a new PlayerGame to the season.
     *
     * @param game the PlayerGame to add.
     */
    public void addGame(PlayerGame game) {
        games.add(game);
    }

}
