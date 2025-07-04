package com.example;

import java.util.Random;

/**
 * Factory class to create different types of items
 */
public class ItemFactory {
    private static final Random rand = new Random();
    
    /**
     * Creates a random item based on dungeon level
     */
    public static Item createRandomItem(int dungeonLevel) {
        double roll = rand.nextDouble();
        
        // Higher chance for consumables (60%)
        if (roll < 0.6) {
            return createRandomConsumable(dungeonLevel);
        } 
        // Some chance for armor (15%)
        else if (roll < 0.75) { // 0.6 ~ 0.75 (15%)
            return createArmor(dungeonLevel);
        } 
        // Some chance for weapon (15%)
        else if (roll < 0.9) { // 0.75 ~ 0.9 (15%)
            return createWeapon(dungeonLevel);
        } 
        // Some chance for scroll (10%)
        else { // 0.9 ~ 1.0 (10%)
            return createScroll(dungeonLevel);
        }
    }
    
    /**
     * Creates a random consumable item
     */
    public static Item createRandomConsumable(int dungeonLevel) {
        // Types of consumables
        String[] consumableTypes = {
            "healing_potion", "bread", "apple", "meat", "strength_potion"
        };
        
        // Select a random consumable type
        String type = consumableTypes[rand.nextInt(consumableTypes.length)];
        
        // Create the base consumable
        Item item = Item.createCommonConsumable(type);
        
        // Scale effect values slightly based on dungeon level
        if (dungeonLevel > 1) {
            int bonusEffect = rand.nextInt(dungeonLevel);
            return new Item(
                item.getName(), 
                item.getType(), 
                "Level " + dungeonLevel + " " + item.getDescription(),
                item.getValue() + (bonusEffect * 2),
                true,
                item.getEffect(),
                item.getEffectValue() + bonusEffect,
                item.getSymbol()
            );
        }
        
        return item;
    }
    
    /**
     * Creates armor with defense value based on dungeon level
     */
    public static Item createArmor(int dungeonLevel) {
        String[] armorTypes = {
            "Leather Armor", "Chain Mail", "Plate Armor", "Dragon Scale"
        };
        
        int index = Math.min(dungeonLevel - 1, armorTypes.length - 1);
        index = Math.max(0, index); // Ensure we don't get negative index
        
        String name = armorTypes[index];
        int armorValue = 1 + index + rand.nextInt(dungeonLevel);
        int value = armorValue * 15;
        
        return new Item(
            name,
            "armor",
            "Provides " + armorValue + " armor protection",
            value,
            false,
            "armor",
            armorValue,
            'A'
        );
    }
    
    /**
     * Creates weapon with strength value based on dungeon level
     */
    public static Item createWeapon(int dungeonLevel) {
        String[] weaponTypes = {
            "Dagger", "Short Sword", "Long Sword", "Battle Axe", "War Hammer"
        };
        
        int index = Math.min(dungeonLevel - 1, weaponTypes.length - 1);
        index = Math.max(0, index); // Ensure we don't get negative index
        
        String name = weaponTypes[index];
        int strengthValue = 1 + index + rand.nextInt(dungeonLevel);
        int value = strengthValue * 20;
        
        return new Item(
            name,
            "weapon",
            "Increases strength by " + strengthValue,
            value,
            false,
            "strength",
            strengthValue,
            'W'
        );
    }

    /**
     * Creates Magic Scrolls
     */
    public static Item createScroll(int dungeonLevel) {
        String[] scrollEffects = {"giant", "berserk"};
        String effect = scrollEffects[rand.nextInt(scrollEffects.length)];
        String name;
        String description;
        int value = 50 * dungeonLevel;

        switch (effect) {
            case "giant":
                name = "Giant's Scroll";
                description = "Doubles your max HP for the current level.";
                break;
            case "berserk":
                name = "berserk Scroll";
                description = "Doubles your strength for the current level.";
                break;
            default:
                name = "Mysterious Scroll";
                description = "A scroll with unknown effects.";
        }

        return new Item(name, "scroll", description, value, true, effect, 0, '?');
    }
}
