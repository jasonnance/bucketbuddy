package com.cs495.bucketbuddy;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Extends StatEntity, providing functionality unique to
 * teams of basketball players.
 */
public class Team extends StatEntity{

    private ArrayList<Player> players;

    /**
     * Initializes a new team with no attributes,
     * seasons, or players and a null id.
     */
    public Team() {
        attrs = new HashMap<String,Object>();
        seasons = new ArrayList<Season>();

    }

    /**
     * Returns all the players on the team.
     *
     * @return a list of all players on the team
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Adds the given player to the team.
     *
     * @param player the player to add to the team
     */
    public void addPlayer(Player player) {
        players.add(player);
    }

    /**
     * Updates the player at the given index, replacing it with
     * the given player.
     *
     * @param index the index of the player to replace in the players array
     * @param player the new player
     */
    public void updatePlayer(int index, Player player) {
        players.set(index, player);
    }

    /**
     * Deletes the player at the given index in the team.
     *
     * @param index the index (in the players array) of the player to delete
     */
    public void deletePlayer(int index) {
        players.remove(index);
    }

    /**
     * Adds the given TeamSeason to the team's list of seasons.
     *
     * @param season the season to add
     */
    public void addSeason(TeamSeason season) {
        seasons.add((Season) season);
    }
}
