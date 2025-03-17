package com.example;

import java.util.Random;

/**
 * Represents an item in the game
 */
public class Item {
    private String name;
    private String type; // "consumable", "weapon", "armor", "scroll", etc.
    private String description;
    private int value; // Gold value
    private boolean isConsumable;
    private String effect; // Effect type: "health", "hunger", "strength", "giant", "berserk", etc.
    private int effectValue; // Amount the effect changes the stat
    private char symbol; // Character representation on map

    /**
     * Scroll Identified boolean
     */
    private boolean isIdentified;
    private String realName; // Real name of scroll
    private String realDescription; // Real description of scroll
    
    public Item(String name, String type, String description, int value, 
                boolean isConsumable, String effect, int effectValue, char symbol) {
        this.type = type;
        this.realDescription = description;
        this.value = value;
        this.isConsumable = isConsumable;
        this.effect = effect;
        this.effectValue = effectValue;
        this.symbol = symbol;
        this.isIdentified = !type.equals("scroll"); // if not scroll, always identified
        this.realName = name;

        if (type.equals("scroll") && !isIdentified) {
            this.name = generateRandomScrollName();
            this.description = "A mysterious scroll with unknown effects.";
        } else {
            this.name = realName;
            this.description = realDescription;
        }
    }
    
    // Getters
    public String getType() { return type; }
    public int getValue() { return value; }
    public boolean isConsumable() { return isConsumable; }
    public String getEffect() { return effect; }
    public int getEffectValue() { return effectValue; }
    public char getSymbol() { return symbol; }
    public String getRealName() { return realName; }
    public String getName() {
        return isIdentified ? realName : name;
    }
    public String getDescription() {
        return isIdentified ? realDescription : description;
    }
    public boolean isIdentified() {
        return isIdentified;
    }
    
    @Override
    public String toString() {
        return name + " (" + description + ")";
    }
    
    /**
     * Factory method to create consumable items
     */
    public static Item createConsumable(String name, String effect, int effectValue) {
        String description;
        int value;
        char symbol;
        
        switch (effect) {
            case "health":
                description = "Restores " + effectValue + " health points";
                value = effectValue * 5;
                symbol = '+';
                break;
            case "hunger":
                description = "Restores " + effectValue + " hunger points";
                value = effectValue * 3;
                symbol = '%';
                break;
            case "strength":
                description = "Temporarily increases strength by " + effectValue;
                value = effectValue * 10;
                symbol = '^';
                break;
            default:
                description = "A mysterious item";
                value = 10;
                symbol = '?';
        }
        
        return new Item(name, "consumable", description, value, true, effect, effectValue, symbol);
    }
    
    /**
     * Factory method to create common consumables
     */
    public static Item createCommonConsumable(String itemType) {
        switch (itemType) {
            case "healing_potion":
                return createConsumable("Healing Potion", "health", 5);
            case "bread":
                return createConsumable("Bread", "hunger", 4);
            case "apple":
                return createConsumable("Apple", "hunger", 2);
            case "meat":
                return createConsumable("Cooked Meat", "hunger", 6);
            case "strength_potion":
                return createConsumable("Strength Potion", "strength", 2);
            default:
                return createConsumable("Strange Herb", "health", 1);
        }
    }

    public void identify() {
        if (type.equals("scroll") && !isIdentified) {
            this.name = realName;
            this.description = realDescription;
            this.isIdentified = true;
        }
    }

    private String generateRandomScrollName() {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append((char) (rand.nextInt(26) + 'A'));
        }
        return sb.toString() + " Scroll";
    }
}
