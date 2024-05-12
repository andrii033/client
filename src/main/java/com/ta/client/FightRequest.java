package com.ta.client;

public class FightRequest {
    private String characterName;
    private int characterHp;
    private int characterLatestDam;
    private int enemyId;
    private int enemyHp;
    private int enemyLatestDam;

    public String getCharacterName() {
        return characterName;
    }

    public int getCharacterHp() {
        return characterHp;
    }

    public int getCharacterLatestDam() {
        return characterLatestDam;
    }

    public int getEnemyId() {
        return enemyId;
    }

    public int getEnemyHp() {
        return enemyHp;
    }

    public int getEnemyLatestDam() {
        return enemyLatestDam;
    }
}

