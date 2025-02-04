package com.example;

/*
 * Code that implements the walls and structure of the dungeon.
 * For Splint 2:
 * - Implementing a procedural dungeon with generated
 * - Randomized structure of walls and empty spaces
 * 
 * Author : Suhwan Kim
 * Date : Feb 3, 2025
 */
public class Dungeon {
    private char[][] map;
    private int width, height;

    public Dungeon(int width, int height) {
        this.width = width;
        this.height = height;
        this.map = new char[height][width];
        generateDungeon();
    }

    private void generateDungeon() {
        // Create walls and empty spaces
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y == 0 || y == height - 1 || x == 0 || x == width - 1)
                    map[y][x] = '#'; // Wall
                else
                    map[y][x] = '.'; // Empty space
            }
        }
    }

    public char[][] getMap() {
        return map;
    }
}
