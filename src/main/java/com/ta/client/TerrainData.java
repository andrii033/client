package com.ta.client;

import java.util.List;
import java.util.Map;

public class TerrainData {
    private String terrainType;
    private int xcoord;
    private int ycoord;

    private Map<String,String> enemies;

    public String getTerrainType() {
        return terrainType;
    }

    public void setTerrainType(String terrainType) {
        this.terrainType = terrainType;
    }

    public int getXcoord() {
        return xcoord;
    }

    public void setXcoord(int xcoord) {
        this.xcoord = xcoord;
    }

    public int getYcoord() {
        return ycoord;
    }

    public void setYcoord(int ycoord) {
        this.ycoord = ycoord;
    }

    public Map<String, String> getEnemies() {
        return enemies;
    }
}
