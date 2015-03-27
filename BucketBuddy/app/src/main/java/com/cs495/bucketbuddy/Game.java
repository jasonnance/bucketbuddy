package com.cs495.bucketbuddy;

import java.util.HashMap;

/**
 * Game is an abstract class providing functionality that is common to
 * TeamGame and PlayerGame, including stat storing and retrieval.
 */
public abstract class Game {

    protected HashMap<String,Object> stats;

    /**
     * Returns the value associated with a given stat name.
     *
     * @param name the name of the statistic to return
     * @return the statistic value if it is in the stats map; otherwise, returns null
     */
    public Object getStat(String name) {
        if (stats.containsKey(name)) {
            return stats.get(name);
        }
        else {
            return null;
        }
    }

    /**
     * Sets the given statistic name and value pair in the stats map.
     *
     * @param name the name of the statistic
     * @param value the value of the statistic
     */
    public void setStat(String name, Object value) {
        stats.put(name,value);
    }
}
