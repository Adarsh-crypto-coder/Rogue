package com.example;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.util.List;

/**
 * A window that displays the player's inventory and allows item management
 */
public class Inventorydisplay extends JFrame {
    private Player player;
    private JPanel inventoryPanel;
    private JLabel statusLabel;
    private JPanel equippedPanel;
    private JPanel statsPanel;
    
    public Inventorydisplay(Player player) {
        this.player = player;
        
        setTitle("Inventory - Dungeon Game");
        setSize(500, 600);
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Player stats panel (top)
        statsPanel = createStatsPanel();
        add(statsPanel, BorderLayout.NORTH);
        
        // Main panel (center) contains inventory and equipped items
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        
        // Inventory panel
        inventoryPanel = new JPanel();
        inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(inventoryPanel);
        scrollPane.setBorder(new TitledBorder("Inventory Items"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Equipped items panel (right)
        equippedPanel = new JPanel();
        equippedPanel.setLayout(new BoxLayout(equippedPanel, BoxLayout.Y_AXIS));
        equippedPanel.setBorder(new TitledBorder("Equipped Items"));
        equippedPanel.setPreferredSize(new Dimension(200, 200));
        mainPanel.add(equippedPanel, BorderLayout.EAST);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Status message (bottom)
        statusLabel = new JLabel("Select an item to use, equip, or drop");
        statusLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(statusLabel, BorderLayout.SOUTH);
        
        // Initial update
        updateDisplay();
        
        // Add window listener to update main game UI when closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // For now, just dispose the window
                dispose();
            }
        });
        
        // Add help button
        JButton helpButton = new JButton("Help");
        helpButton.addActionListener(e -> showHelp());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(helpButton);
        add(buttonPanel, BorderLayout.NORTH);
        
        // Position window
        setLocationRelativeTo(null);
    }
    
    /**
     * Show help dialog
     */
    private void showHelp() {
        String helpText = 
            "<html><h2>Inventory Help</h2>" +
            "<p><b>Consumable Items:</b> Items that can be used once for their effect</p>" +
            "<ul>" +
            "<li><b>Food:</b> Restores hunger</li>" +
            "<li><b>Potions:</b> Restore health or provide temporary effects</li>" +
            "</ul>" +
            "<p><b>Equipment:</b> Weapons and armor that can be equipped</p>" +
            "<ul>" +
            "<li><b>Weapons:</b> Increase your strength</li>" +
            "<li><b>Armor:</b> Reduces damage taken</li>" +
            "</ul>" +
            "<p><b>Actions:</b></p>" +
            "<ul>" +
            "<li><b>Use:</b> Consume an item to get its effect</li>" +
            "<li><b>Equip:</b> Wear armor or wield a weapon</li>" +
            "<li><b>Drop:</b> Remove an item from inventory</li>" +
            "</ul></html>";
        
        JOptionPane.showMessageDialog(this, helpText, "Inventory Help", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Create the player stats panel
     */
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Player Stats"));
        
        JPanel statsGrid = new JPanel(new GridLayout(3, 4, 10, 5));
        statsGrid.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // Health
        statsGrid.add(new JLabel("HP:"));
        statsGrid.add(new JLabel(player.getHp() + "/" + player.getMaxHp()));
        
        // Hunger
        statsGrid.add(new JLabel("Hunger:"));
        statsGrid.add(new JLabel(String.format("%.1f/%.1f", player.getHunger(), player.getMaxHunger())));
        
        // Strength
        statsGrid.add(new JLabel("Strength:"));
        statsGrid.add(new JLabel(String.valueOf(player.getStrength())));
        
        // Armor
        statsGrid.add(new JLabel("Armor:"));
        statsGrid.add(new JLabel(String.valueOf(player.getArmor())));
        
        // Level
        statsGrid.add(new JLabel("Level:"));
        statsGrid.add(new JLabel(String.valueOf(player.getLevel())));
        
        // Gold
        statsGrid.add(new JLabel("Gold:"));
        statsGrid.add(new JLabel(String.valueOf(player.getGold())));
        
        panel.add(statsGrid, BorderLayout.CENTER);
        
        // Inventory capacity
        JLabel capacityLabel = new JLabel("Inventory: " + player.getInventory().size() + 
                                         "/" + player.getInventoryMaxSize() + " items");
        capacityLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(capacityLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Update the entire display
     */
    public void updateDisplay() {
        updateStatsPanel();
        updateInventoryPanel();
        updateEquippedPanel();
    }
    
    /**
     * Update just the stats panel
     */
    private void updateStatsPanel() {
        // Remove old panel and create a new one
        remove(statsPanel);
        statsPanel = createStatsPanel();
        add(statsPanel, BorderLayout.NORTH);
        revalidate();
        repaint();
    }
    
    /**
     * Update the inventory panel
     */
    private void updateInventoryPanel() {
        inventoryPanel.removeAll();
        
        List<Item> inventory = player.getInventory();
        
        if (inventory.isEmpty()) {
            JLabel emptyLabel = new JLabel("Your inventory is empty");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setBorder(new EmptyBorder(20, 0, 0, 0));
            inventoryPanel.add(emptyLabel);
        } else {
            // Add each inventory item with action buttons
            for (int i = 0; i < inventory.size(); i++) {
                inventoryPanel.add(createItemPanel(inventory.get(i), i));
            }
        }
        
        inventoryPanel.revalidate();
        inventoryPanel.repaint();
    }
    
    /**
     * Update the equipped items panel
     */
    private void updateEquippedPanel() {
        equippedPanel.removeAll();
        
        if (player.getEquippedWeapon() == null && player.getEquippedArmor() == null) {
            JLabel emptyLabel = new JLabel("Nothing equipped");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setBorder(new EmptyBorder(20, 0, 0, 0));
            equippedPanel.add(emptyLabel);
        } else {
            if (player.getEquippedWeapon() != null) {
                equippedPanel.add(createEquippedItemPanel(player.getEquippedWeapon(), "weapon"));
            }
            
            if (player.getEquippedArmor() != null) {
                equippedPanel.add(createEquippedItemPanel(player.getEquippedArmor(), "armor"));
            }
        }
        
        equippedPanel.revalidate();
        equippedPanel.repaint();
    }
    
    /**
     * Create a panel for a single inventory item
     */
    private JPanel createItemPanel(Item item, int index) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            new EmptyBorder(8, 5, 8, 5)
        ));
        
        String typeInfo = "";
        if (item.isConsumable()) {
            typeInfo = " [Consumable]";
        } else if (item.getType().equals("weapon")) {
            typeInfo = " [Weapon]";
        } else if (item.getType().equals("armor")) {
            typeInfo = " [Armor]";
        } else if (item.getType().equals("scroll")) {
            typeInfo = " [Scroll]";
        }
        
        JLabel itemLabel = new JLabel(item.getName() + typeInfo);
        itemLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        
        JLabel descLabel = new JLabel(item.getDescription());
        descLabel.setForeground(Color.DARK_GRAY);
        descLabel.setFont(new Font("Dialog", Font.ITALIC, 11));
        
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);
        infoPanel.add(itemLabel);
        infoPanel.add(descLabel);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        // Action buttons based on item type
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        
        if (item.isConsumable()) {
            JButton useButton = new JButton("Use");
            useButton.addActionListener(e -> {
                if (player.useConsumable(index)) {
                    statusLabel.setText(player.getStatusMessage());
                    updateDisplay();
                } else {
                    statusLabel.setText(player.getStatusMessage());
                }
            });
            buttonPanel.add(useButton);
        } else if (item.getType().equals("weapon")) {
            JButton equipButton = new JButton("Equip");
            equipButton.addActionListener(e -> {
                if (player.equipWeapon(index)) {
                    statusLabel.setText(player.getStatusMessage());
                    updateDisplay();
                } else {
                    statusLabel.setText(player.getStatusMessage());
                }
            });
            buttonPanel.add(equipButton);
        } else if (item.getType().equals("armor")) {
            JButton equipButton = new JButton("Equip");
            equipButton.addActionListener(e -> {
                if (player.equipArmor(index)) {
                    statusLabel.setText(player.getStatusMessage());
                    updateDisplay();
                } else {
                    statusLabel.setText(player.getStatusMessage());
                }
            });
            buttonPanel.add(equipButton);
        }
        
        // All items can be dropped
        JButton dropButton = new JButton("Drop");
        dropButton.addActionListener(e -> {
            if (player.dropItem(index)) {
                statusLabel.setText(player.getStatusMessage());
                updateDisplay();
            } else {
                statusLabel.setText(player.getStatusMessage());
            }
        });
        buttonPanel.add(dropButton);
        
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Create a panel for an equipped item
     */
    private JPanel createEquippedItemPanel(Item item, String slot) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            new EmptyBorder(8, 5, 8, 5)
        ));
        
        String slotInfo = slot.equals("weapon") ? "Weapon" : "Armor";
        
        JLabel itemLabel = new JLabel(slotInfo + ": " + item.getName());
        itemLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        
        String effectDesc = slot.equals("weapon") ? 
            "+" + item.getEffectValue() + " strength" : 
            "+" + item.getEffectValue() + " armor";
        
        JLabel descLabel = new JLabel(effectDesc);
        descLabel.setForeground(new Color(0, 100, 0)); // Dark green
        descLabel.setFont(new Font("Dialog", Font.ITALIC, 11));
        
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);
        infoPanel.add(itemLabel);
        infoPanel.add(descLabel);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        // Unequip button
        JButton unequipButton = new JButton("Unequip");
        unequipButton.addActionListener(e -> {
            boolean success = false;
            if (slot.equals("weapon")) {
                success = player.unequipWeapon();
            } else if (slot.equals("armor")) {
                success = player.unequipArmor();
            }
            
            if (success) {
                statusLabel.setText(player.getStatusMessage());
                updateDisplay();
            } else {
                statusLabel.setText(player.getStatusMessage());
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(unequipButton);
        
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    // No main method - this class should only be used from within the game
}
