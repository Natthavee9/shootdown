package entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;

// Represents a fireball projectile shot out of the player's gun
public class Bullet extends GameOBJ {

      // Horizontal firing direction: 1 = shoot right, -1 = shoot left
      public int direction;
      public boolean active = true;

      // Shared fireball sprite asset
      private static BufferedImage fireballImage;
      private static boolean imageLoaded = false;

      public Bullet(int startX, int startY, int direction) {
            this.x = startX;
            this.y = startY;
            this.direction = direction;
            this.width = 30;
            this.height = 20;
            this.speed = 10;
            this.hitbox = new Rectangle(x, y, width, height);

            loadBulletImage();
      }

      // Load fireball sprite with multi-path fallback
      private void loadBulletImage() {
            if (imageLoaded) return;
            imageLoaded = true;

            try {
                  InputStream stream = getClass().getResourceAsStream("/player/bullet_fireball.png");
                  if (stream != null) {
                        fireballImage = ImageIO.read(stream);
                        return;
                  }

                  File[] candidates = {
                        new File("res/player/bullet_fireball.png"),
                        new File("shootdown/res/player/bullet_fireball.png"),
                        new File("../../res/player/bullet_fireball.png")
                  };
                  for (File f : candidates) {
                        if (f.exists()) {
                              fireballImage = ImageIO.read(f);
                              return;
                        }
                  }
            } catch (Exception e) {
                  System.out.println("Warning: Cannot load bullet fireball sprite at res/player/bullet_fireball.png");
            }
      }

      @Override
      public void update() {
            if (!active) return;

            // Move horizontally based on direction and speed
            x += speed * direction;

            // Synchronize collision hitbox
            if (hitbox != null) {
                  hitbox.x = x;
                  hitbox.y = y;
            }

            // Deactivate when leaving screen boundaries
            if (x < -100 || x > 1400) {
                  active = false;
            }
      }

      @Override
      public void draw(Graphics g) {
            if (!active) return;

            if (fireballImage != null) {
                  // Fireball naturally flies right; flip horizontally when shooting left
                  if (direction == -1) {
                        g.drawImage(fireballImage, x + width, y, -width, height, null);
                  } else {
                        g.drawImage(fireballImage, x, y, width, height, null);
                  }
            } else {
                  // Fallback glowing orange projectile if image fails to load
                  g.setColor(new Color(255, 140, 0));
                  g.fillOval(x, y, width, height);
                  g.setColor(Color.YELLOW);
                  g.fillOval(x + 4, y + 4, width - 8, height - 8);
            }
      }
}
