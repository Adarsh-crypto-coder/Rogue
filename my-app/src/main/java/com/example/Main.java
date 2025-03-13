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
        // Objectizing Player Movement. bu Suhwan Kim. Feb 22
        if (player == null) {
            player = new Player(playerStart, dungeon.getMap(), dungeon);
        } else {
            player.setX(playerStart[0]);
            player.setY(playerStart[1]);
            player.setMap(dungeon.getMap());
            player.setDungeon(dungeon);
        }
    
        player.setFloor(dungeon.getLevelNumber());
        System.out.println("✅ Dungeon Loaded: " + levelFile);
        updateMapDisplay();
    }

    private void updateMapDisplay() {
        try {
            document.remove(0, document.getLength());
            // Use getMapWithMonsters to get a map copy with monsters overlaid
            char[][] displayMap = dungeon.getMapWithMonsters();
            
            int playerX = player.getX();
            int playerY = player.getY();
    
            for (int y = 0; y < displayMap.length; y++) {
                for (int x = 0; x < displayMap[0].length; x++) {
                    // Render the player with '@' if on the same tile
                    char tile = (x == playerX && y == playerY) ? '@' : displayMap[y][x];
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
    
            Style statusStyle = styleContext.addStyle("StatusStyle", null);
            StyleConstants.setForeground(statusStyle, Color.WHITE);
            document.insertString(document.getLength(), 
                                "\nFLOOR: " + player.getFloor() + 
                                " | LEVEL: " + player.getLevel() + 
                                " | HP: " + player.getHp() + "/" + player.getMaxHp() + 
                                " | Hunger: " + String.format("%.2f", player.getHunger()) + 
                                " | Strength: " + player.getStrength() + 
                                " | Gold: " + player.getGold() + 
                                " | Armor: " + player.getArmor() + 
                                " | XP: " + player.getXp() + "/" + player.getXpToNextLevel(), statusStyle);
            document.insertString(document.getLength(), "\n" + player.getStatusMessage(), statusStyle);
    
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    
    // ✅ Use KeyBindings instead of KeyListener
    private void setupKeyBindings() {
        InputMap inputMap = textPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = textPane.getActionMap();

        // Movement key bindings
        inputMap.put(KeyStroke.getKeyStroke("W"), "moveUp");
        inputMap.put(KeyStroke.getKeyStroke("A"), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke("S"), "moveDown");
        inputMap.put(KeyStroke.getKeyStroke("D"), "moveRight");

        actionMap.put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.move('W');
                processTurn();
            }
        });

        actionMap.put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.move('A');
                processTurn();
            }
        });

        actionMap.put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.move('S');
                processTurn();
            }
        });

        actionMap.put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.move('D');
                processTurn();
            }
        });
        
        // Attack key binding (F key)
        inputMap.put(KeyStroke.getKeyStroke("F"), "attack");
        actionMap.put("attack", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Monster monster = dungeon.getMonsterAt(player.getX(), player.getY());
                if (monster != null) {
                    player.attackMonster(monster);
                } else {
                    // Update status message if no monster is present
                    // (Assumes Player class has a way to update statusMessage, e.g., via attackMonster or another method.)
                    System.out.println("No monster here to attack!");
                }
                processTurn();
            }
        });
    }
    
    // ✅ Process a full turn: update monsters, check for level change, and update display
    private void processTurn() {
        // Let monsters take their turn
        dungeon.updateMonsters(player);
        
        // Check if player stepped on stairs or died
        checkForLevelChange();
    }

    // ✅ Check if player needs to switch levels or if game is over
    private void checkForLevelChange() {
        int[] stairsUp = dungeon.getStairsUp();
        int[] stairsDown = dungeon.getStairsDown();
    
        if (stairsDown != null && player.getX() == stairsDown[0] && player.getY() == stairsDown[1]) {
            if (dungeon.getLevelNumber() < 8) {  // 마지막 레벨인 8에서는 더 이상 내려갈 수 없음
                System.out.println("🔽 Moving to Level " + (dungeon.getLevelNumber() + 1) + "...");
                loadDungeon("levels/level" + (dungeon.getLevelNumber() + 1) + ".txt");
            } else {
                System.out.println("You're at the bottom of the dungeon!");
                player.setStatusMessage("You're at the bottom of the dungeon!");  // Player 클래스의 setStatusMessage 메서드 사용
            }
        } else if (stairsUp != null && player.getX() == stairsUp[0] && player.getY() == stairsUp[1]) {
            if (dungeon.getLevelNumber() > 1) {
                System.out.println("🔼 Moving to Level " + (dungeon.getLevelNumber() - 1) + "...");
                loadDungeon("levels/level" + (dungeon.getLevelNumber() - 1) + ".txt");
            } else {
                System.out.println("You are already at the top floor!");
                player.setStatusMessage("You are already at the top floor!");  // Player 클래스의 setStatusMessage 메서드 사용
            }
        } else if (player.getHp() <= 0) {
            System.out.println("💀 Game Over! You died.");
            JOptionPane.showMessageDialog(this, "Game Over! You died.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
        
        updateMapDisplay();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
