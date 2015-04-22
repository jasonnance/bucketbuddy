package com.cs495.bucketbuddy;

import java.io.Serializable;

/**
 * Class to store the coordinates of a shot and whether it was made or not
 */
public class Shot implements Serializable{
    private final int x;
    private final int y;
    private final boolean made;

    public Shot(int x, int y, boolean made) {
        this.x = x;
        this.y = y;
        this.made = made;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public boolean getMade() { return made; }
}
