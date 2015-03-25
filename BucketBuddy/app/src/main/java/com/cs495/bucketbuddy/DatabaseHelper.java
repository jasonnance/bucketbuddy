package com.cs495.bucketbuddy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bucketbuddy.db";

    public DatabaseHelper(Context context, String name,
                          SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

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
}
