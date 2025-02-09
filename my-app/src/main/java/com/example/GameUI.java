package com.example;

public class GameUI {

    public void printMap(char[][] map) {
        System.out.println("\n=== Dungeon Map ===");
        for (char[] row : map) {
            System.out.println(new String(row)); // ✅ Correctly prints the map
        }
        System.out.println("===================\n");
    }

    public void printStats(int hp, double hunger) {
        System.out.println("💖 HP: " + hp + " | 🍖 Hunger: " + String.format("%.2f", hunger) + "%");
    }

    // ✅ New method to display full player stats
    public void printFullStats(int hp, double hunger, int level, int strength, int gold, int armor) {
        System.out.println("\n=== Player Stats ===");
        System.out.println("💖 HP: " + hp + " | 🍖 Hunger: " + String.format("%.2f", hunger) + "%");
        System.out.println("🎚️ Level: " + level + " | 💪 Strength: " + strength);
        System.out.println("💰 Gold: " + gold + " | 🛡 Armor: " + armor);
        System.out.println("=====================\n");
    }
}
