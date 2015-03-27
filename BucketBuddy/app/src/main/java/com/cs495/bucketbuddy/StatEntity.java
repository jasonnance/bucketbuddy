package com.cs495.bucketbuddy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * StatEntity is an abstract class generalizing the characteristics of
 * Players and Teams.  A StatEntity has a Map containing key/value pairs of stat names
 * and stat values and a list of seasons.  It also has an id, which should correspond
 * to the entity's id in the database.
 */
public abstract class StatEntity {
    protected long id;
    protected HashMap<String,Object> attrs;
    protected ArrayList<Season> seasons;

    /**
     * Returns the last game in the last season, which is the current game
     * the StatEntity is involved in.
     *
     * @return the current game
     */
    protected Game getCurrentGame() {
        Season currentSeason = seasons.get(seasons.size()-1);
        Game currentGame = currentSeason.getCurrentGame();
        return currentGame;
    }

    /**
     * Returns the StatEntity's database id for uniquely identifying it.
     *
     * @return the entity's id in the database; will be null if the entity has
     * not been added to the database yet
     */
    public long getId() {
        return id;
    }

    /**
     * Set the StatEntity's database id.  NOTE: this method should only be called
     * by the DatabaseHelper during the process of inserting the entity into the
     * database for the first time.  Using it any other time will create a
     * conflict between the object and its counterpart in the database.
     *
     * @param id the new id of the entity
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the attribute value corresponding to the given name.
     *
     * @param name the name of the attribute
     * @return the current value of the attribute (possibly null)
     */
    public Object getAttr(String name) {
        return attrs.get(name);
    }

    /**
     * Returns the names of all attributes currently defined for the StatEntity.
     *
     * @return a set of the names of all stats defined for the entity
     */
    public Set<String> getAllAttrNames() { return attrs.keySet(); }

    /**
     * Sets the value of the attribute corresponding to the given name.
     *
     * @param name the name of the attribute
     * @param value the new value of the attribute
     */
    public void setAttr(String name, Object value) {
        attrs.put(name, value);
    }

    /**
     * Sets the value of the statistic corresponding to the given name in the
     * entity's current game.
     *
     * @param name the name of the statistic
     * @param value the new value of the statistic
     */
    public void setGameStat(String name, Object value) {
        getCurrentGame().setStat(name, value);
    }

    /**
     * Returns the value of the statistic corresponding to the given name in
     * the entity's current game.
     *
     * @param name the name of the statistic
     * @return the current value of the statistic (possibly null)
     */
    public Object getGameStat(String name) { return getCurrentGame().getStat(name); }

    /**
     * Adds a new season to the list of seasons.
     *
     * @param season the season to add
     */
    public void addSeason(Season season) {
        seasons.add(season);
    }

    /** Returns all of the entity's seasons.
     *
     * @return a list of all the entity's seasons
     */
    public ArrayList<Season> getSeasons() { return seasons; }

    /**
     * Returns a nested array containing the value of the given stat for every game in
     * every season.
     *
     * @param name the name of the statistic
     * @return an array containing an array of statistic values for every season
     */
    public ArrayList<ArrayList<Object>> getCareerStat(String name) {
        ArrayList<ArrayList<Object>> careerStat = new ArrayList<ArrayList<Object>>();
        for (Season season : seasons) {
            careerStat.add(season.getSeasonStat(name));
        }
        return careerStat;
    }

}
