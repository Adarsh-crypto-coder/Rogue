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
    private int levelNumber;
    private int[] stairsUp;
    private int[] stairsDown;
    private MonsterManager monsterManager;
    private Random random = new Random();

    public Dungeon(String levelFile) {
        monsterManager = new MonsterManager();
        loadLevel(levelFile);
    }

    private void loadLevel(String levelFile) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(levelFile))) {
            String line = br.readLine();
            
            // Read first line as level number
            if (line != null && line.startsWith("LEVEL ")) {
                levelNumber = Integer.parseInt(line.replace("LEVEL ", "").trim());
            } else {
                System.out.println("❌ Error: Level file does not start with 'LEVEL X'!");
                return;
            }

            // Read rest of the map
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error loading level: " + e.getMessage());
            return;
        }

        if (lines.isEmpty()) {
            System.out.println("❌ Error: Level file is empty!");
            return;
        }

        width = lines.get(0).length();
        height = lines.size();

        // Completely replace old map with new level
        map = new char[height][width];

        System.out.println("✅ Loading Level " + levelNumber + " from: " + levelFile);
        for (int y = 0; y < height; y++) {
            map[y] = lines.get(y).toCharArray();
        }

        findStairs();
        
        // Populate the dungeon with monsters - more monsters on deeper levels
        int baseMonsterCount = 5 + (levelNumber * 2);
        int monsterCount = baseMonsterCount + random.nextInt(3);
        monsterManager.populateDungeon(this, monsterCount);
        
        System.out.println("✅ Level " + levelNumber + " Loaded Successfully with " + 
                          monsterManager.getAllMonsters().size() + " monsters");
    }

    private void findStairs() {
        if (map == null) return;

        stairsUp = null;
        stairsDown = null;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (map[y][x] == '>') {
                    stairsDown = new int[]{x, y};
                } else if (map[y][x] == '<') {
                    stairsUp = new int[]{x, y};
                }
            }
        }

        System.out.println("✅ Stairs detected: " +
            (stairsDown != null ? "DOWN(" + stairsDown[0] + "," + stairsDown[1] + ") " : "No Down Stairs ") +
            (stairsUp != null ? "UP(" + stairsUp[0] + "," + stairsUp[1] + ")" : "No Up Stairs"));
    }

    public char[][] getMap() {
        return map;
    }
    
    public char[][] getMapWithMonsters() {
        // Create a copy of the map so we don't modify the original
        char[][] mapCopy = new char[height][width];
        for (int y = 0; y < height; y++) {
            System.arraycopy(map[y], 0, mapCopy[y], 0, width);
        }
        
        // Render monsters on the map copy
        monsterManager.renderMonstersOnMap(mapCopy);
        
        return mapCopy;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public int[] getStairsUp() {
        return stairsUp;
    }

    public int[] getStairsDown() {
        return stairsDown;
    }
    
    public MonsterManager getMonsterManager() {
        return monsterManager;
    }

    public int[] getPlayerStartPosition() {
        if (map == null) return new int[]{1, 1};

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (map[y][x] == 'P') {
                    return new int[]{x, y};
                }
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (map[y][x] == '.') {
                    return new int[]{x, y};
                }
            }
        }

        return new int[]{1, 1}; 
    }
    
    // Process monster turns after player moves
    public void updateMonsters(Player player) {
        monsterManager.updateMonsters(player, map);
    }
    
    // Check if a monster is at coordinates
    public Monster getMonsterAt(int x, int y) {
        return monsterManager.getMonsterAt(x, y);
    }
}