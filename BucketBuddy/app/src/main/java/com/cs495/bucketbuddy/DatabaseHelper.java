package com.cs495.bucketbuddy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Provides the interface into the database, translating back and forth
 * between the Java objects representing Players and Teams and the data about them
 * in the database.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bucketbuddy.db";

    public DatabaseHelper(Context context, String name,
                          SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    /**
     * Creates all the tables and constraints for the Bucket Buddy database
     * if not already present on the device.
     * @param db the pointer to the database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Enable foreign key constraints
        db.execSQL("PRAGMA foreign_keys=ON;");

        String CREATE_GAME = "CREATE TABLE Game (\n" +
                "    seasonNumber INTEGER NOT NULL ,\n" +
                "    entityID INTEGER NOT NULL ,\n" +
                "    gameNumber INTEGER NOT NULL ,\n" +
                "    PRIMARY KEY (seasonNumber, entityID, gameNumber),\n" +
                "    FOREIGN KEY (seasonNumber,entityID) REFERENCES Season(seasonNumber,entityID) ON DELETE CASCADE\n" +
                ");\n";
        String CREATE_GAMESTAT = "CREATE TABLE GameStat (\n" +
                "    statName VARCHAR NOT NULL ,\n" +
                "    statVal BLOB NOT NULL ,\n" +
                "    gameNumber INTEGER NOT NULL ,\n" +
                "    seasonNumber INTEGER NOT NULL ,\n" +
                "    entityID INTEGER NOT NULL ,\n" +
                "    PRIMARY KEY (statName, gameNumber, seasonNumber, entityID),\n" +
                "    FOREIGN KEY (gameNumber,seasonNumber,entityID) REFERENCES Game(gameNumber,seasonNumber,entityID) ON DELETE CASCADE\n" +
                ");\n";
        String CREATE_SEASON =
                "CREATE TABLE Season (\n" +
                "    seasonNumber INTEGER NOT NULL ,\n" +
                "    entityID INTEGER NOT NULL ,\n" +
                "    PRIMARY KEY (seasonNumber, entityID),\n" +
                "    FOREIGN KEY (entityID) REFERENCES StatEntity(entityID) ON DELETE CASCADE\n" +
                ");\n";
        String CREATE_STATENTITY =
                "CREATE TABLE StatEntity (\n" +
                "    entityID INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,\n" +
                "    type VARCHAR NOT NULL\n" +
                ");\n";
        String CREATE_STATENTITYATTR =
                "CREATE TABLE StatEntityAttr (\n" +
                "    attrName VARCHAR NOT NULL ,\n" +
                "    attrVal BLOB NOT NULL ,\n" +
                "    entityID INTEGER NOT NULL ,\n" +
                "    PRIMARY KEY (attrName, entityID),\n" +
                "    FOREIGN KEY (entityID) REFERENCES StatEntity(entityID) ON DELETE CASCADE\n" +
                ");";

        db.execSQL(CREATE_GAME);
        db.execSQL(CREATE_GAMESTAT);
        db.execSQL(CREATE_SEASON);
        db.execSQL(CREATE_STATENTITY);
        db.execSQL(CREATE_STATENTITYATTR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
    }

    /**
     * Adds a new StatEntity (player or team) to the database).  Creates the StatEntity
     * in the database and assigns its id to the java object.
     *
     * @param entity the entity to be added
     * @return the given entity with its id set to the value given to it in the database
     */
    public StatEntity addStatEntity(StatEntity entity) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Add the new entity to the StatEntity table
        String type = (entity instanceof Player) ? "player" : "team";
        ContentValues values = new ContentValues();
        values.put("type", type);
        // We need the id the database gave our new entity
        long newId = db.insert("StatEntity", null, values);

        // Update the entity's id to show that it now exists in the database
        entity.setId(newId);

        // Make sure we've got all the new entity's info in the database
        updateStatEntity(entity);

        return entity;
    }

    /**
     * Updates the given StatEntity's information in the database.
     * All Attrs, Seasons, Games, and GameStats will be "INSERT OR UPDATE"ed, meaning
     * they will be added if not present, and their values will be overwritten if they are
     * already there.  This method assumes the StatEntity has already been added to the db
     * via the addStatEntity method.
     *
     * @param entity the entity to be updated
     */
    public void updateStatEntity(StatEntity entity) {
        SQLiteDatabase db = this.getWritableDatabase();

        String type = (entity instanceof Player) ? "player" : "team";

        // Enclose all inserts in a transaction for performance purposes
        db.beginTransaction();
        ContentValues attrValues = new ContentValues();
        // Add the entity's attributes to the StatEntityAttr table
        for (String attrName : entity.getAllAttrNames()) {
            Log.d("dbDebug", "attrName: " + attrName);
            Log.d("dbDebug", "attrVal: " + entity.getAttr(attrName));
            Log.d("dbDebug", "serial attrVal: "+ serialize(entity.getAttr(attrName)));
            Log.d("dbDebug", "entityID: " + entity.getId());
            attrValues.put("attrName", attrName);
            attrValues.put("attrVal", serialize(entity.getAttr(attrName)));
            attrValues.put("entityID", entity.getId());
            long success = db.replace("StatEntityAttr", null, attrValues);
            Log.d("dbDebug", "replace returned " + String.valueOf(success));
            attrValues.clear();
        }

        // If the entity is a player, we need to add their team as an attr;
        // otherwise, we need to add the team's players as an attr.
        if (type.equals("player")) {
            attrValues.put("attrName", "teamId");
            attrValues.put("attrVal", serialize(((Player) entity).getTeamId()));
            attrValues.put("entityID", entity.getId());
            db.replace("StatEntityAttr", null, attrValues);
        }
        else {
            Log.d("dbDebugPlayer", "update got player ids of size: " + String.valueOf(((Team) entity).getPlayerIds().size()));
            Log.d("dbDebugPlayer", "serial/deserial returned: " + ((ArrayList<Long>) deserialize(serialize(((Team) entity).getPlayerIds()))).size());
            attrValues.put("attrName", "playerIds");
            attrValues.put("attrVal", serialize(((Team) entity).getPlayerIds()));
            attrValues.put("entityID", entity.getId());
            long playerIdSuccess = db.replace("StatEntityAttr", null, attrValues);
            Log.d("dbDebugPlayer", "playerId insert returned " + String.valueOf(playerIdSuccess));
        }

        // Add all the entity's seasons to the Season table
        ArrayList<Season> seasons = entity.getSeasons();
        ContentValues seasonValues = new ContentValues();
        for (int i = 0; i < seasons.size(); i++) {
            //Log.d("gamescreenDebug", "updating season " + String.valueOf(i));
            seasonValues.put("seasonNumber", i);
            seasonValues.put("entityID", entity.getId());
            db.replace("Season", null, seasonValues);
            seasonValues.clear();

            // Add all the season's games to the Game table
            ArrayList<Game> games = seasons.get(i).getGames();
            ContentValues gameValues = new ContentValues();
            for (int j = 0; j < games.size(); j++) {
                //Log.d("gamescreenDebug", "updating game " + String.valueOf(j));
                gameValues.put("seasonNumber", i);
                gameValues.put("entityID", entity.getId());
                gameValues.put("gameNumber", j);
                db.replace("Game", null, gameValues);
                gameValues.clear();

                // Add all the game's stats to the GameStat table
                ContentValues gameStats = new ContentValues();
                for (String statName : games.get(j).getAllStatNames()) {
                    //Log.d("gamescreenDebug", "updating game " + String.valueOf(j) + " with game stat " + statName);
                    gameStats.put("statName", statName);
                    gameStats.put("statVal", serialize(games.get(j).getStat(statName)));
                    gameStats.put("gameNumber", j);
                    gameStats.put("seasonNumber", i);
                    gameStats.put("entityID", entity.getId());
                    db.replace("GameStat", null, gameStats);
                    gameStats.clear();
                }
            }
        }

        // Clean up
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    /**
     * Deletes the StatEntity with the given id from the database.  All related
     * Attrs, Seasons, Games, and GameStats will be deleted as well.
     *
     * @param id the id of the StatEntity to delete
     */
    public void deleteStatEntity(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Perform the delete; since all foreign keys are set to
        // "ON DELETE CASCADE", this will not cause a foreign key violation
        // and will cascade the deletes through the tables.
        String whereClause = "entityID =" + String.valueOf(id);
        db.delete("StatEntity", whereClause, null);

        db.close();
    }

    /**
     * Pulls the StatEntity with the given id from the database.
     *
     * @param entityId the id of the StatEntity to retrieve
     * @return the StatEntity with all attrs, seasons, games, and stats from the database
     */
    @Nullable
    public StatEntity getStatEntity(long entityId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Determine whether we're creating a team or a player
        Cursor typeCur = db.rawQuery("SELECT type FROM StatEntity" +
                " WHERE entityID =" + String.valueOf(entityId), null);

        // Create the entity as required; return null if the given id didn't bring
        // up a row in the database
        StatEntity entity;
        if (typeCur.moveToFirst()) {
            String type = typeCur.getString(0);
            entity = (type.equals("player")) ? new Player() : new Team();
            entity.setId(entityId);
        }
        else { return null; }
        typeCur.close();

        // Load attributes into the entity
        entity = loadAttrs(db, entity);

        // Load all seasons in the database into the entity -- this will also
        // load all games into each season, and all gamestats into each game
        entity = loadSeasons(db, entity);

        // clean up and return the finished entity
        db.close();
        return entity;
    }

    /**
     * Returns all teams currently present in the database.
     *
     * @return an arraylist of all teams in the database
     */
    public ArrayList<Team> getAllTeams() {
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<Team> allTeams = new ArrayList<Team>();
        Cursor teamFinderCur = db.rawQuery("SELECT entityID FROM StatEntity" +
                " WHERE type == 'team'", null);

        if (teamFinderCur.moveToFirst()) {
            do {
                // Load each team found in the database
                long entityId = teamFinderCur.getLong(0);
                allTeams.add((Team) getStatEntity(entityId));
            } while (teamFinderCur.moveToNext());
        }
        return allTeams;
    }

    /**
     * Loads all attributes describing the given entity into that entity.
     * This includes the entity's teamId if the entity is a player or the
     * entity's playerIds if the entity is a team.
     *
     * @param db the pointer to the database
     * @param entity the entity into which to load attributes
     * @return the entity with all attributes filled out
     */
    private StatEntity loadAttrs(SQLiteDatabase db, StatEntity entity) {
        Log.d("dbDebug", "loading attrs for entity " + entity.getId());
        Cursor attrCur = db.rawQuery(
                "SELECT attrName, attrVal FROM StatEntityAttr" +
                        " WHERE entityID =" + String.valueOf(entity.getId()), null);
        if (attrCur.moveToFirst()) {
            do {
                String attrName = attrCur.getString(0);
                byte[] serializedAttrVal = attrCur.getBlob(1);
                Object attrVal = deserialize(serializedAttrVal);
                Log.d("dbDebug", "attrName: " + attrName);
                Log.d("dbDebug", "serialAttrVal: " + serializedAttrVal);
                Log.d("dbDebug", "attrVal: " + attrVal);

                // If the entity is a player, we need to set its teamId
                if(attrName.equals("teamId")) {
                    ((Player) entity).setTeamId((Long) attrVal);
                }
                // If the entity is a team, we need to set its playerIds (if it has any)
                else if(attrName.equals("playerIds")) {
                    if (attrVal != null) {
                        for (Long playerId : (ArrayList<Long>) attrVal) {
                            ((Team) entity).addPlayerId(playerId);
                        }
                    }
                }
                // Otherwise, this is a normal attribute
                else{
                    entity.setAttr(attrName, attrVal);
                }

            } while (attrCur.moveToNext());
        }
        attrCur.close();
        return entity;
    }

    /**
     * Loads all Seasons into the given entity.  This entails loading all Games
     * into each Season and all GameStats into each Game.
     *
     * @param db the pointer to the database
     * @param entity the entity into which to load seasons
     * @return the entity with all seasons, games, and gamestats filled out
     */
    private StatEntity loadSeasons(SQLiteDatabase db, StatEntity entity) {
        Cursor seasonCur = db.rawQuery("SELECT seasonNumber FROM Season" +
                " WHERE entityID =" + String.valueOf(entity.getId()), null);
        if (seasonCur.moveToFirst()) {
            do {
                int seasonNumber = seasonCur.getInt(0);
                Season season = (entity instanceof Player) ?
                        new PlayerSeason() : new TeamSeason();

                season = loadGames(db, season, seasonNumber, entity.getId());
                entity.addSeason(season);
            } while (seasonCur.moveToNext());
        }
        seasonCur.close();
        return entity;
    }

    /**
     * Loads all games into the given season.  This entails loading all GameStats into each
     * Game.
     *
     * @param db the pointer to the database
     * @param season the season into which to load games
     * @param seasonNumber the number of the season -- part of the games' primary key
     * @param entityId the id of the entity to which the season belongs -- part of the games' primary key
     * @return the season with all games and gamestats filled out
     */
    private Season loadGames(SQLiteDatabase db, Season season, int seasonNumber, long entityId) {
        Cursor gameCur = db.rawQuery("SELECT gameNumber" +
                " FROM Game WHERE seasonNumber =" + String.valueOf(seasonNumber) +
                " AND entityID =" + String.valueOf(entityId), null);
        if (gameCur.moveToFirst()) {
            do {
                int gameNumber = gameCur.getInt(0);
                Game game = ((season instanceof PlayerSeason) ?
                        new PlayerGame() : new TeamGame());
                game = loadStats(db, game, gameNumber, seasonNumber, entityId);
                season.addGame(game);
            } while (gameCur.moveToNext());
        }
        gameCur.close();
        return season;
    }

    /**
     * Loads all gamestats into the given game.
     *
     * @param db the pointer to the database
     * @param game the game into which to load gamestats
     * @param gameNumber the number of the game -- part of the gamestats' primary key
     * @param seasonNumber the number of the season -- part of the gamestats' primary key
     * @param entityId the id of the entity to which the season/game belong -- part of the gamestats' primary key
     * @return the game with all gamestats filled out
     */
    private Game loadStats(SQLiteDatabase db, Game game, int gameNumber, int seasonNumber, long entityId) {
        Cursor statCur = db.rawQuery("SELECT statName, statVal" +
                " FROM GameStat WHERE gameNumber =" + String.valueOf(gameNumber) +
                " AND seasonNumber =" + String.valueOf(seasonNumber) +
                " AND entityId =" + String.valueOf(entityId), null);
        if (statCur.moveToFirst()) {
            do {
                String statName = statCur.getString(0);
                byte[] serializedStatVal = statCur.getBlob(1);
                Object statVal = deserialize(serializedStatVal);
                game.setStat(statName, statVal);
            } while (statCur.moveToNext());
        }
        statCur.close();
        return game;
    }

    /**
     * Turns the given object into a byte array for storage in the database.
     *
     * @param obj the object to be serialized
     * @return the byte array representation of the object
     */
    @Nullable
    private byte[] serialize(Object obj) {
        try {

            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(obj);
            so.close();
            return bo.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Converts the given serialized object byte array back into an object
     *
     * @param strObj the serialized bytes to convert to an object
     * @return the deserialized object
     */
    @Nullable
    private Object deserialize(byte[] strObj) {
        try {
            ByteArrayInputStream bi = new ByteArrayInputStream(strObj);
            ObjectInputStream si = new ObjectInputStream(bi);
            return si.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
