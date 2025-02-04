package com.example;

import java.io.IOException;

/*
 * This is the foundation code for Splint 2.
 * 
 * Player.java  : Movement and Action part of the player.
 * Dungeon.java : Generate and Control of the dungeon.
 * GameUI.java  : Output of the game's UI and state.
 * 
 * Author : Suhwan Kim
 * Date : Feb 3, 2025
 */

 public class Main {
    public static void main(String[] args) {
        Dungeon dungeon = new Dungeon(10, 10);
        Player player = new Player(1, 1, dungeon.getMap()); // Create a New player
        GameUI ui = new GameUI();

        try {
            while (true) {
                ui.printMap(dungeon.getMap()); // Dungeon Map Output
                /*
                 * The current way I've written it is to type the move key in the input buffer and press enter to move.
                 * 
                 * Author : Suhwan Kim
                 * Date : Feb 3, 2025
                 */

                // TODO : We should probably modify it to reflect WASD or arrow key input immediately.
                System.out.print("Move (WASD): ");
                char move = (char) System.in.read(); // Get input
                player.move(move); // Move players
                System.in.read(); // Emptying the input buffer
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
