package com.example;

import java.util.Random;

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
    private Random rand = new Random(); // Add Random for attack calculations

    public Player(int[] startPosition, char[][] map) {
        this.x = startPosition[0];
        this.y = startPosition[1];
        this.map = map;
        this.hp = 10;          // Initial HP
        this.hunger = 10.0;    // Initial Hunger
        this.level = 1;        // ✅ Player starts at Level 1
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
        if (hp <= 0) { 
            statusMessage = "You are dead! Cannot move.";
            return;
        }

        int newX = x;
        int newY = y;

        switch (direction) {
            case 'W': newY--; break; // Move up
            case 'A': newX--; break; // Move left
            case 'S': newY++; break; // Move down
            case 'D': newX++; break; // Move right
            default: return;
        }

        
        if (newX < 0 || newY < 0 || newY >= map.length || newX >= map[0].length) {
            statusMessage = "You cannot move outside the dungeon!";
            return;
        }

        char tile = map[newY][newX];

       
        if (map[y][x] == 'P') {
            map[y][x] = '.'; 
        }

        
        if (tile != '#') { 
            x = newX;
            y = newY;
            decreaseHunger();
        }

        
        if (tile == '>') {
            statusMessage = "Going down to the next floor!";
            level++; 
            System.out.println("🔽 Moving to Level " + level + "...");
            loadNewDungeon("levels/level" + level + ".txt");
        } else if (tile == '<') {
            if (level > 1) {
                statusMessage = "Going up to the previous floor!";
                level--; 
                System.out.println("🔼 Moving to Level " + level + "...");
                loadNewDungeon("levels/level" + level + ".txt"); 
            } else {
                statusMessage = "You are already at the top floor!";
            }
        }
    }

    private void decreaseHunger() {
        hunger -= 0.1; // Hunger slowly decreases
        if (hunger <= 0) {
            takeDamage(1); // Lose HP if starving
            hunger = 0;
            statusMessage = "You're starving!";
        }
    }

    /**
     * Modified takeDamage method for Player that accounts for armor
     */
    public void takeDamage(int amount) {
        
        int reducedDamage = Math.max(1, amount - (armor / 2));
        hp -= reducedDamage;
        
        if (hp <= 0) {
            hp = 0;
            statusMessage = "You have died!";
        } else {
            statusMessage = "You took " + reducedDamage + " damage!";
        }
    }

    /**
     * Method for attacking monsters
     */
    public void attackMonster(Monster monster) {
        if (monster == null || !monster.isAlive()) {
            statusMessage = "There is no monster there!";
            return;
        }
        
       
        int damage = strength + rand.nextInt(3);
        
        
        monster.takeDamage(damage);
        statusMessage = "You attack " + monster.getName() + " for " + damage + " damage!";
        
        
        if (!monster.isAlive()) {
            
            int expGained = monster.getExpValue();
            int goldGained = monster.getExpValue() / 3;
            
            this.gold += goldGained;
            statusMessage = "You defeated " + monster.getName() + "! Gained " + goldGained + " gold.";
            
            
            if (rand.nextDouble() < 0.25) {
                statusMessage += " You found an item!";
                
            }
        }
    }

    private void loadNewDungeon(String levelFile) {
        System.out.println("🔄 Loading new dungeon from: " + levelFile);
    
        Dungeon newDungeon = new Dungeon(levelFile); 
    
        if (newDungeon.getMap() == null || newDungeon.getMap().length == 0) {
            System.out.println("❌ Error: Failed to load new dungeon!");
            statusMessage = "Dungeon load failed!";
            return;
        }
    
        
        this.level = newDungeon.getLevelNumber();
    
        
        if (map[y][x] == '@') {
            map[y][x] = '.';
        }
    
        
        this.map = newDungeon.getMap();
        int[] startPos = newDungeon.getPlayerStartPosition();
        this.x = startPos[0];
        this.y = startPos[1];
    
        System.out.println("✅ Successfully loaded new dungeon: " + levelFile);
        statusMessage = "You have entered Level " + level + "!";
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