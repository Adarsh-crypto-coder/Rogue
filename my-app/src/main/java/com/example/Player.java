package com.example;

public class Player {
    private int x, y;
    private int hp;
    private double hunger;
    private int level;
    private int strength;
    private int gold;
    private int armor;
    private char[][] map;
    private String statusMessage;

    public Player(int[] startPosition, char[][] map) {
        this.x = startPosition[0];
        this.y = startPosition[1];
        this.map = map;
        this.hp = 10;          // Initial HP
        this.hunger = 10.0;    // Initial Hunger
        this.level = 1;        // Player starts at level 1
        this.strength = 5;     // Base Strength
        this.gold = 0;         // No gold at start
        this.armor = 0;        // No armor at start
        this.statusMessage = "Welcome to the dungeon!"; 
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getHp() { return hp; }
    public double getHunger() { return hunger; }
    public int getLevel() { return level; }
    public int getStrength() { return strength; }
    public int getGold() { return gold; }
    public int getArmor() { return armor; }
    public String getStatusMessage() { return statusMessage; }

    public void move(char direction) {
        int newX = x;
        int newY = y;

        switch (direction) {
            case 'W': newY--; break; // Move up
            case 'A': newX--; break; // Move left
            case 'S': newY++; break; // Move down
            case 'D': newX++; break; // Move right
            default: return;
        }

        char tile = map[newY][newX];

        // Prevent movement into walls
        if (tile != '#') { 
            x = newX;
            y = newY;
            decreaseHunger(); // Player gets hungrier with movement
        }

        // Handle Stairs (`>` for down, `<` for up)
        if (tile == '>') {
            statusMessage = "Going down to the next floor!";
            level++; // Increase level when going down
            loadNewDungeon("levels/level2.txt");
        } else if (tile == '<') {
            statusMessage = "Going up to the previous floor!";
            level = Math.max(1, level - 1); // Prevents negative levels
            loadNewDungeon("levels/level1.txt");
        }
    }

    private void decreaseHunger() {
        hunger -= 0.1; // Hunger slowly decreases
        if (hunger <= 0) {
            hp--; // Lose HP if starving
            hunger = 0;
            statusMessage = "You're starving!";
        }
    }

    private void loadNewDungeon(String levelFile) {
        Dungeon newDungeon = new Dungeon(levelFile);
        map = newDungeon.getMap();
        int[] startPos = newDungeon.getPlayerStartPosition();
        x = startPos[0];
        y = startPos[1];
    }

    public void addGold(int amount) {
        gold += amount;
        statusMessage = "You found " + amount + " gold!";
    }

    public void equipArmor(int armorValue) {
        armor += armorValue;
        statusMessage = "You equipped armor! Defense +" + armorValue;
    }

    public void increaseStrength(int amount) {
        strength += amount;
        statusMessage = "You feel stronger! Strength +" + amount;
    }
}
