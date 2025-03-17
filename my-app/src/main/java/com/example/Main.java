package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.text.*;
import java.io.File;
import java.util.Random;

public class Main extends JFrame {
    private Dungeon dungeon;
    private Player player;
    private JTextPane textPane;
    private StyleContext styleContext;
    private DefaultStyledDocument document;
    private String currentLevelFile;
    private Random random = new Random();
    private Inventorydisplay inventoryDisplay;

    public Main() {
        styleContext = new StyleContext();
        document = new DefaultStyledDocument(styleContext);
        
        textPane = new JTextPane();
        textPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textPane.setEditable(false);
        textPane.setBackground(Color.BLACK);
        textPane.setDocument(document);

        currentLevelFile = "levels/level1.txt";
        File levelFile = new File(currentLevelFile);
        System.out.println("🔍 Looking for level file at: " + levelFile.getAbsolutePath());

        if (!levelFile.exists()) {
            System.out.println("❌ Error: Level file not found at " + levelFile.getAbsolutePath());
            JOptionPane.showMessageDialog(this, "Level file not found!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        dungeon = new Dungeon(currentLevelFile);
        player = new Player(new int[]{1, 1}, new char[0][0], dungeon); 
        loadDungeon(currentLevelFile);
        
        // Create UI panel with buttons
        JPanel buttonPanel = createButtonPanel();

        // Main panel setup
        add(new JScrollPane(textPane), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setTitle("Rogue-like Dungeon");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setFocusable(true);
        requestFocusInWindow();
        addWindowFocusListener(new java.awt.event.WindowAdapter() {
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                requestFocusInWindow();
            }
        });

        setupKeyBindings();
        
        setVisible(true);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        // Inventory button
        JButton inventoryButton = new JButton("Inventory");
        inventoryButton.addActionListener(e -> openInventory());
        panel.add(inventoryButton);
        
        // Attack button
        JButton attackButton = new JButton("Attack (F)");
        attackButton.addActionListener(e -> {
            Monster monster = dungeon.getMonsterAt(player.getX(), player.getY());
            if (monster != null) {
                player.attackMonster(monster);
            }
            processTurn();
        });
        panel.add(attackButton);
        
        // Use item button
        JButton useItemButton = new JButton("Use Item");
        useItemButton.addActionListener(e -> {
            if (player.getInventory().isEmpty()) {
                player.takeDamage(0); // Hack to update status message
                player.getStatusMessage();
                updateMapDisplay();
                return;
            }
            
            String[] options = player.getInventory().stream()
                .map(item -> item.getName() + " - " + item.getDescription())
                .toArray(String[]::new);
                
            int choice = JOptionPane.showOptionDialog(
                this,
                "Select an item to use:",
                "Use Item",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );
            
            if (choice >= 0) {
                player.useConsumable(choice);
                processTurn();
            }
        });
        panel.add(useItemButton);
        
        return panel;
    }
    
    private void openInventory() {
        if (inventoryDisplay == null || !inventoryDisplay.isDisplayable()) {
            inventoryDisplay = new Inventorydisplay(player);
            inventoryDisplay.setLocationRelativeTo(this);
            inventoryDisplay.setVisible(true);
        } else {
            inventoryDisplay.toFront();
        }
    }

    private void loadDungeon(String levelFile) {
        System.out.println("🔄 Loading dungeon from: " + levelFile);
        currentLevelFile = levelFile;
        dungeon = new Dungeon(levelFile);

        if (dungeon.getMap() == null || dungeon.getMap().length == 0) {
            System.out.println("❌ Error: Level file is empty or not loaded!");
            JOptionPane.showMessageDialog(this, "Error: Level file is empty or not loaded!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        int[] playerStart = dungeon.getPlayerStartPosition();
        player.setPosition(playerStart[0], playerStart[1]);
        player.setMap(dungeon.getMap());
        
        System.out.println("✅ Dungeon Loaded: " + levelFile);
        
        // Add some items to the dungeon for testing (in real game, these would be part of level files)
        addRandomItemsToMap();
        
        updateMapDisplay();
    }
    
    private void addRandomItemsToMap() {
        // Add some random consumable items to the map
        char[][] map = dungeon.getMap();
        int itemCount = 3 + random.nextInt(3); // 3-5 items
        
        for (int i = 0; i < itemCount; i++) {
            // Find a random floor tile
            int attempts = 0;
            int maxAttempts = 100;
            
            while (attempts < maxAttempts) {
                int x = random.nextInt(map[0].length);
                int y = random.nextInt(map.length);
                
                if (map[y][x] == '.') {
                    map[y][x] = '!'; // Item symbol
                    break;
                }
                
                attempts++;
            }
        }
        
        // Add some gold as well
        int goldCount = 2 + random.nextInt(3); // 2-4 gold piles
        
        for (int i = 0; i < goldCount; i++) {
            int attempts = 0;
            int maxAttempts = 100;
            
            while (attempts < maxAttempts) {
                int x = random.nextInt(map[0].length);
                int y = random.nextInt(map.length);
                
                if (map[y][x] == '.') {
                    map[y][x] = '$'; // Gold symbol
                    break;
                }
                
                attempts++;
            }
        }
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
                    
                    // Updated color scheme to include items and gold
                    switch (tile) {
                        case '#': StyleConstants.setForeground(style, Color.YELLOW); break;
                        case '.': StyleConstants.setForeground(style, Color.LIGHT_GRAY); break;
                        case '>': StyleConstants.setForeground(style, Color.CYAN); break;
                        case '<': StyleConstants.setForeground(style, Color.MAGENTA); break;
                        case '@': StyleConstants.setForeground(style, Color.GREEN); break;
                        case '!': StyleConstants.setForeground(style, Color.ORANGE); break; // Item
                        case '$': StyleConstants.setForeground(style, Color.YELLOW); break; // Gold
                        case 'Z': case 'B': case 'H': case 'C': case 'E': case 'A': 
                        case 'L': case 'D': case 'K': case 'G': case 'Ω': case 'X':
                            StyleConstants.setForeground(style, Color.RED); break; // Monsters
                        default: StyleConstants.setForeground(style, Color.WHITE); break;
                    }
                    
                    document.insertString(document.getLength(), String.valueOf(tile), style);
                }
                document.insertString(document.getLength(), "\n", null);
            }
    
            int currentFloor = dungeon.getLevelNumber();
            Style statusStyle = styleContext.addStyle("StatusStyle", null);
            StyleConstants.setForeground(statusStyle, Color.WHITE);
            
            // Display legend
            document.insertString(document.getLength(), "\n=== MAP LEGEND ===\n", statusStyle);
            document.insertString(document.getLength(), "@ = Player | # = Wall | . = Floor | > = Stairs Down | < = Stairs Up\n", statusStyle);
            document.insertString(document.getLength(), "! = Item | $ = Gold | Letters = Monsters\n", statusStyle);
            
            // Display player stats
            document.insertString(document.getLength(), "\n=== PLAYER STATS ===\n", statusStyle);
            document.insertString(document.getLength(), "FLOOR: " + currentFloor + 
                                    " | LEVEL: " + player.getLevel() + 
                                    " | HP: " + player.getHp() + 
                                    " | Hunger: " + String.format("%.2f", player.getHunger()) + 
                                    " | Strength: " + player.getStrength() +
                                    " | Gold: " + player.getGold() +
                                    " | Armor: " + player.getArmor() + "\n", statusStyle);
            
            // Display status message
            document.insertString(document.getLength(), "▶ " + player.getStatusMessage() + "\n", statusStyle);
            
            // Display inventory summary
            document.insertString(document.getLength(), "\n=== INVENTORY (" + player.getInventory().size() + " items) ===\n", statusStyle);
            
            // Show first few items
            int maxDisplay = Math.min(3, player.getInventory().size());
            for (int i = 0; i < maxDisplay; i++) {
                Item item = player.getInventory().get(i);
                document.insertString(document.getLength(), "- " + item.getName() + "\n", statusStyle);
            }
            
            if (player.getInventory().size() > maxDisplay) {
                document.insertString(document.getLength(), "- ... (" + (player.getInventory().size() - maxDisplay) + " more items)\n", statusStyle);
            }
            
            if (player.getInventory().isEmpty()) {
                document.insertString(document.getLength(), "- No items\n", statusStyle);
            }
            
            // Display controls
            document.insertString(document.getLength(), "\n=== CONTROLS ===\n", statusStyle);
            document.insertString(document.getLength(), "WASD = Movement | F = Attack | I = Inventory\n", statusStyle);
    
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
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
                    System.out.println("No monster here to attack!");
                }
                processTurn();
            }
        });
        
        // Inventory key binding (I key)
        inputMap.put(KeyStroke.getKeyStroke("I"), "inventory");
        actionMap.put("inventory", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openInventory();
            }
        });
    }
    
    private void processTurn() {
        // Let monsters take their turn
        dungeon.updateMonsters(player);
        
        // Check if player stepped on stairs or died
        checkForLevelChange();
    }

    private void checkForLevelChange() {
        int[] stairsUp = dungeon.getStairsUp();
        int[] stairsDown = dungeon.getStairsDown();

        if (stairsDown != null && player.getX() == stairsDown[0] && player.getY() == stairsDown[1]) {
            int nextLevel = dungeon.getLevelNumber() + 1;
            File nextLevelFile = new File("levels/level" + nextLevel + ".txt");
            if (nextLevelFile.exists()) {
                System.out.println("🔽 Moving to Level " + nextLevel + "...");
                player.onLevelChange(nextLevel);
                loadDungeon(nextLevelFile.getPath());
                return;
            } else if (dungeon.isLastLevel()) {
                player.setStatusMessage("It's at the bottom of Dungeon.");
                updateMapDisplay();
                return;
            }
        } else if (stairsUp != null && player.getX() == stairsUp[0] && player.getY() == stairsUp[1]) {
            int prevLevel = dungeon.getLevelNumber() - 1;
            if (prevLevel >= 1) {
                File prevLevelFile = new File("levels/level" + prevLevel + ".txt");
                if (prevLevelFile.exists()) {
                    System.out.println("🔼 Moving to Level " + prevLevel + "...");
                    player.onLevelChange(prevLevel);
                    loadDungeon(prevLevelFile.getPath());
                    return;
                }
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
