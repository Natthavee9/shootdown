package entity;

import java.awt.*;

// Represents a projectile shot by entities in the game
public class Bullet extends GameOBJ {

      // Horizontal firing direction: 1 = shoot right, -1 = shoot left
      public int direction;

      public Bullet(int startX, int startY, int direction) {
            this.x = startX;
            this.y = startY;
            this.direction = direction;
      }

      @Override
      public void update() {
            // Update horizontal position based on direction and speed
            x += speed * direction;

            // Synchronize hitbox position with bullet coordinates
            hitbox.x = x;
            hitbox.y = y;
      }

      @Override
      public void draw(Graphics g) {
            // Render bullet as a colored rectangular projectile
            g.setColor(Color.YELLOW);
            g.fillRect(x, y, width, height);
      }
}
