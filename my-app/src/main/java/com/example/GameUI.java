package com.example;

/*
 * Code for the game's UI and screen output.
 * For Splint 2: Outputting the game to the screen! <- This is important.
 * 
 * Authot : Suhwan Kim
 * Date : Feb 3, 2025
 */
public class GameUI {
    public void printMap(char[][] map) {
        for (char[] row : map) {
            System.out.println(row);
        }
    }

    public void printStats(int hp, int level) {
        System.out.println("HP: " + hp + " | Level: " + level);
    }
}
