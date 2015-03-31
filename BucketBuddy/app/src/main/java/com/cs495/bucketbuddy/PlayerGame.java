package com.cs495.bucketbuddy;

import java.util.HashMap;

/**
 * Extends Game, providing functionality specific to players.
 */
public class PlayerGame extends Game {

    /**
     * Intitializes a new PlayerGame with no statistics.
     */
    public PlayerGame() {
        stats = new HashMap<String,Object>();
    }
}
