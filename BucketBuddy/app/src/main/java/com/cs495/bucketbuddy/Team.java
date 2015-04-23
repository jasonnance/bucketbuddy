package com.cs495.bucketbuddy;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Extends StatEntity, providing functionality unique to
 * teams of basketball players.
 */
public class Team extends StatEntity{

    private ArrayList<Long> playerIds;

    public static final String[] REQUIRED_STATS = new String[] {"points","rebounds","assists",
            "ftm","fta","fouls","turnovers","blocks","steals","fgm","fga","2pm","2pa","3pm","3pa",
            "oppScore","shotCoords"};
    public static final String[] GRAPHABLE_STATS = new String[] {"points","rebounds","assists",
            "ftm","fta","fouls","turnovers","blocks","steals","fgm","fga","2pm","2pa","3pm","3pa",
            "oppScore"};

    /**
     * Initializes a new team with no attributes,
     * seasons, or players and a null id.
     */
    public Team() {
        attrs = new HashMap<String,Object>();
        seasons = new ArrayList<Season>();
        playerIds = new ArrayList<Long>();
    }

    /**
     * Returns all the players on the team.
     *
     * @return a list of all players on the team
     */
    public ArrayList<Long> getPlayerIds() {
        return playerIds;
    }

    /**
     * Adds the given playerId to the team.
     *
     * @param playerId the id of the player to add to the team
     */
    public void addPlayerId(long playerId) {
        playerIds.add(playerId);
    }

    /**
     * Updates the player at the given index, replacing it with
     * the given player.
     *
     * @param index the index of the player to replace in the players array
     * @param playerId the id of the new player
     */
    public void updatePlayer(int index, long playerId) {
        playerIds.set(index, playerId);
    }

    /**
     * Deletes the player at the given index in the team.
     *
     * @param index the index (in the players array) of the player to delete
     */
    public void deletePlayer(int index) {
        playerIds.remove(index);
    }

    /**
     * Adds the given TeamSeason to the team's list of seasons.
     *
     * @param season the season to add
     */
    public void addSeason(TeamSeason season) {
        seasons.add(season);
    }
}
