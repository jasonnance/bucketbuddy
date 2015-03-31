CREATE TABLE Game (
    seasonNumber INTEGER NOT NULL ,
    entityID INTEGER NOT NULL ,
    gameNumber INTEGER NOT NULL ,
    PRIMARY KEY (seasonNumber, entityID, gameNumber),
    FOREIGN KEY (seasonNumber,entityID) REFERENCES Season(seasonNumber,entityID) ON DELETE CASCADE
);
CREATE TABLE GameStat (
    statName VARCHAR NOT NULL ,
    statVal VARCHAR NOT NULL ,
    gameNumber INTEGER NOT NULL ,
    seasonNumber INTEGER NOT NULL ,
    entityID INTEGER NOT NULL ,
    PRIMARY KEY (statName, statVal, gameNumber, seasonNumber, entityID),
    FOREIGN KEY (gameNumber,seasonNumber,entityID) REFERENCES Game(gameNumber,seasonNumber,entityID) ON DELETE CASCADE
);
CREATE TABLE Season (
    seasonNumber INTEGER NOT NULL ,
    entityID INTEGER NOT NULL ,
    PRIMARY KEY (seasonNumber, entityID),
    FOREIGN KEY (entityID) REFERENCES StatEntity(entityID) ON DELETE CASCADE
);
CREATE TABLE StatEntity (
    entityID INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,
    type VARCHAR NOT NULL
);
CREATE TABLE StatEntityAttr (
    attrName VARCHAR NOT NULL ,
    attrVal VARCHAR NOT NULL ,
    entityID INTEGER NOT NULL ,
    PRIMARY KEY (attrName, attrVal, entityID),
    FOREIGN KEY (entityID) REFERENCES StatEntity(entityID) ON DELETE CASCADE
);
