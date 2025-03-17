package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Player {
    private int x, y;
    private int hp;
    private int maxHp;
    private int baseMaxHp;
    private double hunger;
    private double maxHunger;
    private int level;
    private int strength;
    private int baseStrength;
    private int berserkStrength;
    private int gentleStrength;
    private int gold;
    private int armor;
    private int exp;
    private int expToNextLevel;
    private char[][] map;
    private String statusMessage;
    private Random rand = new Random();
    private int currentLevel;
    
    // Inventory fields
    private List<Item> inventory;
    private Item equippedWeapon;
    private Item equippedArmor;
    private int inventoryMaxSize = 10;

    // Identified Scrolls
    private Map<String, String> identifiedScrolls;

    private Dungeon dungeon;

    public Player(int[] startPosition, char[][] map, Dungeon dungeon) {
        this.x = startPosition[0];
        this.y = startPosition[1];
        this.map = map;
        this.baseMaxHp = 1000;
        this.maxHp = baseMaxHp;
        this.hp = maxHp;        
        this.maxHunger = 100.0; 
        this.hunger = maxHunger;
        this.level = 1;        
        this.baseStrength = 3;
        this.strength = baseStrength;
        this.berserkStrength = strength*2;
        this.gentleStrength = strength;
        this.gold = 0;         
        this.armor = 0;        
        this.exp = 0;
        this.expToNextLevel = 10;
        this.statusMessage = "Welcome to the dungeon!";
        this.inventory = new ArrayList<>();
        this.identifiedScrolls = new HashMap<>();
        this.dungeon = dungeon;
        this.currentLevel = dungeon.getLevelNumber();
        
        // Give the player a starting item (bread)
        addItemToInventory(Item.createCommonConsumable("bread"));
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public double getHunger() { return hunger; }
    public double getMaxHunger() { return maxHunger; }
    public int getLevel() { return level; }
    public int getStrength() { return strength; }
    public int getBaseStrength() { return baseStrength; }
    public int getGold() { return gold; }
    public int getArmor() { return armor; }
    public int getExp() { return exp; }
    public int getExpToNextLevel() { return expToNextLevel; }
    public String getStatusMessage() { return statusMessage; }

    // Setters
    public void setStatusMessage(String message) { 
        this.statusMessage = message;
    }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void setMap(char[][] map) {
        this.map = map;
    }
    
    // Inventory getters
    public List<Item> getInventory() { return inventory; }
    public Item getEquippedWeapon() { return equippedWeapon; }
    public Item getEquippedArmor() { return equippedArmor; }
    public int getInventoryMaxSize() { return inventoryMaxSize; }

    /**
     * Move the player in the specified direction
     */
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

        // Check bounds
        if (newX < 0 || newY < 0 || newY >= map.length || newX >= map[0].length) {
            statusMessage = "You cannot move outside the dungeon!";
            return;
        }

        char tile = map[newY][newX];

        // Clear the player's old position
        if (map[y][x] == 'P') {
            map[y][x] = '.'; 
        }

        // Move if not a wall
        if (tile != '#') { 
            x = newX;
            y = newY;
            decreaseHunger();
            
            // Handle special tiles
            if (tile == '!') {
                // Found an item
                Item foundItem = ItemFactory.createRandomItem(level);
                if (addItemToInventory(foundItem)) {
                    map[newY][newX] = '.'; // Remove item from map
                }
            } else if (tile == '$') {
                // Found gold
                int goldAmount = 5 + rand.nextInt(level * 5);
                addGold(goldAmount);
                map[newY][newX] = '.'; // Remove gold from map
            }
        } else {
            statusMessage = "You bump into a wall.";
            return;
        }

        // Handle stairs
        if (tile == '>') {
            statusMessage = "Going down to the next floor!";
        } else if (tile == '<') {
            if (level > 1) {
                statusMessage = "Going up to the previous floor!";
            } else {
                statusMessage = "You are already at the top floor!";
            }
        }
    }

    /**
     * Decrease the player's hunger over time
     */
    private void decreaseHunger() {
        hunger -= 0.1; // Hunger slowly decreases
        if (hunger <= 0) {
            takeDamage(1); // Lose HP if starving
            hunger = 0;
            statusMessage = "You're starving!";
        }
    }

    /**
     * Take damage, accounting for armor
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
     * Attack a monster
     */
    public void attackMonster(Monster monster) {
        if (monster == null || !monster.isAlive()) {
            statusMessage = "There is no monster there!";
            return;
        }
        
        // Calculate damage based on strength
        int damage = strength + rand.nextInt(3);
        
        monster.takeDamage(damage);
        statusMessage = "You attack " + monster.getName() + " for " + damage + " damage!";
        
        // Handle monster defeat
        if (!monster.isAlive()) {
            int expGained = monster.getExpValue();
            int goldGained = monster.getExpValue() / 3;
            
            this.gold += goldGained;
            this.addExp(expGained);
            statusMessage = "You defeated " + monster.getName() + "! Gained " + goldGained + " gold." + expGained + " experience.";
            
            // Chance to drop an item when monster is defeated
            if (rand.nextDouble() < 0.35) {
                Item droppedItem = ItemFactory.createRandomItem(level);
                addItemToInventory(droppedItem);
                statusMessage += " You found " + droppedItem.getName() + "!";
            }
        }
    }
    
    // ======= INVENTORY METHODS =======
    
    /**
     * Add an item to the player's inventory
     */
    public boolean addItemToInventory(Item item) {
        if (inventory.size() >= inventoryMaxSize) {
            statusMessage = "Your inventory is full! Cannot pick up " + item.getName();
            return false;
        }

        if (item.getType().equals("scroll")) {
            String scrollEffect = item.getEffect();
            if (identifiedScrolls.containsKey(scrollEffect)) {
                // 이미 식별된 스크롤인 경우 실제 이름과 설명 사용
                item.identify();
            }
        }
        
        inventory.add(item);
        statusMessage = "Added " + item.getName() + " to inventory.";
        return true;
    }
    
    /**
     * Remove an item from the player's inventory
     */
    public boolean removeItemFromInventory(int index) {
        if (index < 0 || index >= inventory.size()) {
            statusMessage = "Invalid inventory index!";
            return false;
        }
        
        Item removed = inventory.remove(index);
        statusMessage = "Removed " + removed.getName() + " from inventory.";
        return true;
    }
    
    /**
     * Use a consumable item from inventory
     */
    public boolean useConsumable(int index) {
        if (index < 0 || index >= inventory.size()) {
            statusMessage = "Invalid inventory index!";
            return false;
        }
        
        Item item = inventory.get(index);
        
        if (!item.isConsumable()) {
            statusMessage = item.getName() + " is not consumable!";
            return false;
        }

        // effect of scrolls
        if (item.getType().equals("scroll")) {
            applyScrollEffect(item);
            item.identify();
            identifiedScrolls.put(item.getRealName(), item.getEffect());
            inventory.remove(index);
            return true;
        }
        
        // Apply item effect
        String effect = item.getEffect();
        int value = item.getEffectValue();
        
        switch (effect) {
            case "health":
                int oldHp = hp;
                hp = Math.min(maxHp, hp + value);
                statusMessage = "You used " + item.getName() + " and restored " + (hp - oldHp) + " health.";
                break;
                
            case "hunger":
                double oldHunger = hunger;
                hunger = Math.min(maxHunger, hunger + value);
                statusMessage = "You consumed " + item.getName() + " and restored " + 
                               String.format("%.1f", (hunger - oldHunger)) + " hunger.";
                break;
                
            case "strength":
                strength = baseStrength + value;
                statusMessage = "You used " + item.getName() + " and temporarily gained " + value + " strength!";
                // Note: This would ideally have a duration, but we'll keep it simple for now
                break;
                
            default:
                statusMessage = "You used " + item.getName() + " but nothing happened.";
                break;
        }
        
        // Remove the item after use
        inventory.remove(index);
        return true;
    }
    
    /**
     * Equip weapon from inventory
     */
    public boolean equipWeapon(int index) {
        if (index < 0 || index >= inventory.size()) {
            statusMessage = "Invalid inventory index!";
            return false;
        }
        
        Item item = inventory.get(index);
        
        if (!item.getType().equals("weapon")) {
            statusMessage = item.getName() + " is not a weapon!";
            return false;
        }
        
        // Unequip current weapon if any
        if (equippedWeapon != null) {
            // Remove strength bonus from old weapon
            strength = baseStrength;
            // Add old weapon back to inventory
            inventory.add(equippedWeapon);
        }
        
        // Equip new weapon
        equippedWeapon = item;
        inventory.remove(index);
        
        // Add strength bonus from new weapon
        strength = baseStrength + equippedWeapon.getEffectValue();
        
        statusMessage = "Equipped " + equippedWeapon.getName() + " (+" + equippedWeapon.getEffectValue() + " strength)";
        return true;
    }
    
    /**
     * Unequip current weapon
     */
    public boolean unequipWeapon() {
        if (equippedWeapon == null) {
            statusMessage = "You don't have a weapon equipped!";
            return false;
        }
        
        if (inventory.size() >= inventoryMaxSize) {
            statusMessage = "Your inventory is full! Cannot unequip weapon.";
            return false;
        }
        
        // Add weapon to inventory
        inventory.add(equippedWeapon);
        
        // Reset strength
        strength = baseStrength;
        
        // Clear equipped weapon
        String weaponName = equippedWeapon.getName();
        equippedWeapon = null;
        
        statusMessage = "Unequipped " + weaponName + ".";
        return true;
    }
    
    /**
     * Equip armor from inventory
     */
    public boolean equipArmor(int index) {
        if (index < 0 || index >= inventory.size()) {
            statusMessage = "Invalid inventory index!";
            return false;
        }
        
        Item item = inventory.get(index);
        
        if (!item.getType().equals("armor")) {
            statusMessage = item.getName() + " is not armor!";
            return false;
        }
        
        // Unequip current armor if any
        if (equippedArmor != null) {
            // Remove armor bonus
            armor = 0;
            // Add old armor back to inventory
            inventory.add(equippedArmor);
        }
        
        // Equip new armor
        equippedArmor = item;
        inventory.remove(index);
        
        // Add armor bonus
        armor = equippedArmor.getEffectValue();
        
        statusMessage = "Equipped " + equippedArmor.getName() + " (+" + equippedArmor.getEffectValue() + " armor)";
        return true;
    }
    
    /**
     * Unequip current armor
     */
    public boolean unequipArmor() {
        if (equippedArmor == null) {
            statusMessage = "You don't have armor equipped!";
            return false;
        }
        
        if (inventory.size() >= inventoryMaxSize) {
            statusMessage = "Your inventory is full! Cannot unequip armor.";
            return false;
        }
        
        // Add armor to inventory
        inventory.add(equippedArmor);
        
        // Reset armor
        armor = 0;
        
        // Clear equipped armor
        String armorName = equippedArmor.getName();
        equippedArmor = null;
        
        statusMessage = "Unequipped " + armorName + ".";
        return true;
    }
    
    /**
     * Drop an item from inventory onto the ground
     */
    public boolean dropItem(int index) {
        if (index < 0 || index >= inventory.size()) {
            statusMessage = "Invalid inventory index!";
            return false;
        }
        
        Item item = inventory.remove(index);
        statusMessage = "Dropped " + item.getName() + " on the ground.";
        
        // Ideally we would place the item on the map here
        // but for simplicity we'll just remove it
        
        return true;
    }
    
    /**
     * Load a new dungeon level
     */
    private void loadNewDungeon(String levelFile) {
        System.out.println("🔄 Loading new dungeon from: " + levelFile);
    
        Dungeon newDungeon = new Dungeon(levelFile); 
    
        if (newDungeon.getMap() == null || newDungeon.getMap().length == 0) {
            System.out.println("❌ Error: Failed to load new dungeon!");
            statusMessage = "Dungeon load failed!";
            return;
        }
    
        this.level = newDungeon.getLevelNumber();
        this.map = newDungeon.getMap();
    
        if (map[y][x] == '@') {
            map[y][x] = '.';
        }
    
        int[] startPos = newDungeon.getPlayerStartPosition();
        this.x = startPos[0];
        this.y = startPos[1];
    
        System.out.println("✅ Successfully loaded new dungeon: " + levelFile);
        statusMessage = "You have entered Level " + level + "!";
    }

    /**
     * Add experience points and check for level up
     */
    public void addExp(int amount) {
        this.exp += amount;
        statusMessage = "You gained " + amount + " experience points!";
        
        // Check if player has enough experience to level up
        while (exp >= expToNextLevel) {
            levelUp();
        }
    }

    /**
     * Add gold to the player
     */
    public void addGold(int amount) {
        gold += amount;
        statusMessage = "You found " + amount + " gold!";
    }
    
    /**
     * Increase max HP (e.g., when leveling up)
     */
    public void increaseMaxHp(int amount) {
        maxHp += amount;
        hp += amount; // Also heal by the same amount
        statusMessage = "Your maximum HP increased by " + amount + "!";
    }
    
    /**
     * Increase base strength (e.g., when leveling up)
     */
    public void increaseBaseStrength(int amount) {
        baseStrength += amount;
        
        // Update current strength if no weapon is equipped or add to weapon bonus
        if (equippedWeapon == null) {
            strength = baseStrength;
        } else {
            strength = baseStrength + equippedWeapon.getEffectValue();
        }
        
        statusMessage = "Your base strength increased by " + amount + "!";
    }
    
    /**
     * Heal the player
     */
    public void heal(int amount) {
        int oldHp = hp;
        hp = Math.min(maxHp, hp + amount);
        statusMessage = "You healed for " + (hp - oldHp) + " HP!";
    }
    
    /**
     * Restore hunger
     */
    public void restoreHunger(double amount) {
        double oldHunger = hunger;
        hunger = Math.min(maxHunger, hunger + amount);
        statusMessage = "You restored " + String.format("%.1f", (hunger - oldHunger)) + " hunger!";
    }

    /**
     * Level up the player
     */
    private void levelUp() {
        level++;
        exp -= expToNextLevel; // Deduct the exp used for leveling up
        expToNextLevel = (int) (expToNextLevel * 1.5); // Increase the exp needed for next level
        
        // Increase player stats
        baseMaxHp += 20;
        maxHp = baseMaxHp;
        hp = maxHp; // Fully heal the player
        baseStrength += 2;
        strength = baseStrength; // Update strength
        
        statusMessage = "You leveled up to level " + level + "!";
    }

    private void applyScrollEffect(Item scroll) {
        if (dungeon == null) {
            statusMessage = "Error: Dungeon not initialized!";
            return;
        }

        switch (scroll.getEffect()) {
            case "giant":
                maxHp *= 2;
                hp = maxHp;
                statusMessage = "Your max HP has doubled for this level!";
                break;
            case "berserk":
                gentleStrength = strength;
                berserkStrength = strength*2;
                strength = berserkStrength;
                statusMessage = "All monsters on this level have been vanquished!";
                break;
            default:
                statusMessage = "The scroll had no effect.";
        }
    }

    public void onLevelChange(int newLevel) {
        if (this.currentLevel != newLevel) {
            resetScrollEffects();
            this.currentLevel = newLevel;
        }
    }

    private void resetScrollEffects() {
        if (maxHp > baseMaxHp) { // Giant's Scroll effect reset
            maxHp = baseMaxHp;
            hp = Math.min(hp, maxHp);
        }

        if (strength > baseStrength) { // Berserk Scroll effect reset
            strength = baseStrength;
        }
        // Another Scroll effects reset
    }
}
