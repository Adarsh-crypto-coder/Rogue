package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.text.*;
import java.io.File;

public class Main extends JFrame {
    private Dungeon dungeon;
    private Player player;
    private JTextPane textPane;
    private StyleContext styleContext;
    private DefaultStyledDocument document;
    private String currentLevelFile;  // ✅ Track current level file

    public Main() {
        // ✅ Initialize document BEFORE loading the dungeon
        styleContext = new StyleContext();
        document = new DefaultStyledDocument(styleContext);
        
        // ✅ Set up JTextPane
        textPane = new JTextPane();
        textPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textPane.setEditable(false);
        textPane.setBackground(Color.BLACK);
        textPane.setDocument(document); // ✅ Assign the document here

        // ✅ Ensure the level file exists
        currentLevelFile = "levels/level1.txt"; // ✅ Start at Level 1
        File levelFile = new File(currentLevelFile);
        System.out.println("🔍 Looking for level file at: " + levelFile.getAbsolutePath());

        if (!levelFile.exists()) {
            System.out.println("❌ Error: Level file not found at " + levelFile.getAbsolutePath());
            JOptionPane.showMessageDialog(this, "Level file not found!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // ✅ Now it's safe to load the level
        loadDungeon(currentLevelFile);

        // ✅ UI Setup
        add(new JScrollPane(textPane), BorderLayout.CENTER);
        setTitle("Rogue-like Dungeon");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setFocusable(true);
        requestFocusInWindow();
        addWindowFocusListener(new java.awt.event.WindowAdapter() {
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                requestFocusInWindow();
            }
        });

        // ✅ Setup keybindings
        setupKeyBindings();

        setVisible(true);
    }

    // ✅ Method to load the dungeon
    private void loadDungeon(String levelFile) {
        System.out.println("🔄 Loading dungeon from: " + levelFile);
        currentLevelFile = levelFile;  // ✅ Track the current level
        dungeon = new Dungeon(levelFile);

        if (dungeon.getMap() == null || dungeon.getMap().length == 0) {
            System.out.println("❌ Error: Level file is empty or not loaded!");
            JOptionPane.showMessageDialog(this, "Error: Level file is empty or not loaded!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        int[] playerStart = dungeon.getPlayerStartPosition();
        player = new Player(playerStart, dungeon.getMap());

        System.out.println("✅ Dungeon Loaded: " + levelFile);
        updateMapDisplay();
    }

    private void updateMapDisplay() {
        if (document == null) {
            System.out.println("❌ Error: `document` is null, skipping updateMapDisplay()");
            return;
        }
    
        try {
            document.remove(0, document.getLength());
            char[][] map = dungeon.getMap();
    
            if (map == null || map.length == 0 || map[0].length == 0) {
                System.out.println("❌ Error: Dungeon map is empty!");
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
                        case '>': StyleConstants.setForeground(style, Color.CYAN); break;
                        case '<': StyleConstants.setForeground(style, Color.MAGENTA); break;
                        default: StyleConstants.setForeground(style, Color.WHITE); break;
                    }
    
                    document.insertString(document.getLength(), String.valueOf(tile), style);
                }
                document.insertString(document.getLength(), "\n", null);
            }
    
            // ✅ Show the correct level number from the text file
            int currentLevel = dungeon.getLevelNumber(); 
    
            // Display full player stats with level
            Style statusStyle = styleContext.addStyle("StatusStyle", null);
            StyleConstants.setForeground(statusStyle, Color.WHITE);
            document.insertString(document.getLength(), "\nLEVEL: " + currentLevel + 
                                " | HP: " + player.getHp() + 
                                " | Hunger: " + String.format("%.2f", player.getHunger()) + 
                                " | Strength: " + player.getStrength() +
                                " | Gold: " + player.getGold() +
                                " | Armor: " + player.getArmor(), statusStyle);
            document.insertString(document.getLength(), "\n" + player.getStatusMessage(), statusStyle);
    
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    

    // ✅ Use KeyBindings instead of KeyListener
    private void setupKeyBindings() {
        InputMap inputMap = textPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = textPane.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("W"), "moveUp");
        inputMap.put(KeyStroke.getKeyStroke("A"), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke("S"), "moveDown");
        inputMap.put(KeyStroke.getKeyStroke("D"), "moveRight");

        actionMap.put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.move('W');
                checkForLevelChange();
            }
        });

        actionMap.put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.move('A');
                checkForLevelChange();
            }
        });

        actionMap.put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.move('S');
                checkForLevelChange();
            }
        });

        actionMap.put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.move('D');
                checkForLevelChange();
            }
        });
    }

    // ✅ Check if player needs to switch levels
    private void checkForLevelChange() {
        int[] stairsUp = dungeon.getStairsUp();
        int[] stairsDown = dungeon.getStairsDown();
    
        if (stairsDown != null && player.getX() == stairsDown[0] && player.getY() == stairsDown[1]) {
            if (currentLevelFile.equals("levels/level1.txt")) {
                System.out.println("🔽 Moving to Level 2...");
                loadDungeon("levels/level2.txt");
            } else if (currentLevelFile.equals("levels/level2.txt")) {
                System.out.println("🔽 Moving to Level 3...");
                loadDungeon("levels/level3.txt");
            }
        } 
        else if (stairsUp != null && player.getX() == stairsUp[0] && player.getY() == stairsUp[1]) {
            if (currentLevelFile.equals("levels/level3.txt")) {
                System.out.println("🔼 Moving to Level 2...");
                loadDungeon("levels/level2.txt");
            } else if (currentLevelFile.equals("levels/level2.txt")) {
                System.out.println("🔼 Moving to Level 1...");
                loadDungeon("levels/level1.txt");
            }
        } 
        else if (player.getHp() <= 0) {
            System.out.println("💀 Game Over! You died.");
            JOptionPane.showMessageDialog(this, "Game Over! You died.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        } 
        else {
            updateMapDisplay();
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}
