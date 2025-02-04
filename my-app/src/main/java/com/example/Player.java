package com.example;

/*
 * The code responsible for the player's behavior and control.
 * For Splint 2: Player's basic movement functionality(not walking through walls).
 * 
 * Author : Suhwan Kim
 * Date : Feb 3, 2025
 */
public class Player {
    private int x, y;
    private char[][] map;

    public Player(int startX, int startY, char[][] map) {
        this.x = startX;
        this.y = startY;
        this.map = map;
        this.map[y][x] = '@'; // Setting the Player Initial Position
    }

    public void move(char direction) {
        int newX = x, newY = y;
        switch (Character.toUpperCase(direction)) {
            case 'W': newY--; break;
            case 'A': newX--; break;
            case 'S': newY++; break;
            case 'D': newX++; break;
            default: return;
        }

        if (map[newY][newX] != '#') { // If not a wall, move
            map[y][x] = '.'; // Replace the existing location with an empty space
            x = newX;
            y = newY;
            map[y][x] = '@'; // Show players in new locations
        }
    }
}
