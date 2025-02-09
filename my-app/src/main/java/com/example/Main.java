package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.text.*;

/*
 * This is the foundation code for Splint 2.
 * 
 * Player.java  : Movement and Action part of the player.
 * Dungeon.java : Generate and Control of the dungeon.
 * GameUI.java  : Output of the game's UI and state.
 * 
 * We additionally used a JFrame for demonstration purposes.
 * TODO: We haven't implemented the Player UI functionality yet!
 * 
 * Author : Suhwan Kim
 * Date : Feb 8, 2025
 */
public class Main extends JFrame implements KeyListener {
    private Dungeon dungeon;
    private Player player;
    private JTextPane textPane;
    private StyleContext styleContext;
    private DefaultStyledDocument document;

    public Main() {
        dungeon = new Dungeon(100, 100);
        player = new Player(dungeon.getRandomRoomCenter(), dungeon.getMap()); // Start the player in the center of a random room

        // Window Settings
        setTitle("Rogue-like Dungeon");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set text area (display map with ASCII art style)
        textPane = new JTextPane();
        textPane.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textPane.setEditable(false);
        textPane.setBackground(Color.BLACK); // Background Color

        // Initialize document
        styleContext = new StyleContext();
        document = new DefaultStyledDocument(styleContext);
        textPane.setDocument(document); // Setting a document in the JTextPane

        add(new JScrollPane(textPane), BorderLayout.CENTER);

        // Add a key listener : I've found it works well on macOS, but I need to check if it works on Windows (Feb 8, Suhwan)
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        setVisible(true);

        // Show initial map
        updateMapDisplay();
    }

    private void updateMapDisplay() {
        try {
            document.remove(0, document.getLength()); // Delete existing text
            char[][] map = dungeon.getMap();
            int playerX = player.getX();
            int playerY = player.getY();
    
            // 플레이어 주변의 맵만 표시 (예: 80x24 크기)
            int startX = Math.max(0, playerX - 40);
            int startY = Math.max(0, playerY - 12);
            int endX = Math.min(map[0].length, playerX + 40);
            int endY = Math.min(map.length, playerY + 12);
    
            for (int y = startY; y < endY; y++) {
                for (int x = startX; x < endX; x++) {
                    char tile = map[y][x];
                    Style style = styleContext.addStyle("Style", null);
                    switch (tile) {
                        case '#':
                            StyleConstants.setForeground(style, Color.YELLOW); // Wall
                            break;
                        case '.':
                            StyleConstants.setForeground(style, Color.LIGHT_GRAY); // Floor
                            break;
                        case '=':
                            StyleConstants.setForeground(style, Color.ORANGE); // Corrider
                            break;
                        case '@':
                            StyleConstants.setForeground(style, Color.GREEN); // Player
                            break;
                        case ' ':
                            StyleConstants.setForeground(style, Color.BLACK); // Empty Place
                            break;
                        default:
                            StyleConstants.setForeground(style, Color.WHITE);
                            break;
                    }
                    document.insertString(document.getLength(), String.valueOf(tile), style);
                }
                document.insertString(document.getLength(), "\n", null);
            }
    
            // Show status information
            // TODO : WE HAVE JOB TODO!!!!!!!!!!
            document.insertString(document.getLength(), "\nHP: 10 | Hunger: 10", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        char move = ' ';
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: move = 'W'; break;
            case KeyEvent.VK_A: move = 'A'; break;
            case KeyEvent.VK_S: move = 'S'; break;
            case KeyEvent.VK_D: move = 'D'; break;
            default: return;
        }
        player.move(move); // Move Player
        updateMapDisplay(); // Redrawing maps
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        new Main(); // Start Game
    }
}
