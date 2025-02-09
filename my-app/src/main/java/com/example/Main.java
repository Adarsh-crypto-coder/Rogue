package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.text.*;
import java.io.File;

/**
 * Rogue-like Dungeon Game
 * Loads levels from a text file and allows movement.
 * 
 * Author : Suhwan Kim
 * Updated : Feb 8, 2025
 */
public class Main extends JFrame implements KeyListener {
    private Dungeon dungeon;
    private Player player;
    private JTextPane textPane;
    private StyleContext styleContext;
    private DefaultStyledDocument document;

    public Main() {
        // Ensure the level file exists
        String levelFilePath = "levels/level1.txt";
        File levelFile = new File(levelFilePath);
        System.out.println("Looking for level file at: " + levelFile.getAbsolutePath());

        if (!levelFile.exists()) {
            System.out.println("Error: Level file not found at " + levelFile.getAbsolutePath());
            JOptionPane.showMessageDialog(this, "Level file not found!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Load the level
        dungeon = new Dungeon(levelFilePath);

        if (dungeon.getMap() == null || dungeon.getMap().length == 0) {
            System.out.println("Error: Level file is empty or not loaded!");
            JOptionPane.showMessageDialog(this, "Error: Level file is empty or not loaded!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        int[] playerStart = dungeon.getPlayerStartPosition();
        player = new Player(playerStart, dungeon.getMap());

        // Window Settings
        setTitle("Rogue-like Dungeon");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set text area (display map with ASCII art style)
        textPane = new JTextPane();
        textPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textPane.setEditable(false);
        textPane.setBackground(Color.BLACK);

        // Initialize document
        styleContext = new StyleContext();
        document = new DefaultStyledDocument(styleContext);
        textPane.setDocument(document);

        add(new JScrollPane(textPane), BorderLayout.CENTER);

        // Add a key listener
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        
        setVisible(true);
        this.requestFocusInWindow(); // 🔹 Forces focus on window
        this.addWindowFocusListener(new java.awt.event.WindowAdapter() {
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                requestFocusInWindow(); // 🔹 Ensures key events are captured
            }
        });
        
        // Show initial map
        updateMapDisplay();
    }

    private void updateMapDisplay() {
        try {
            document.remove(0, document.getLength());
            char[][] map = dungeon.getMap();
            
            if (map == null || map.length == 0 || map[0].length == 0) {
                System.out.println("Error: Dungeon map is empty!");
                document.insertString(0, "Error: Dungeon map is empty!", null);
                return;
            }

            int playerX = player.getX();
            int playerY = player.getY();

            for (int y = 0; y < map.length; y++) {
                for (int x = 0; x < map[0].length; x++) {
                    char tile = (x == playerX && y == playerY) ? '@' : map[y][x];
                    Style style = styleContext.addStyle("Style", null);

                    switch (tile) {
                        case '#': StyleConstants.setForeground(style, Color.YELLOW); break;
                        case '.': StyleConstants.setForeground(style, Color.LIGHT_GRAY); break;
                        case '=': StyleConstants.setForeground(style, Color.ORANGE); break;
                        case '@': StyleConstants.setForeground(style, Color.GREEN); break;
                        case '>': StyleConstants.setForeground(style, Color.CYAN); break;
                        case '<': StyleConstants.setForeground(style, Color.MAGENTA); break;
                        default: StyleConstants.setForeground(style, Color.WHITE); break;
                    }

                    document.insertString(document.getLength(), String.valueOf(tile), style);
                }
                document.insertString(document.getLength(), "\n", null);
            }

            // Display full player stats
            Style statusStyle = styleContext.addStyle("StatusStyle", null);
            StyleConstants.setForeground(statusStyle, Color.WHITE);
            document.insertString(document.getLength(), "\nHP: " + player.getHp() + 
                                " | Hunger: " + String.format("%.2f", player.getHunger()) + 
                                " | Level: " + player.getLevel() +
                                " | Strength: " + player.getStrength() +
                                " | Gold: " + player.getGold() +
                                " | Armor: " + player.getArmor(), statusStyle);
            document.insertString(document.getLength(), "\n" + player.getStatusMessage(), statusStyle);

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
    System.out.println("Key Pressed: " + e.getKeyCode()); // ✅ Debugging output

    if (player.getHp() <= 0) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            System.exit(0);
        }
        return;
    }

    char move = ' ';
    switch (e.getKeyCode()) {
        case KeyEvent.VK_W: move = 'W'; break;
        case KeyEvent.VK_A: move = 'A'; break;
        case KeyEvent.VK_S: move = 'S'; break;
        case KeyEvent.VK_D: move = 'D'; break;
        default: return;
    }

    System.out.println("Moving player: " + move); // ✅ Debug output
    player.move(move);
    updateMapDisplay();
}


    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        new Main();
    }
}
