package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;



/**
 * Manages all monsters in the dungeon
 */
public class MonsterManager {
    private List<Monster> monsters = new ArrayList<>();
    private Random rand = new Random();
    
    public MonsterManager() {
        
    }
    
    /**
     * Populate the dungeon with monsters based on level
     */
    public void populateDungeon(Dungeon dungeon, int monsterCount) {
        char[][] map = dungeon.getMap();
        int level = dungeon.getLevelNumber();
        
        monsters.clear();
        
       
        List<int[]> validPositions = new ArrayList<>();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == '.') {
                    validPositions.add(new int[]{x, y});
                }
            }
        }
        
        Collections.shuffle(validPositions);
        
        monsterCount = Math.min(monsterCount, validPositions.size());
        
        
        for (int i = 0; i < monsterCount - 1; i++) {
            if (validPositions.isEmpty()) break;
            
            int[] pos = validPositions.remove(0);
            Monster monster = MonsterFactory.createForLevel(level, pos[0], pos[1]);
            monsters.add(monster);
        }
        
        // Maybe add a boss on higher levels (level 2+)
        // Downgraded for demonstration purposes. 0.3 -> 0.1
        if (level >= 2 && !validPositions.isEmpty() && rand.nextDouble() < 0.1) {
            int[] pos = validPositions.remove(0);
            String[] bossNames = {
                "Grimclaw the Destroyer", 
                "Nightwhisper", 
                "The Ancient Golem", 
                "Flameheart the Corrupted", 
                "King Rotface", 
                "Voidwalker Prime"
            };
            
            Monster boss = MonsterFactory.createBoss(
                bossNames[rand.nextInt(bossNames.length)], 
                pos[0], pos[1], level
            );
            monsters.add(boss);
            System.out.println("🔥 WARNING: Boss " + boss.getName() + " has appeared on level " + level + "!");
        }
    }
    
    /**
     * Handle monster turns
     */
    public void updateMonsters(Player player, char[][] map) {
        Iterator<Monster> iterator = monsters.iterator();
        while (iterator.hasNext()) {
            Monster monster = iterator.next();
    
            if (!monster.isAlive()) {
                iterator.remove();
                continue;
            }
    
            monster.updateStatus();
    
            if (monster.getStatusEffect().equals("stunned")) {
                continue;
            }
    
            int monsterX = monster.getX();
            int monsterY = monster.getY();
            int playerX = player.getX();
            int playerY = player.getY();
    
            boolean canAttack = Math.abs(monsterX - playerX) <= 1 && 
                               Math.abs(monsterY - playerY) <= 1;
    
            if (canAttack && monster.isHostile()) {
                int damage = monster.calculateAttackDamage();
                player.takeDamage(damage);
                System.out.println(monster.getName() + " attacks you for " + damage + " damage!");
            } else if (monster.isHostile()) {
                // Move towards player only if the path is clear
                int dx = Integer.compare(playerX, monsterX);
                int dy = Integer.compare(playerY, monsterY);
    
                int newX = monsterX + dx;
                int newY = monsterY + dy;
    
                if (newX >= 0 && newY >= 0 && newY < map.length && newX < map[0].length && map[newY][newX] == '.') {
                    // Check if the new position is not occupied by another monster
                    boolean positionOccupied = false;
                    for (Monster otherMonster : monsters) {
                        if (otherMonster.getX() == newX && otherMonster.getY() == newY && otherMonster.isAlive()) {
                            positionOccupied = true;
                            break;
                        }
                    }
    
                    if (!positionOccupied) {
                        monster.setX(newX);
                        monster.setY(newY);
                    }
                }
            }
        }
    }
    
    /**
     * Check if a monster is at the given position
     */
    public Monster getMonsterAt(int x, int y) {
        for (Monster monster : monsters) {
            if (monster.getX() == x && monster.getY() == y) {
                return monster;
            }
        }
        return null;
    }
    
    /**
     * Render monsters on map (call before displaying)
     */
    public void renderMonstersOnMap(char[][] mapCopy) {
        for (Monster monster : monsters) {
            if (monster.isAlive()) {
                mapCopy[monster.getY()][monster.getX()] = monster.getSymbol();
            }
        }
    }
    
    public List<Monster> getAllMonsters() {
        return monsters;
    }
    
    /**
     * Get a list of nearby monsters for display in UI
     */
    public List<Monster> getVisibleMonsters(int playerX, int playerY, int visibilityRange) {
        List<Monster> visible = new ArrayList<>();
        for (Monster monster : monsters) {
            int dx = Math.abs(monster.getX() - playerX);
            int dy = Math.abs(monster.getY() - playerY);
            int distance = Math.max(dx, dy); 
            
            if (distance <= visibilityRange) {
                visible.add(monster);
            }
        }
        return visible;
    }
}
