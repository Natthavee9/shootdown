package tile;

import java.awt.Graphics;
import main.GamePanel;

public class TileManager {

      GamePanel gp;
      public Tile[] tile; // Array to store tile types (up to 10 types)
      public int[][] mapTileNum; // Active map layout [col][row]

      public int currentLevel = 1; // Tracks current level index

     
      public final int tileSize = 64;
      public final int maxScreenCol = 20;
      public final int maxScreenRow = 12;

      // Level 1 map layout (20 columns x 12 rows, 64px per tile) aligned with map_sewer_lab.png
      // 0: Air, 1: Main ground, 2: Floating platform, 3: Acid pool, 4: Wooden crate
      int[][] mapLevel1 = {
                  // R0 - R4 (y = 0..319): Ceiling / upper sky (empty air)
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

                  // R5 (y = 320..384): High turret (c5), pipe catwalk (c6..9), top ladder catwalk (c12..14)
                  { 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0 },

                  // R6 (y = 384..448): Platform 1 (c2,3), mid turret (c5,6), pipe catwalk (c6..9), barrel ledge (c11,12)
                  { 0, 0, 2, 2, 0, 2, 2, 2, 2, 2, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0 },

                  // R7 (y = 448..512): Left brick (c0,1), bridge/walkway (c5..9), acid gap 2 (c10), sewer ground (c11..16), door floor (c17..19)
                  { 1, 1, 0, 0, 0, 2, 2, 2, 2, 2, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 },

                  // R8 - R11 (y = 512..768): Underground base & Acid pit (cols 2..10 is acid)
                  { 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
                  { 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
                  { 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
                  { 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1 }
      };

      // Level 2 map layout (Military Sunset Outpost) aligned with map_sunset.png
      int[][] mapLevel2 = {
                  // R0 - R5 (y = 0..383): Sky, watchtower tower, mountains
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },

                  // R6 (y = 384..447): Watchtower balcony (c1..2), Pillar 1 (c5..6), Pillar 2 (c10..12), Radar roof (c13..15)
                  { 0, 2, 2, 0, 0, 2, 2, 0, 0, 0, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0 },

                  // R7 (y = 448..511): Bunker terrace (c12..16)
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0 },

                  // R8 - R11 (y = 512..768): Continuous solid ground across all columns (no acid)
                  { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
                  { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
                  { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
                  { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }
      };

      public TileManager(GamePanel gp) {
            this.gp = gp;
            tile = new Tile[10];
            mapTileNum = new int[maxScreenCol][maxScreenRow];

            setupTiles();
            loadMap(currentLevel); // Load level 1 by default
      }

      // Configure collision properties for each tile type
      public void setupTiles() {
            // 0: Air / Empty space (passable)
            tile[0] = new Tile();
            tile[0].collision = false;

            // 1: Main ground (solid)
            tile[1] = new Tile();
            tile[1].collision = true;

            // 2: Floating metal platform (solid standing surface, allows jump-through from bottom)
            tile[2] = new Tile();
            tile[2].collision = true;

            // 3: Acid pool (non-collidable: entities fall through)
            tile[3] = new Tile();
            tile[3].collision = false;

            // 4: Wooden crate (solid)
            tile[4] = new Tile();
            tile[4].collision = true;
      }

      public void loadMap(int level) {
            currentLevel = level;
            platforms = (level == 2) ? platformsLevel2 : platformsLevel1;
            int[][] targetMap = (level == 1) ? mapLevel1 : mapLevel2;

            // Copy selected level data into active map array [col][row]
            for (int row = 0; row < maxScreenRow; row++) {
                  for (int col = 0; col < maxScreenCol; col++) {
                        mapTileNum[col][row] = targetMap[row][col];
                  }
            }
      }

      // Check whether a specific tile coordinate has solid collision
      public boolean isSolid(int col, int row) {
            if (col < 0 || col >= maxScreenCol || row < 0 || row >= maxScreenRow) {
                  return false;
            }
            int tileNum = mapTileNum[col][row];
            return tileNum > 0 && tile[tileNum] != null && tile[tileNum].collision;
      }

      // Get tile type number at specific grid coordinate
      public int getTileType(int col, int row) {
            if (col < 0 || col >= maxScreenCol || row < 0 || row >= maxScreenRow) {
                  return 0;
            }
            return mapTileNum[col][row];
      }

      // Structured platform representation for pixel-exact platformer physics
      public static class PlatformLedge {
            public final String name;
            public final int minX, maxX;
            public final int surfaceY;
            public final boolean isSolidGround; // true = ground/crate, false = jump-through/drop-through platform

            public PlatformLedge(String name, int minX, int maxX, int surfaceY, boolean isSolidGround) {
                  this.name = name;
                  this.minX = minX;
                  this.maxX = maxX;
                  this.surfaceY = surfaceY;
                  this.isSolidGround = isSolidGround;
            }
      }

      // Level 1 platforms: Exact pixel bounds and standing heights in map_sewer_lab.png
      public static final PlatformLedge[] platformsLevel1 = {
            new PlatformLedge("Left Spawn Ledge", 0, 130, 454, true),
            new PlatformLedge("1. Platform 1", 130, 260, 398, false),
            new PlatformLedge("2. High Turret Platform", 325, 418, 324, false),
            new PlatformLedge("3. Mid Turret Platform", 368, 438, 408, false),
            new PlatformLedge("4. Pipe Catwalk", 440, 660, 374, false),
            new PlatformLedge("5. Lower Bridge Walkway", 365, 660, 478, false),
            new PlatformLedge("6. Barrel Ledge", 715, 830, 402, false),
            new PlatformLedge("7. Top Roof Catwalk", 770, 945, 320, false),
            new PlatformLedge("8. Right Sewer Ground", 718, 1080, 478, true),
            new PlatformLedge("8b. Bunker Door Floor", 1080, 1280, 484, true)
      };

      // Level 2 platforms: Exact pixel bounds and standing heights in map_sunset.png
      // NOTE: map_sunset has a solid continuous ground across the ENTIRE map (x = 0..1280)
      public static final PlatformLedge[] platformsLevel2 = {
            // Elevated Floating Platforms (jump-through / drop-through)
            new PlatformLedge("Watchtower Balcony", 45, 135, 412, false),
            new PlatformLedge("Pillar 1 Platform", 319, 442, 424, false),
            new PlatformLedge("Pillar 2 Platform", 620, 785, 446, false),
            new PlatformLedge("Radar Rooftop", 820, 985, 388, false),

            // Raised Bunker Terrace
            new PlatformLedge("Bunker Terrace", 785, 1050, 506, true),

            // Continuous ground surfaces across the entire map (no holes, no acid)
            new PlatformLedge("Watchtower Ground", 0, 380, 547, true),
            new PlatformLedge("Mid Floor Walkway & Pillar 2 Ground", 380, 785, 544, true),
            new PlatformLedge("Right Exit Ground", 1050, 1280, 542, true),
            new PlatformLedge("Continuous Full Ground", 0, 1280, 544, true)
      };

      // Active platforms for collision
      public PlatformLedge[] platforms = platformsLevel1;

      // Query if an entity's feet can land on any valid platform surface
      public int getLandingSurfaceY(int footLeftX, int footRightX, int currentFeetY, int nextFeetY, boolean allowDropThrough) {
            int bestSurfaceY = -1;
            int minDiff = Integer.MAX_VALUE;
            int footCenterX = (footLeftX + footRightX) / 2;

            for (PlatformLedge p : platforms) {
                  // Player center of mass must be horizontally supported by platform
                  if (footCenterX >= p.minX && footCenterX <= p.maxX) {
                        // Skip floating platform when actively dropping through
                        if (!p.isSolidGround && !allowDropThrough) {
                              continue;
                        }
                        // Check landing within downward movement range (+8px step tolerance)
                        if (currentFeetY <= p.surfaceY + 8 && nextFeetY >= p.surfaceY - 4) {
                              int diff = Math.abs(currentFeetY - p.surfaceY);
                              if (diff < minDiff) {
                                    minDiff = diff;
                                    bestSurfaceY = p.surfaceY;
                              }
                        }
                  }
            }
            return bestSurfaceY;
      }

      // Query if the platform beneath entity feet allows drop-through (Down / S key)
      public boolean canDropThrough(int footLeftX, int footRightX, int currentFeetY) {
            int footCenterX = (footLeftX + footRightX) / 2;
            for (PlatformLedge p : platforms) {
                  if (!p.isSolidGround && footCenterX >= p.minX && footCenterX <= p.maxX) {
                        if (Math.abs(currentFeetY - p.surfaceY) <= 8) {
                              return true;
                        }
                  }
            }
            return false;
      }

      // Returns the exact pixel Y of the standing surface for the given tile
      public int getSurfaceY(int col, int row) {
            if (col < 0 || col >= maxScreenCol || row < 0 || row >= maxScreenRow) {
                  return row * tileSize;
            }
            int t = mapTileNum[col][row];
            if (t == 0 || t == 3) {
                  return (row + 1) * tileSize;
            }

            // LEVEL 2 (Sunset Military Outpost)
            if (currentLevel == 2) {
                  if (row == 6) {
                        if (col >= 13 && col <= 15) return 388; // Radar Rooftop
                        if (col >= 1 && col <= 2) return 412;   // Watchtower Balcony
                        if (col >= 5 && col <= 6) return 424;   // Pillar 1 Platform
                        if (col >= 10 && col <= 12) return 446; // Pillar 2 Platform
                        return 424;
                  }
                  if (row == 7) {
                        if (col >= 12 && col <= 16) return 506; // Bunker Terrace
                        return 506;
                  }
                  if (row >= 8) {
                        if (col <= 5) return 547;
                        if (col <= 9) return 544;
                        return 542;
                  }
                  return row * tileSize;
            }

            // LEVEL 1 (Sewer Lab)
            // Row 5: High platforms (Turret col 5, Catwalk cols 6-9, Top catwalk cols 12-14)
            if (row == 5) {
                  if (col == 5) return 324; // Turret platform (col 5)
                  if (col >= 6 && col <= 9) return 374; // Pipe Catwalk (aligned to caution walkway)
                  if (col >= 12 && col <= 14) return 320; // Top ladder catwalk
                  return 320;
            }

            // Row 6: Mid platforms (Platform 1 cols 2-3, Mid turret col 5, Catwalk cols 6-9, Barrel ledge cols 11-12)
            if (row == 6) {
                  if (col >= 2 && col <= 3) return 398; // Platform 1 (cols 2-3 only; col 4 is open acid gap!)
                  if (col == 5) return 408; // Mid Turret platform (caution slab)
                  if (col >= 6 && col <= 9) return 374; // Pipe Catwalk surface
                  if (col >= 11 && col <= 12) return 402; // Barrel ledge
                  return 384;
            }

            // Row 7: Ground level, bridges, and bunker door floor
            if (row == 7) {
                  if (col <= 1) {
                        return 454; // Left brick spawn ledge
                  } else if (col >= 5 && col <= 9) {
                        return 478; // Lower bridge walkway over toxic acid
                  } else if (col >= 11 && col <= 16) {
                        return 478; // Main sewer stone ground & crate base
                  } else if (col >= 17) {
                        return 484; // Green moss floor in front of laboratory bunker door
                  }
                  return 478;
            }

            return row * tileSize;
      }

      public void draw(Graphics g) {
            // Background image renders the visual map; tiles remain transparent
      }
}
