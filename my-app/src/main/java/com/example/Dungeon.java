package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Dungeon {
    private char[][] map;
    private int width, height;
    private int[] stairsUp;
    private int[] stairsDown;
    private Random random = new Random();

    public Dungeon(String levelFile) {
        loadLevel(levelFile);
        if (map != null && map.length > 0 && map[0].length > 0) { 
            placeStairs(); // 🔹 Only place stairs if map is valid
        } else {
            System.out.println("Error: Dungeon map not loaded properly, skipping stairs placement.");
        }
    }

    private void loadLevel(String levelFile) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(levelFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error loading level: " + e.getMessage());
            return;
        }

        if (lines.isEmpty()) {
            System.out.println("Error: Level file is empty!");
            return;
        }

        height = lines.size();
        width = lines.get(0).length();
        map = new char[height][width];

        for (int y = 0; y < height; y++) {
            map[y] = lines.get(y).toCharArray();
            System.out.println("Loaded row: " + new String(map[y])); // Debugging output
        }

        System.out.println("Level Loaded Successfully: " + levelFile);
    }

    private void placeStairs() {
        if (map == null || map.length == 0 || map[0].length == 0) {
            System.out.println("Error: Cannot place stairs, map is empty.");
            return;
        }

        int x, y;
        do {
            x = random.nextInt(width);
            y = random.nextInt(height);
        } while (map[y][x] != '.'); // Ensure it's placed on a valid floor tile

        map[y][x] = '>'; // Stairs down
        stairsDown = new int[]{x, y};

        do {
            x = random.nextInt(width);
            y = random.nextInt(height);
        } while (map[y][x] != '.'); // Ensure it's placed on a valid floor tile

        map[y][x] = '<'; // Stairs up
        stairsUp = new int[]{x, y};

        System.out.println("Stairs placed at: DOWN(" + stairsDown[0] + "," + stairsDown[1] + ") UP(" + stairsUp[0] + "," + stairsUp[1] + ")");
    }

    public char[][] getMap() {
        return map;
    }

    public int[] getStairsUp() {
        return stairsUp;
    }

    public int[] getStairsDown() {
        return stairsDown;
    }

    public int[] getPlayerStartPosition() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (map[y][x] == 'P') {
                    return new int[]{x, y};
                }
            }
        }
        return new int[]{1, 1}; // Default if no 'P' found
    }
}
