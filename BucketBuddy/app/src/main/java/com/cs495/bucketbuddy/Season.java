package com.cs495.bucketbuddy;

import java.util.ArrayList;

/*
Season is a wrapper around a list of games, which
is also able to aggregate the stats of those games.
 */
public abstract class Season {
    protected ArrayList<Game> games;

    /**
     * Returns a list containing the value of the given stat
     * for every game in the season.
     *
     * @param name the name of the stat
     * @return a list containing the stat value for each game in the season
     */
    public ArrayList<Object> getSeasonStat(String name) {
        ArrayList<Object> seasonStat = new ArrayList<Object>();
        for (Game game : games) {
            seasonStat.add(game.getStat(name));
        }
        return seasonStat;
    }

    /**
     * Adds a new game to the season.
     *
     * @param game the game to add
     */
    public void addGame(Game game) {
        games.add(game);
    }

    /**
     * Returns the list of games in the season.
     *
     * @return the list of games in the season
     */
    public ArrayList<Game> getGames() { return games; }

    /**
     * Returns the current game (last game in the season).
     *
     * @return the last game in the season
     */
    public Game getCurrentGame() {
        return games.get(games.size()-1);
    }
}
