package entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;
import main.GamePanel;

public class GreenFireball extends GameOBJ {

      private GamePanel gp;
      public double posX, posY;     // Sub-pixel position for smooth angled flight
      public double vx, vy;         // Constant velocity vector (straight-line trajectory)
      public int direction = -1;    // Primary horizontal orientation (-1 = left, 1 = right)
      public boolean active = true;
      public int lifespan = 200;    // Lifetime in frames before dissolving (~3.3 seconds)

      // Shared green fireball sprite asset
      private static BufferedImage fireballImage;
      private static boolean imageLoaded = false;

      // Primary constructor with target coordinates (aims straight towards target)
      public GreenFireball(GamePanel gp, int startX, int startY, double targetX, double targetY) {
            this.gp = gp;
            this.posX = startX;
            this.posY = startY;
            this.x = startX;
            this.y = startY;
            this.width = 32;
            this.height = 22;
            this.speed = 5;
            this.hitbox = new Rectangle(x + 4, y + 4, width - 8, height - 8);

            // Compute constant straight-line trajectory vector towards target
            double dx = targetX - (startX + width / 2.0);
            double dy = targetY - (startY + height / 2.0);
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist > 0.001) {
                  this.vx = (dx / dist) * speed;
                  this.vy = (dy / dist) * speed;
            } else {
                  this.vx = -speed;
                  this.vy = 0;
            }
            this.direction = (vx < 0) ? -1 : 1;

            loadFireballImage();
      }

      // Overloaded constructor for horizontal firing compatibility
      public GreenFireball(GamePanel gp, int startX, int startY, int dir) {
            this(gp, startX, startY, startX + (dir != 0 ? dir : -1) * 300, startY);
      }

      // Load green fireball sprite from resources with multi-path resolution
      private void loadFireballImage() {
            if (imageLoaded) return;
            imageLoaded = true;

            try {
                  InputStream stream = getClass().getResourceAsStream("/enemy/green_fireball.png");
                  if (stream != null) {
                        fireballImage = ImageIO.read(stream);
                        return;
                  }

                  File[] candidates = {
                        new File("res/enemy/green_fireball.png"),
                        new File("shootdown/res/enemy/green_fireball.png"),
                        new File("../../res/enemy/green_fireball.png")
                  };
                  for (File f : candidates) {
                        if (f.exists()) {
                              fireballImage = ImageIO.read(f);
                              return;
                        }
                  }
            } catch (Exception e) {
                  System.out.println("Warning: Cannot load green fireball sprite at res/enemy/green_fireball.png");
            }
      }

      @Override
      public void update() {
            if (!active) return;

            lifespan--;
            if (lifespan <= 0) {
                  active = false;
                  return;
            }

            // Straight-line linear projectile flight ("แบบเป็นทางตรง ไม่ต้องแบบเลี้ยวตามการกระโดด")
            // Travels directly along the constant velocity vector without curving or tracking mid-flight
            posX += vx;
            posY += vy;
            x = (int) Math.round(posX);
            y = (int) Math.round(posY);
            direction = (vx < 0) ? -1 : 1;

            // Synchronize collision hitbox
            if (hitbox != null) {
                  hitbox.x = x + 4;
                  hitbox.y = y + 4;
            }

            // Deactivate when leaving screen boundaries
            if (gp != null) {
                  if (x < -80 || x > gp.screenWidth + 80 || y < -80 || y > gp.screenHeight + 80) {
                        active = false;
                  }
            } else if (x < -80 || x > 1400 || y < -80 || y > 800) {
                  active = false;
            }
      }

      @Override
      public void draw(Graphics g) {
            if (!active) return;

            if (fireballImage != null) {
                  // If flying left, flip the sprite so the flame trails behind
                  if (direction < 0) {
                        g.drawImage(fireballImage, x + width, y, -width, height, null);
                  } else {
                        g.drawImage(fireballImage, x, y, width, height, null);
                  }
            } else {
                  // Fallback green glowing oval if image is not loaded
                  g.setColor(new Color(50, 255, 100));
                  g.fillOval(x, y, width, height);
                  g.setColor(Color.WHITE);
                  g.fillOval(x + 6, y + 4, width - 12, height - 8);
            }
      }
}
