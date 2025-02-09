package com.example;

/*
 * Code for the game's UI and screen output.
 * For Splint 2: Outputting the game to the screen! <- This is important.
 * Not Use Now...
 * 
 * Authot : Suhwan Kim
 * Date : Feb 8, 2025
 */
public class GameUI {
    public void printMap(char[][] map) {
        for (char[] row : map) {
            System.out.println(row);
        }
    }

    public void printStats(int hp, int hunger) {
        System.out.println("HP: " + hp + " | Hunger: " + hunger);
    }
}
