package com.cs495.bucketbuddy;

import java.util.HashMap;

/**
 * Extends Game, providing functionality unique to teams.
 */
public class TeamGame extends Game {

    /**
     * Initializes a new TeamGame with no stats.
     */
    public TeamGame() {
        stats = new HashMap<String,Object>();
    }
}
