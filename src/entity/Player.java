package entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;
import main.GamePanel;
import main.Keyhandler;

// Represents the controllable player character
public class Player extends GameOBJ {

      GamePanel gp;
      Keyhandler keyH;

      // Animation sprite arrays
      private BufferedImage[] idleSprites = new BufferedImage[4];
      private BufferedImage[] runSprites = new BufferedImage[6];
      private BufferedImage[] jumpSprites = new BufferedImage[5];

      // Movement & physics states
      public String direction = "right";  // "left" or "right"
      public boolean isMoving = false;
      public boolean isJumping = false;
      public int ySpeed = 0;              // Vertical velocity for jumping & gravity
      public final int gravity = 1;       // Gravity acceleration
      public int groundY = 490;           // Default ground baseline coordinate

      // Animation frame counters
      public int spriteCounter = 0;
      public int spriteNum = 0;

      public Player(GamePanel gp, Keyhandler keyH) {
            this.gp = gp;
            this.keyH = keyH;

            setDefaultValues();
            getPlayerImage();
      }

      // Initialize player coordinates, size, speed, and collision hitbox
      public void setDefaultValues() {
            x = 100;
            y = 490;
            width = 90;
            height = 120;
            speed = 5;

            // Hitbox offset for fine-tuned collision
            hitbox = new Rectangle(x + 20, y + 15, width - 40, height - 20);
      }

      // Load player sprites from resources with fallback directory support
      public void getPlayerImage() {
            for (int i = 0; i < 4; i++) {
                  idleSprites[i] = loadImage("res/player/idle_" + (i + 1) + ".png");
            }
            for (int i = 0; i < 6; i++) {
                  runSprites[i] = loadImage("res/player/run_" + (i + 1) + ".png");
            }
            for (int i = 0; i < 5; i++) {
                  jumpSprites[i] = loadImage("res/player/jump_" + (i + 1) + ".png");
            }
      }

      // Helper to load images robustly across different working directories
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
                  System.out.println("Warning: Cannot load sprite at " + path);
            }
            return null;
      }

      // Update player position, physics, and animation state each frame
      @Override
      public void update() {
            isMoving = false;

            // Horizontal movement
            if (keyH.leftPress) {
                  x -= speed;
                  direction = "left";
                  isMoving = true;
            }
            if (keyH.rightPress) {
                  x += speed;
                  direction = "right";
                  isMoving = true;
            }

            // Jump initiation
            if (keyH.upPress && !isJumping) {
                  ySpeed = -16; // Upward impulse
                  isJumping = true;
            }

            // Apply gravity and vertical movement
            if (isJumping || y < groundY) {
                  y += ySpeed;
                  ySpeed += gravity;

                  // Fast fall when pressing down
                  if (keyH.downPress) {
                        ySpeed += 1;
                  }

                  // Land on ground
                  if (y >= groundY) {
                        y = groundY;
                        ySpeed = 0;
                        isJumping = false;
                  }
            }

            // Keep player within horizontal screen boundaries
            if (x < 0) {
                  x = 0;
            }
            if (x > gp.screenWidth - width) {
                  x = gp.screenWidth - width;
            }

            // Synchronize collision hitbox
            if (hitbox != null) {
                  hitbox.x = x + 20;
                  hitbox.y = y + 15;
            }

            // Animation frame timing
            spriteCounter++;
            if (spriteCounter > 6) {
                  spriteCounter = 0;
                  spriteNum++;
            }
      }

      // Render player sprite or fallback rectangle onto the screen
      @Override
      public void draw(Graphics g) {
            BufferedImage image = null;

            // Select sprite based on state
            if (isJumping) {
                  int jumpIndex = Math.min(Math.max((ySpeed + 16) / 7, 0), 4);
                  image = jumpSprites[jumpIndex];
                  if (image == null) image = jumpSprites[0];
            } else if (isMoving) {
                  image = runSprites[spriteNum % 6];
            } else {
                  image = idleSprites[spriteNum % 4];
            }

            // Draw character
            if (image != null) {
                  if ("left".equals(direction)) {
                        // Flip image horizontally when facing left
                        g.drawImage(image, x + width, y, -width, height, null);
                  } else {
                        g.drawImage(image, x, y, width, height, null);
                  }
            } else {
                  // Fallback colored rectangle if sprites fail to load
                  g.setColor(new Color(66, 135, 245));
                  g.fillRect(x, y, width, height);
                  g.setColor(Color.WHITE);
                  g.drawString("Player", x + 18, y - 5);
            }
      }
}
