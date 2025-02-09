package com.example;

/*
 * The code responsible for the player's behavior and control.
 * For Splint 2: Player's basic movement functionality.
 * 
 * Author : Suhwan Kim
 * Date : Feb 8, 2025
 */
public class Player {
    private int x, y;
    private char[][] map;

    public Player(int[] startPosition, char[][] map) {
        this.x = startPosition[0];
        this.y = startPosition[1];
        this.map = map;
        this.map[y][x] = '@'; // Setting the Player Initial Position
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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
    
        // Moveable tiles: floor (.), codrrider (=)
        if (map[newY][newX] == '.' || map[newY][newX] == '=') {
            if (map[y][x] == '@') {
                if (map[y][x] == '=') {
                    map[y][x] = '=';
                } else {
                    map[y][x] = '.';
                }
            }
            x = newX;
            y = newY;
            map[y][x] = '@';
        }
    }
}
