package com.example;

import java.io.IOException;

/*
 * This is the foundation code for Splint 2.
 * It performs the simple function of making the player move around a 10x10 room via WASD input.
 * 
 * Author : Suhwan Kim
 * Date : Jan 30, 2025
 */

public class Main {
    static final int WIDTH = 10, HEIGHT = 10;
    static char[][] map = new char[HEIGHT][WIDTH];
    static int playerX = 1, playerY = 1;

    public static void main(String[] args) {
        System.out.println(new Main().getGreeting());
        initMap();

        try {
            while (true) {
                printMap();
                System.out.print("Move (WASD): ");
                char move = (char) System.in.read(); 
                movePlayer(move);
                System.in.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initMap() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (y == 0 || y == HEIGHT - 1 || x == 0 || x == WIDTH - 1)
                    map[y][x] = '#'; // Wall
                else
                    map[y][x] = '.'; // Floor
            }
        }
        map[playerY][playerX] = '@'; // Player
    }

    private static void printMap() {
        for (char[] row : map) {
            System.out.println(row);
        }
    }

    private static void movePlayer(char move) {
        int newX = playerX, newY = playerY;
        switch (Character.toUpperCase(move)) {
            case 'W': newY--; break;
            case 'A': newX--; break;
            case 'S': newY++; break;
            case 'D': newX++; break;
            default: return;
        }

        if (map[newY][newX] != '#') {
            map[playerY][playerX] = '.';
            playerX = newX;
            playerY = newY;
            map[playerY][playerX] = '@';
        }
    }

    public String getGreeting() {
        return "Hello, World!";
    }
}
