package tile;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import main.GamePanel;

public class TileManager {

      GamePanel gp;
      public Tile[] tile; // Array to store tile types (up to 10 types)
      public int[][] mapTileNum; // Active map layout currently rendered on screen

      public int currentLevel = 1; // Tracks current level index

      // Level 1 map layout (20 columns x 12 rows)
      int[][] mapLevel1 = {
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                  { 1, 1, 1, 1, 3, 3, 3, 3, 1, 1, 1, 1, 1, 3, 3, 1, 1, 1, 1, 1 },
                  { 1, 1, 1, 1, 3, 3, 3, 3, 1, 1, 1, 1, 1, 3, 3, 1, 1, 1, 1, 1 }
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
            mapTileNum = new int[20][12]; // Map dimensions: 20 columns, 12 rows

            getTileImage();
            loadMap(currentLevel); // Load level 1 by default
      }

      public void getTileImage() {
            // 0: Air / Empty space (no rendering)
            tile[0] = new Tile();
            tile[0].collision = false;

            // 1: Main ground
            tile[1] = new Tile();
            tile[1].collision = true;

            // 2: Floating metal platform
            tile[2] = new Tile();
            tile[2].collision = true;

            // 3: Acid pool
            tile[3] = new Tile();
            tile[3].collision = false; // Non-collidable: entities fall through

            // 4: Wooden crate
            tile[4] = new Tile();
            tile[4].collision = true;

            try {
                  File file = new File("res/tileset.png");
                  if (file.exists()) {
                        // Load tileset sprite sheet from resources
                        BufferedImage spriteSheet = ImageIO.read(file);
                        if (spriteSheet != null) {
                              tile[1].image = spriteSheet.getSubimage(0, 0, 64, 64);
                              tile[2].image = spriteSheet.getSubimage(64, 0, 64, 64);
                              tile[3].image = spriteSheet.getSubimage(128, 0, 64, 64);
                              tile[4].image = spriteSheet.getSubimage(192, 0, 64, 64);
                        }
                  } else {
                        System.out.println("Warning: 'res/tileset.png' not found in res folder!");
                  }
            } catch (Exception e) {
                  e.printStackTrace();
            }
      }

      public void loadMap(int level) {
            int[][] targetMap = (level == 1) ? mapLevel1 : mapLevel2;

            // Copy selected level data into active map array
            for (int row = 0; row < 12; row++) {
                  for (int col = 0; col < 20; col++) {
                        mapTileNum[col][row] = targetMap[row][col];
                  }
            }
      }

      public void draw(Graphics g) {
            int col = 0;
            int row = 0;
            int x = 0;
            int y = 0;

            // Loop through the 20x12 grid to render tiles
            while (col < 20 && row < 12) {
                  int tileNum = mapTileNum[col][row];

                  // Render tile only if it's not air (0) and image exists
                  if (tileNum != 0 && tile[tileNum] != null && tile[tileNum].image != null) {
                        g.drawImage(tile[tileNum].image, x, y, 64, 64, null);
                  }

                  col++;
                  x += 64;

                  // Move to the next row after completing 20 columns
                  if (col == 20) {
                        col = 0;
                        x = 0;
                        row++;
                        y += 64;
                  }
            }
      }
}
