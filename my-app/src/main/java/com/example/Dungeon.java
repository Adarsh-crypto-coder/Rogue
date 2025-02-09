package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * Code that implements the walls and structure of the dungeon.
 * For Splint 2:
 * - Implementing a procedural dungeon with generated
 * - Randomized structure of walls and empty spaces
 * 
 * Author : Suhwan Kim
 * Date : Feb 8, 2025
 */
public class Dungeon {
    private char[][] map;
    private int width, height;
    private List<Room> rooms;
    private Random random;

    public Dungeon(int width, int height) {
        this.width = width;
        this.height = height;
        this.map = new char[height][width];
        this.rooms = new ArrayList<>();
        this.random = new Random();
        generateDungeon();
    }

    private void generateDungeon() {
        // Initialize map (fill with empty space)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map[y][x] = ' '; // empty space
            }
        }
    
        // Create a room
        int numRooms = random.nextInt(6) + 5; // number of room(default : 5~10 rooms)
        for (int i = 0; i < numRooms; i++) {
            int roomWidth = random.nextInt(13) + 3; // Sizes 3-15 (minimum 3 spaces)
            int roomHeight = random.nextInt(13) + 3; // Sizes 3-15 (minimum 3 spaces)
            int x = random.nextInt(width - roomWidth - 1) + 1;
            int y = random.nextInt(height - roomHeight - 1) + 1;
    
            Room newRoom = new Room(x, y, roomWidth, roomHeight);
            boolean overlap = false;
            for (Room room : rooms) {
                if (newRoom.intersects(room)) {
                    overlap = true;
                    break;
                }
            }
    
            if (!overlap) {
                rooms.add(newRoom);
                carveRoom(newRoom);
            }
        }
    
        // Connect Rooms
        for (int i = 1; i < rooms.size(); i++) {
            Room prevRoom = rooms.get(i - 1);
            Room currentRoom = rooms.get(i);
            connectRooms(prevRoom, currentRoom);
        }
    }
    

    private void carveRoom(Room room) {
        // Create Walls
        for (int y = room.y; y < room.y + room.height; y++) {
            for (int x = room.x; x < room.x + room.width; x++) {
                if (y == room.y || y == room.y + room.height - 1 || x == room.x || x == room.x + room.width - 1) {
                    map[y][x] = '#'; // 벽
                } else {
                    map[y][x] = '.'; // 바닥
                }
            }
        }
    }

    private void connectRooms(Room room1, Room room2) {
        int x1 = room1.x + random.nextInt(room1.width - 2) + 1;
        int y1 = room1.y + random.nextInt(room1.height - 2) + 1;
        int x2 = room2.x + random.nextInt(room2.width - 2) + 1;
        int y2 = room2.y + random.nextInt(room2.height - 2) + 1;
    
        // Connect aisles only vertically or horizontally
        while (x1 != x2 || y1 != y2) {
            if (x1 != x2) {
                if (x1 < x2) x1++;
                else if (x1 > x2) x1--;
            } else if (y1 != y2) {
                if (y1 < y2) y1++;
                else if (y1 > y2) y1--;
            }
    
            if (map[y1][x1] == '#' || map[y1][x1] == ' ') {
                map[y1][x1] = '=';
            }
        }
    }

    public char[][] getMap() {
        return map;
    }

    public int[] getRandomRoomCenter() {
        Room room = rooms.get(random.nextInt(rooms.size()));
        int centerX = room.x + room.width / 2;
        int centerY = room.y + room.height / 2;
        return new int[]{centerX, centerY};
    }

    private static class Room {
        int x, y, width, height;

        Room(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        boolean intersects(Room other) {
            return x < other.x + other.width &&
                   x + width > other.x &&
                   y < other.y + other.height &&
                   y + height > other.y;
        }
    }
}
