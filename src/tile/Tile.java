package tile;

import java.awt.image.BufferedImage;

// Represents an individual tile type and its properties
public class Tile {
      public BufferedImage image;       // Tile texture image
      public boolean collision = false; // Collision flag (true = solid, false = passable)
}
