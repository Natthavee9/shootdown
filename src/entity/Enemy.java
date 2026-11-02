package entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;
import main.GamePanel;

// Represents an enemy character on the map (Snail, Octopus, Skull, or Snake)
public class Enemy extends GameOBJ {

      GamePanel gp;
      public String type; // "snail", "octopus", "skull", or "snake_slither"
      public boolean alive = true;
      public int hp = 1;

      // Animation sprite array (2 frames for walking/floating/slithering)
      private BufferedImage[] sprites = new BufferedImage[2];
      private int spriteCounter = 0;
      private int spriteNum = 0;

      // Patrol movement parameters
      public String direction = "left"; // "left" or "right"
      public int minX, maxX;            // Horizontal patrol boundaries
      public boolean isFlying = false;  // Floating behavior flag
      private int startY;               // Baseline Y for flying enemies
      private double floatAngle = 0;    // Sinusoidal float offset

      public Enemy(GamePanel gp, String type, int startX, int startY, int minX, int maxX) {
            this.gp = gp;
            this.type = type;
            this.x = startX;
            this.y = startY;
            this.startY = startY;
            this.minX = minX;
            this.maxX = maxX;

            initEnemyStats();
            loadEnemySprites();
      }

      // Configure dimensions, speed, and hitbox per enemy type
      private void initEnemyStats() {
            if ("snail".equalsIgnoreCase(type)) {
                  width = 48;
                  height = 38;
                  speed = 1;
                  isFlying = false;
            } else if ("octopus".equalsIgnoreCase(type)) {
                  width = 50;
                  height = 50;
                  speed = 2;
                  isFlying = false;
            } else if ("skull".equalsIgnoreCase(type)) {
                  width = 46;
                  height = 44;
                  speed = 1;
                  isFlying = true;
            } else if (isSnake()) {
                  width = 64;
                  height = 36;
                  speed = 2;
                  isFlying = false;
            } else if ("beetle".equalsIgnoreCase(type) || "beetle_fly".equalsIgnoreCase(type)) {
                  width = 50;
                  height = 44;
                  speed = 2;
                  isFlying = true;
            } else if ("dragon".equalsIgnoreCase(type) || "dragon_run".equalsIgnoreCase(type)) {
                  width = 64;
                  height = 52;
                  speed = 2;
                  isFlying = false;
            } else if ("eyeball".equalsIgnoreCase(type) || "eyeball_walk".equalsIgnoreCase(type)) {
                  width = 48;
                  height = 48;
                  speed = 1;
                  isFlying = false;
            } else {
                  width = 48;
                  height = 48;
                  speed = 1;
                  isFlying = false;
            }

            if (isSnake() || "snail".equalsIgnoreCase(type)) {
                  // Crawling enemies have a taller top hitbox to reliably detect walking player
                  hitbox = new Rectangle(x + 2, y - 8, width - 4, height + 10);
            } else {
                  hitbox = new Rectangle(x + 4, y + 4, width - 8, height - 8);
            }
      }

      // Helper to check if type is snake variant
      private boolean isSnake() {
            return "snake".equalsIgnoreCase(type)
                || "snake_slither".equalsIgnoreCase(type)
                || "snake_silther".equalsIgnoreCase(type);
      }

      // Load 2 animation frames for the specific enemy type
      private void loadEnemySprites() {
            String prefix = "";
            if ("snail".equalsIgnoreCase(type)) {
                  prefix = "snail_crawl_";
            } else if ("octopus".equalsIgnoreCase(type)) {
                  prefix = "octopus_walk_";
            } else if ("skull".equalsIgnoreCase(type)) {
                  prefix = "skull_float_";
            } else if (isSnake()) {
                  prefix = "snake_slither_";
            } else if ("beetle".equalsIgnoreCase(type) || "beetle_fly".equalsIgnoreCase(type)) {
                  prefix = "beetle_fly_";
            } else if ("dragon".equalsIgnoreCase(type) || "dragon_run".equalsIgnoreCase(type)) {
                  prefix = "dragon_run_";
            } else if ("eyeball".equalsIgnoreCase(type) || "eyeball_walk".equalsIgnoreCase(type)) {
                  prefix = "eyeball_walk_";
            }

            for (int i = 0; i < 2; i++) {
                  sprites[i] = loadImage("res/enemy/" + prefix + (i + 1) + ".png");
            }
      }

      private BufferedImage loadImage(String path) {
            try {
                  InputStream stream = getClass().getResourceAsStream("/" + path.replaceFirst("^res/", ""));
                  if (stream != null) {
                        return ImageIO.read(stream);
                  }

                  File[] candidates = {
                        new File(path),
                        new File("shootdown/" + path),
                        new File("../../" + path)
                  };
                  for (File f : candidates) {
                        if (f.exists()) {
                              return ImageIO.read(f);
                        }
                  }
            } catch (Exception e) {
                  System.out.println("Warning: Cannot load enemy sprite at " + path);
            }
            return null;
      }

      @Override
      public void update() {
            if (!alive) return;

            // 1. Horizontal patrol
            if ("left".equals(direction)) {
                  x -= speed;
                  if (x <= minX) {
                        x = minX;
                        direction = "right";
                  }
            } else {
                  x += speed;
                  if (x >= maxX) {
                        x = maxX;
                        direction = "left";
                  }
            }

            // 2. Vertical sinusoidal floating for flying enemies (Skull)
            if (isFlying) {
                  floatAngle += 0.06;
                  y = (int) (startY + Math.sin(floatAngle) * 12);
            }

            // Synchronize collision hitbox
            if (hitbox != null) {
                  if (isSnake() || "snail".equalsIgnoreCase(type)) {
                        hitbox.x = x + 2;
                        hitbox.y = y - 8;
                  } else {
                        hitbox.x = x + 4;
                        hitbox.y = y + 4;
                  }
            }

            // 3. Animation cycle
            spriteCounter++;
            if (spriteCounter > 12) {
                  spriteCounter = 0;
                  spriteNum = (spriteNum + 1) % 2;
            }
      }

      @Override
      public void draw(Graphics g) {
            if (!alive) return;

            BufferedImage image = sprites[spriteNum];
            if (image != null) {
                  boolean flip;
                  if (isSnake()) {
                        // Snake source sprite faces right by default
                        flip = "left".equals(direction);
                  } else {
                        // Snail and Octopus source sprites face left by default
                        flip = "right".equals(direction);
                  }

                  if (flip) {
                        g.drawImage(image, x + width, y, -width, height, null);
                  } else {
                        g.drawImage(image, x, y, width, height, null);
                  }
            } else {
                  // Fallback colored rectangle if sprites fail to load
                  g.setColor(Color.RED);
                  g.fillRect(x, y, width, height);
            }
      }
}
