package com.cs495.bucketbuddy;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

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
                "    FOREIGN KEY (seasonNumber,entityID) REFERENCES Season(seasonNumber,entityID)\n" +
                ");\n" +
                "CREATE TABLE GameStat (\n" +
                "    statName VARCHAR NOT NULL ,\n" +
                "    statVal VARCHAR NOT NULL ,\n" +
                "    gameNumber INTEGER NOT NULL ,\n" +
                "    seasonNumber INTEGER NOT NULL ,\n" +
                "    entityID INTEGER NOT NULL ,\n" +
                "    PRIMARY KEY (statName, statVal, gameNumber, seasonNumber, entityID),\n" +
                "    FOREIGN KEY (gameNumber,seasonNumber,entityID) REFERENCES Game(gameNumber,seasonNumber,entityID)\n" +
                ");\n" +
                "CREATE TABLE Season (\n" +
                "    seasonNumber INTEGER NOT NULL ,\n" +
                "    entityID INTEGER NOT NULL ,\n" +
                "    PRIMARY KEY (seasonNumber, entityID),\n" +
                "    FOREIGN KEY (entityID) REFERENCES StatEntity(entityID)\n" +
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
                "    FOREIGN KEY (entityID) REFERENCES StatEntity(entityID)\n" +
                ");";

        db.execSQL(CREATE_TABLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
    }

    public void addStatEntity(StatEntity entity) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Take care of the StatEntity table first
        String type = (entity instanceof Player) ? "'player'" : "'team'";
        ContentValues values = new ContentValues();
        values.put("type", type);

        // We need the id the database gave our new entity
        long newId = db.insert("StatEntity", null, values);

        // Add the entity's attributes to the StatEntityAttr table
        for (String attrName : entity.getAllAttrNames()) {
            ContentValues attrValues = new ContentValues();
            attrValues.put("attrName", attrName);
            attrValues.put("attrVal", serialize(entity.getAttr(attrName)));
            attrValues.put("entityID", newId);
            db.insert("StatEntityAttr", null, attrValues);
        }
        // TODO
        //for (Season season : entity)


    }

    /**
     * Turns the given object into a string for storage in the database.
     *
     * @param obj the object to be serialized
     * @return the string representation of the object
     */
    private String serialize(Object obj) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(obj);
            so.flush();
            return bo.toString();
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }

    private Object deserialize(String strObj) {
        try {
            byte b[] = strObj.getBytes();
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);
            return si.readObject();
        } catch (IOException e) {
            System.out.println(e);
            return null;
        } catch (ClassNotFoundException e) {
            System.out.println(e);
            return null;
        }
    }
}
