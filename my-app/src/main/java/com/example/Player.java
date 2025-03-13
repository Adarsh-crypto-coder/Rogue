package com.example;

import java.util.Random;

public class Player {
    private int x, y;
    private int hp;
    private int maxHp;
    private double hunger;
    private int floor;
    private int level;
    private int strength;
    private int gold;
    private int armor;
    private char[][] map;
    private String statusMessage;
    private Dungeon dungeon;
    private Random rand = new Random(); // Add Random for attack calculations
    private int xp;
    private int xpToNextLevel;

    public Player(int[] startPosition, char[][] map, Dungeon dungeon) {
        this.x = startPosition[0];
        this.y = startPosition[1];
        this.map = map;
        this.dungeon = dungeon;
        this.floor = 1;
        this.hp = 100;          // Initial HP
        this.maxHp = 100;
        this.hunger = 10.0;    // Initial Hunger
        this.level = 1;        // ✅ Player starts at Level 1
        this.strength = 10;     // Base Strength
        this.gold = 0;         // No gold at start
        this.armor = 0;        // No armor at start
        this.xp = 0;
        this.xpToNextLevel = 5; // need Exp
        this.statusMessage = "Welcome to the dungeon!";
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getHp() { return hp; }
    public double getHunger() { return hunger; }
    public int getLevel() { return level; }
    public int getFloor() { return floor; }
    public int getStrength() { return strength; }
    public int getGold() { return gold; }
    public int getArmor() { return armor; }
    public String getStatusMessage() { return statusMessage; }
    public int getXp() { return xp; }
    public int getXpToNextLevel() { return xpToNextLevel; }
    public int getMaxHp() { return maxHp; }

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

        if (tile == '#') {
            statusMessage = "You cannot move through walls!";
            return;
        }
    
        // Check if new position has a monster
        Monster monster = dungeon.getMonsterAt(newX, newY);
        if (monster != null && monster.isAlive()) {
            // Players and monsters damage each other during collisions
            int playerDamage = calculateAttackDamage();
            monster.takeDamage(playerDamage);
            statusMessage = "You attack " + monster.getName() + " for " + playerDamage + " damage!";
    
            int monsterDamage = monster.calculateAttackDamage();
            this.takeDamage(monsterDamage);
            statusMessage += " " + monster.getName() + " attacks you for " + monsterDamage + " damage!";
    
            // Check if a monster is dead
            if (!monster.isAlive()) {
                statusMessage += " You defeated " + monster.getName() + "!";
            }
    
            return; // Not moving when colliding with monsters
        }
    
        // Move player
        x = newX;
        y = newY;
        decreaseHunger();
    
        // Check for stairs or other special tiles
        if (tile == '>') {
            statusMessage = "Going down to the next floor!";
            floor++; 
            System.out.println("🔽 Moving to Level " + floor + "...");
            loadNewDungeon("levels/level" + floor + ".txt");
        } else if (tile == '<') {
            if (floor > 1) {
                statusMessage = "Going up to the previous floor!";
                floor--; 
                System.out.println("🔼 Moving to Level " + floor + "...");
                loadNewDungeon("levels/level" + floor + ".txt"); 
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
            this.gainXp(expGained);
            statusMessage = "You defeated " + monster.getName() + "! Gained " + goldGained + " gold and " + expGained + " XP.";

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
    
        this.floor = newDungeon.getLevelNumber();
    
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

    public int calculateAttackDamage() {
        return strength + rand.nextInt(3);
    }

    public void gainXp(int amount) {
        xp += amount;
        statusMessage = "You gained " + amount + " XP!";
    
        if (xp >= xpToNextLevel) {
            levelUp();
        }
    }
    
    // Level up logic
    private void levelUp() {
        level++;
        xp -= xpToNextLevel;
        xpToNextLevel = (int) (xpToNextLevel * 1.5);
        strength += 2;
        maxHp += 10;
        hp = maxHp;
        statusMessage = "You leveled up! You are now level " + level + "!";
    }

    // Objectizing Player Movement. bu Suhwan Kim. Feb 22
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    public void setMap(char[][] map) {
        this.map = map;
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void setFloor(int floor) {
        this.floor = floor; 
    }

    public void setLevel(int level) {
        this.level = level; 
    }
}
