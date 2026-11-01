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

                  // R5 (y = 320..384): High turret (c4,5), pipe catwalk (c6..9), top ladder catwalk (c12..14)
                  { 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0 },

                  // R6 (y = 384..448): Platform 1 (c2..4), pipe catwalk (c6..9), barrel ledge (c11,12), crates (c15,16)
                  { 0, 0, 2, 2, 2, 0, 2, 2, 2, 2, 0, 2, 2, 0, 0, 4, 4, 0, 0, 0 },

                  // R7 (y = 448..512): Left brick ledge (c0,1), bridge over acid (c5..8), main sewer ground (c9..16), door floor (c17..19)
                  { 1, 1, 0, 0, 0, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },

                  // R8 - R11 (y = 512..768): Underground base & Acid pit (cols 2..4 is acid)
                  { 1, 1, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
                  { 1, 1, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
                  { 1, 1, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
                  { 1, 1, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }
      };

      // Level 2 map layout (alternative stage)
      int[][] mapLevel2 = {
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
                  { 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
                  { 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }
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

      // Returns the exact pixel Y of the standing surface for the given tile
      public int getSurfaceY(int col, int row) {
            if (col < 0 || col >= maxScreenCol || row < 0 || row >= maxScreenRow) {
                  return row * tileSize;
            }
            int t = mapTileNum[col][row];
            if (t == 0 || t == 3) {
                  return (row + 1) * tileSize;
            }

            // Row 5: High platforms (Turret cols 4-5, Catwalk cols 6-9, Top catwalk cols 12-14)
            if (row == 5) {
                  if (col >= 4 && col <= 5) return 324; // Turret platform
                  if (col >= 6 && col <= 9) return 374; // Pipe Catwalk (aligned to caution walkway)
                  if (col >= 12 && col <= 14) return 320; // Top ladder catwalk
                  return 320;
            }

            // Row 6: Mid platforms (Platform 1 cols 2-4, Catwalk cols 6-9, Barrel ledge cols 11-12, Crates cols 15-16)
            if (row == 6) {
                  if (col >= 2 && col <= 4) return 380; // Platform 1
                  if (col >= 6 && col <= 9) return 374; // Pipe Catwalk surface
                  if (col >= 11 && col <= 12) return 402; // Barrel ledge
                  if (col >= 15 && col <= 16) return 406; // Top of stacked crates
                  return 384;
            }

            // Row 7: Ground level, bridges, and bunker door floor
            if (row == 7) {
                  if (col <= 1) {
                        return 454; // Left brick spawn ledge
                  } else if (col >= 5 && col <= 8) {
                        return 478; // Lower bridge walkway over toxic acid
                  } else if (col >= 9 && col <= 16) {
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
