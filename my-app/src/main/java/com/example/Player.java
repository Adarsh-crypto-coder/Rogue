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
    private int hp;
    private double hunger;
    private char[][] map;
    private String statusMessage;

    public Player(int[] startPosition, char[][] map) {
        this.x = startPosition[0];
        this.y = startPosition[1];
        this.map = map;
        this.map[y][x] = '@'; // Setting the Player Initial Position
        this.hp = 10; // Initial HP
        this.hunger = 10.0; // Initial Hunger
        this.statusMessage = ""; // Initial status messages
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHp() {
        return hp;
    }

    public double getHunger() {
        return hunger;
    }

    public String getStatusMessage() {
        return statusMessage;
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

        // Moveable tiles: floor (.), corridor (=)
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

            // Hunger decrease
            hunger -= 0.05;
            if (hunger <= 0) {
                hunger = 0;
                hp--; // Starvation
                statusMessage = "Too Hungry...";
            } else {
                statusMessage = "";
            }

            // game over
            if (hp <= 0) {
                hp = 0;
                statusMessage = "Game Over! Press SPACE to exit.";
            }
        }
    }
}
