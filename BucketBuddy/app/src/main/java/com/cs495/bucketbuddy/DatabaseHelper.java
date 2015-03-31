package com.cs495.bucketbuddy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

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
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
        String CREATE_TABLES = "CREATE TABLE Game (\n" +
                "    seasonNumber INTEGER NOT NULL ,\n" +
                "    entityID INTEGER NOT NULL ,\n" +
                "    gameNumber INTEGER NOT NULL ,\n" +
                "    PRIMARY KEY (seasonNumber, entityID, gameNumber),\n" +
                "    FOREIGN KEY (seasonNumber,entityID) REFERENCES Season(seasonNumber,entityID) ON DELETE CASCADE\n" +
                ");\n" +
                "CREATE TABLE GameStat (\n" +
                "    statName VARCHAR NOT NULL ,\n" +
                "    statVal VARCHAR NOT NULL ,\n" +
                "    gameNumber INTEGER NOT NULL ,\n" +
                "    seasonNumber INTEGER NOT NULL ,\n" +
                "    entityID INTEGER NOT NULL ,\n" +
                "    PRIMARY KEY (statName, statVal, gameNumber, seasonNumber, entityID),\n" +
                "    FOREIGN KEY (gameNumber,seasonNumber,entityID) REFERENCES Game(gameNumber,seasonNumber,entityID) ON DELETE CASCADE\n" +
                ");\n" +
                "CREATE TABLE Season (\n" +
                "    seasonNumber INTEGER NOT NULL ,\n" +
                "    entityID INTEGER NOT NULL ,\n" +
                "    PRIMARY KEY (seasonNumber, entityID),\n" +
                "    FOREIGN KEY (entityID) REFERENCES StatEntity(entityID) ON DELETE CASCADE\n" +
                ");\n" +
                "CREATE TABLE StatEntity (\n" +
                "    entityID INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,\n" +
                "    type VARCHAR NOT NULL\n" +
                ");\n" +
                "CREATE TABLE StatEntityAttr (\n" +
                "    attrName VARCHAR NOT NULL ,\n" +
                "    attrVal VARCHAR NOT NULL ,\n" +
                "    entityID INTEGER NOT NULL ,\n" +
                "    PRIMARY KEY (attrName, attrVal, entityID),\n" +
                "    FOREIGN KEY (entityID) REFERENCES StatEntity(entityID) ON DELETE CASCADE\n" +
                ");";

        db.execSQL(CREATE_TABLES);
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
            attrValues.put("attrName", attrName);
            attrValues.put("attrVal", serialize(entity.getAttr(attrName)));
            attrValues.put("entityID", entity.getId());
            db.replace("StatEntityAttr", null, attrValues);
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
            attrValues.put("attrName", "playerIds");
            attrValues.put("attrVal", serialize(((Team) entity).getPlayerIds()));
            attrValues.put("entityID", entity.getId());
            db.replace("StatEntityAttr", null, attrValues);
        }

        // Add all the entity's seasons to the Season table
        ArrayList<Season> seasons = entity.getSeasons();
        ContentValues seasonValues = new ContentValues();
        for (int i = 0; i < seasons.size(); i++) {
            seasonValues.put("seasonNumber", i);
            seasonValues.put("entityID", entity.getId());
            db.replace("Season", null, seasonValues);
            seasonValues.clear();

            // Add all the season's games to the Game table
            ArrayList<Game> games = seasons.get(i).getGames();
            ContentValues gameValues = new ContentValues();
            for (int j = 0; j < games.size(); j++) {
                gameValues.put("seasonNumber", i);
                gameValues.put("entityID", entity.getId());
                gameValues.put("gameNumber", j);
                db.replace("Game", null, gameValues);
                gameValues.clear();

                // Add all the game's stats to the GameStat table
                ContentValues gameStats = new ContentValues();
                for (String statName : games.get(j).getAllStatNames()) {
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
                "WHERE entityID =" + String.valueOf(entityId), null);

        // Create the entity as required; return null if the given id didn't bring
        // up a row in the database
        StatEntity entity;
        if (typeCur.moveToFirst()) {
            String type = typeCur.getString(0);
            entity = (type.equals("player")) ? new Player() : new Team();
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
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Team> allTeams = new ArrayList<>();
        Cursor teamFinderCur = db.rawQuery("SELECT entityID FROM StatEntity" +
                "WHERE type = 'team'", null);

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
        Cursor attrCur = db.rawQuery(
                "SELECT attrName, attrVal FROM StatEntityAttr" +
                        "WHERE entityID =" + String.valueOf(entity.getId()), null);
        if (attrCur.moveToFirst()) {
            do {
                String attrName = attrCur.getString(0);
                String serializedAttrVal = attrCur.getString(1);
                Object attrVal = deserialize(serializedAttrVal);

                switch(attrName) {
                    // If the entity is a player, we need to set its teamId
                    case ("teamId"):
                        ((Player) entity).setTeamId((long) attrVal);
                        break;
                    // If the entity is a team, we need to set its playerIds
                    case ("playerIds"):
                        for (Long playerId : (ArrayList<Long>) attrVal) {
                            ((Team) entity).addPlayerId(playerId);
                        }
                        break;
                // Otherwise, this is a normal attribute
                    default:
                        entity.setAttr(attrName, attrVal);
                        break;
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
                "WHERE entityID =" + String.valueOf(entity.getId()), null);
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
                "FROM Game WHERE seasonNumber =" + String.valueOf(seasonNumber) +
                "AND entityID =" + String.valueOf(entityId), null);
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
                "FROM GameStat WHERE gameNumber =" + String.valueOf(gameNumber) +
                "AND seasonNumber =" + String.valueOf(seasonNumber) +
                "AND entityId =" + String.valueOf(entityId), null);
        if (statCur.moveToFirst()) {
            do {
                String statName = statCur.getString(0);
                String serializedStatVal = statCur.getString(1);
                Object statVal = deserialize(serializedStatVal);
                game.setStat(statName, statVal);
            } while (statCur.moveToNext());
        }
        statCur.close();
        return game;
    }

    /**
     * Turns the given object into a string for storage in the database.
     *
     * @param obj the object to be serialized
     * @return the string representation of the object
     */
    @Nullable
    private String serialize(Object obj) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(obj);
            so.flush();
            return bo.toString();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Converts the given serialized object string back into an object
     *
     * @param strObj the serialized string to convert to an object
     * @return the deserialized object
     */
    @Nullable
    private Object deserialize(String strObj) {
        try {
            byte b[] = strObj.getBytes();
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);
            return si.readObject();
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
