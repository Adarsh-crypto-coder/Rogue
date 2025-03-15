package com.example;

/**
 * Represents an item in the game
 */
public class Item {
    private String name;
    private String type; // "consumable", "weapon", "armor", etc.
    private String description;
    private int value; // Gold value
    private boolean isConsumable;
    private String effect; // Effect type: "health", "hunger", "strength", etc.
    private int effectValue; // Amount the effect changes the stat
    private char symbol; // Character representation on map
    
    public Item(String name, String type, String description, int value, 
                boolean isConsumable, String effect, int effectValue, char symbol) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.value = value;
        this.isConsumable = isConsumable;
        this.effect = effect;
        this.effectValue = effectValue;
        this.symbol = symbol;
    }
    
    // Getters
    public String getName() { return name; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public int getValue() { return value; }
    public boolean isConsumable() { return isConsumable; }
    public String getEffect() { return effect; }
    public int getEffectValue() { return effectValue; }
    public char getSymbol() { return symbol; }
    
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
}