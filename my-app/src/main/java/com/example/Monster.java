package com.example;

import java.util.Random;

/**
 * Base Monster class that defines common properties and behaviors for all monsters
 */
public class Monster {
    private String name;
    private String type;
    private int x, y;
    private int hp;
    private int maxHp;
    private int attackPower;
    private int defenseValue;
    private int expValue;     // Experience granted when defeated
    private char symbol;      // Character representation on map
    private boolean isHostile;
    private String statusEffect; // Current status effect (poisoned, stunned, etc.)
    private int statusDuration;  // How many turns the status effect remains
    
    public Monster(String name, String type, int x, int y, int hp, int attackPower, 
                  int defenseValue, int expValue, char symbol, boolean isHostile) {
        this.name = name;
        this.type = type;
        this.x = x;
        this.y = y;
        this.hp = hp;
        this.maxHp = hp;
        this.attackPower = attackPower;
        this.defenseValue = defenseValue;
        this.expValue = expValue;
        this.symbol = symbol;
        this.isHostile = isHostile;
        this.statusEffect = "normal";
        this.statusDuration = 0;
    }
    
    
    public String getName() { return name; }
    public String getType() { return type; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAttackPower() { return attackPower; }
    public int getDefenseValue() { return defenseValue; }
    public int getExpValue() { return expValue; }
    public char getSymbol() { return symbol; }
    public boolean isHostile() { return isHostile; }
    public String getStatusEffect() { return statusEffect; }
    public int getStatusDuration() { return statusDuration; }
    
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    
    
    public void applyStatus(String status, int duration) {
        this.statusEffect = status;
        this.statusDuration = duration;
        System.out.println(name + " is now " + status + " for " + duration + " turns!");
    }
    
    public void updateStatus() {
        if (statusDuration > 0) {
            statusDuration--;
            
            
            switch (statusEffect) {
                case "poisoned":
                    takeDamage(1);
                    break;
                case "burning":
                    takeDamage(2);
                    break;
                
            }
            
            if (statusDuration == 0) {
                statusEffect = "normal";
                System.out.println(name + " recovered from status effect.");
            }
        }
    }
    
    
    public int calculateAttackDamage() {
        Random rand = new Random();
        
        int damage = attackPower + rand.nextInt(3) - 1;
        
        
        if (statusEffect.equals("weakened")) {
            damage = Math.max(1, damage / 2);
        }
        
        return Math.max(1, damage); 
    }
    
    public void takeDamage(int damage) {
        int actualDamage = Math.max(1, damage - defenseValue);
        hp -= actualDamage;
        System.out.println(name + " takes " + actualDamage + " damage!");
        
        if (hp <= 0) {
            hp = 0;
            System.out.println(name + " has been defeated!");
        }
    }
    
    public boolean isAlive() {
        return hp > 0;
    }
    
    
    public void moveTowards(int targetX, int targetY, char[][] map) {
        
        if (statusEffect.equals("stunned")) {
            return;
        }
        
        
        int dx = Integer.compare(targetX, x);
        int dy = Integer.compare(targetY, y);
        
        
        if (dx != 0 && isValidMove(x + dx, y, map)) {
            x += dx;
        } 
        
        else if (dy != 0 && isValidMove(x, y + dy, map)) {
            y += dy;
        }
        
        else if (isValidMove(x + dx, y + dy, map)) {
            x += dx;
            y += dy;
        }
    }
    
    private boolean isValidMove(int newX, int newY, char[][] map) {
        if (newX < 0 || newY < 0 || newY >= map.length || newX >= map[0].length) {
            return false;
        }
        
        char tile = map[newY][newX];
        return tile == '.' || tile == 'P';
    }
    
    
    public String getDescription() {
        String healthStatus;
        double healthPercent = (double) hp / maxHp * 100;
        
        if (healthPercent > 75) {
            healthStatus = "Healthy";
        } else if (healthPercent > 50) {
            healthStatus = "Injured";
        } else if (healthPercent > 25) {
            healthStatus = "Badly Wounded";
        } else {
            healthStatus = "Near Death";
        }
        
        return String.format("%s (%s) - %s [HP: %d/%d] %s", 
                              name, type, healthStatus, hp, maxHp,
                              statusEffect.equals("normal") ? "" : "[" + statusEffect + "]");
    }
}
