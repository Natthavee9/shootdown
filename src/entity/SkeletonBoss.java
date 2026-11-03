package entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;
import main.GamePanel;


public class SkeletonBoss extends Enemy {

      // Boss durability & combat stats
      public int maxHp = 15;
      private int shootCooldown = 60; // Initial delay before first attack
      private int hurtCounter = 0;    // Flash timer when damaged
      private BufferedImage bossSprite;

      public SkeletonBoss(GamePanel gp, int startX, int startY, int minX, int maxX) {
            super(gp, "skeleton_boss", startX, startY, minX, maxX);

            // Boss dimensions and high health configuration
            this.width = 88;
            this.height = 100;
            this.speed = 1;
            this.isFlying = false;
            this.maxHp = 15;
            this.hp = maxHp;

            // Accurate boss collision hitbox
            this.hitbox = new Rectangle(x + 10, y + 10, width - 20, height - 15);

            loadBossSprite();
      }

      // Load boss artwork from res/enemy/skeleton_boss.png
      private void loadBossSprite() {
            try {
                  InputStream stream = getClass().getResourceAsStream("/enemy/skeleton_boss.png");
                  if (stream == null) {
                        stream = getClass().getResourceAsStream("/res/enemy/skeleton_boss.png");
                  }
                  if (stream != null) {
                        bossSprite = ImageIO.read(stream);
                        return;
                  }

                  File[] candidates = {
                        new File("res/enemy/skeleton_boss.png"),
                        new File("shootdown/res/enemy/skeleton_boss.png"),
                        new File("../../res/enemy/skeleton_boss.png")
                  };
                  for (File f : candidates) {
                        if (f.exists()) {
                              bossSprite = ImageIO.read(f);
                              return;
                        }
                  }
            } catch (Exception e) {
                  System.out.println("Warning: Cannot load skeleton boss sprite at res/enemy/skeleton_boss.png");
            }
      }

      /**
       * Take damage from player's bullet, triggering hurt flash animation.
       */
      public void takeDamage(int damage) {
            hp -= damage;
            hurtCounter = 10; // Flash for 10 frames
            if (hp <= 0) {
                  hp = 0;
                  alive = false;
            }
      }

      @Override
      public void update() {
            if (!alive) return;

            // 1. Horizontal patrol between boundaries
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

            // 2. Face player if player is within engagement range
            if (gp != null && gp.player != null) {
                  if (gp.player.x < x + width / 2) {
                        direction = "left";
                  } else {
                        direction = "right";
                  }
            }

            // 3. Attack Player: periodically shoot green fireballs
            if (shootCooldown > 0) {
                  shootCooldown--;
            } else {
                  shootGreenFireball();
                  shootCooldown = 95; // Attack every ~1.6 seconds (95 frames at 60 FPS)
            }

            // 4. Update hurt flash timer
            if (hurtCounter > 0) {
                  hurtCounter--;
            }

            // 5. Synchronize collision hitbox
            if (hitbox != null) {
                  hitbox.x = x + 10;
                  hitbox.y = y + 10;
            }
      }

      /**
       * Launch green fireball aiming in a straight line based on player position and horizontal movement
       * ("ยิงตามการเคลื่อนที่ของ player แบบเป็นทางตรง")
       */
      private void shootGreenFireball() {
            if (gp == null || !alive) return;

            int fireDir = "left".equals(direction) ? -1 : 1;
            int spawnX = (fireDir < 0) ? (x - 20) : (x + width - 10);
            int spawnY = y + height / 2 - 10; // Mid-torso height

            double targetX;
            double targetY;

            if (gp.player != null) {
                  // Aim at player's horizontal position with movement lead
                  targetX = gp.player.x + gp.player.width / 2.0;

                  // Predictive aiming based on player's current horizontal movement
                  if (gp.player.isMoving) {
                        if ("right".equals(gp.player.direction)) {
                              targetX += 25;
                        } else if ("left".equals(gp.player.direction)) {
                              targetX -= 25;
                        }
                  }

                  // Aim at player's torso level; do not skew/curve upward when jumping ("ไม่ต้องแบบเลี้ยวตามการกระโดด")
                  if (gp.player.isJumping && gp.tileM != null) {
                        int groundY = gp.tileM.getGroundSurfaceY((int) targetX);
                        if (groundY != -1) {
                              targetY = groundY - gp.player.height / 2.0;
                        } else {
                              targetY = spawnY;
                        }
                  } else {
                        targetY = gp.player.y + gp.player.height / 2.0;
                  }
            } else {
                  targetX = spawnX + fireDir * 300;
                  targetY = spawnY;
            }

            gp.bossProjectiles.add(new GreenFireball(gp, spawnX, spawnY, targetX, targetY));
      }

      @Override
      public void draw(Graphics g) {
            if (!alive) return;

            // 1. Draw Boss Sprite (with damage flash effect when hurt)
            if (bossSprite != null) {
                  // By default skeleton_boss sprite faces right; flip when facing left
                  boolean flip = "left".equals(direction);

                  // Flash white/red silhouette every 2 frames when taking damage
                  if (hurtCounter > 0 && (hurtCounter % 4 < 2)) {
                        // Render bright red tint backing box to indicate hit impact
                        g.setColor(new Color(255, 60, 60, 160));
                        g.fillRect(x + 4, y + 4, width - 8, height - 8);
                  }

                  if (flip) {
                        g.drawImage(bossSprite, x + width, y, -width, height, null);
                  } else {
                        g.drawImage(bossSprite, x, y, width, height, null);
                  }
            } else {
                  // Fallback colored rectangle if sprite is unavailable
                  g.setColor(Color.DARK_GRAY);
                  g.fillRect(x, y, width, height);
            }

            // 2. Draw Boss Health Bar above the boss head
            drawBossHealthBar(g);
      }

      /**
       * Render high-visibility Boss Health Bar with title and HP value
       */
      private void drawBossHealthBar(Graphics g) {
            int barWidth = 90;
            int barHeight = 8;
            int barX = x + (width - barWidth) / 2;
            int barY = y - 22;

            // Health bar container background (black with dark grey border)
            g.setColor(new Color(20, 20, 20, 220));
            g.fillRect(barX - 2, barY - 2, barWidth + 4, barHeight + 4);
            g.setColor(new Color(180, 150, 50)); // Gold border
            g.drawRect(barX - 2, barY - 2, barWidth + 4, barHeight + 4);

            // Red background for depleted health
            g.setColor(new Color(90, 20, 20));
            g.fillRect(barX, barY, barWidth, barHeight);

            // Vibrant green/yellow health fill proportional to remaining HP
            int fillWidth = (int) ((hp / (float) maxHp) * barWidth);
            if (fillWidth > 0) {
                  if (hp > maxHp * 0.5) {
                        g.setColor(new Color(40, 220, 80)); // Green when healthy
                  } else if (hp > maxHp * 0.25) {
                        g.setColor(new Color(240, 180, 30)); // Yellow when wounded
                  } else {
                        g.setColor(new Color(240, 50, 40));  // Red when critical
                  }
                  g.fillRect(barX, barY, fillWidth, barHeight);
            }

            // Boss Title above the bar
            g.setColor(new Color(255, 215, 0)); // Gold text
            g.setFont(new Font("Arial", Font.BOLD, 10));
            String bossTitle = "SKELETON BOSS [" + hp + "/" + maxHp + "]";
            int titleWidth = g.getFontMetrics().stringWidth(bossTitle);
            g.drawString(bossTitle, x + (width - titleWidth) / 2, barY - 4);
      }
}

