package com.example;

import java.util.Random;

/**
 * Factory class to create different types of monsters
 */
public class MonsterFactory {
    private static final Random rand = new Random();
    
    
    private static final String[] TYPES = {
        "Undead", "Beast", "Humanoid", "Construct", "Elemental", "Aberration"
    };
    
    
    private static final String[][] NAMES_BY_TYPE = {
        
        {"Skeleton Warrior", "Zombie", "Ghost", "Wraith", "Vampire Thrall", "Bone Collector"},
        
        {"Dire Rat", "Giant Spider", "Cave Bear", "Venomous Snake", "Blood Bat", "Dungeon Wolf"},
        
        {"Goblin Scout", "Orc Warrior", "Kobold Trapper", "Dark Elf Assassin", "Troll Brute", "Hobgoblin"},
        
        {"Stone Golem", "Mechanical Sentinel", "Animated Armor", "Clockwork Beast", "Living Statue", "Arcane Turret"},
        
        {"Fire Wisp", "Water Sprite", "Earth Rumbler", "Wind Harrier", "Shadow Elemental", "Crystal Guardian"},
        
        {"Mind Flayer", "Gelatinous Cube", "Rust Monster", "Beholder Spawn", "Chaos Tendril", "Reality Warper"}
    };
    
    
    private static final char[] SYMBOLS_BY_TYPE = {
        'Z', // Undead
        'B', // Beast
        'H', // Humanoid
        'C', // Construct
        'E', // Elemental
        'A'  // Aberration
    };
    
    /**
     * Create a monster appropriate for the given dungeon level
     */
    public static Monster createForLevel(int dungeonLevel, int x, int y) {
        int typeIndex = rand.nextInt(TYPES.length);
        String type = TYPES[typeIndex];
        
        int nameIndex = rand.nextInt(NAMES_BY_TYPE[typeIndex].length);
        String name = NAMES_BY_TYPE[typeIndex][nameIndex];
        
        char symbol = SYMBOLS_BY_TYPE[typeIndex];
        
       
        int baseHp = 5 + (2 * dungeonLevel);
        int hp = baseHp + rand.nextInt(dungeonLevel * 3);
        
        int baseAttack = 2 + dungeonLevel;
        int attack = baseAttack + rand.nextInt(2);
        
        int baseDefense = dungeonLevel / 2;
        int defense = baseDefense + rand.nextInt(2);
        
        int expValue = 5 * dungeonLevel + rand.nextInt(5 * dungeonLevel);
        
        return new Monster(name, type, x, y, hp, attack, defense, expValue, symbol, true);
    }
    
    /**
     * Create a specific type of monster
     */
    public static Monster createSpecificType(String specificType, int x, int y, int level) {
        int typeIndex = 0;
        for (int i = 0; i < TYPES.length; i++) {
            if (TYPES[i].equals(specificType)) {
                typeIndex = i;
                break;
            }
        }
        
        int nameIndex = rand.nextInt(NAMES_BY_TYPE[typeIndex].length);
        String name = NAMES_BY_TYPE[typeIndex][nameIndex];
        char symbol = SYMBOLS_BY_TYPE[typeIndex];
        
        
        int hp, attack, defense, exp;
        
        switch (specificType) {
            case "Undead":
                hp = 6 * level;
                attack = 3 + level;
                defense = level;
                exp = 6 * level;
                break;
            case "Beast":
                hp = 4 * level;
                attack = 4 + level;
                defense = level / 2;
                exp = 5 * level;
                break;
            case "Humanoid":
                hp = 5 * level;
                attack = 3 + level;
                defense = 1 + level;
                exp = 7 * level;
                break;
            case "Construct":
                hp = 8 * level;
                attack = 2 + level;
                defense = 2 + level;
                exp = 8 * level;
                break;
            case "Elemental":
                hp = 4 * level;
                attack = 5 + level;
                defense = level / 2;
                exp = 7 * level;
                break;
            case "Aberration":
                hp = 6 * level;
                attack = 4 + level;
                defense = 1 + level;
                exp = 10 * level;
                break;
            default:
                hp = 5 * level;
                attack = 3 + level;
                defense = level;
                exp = 5 * level;
        }
        
        
        hp += rand.nextInt(level * 2);
        attack += rand.nextInt(2);
        defense += rand.nextInt(level / 2);
        
        return new Monster(name, specificType, x, y, hp, attack, defense, exp, symbol, true);
    }
    
    /**
     * Create a specific named monster (for bosses, etc.)
     */
    public static Monster createBoss(String name, int x, int y, int level) {
        
        String type;
        char symbol;
        
        if (name.contains("Lich") || name.contains("Death")) {
            type = "Undead";
            symbol = 'L';
        } else if (name.contains("Dragon") || name.contains("Beast")) {
            type = "Beast";
            symbol = 'D';
        } else if (name.contains("Lord") || name.contains("King")) {
            type = "Humanoid";
            symbol = 'K';
        } else if (name.contains("Golem") || name.contains("Sentinel")) {
            type = "Construct";
            symbol = 'G';
        } else if (name.contains("Elemental") || name.contains("Essence")) {
            type = "Elemental";
            symbol = 'Ω';
        } else {
            type = "Aberration";
            symbol = 'X';
        }
        
        
        int hp = 15 * level + rand.nextInt(10);
        int attack = 5 + (2 * level);
        int defense = 2 + level;
        int exp = 20 * level;
        
        Monster boss = new Monster(name, type, x, y, hp, attack, defense, exp, symbol, true);
        
        // Bosses may have special status resistance
        if (rand.nextBoolean()) {
            boss.applyStatus("status resistant", 999); // Permanent effect
        }
        
        return boss;
    }
}

