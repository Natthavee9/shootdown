package entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;
import main.GamePanel;
import main.Keyhandler;

// Represents the controllable player character, scaled and aligned to the map grid
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
      public final int jumpStrength = -14;// Upward impulse
      public final int maxFallSpeed = 12; // Terminal fall velocity

      // Cooldown timer to allow dropping through floating platforms (Tile 2)
      private int dropThroughCooldown = 0;

      // Shooting cooldown timer
      public int shotCooldown = 0;

      // Health and invincibility states
      public int maxLife = 3;
      public int life = 3;
      public boolean invincible = false;
      public int invincibleCounter = 0;

      // Animation frame counters
      public int spriteCounter = 0;
      public int spriteNum = 0;

      public Player(GamePanel gp, Keyhandler keyH) {
            this.gp = gp;
            this.keyH = keyH;

            setDefaultValues();
            getPlayerImage();
      }

      // Initialize player coordinates, size, and collision hitbox scaled to match the map
      public void setDefaultValues() {
            // Player proportions matching doors, barrels, crates, and railings (54 x 76)
            width = 54;
            height = 76;
            speed = 4;

            // Spawn player standing flush on the left brick ledge (y = 454)
            int spawnSurfaceY = 454;
            x = 40;
            y = spawnSurfaceY - height; // 378

            // Hitbox centered horizontally, with bottom kept slightly above feet to prevent floor snagging
            hitbox = new Rectangle(x + 10, y + 8, width - 20, height - 12);

            life = maxLife;
            invincible = false;
            invincibleCounter = 0;
      }

      // Respawn player on the left ledge after falling into acid or pit
      public void respawn() {
            int spawnSurfaceY = 454;
            x = 40;
            y = spawnSurfaceY - height; // 378
            ySpeed = 0;
            isJumping = false;
            dropThroughCooldown = 0;
            shotCooldown = 0;
            life = maxLife;
            invincible = false;
            invincibleCounter = 0;
            if (hitbox != null) {
                  hitbox.x = x + 10;
                  hitbox.y = y + 8;
            }
      }

      // Load player sprites from resources with multi-path resolution
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

      // Update player position, physics, and tile collision each frame
      @Override
      public void update() {
            isMoving = false;

            if (dropThroughCooldown > 0) {
                  dropThroughCooldown--;
            }
            if (shotCooldown > 0) {
                  shotCooldown--;
            }

            // Invincibility cooldown timer after getting damaged
            if (invincible) {
                  invincibleCounter++;
                  if (invincibleCounter > 60) { // 1 second of invulnerability
                        invincible = false;
                        invincibleCounter = 0;
                  }
            }

            // 1. Horizontal movement & solid obstacle collision
            int dx = 0;
            if (keyH.leftPress) {
                  dx -= speed;
                  direction = "left";
                  isMoving = true;
            }
            if (keyH.rightPress) {
                  dx += speed;
                  direction = "right";
                  isMoving = true;
            }

            if (dx != 0 && gp.tileM != null) {
                  int tileSize = gp.tileM.tileSize;
                  if (dx > 0) {
                        int nextRightX = hitbox.x + hitbox.width + dx;
                        int checkCol = nextRightX / tileSize;
                        int topRow = hitbox.y / tileSize;
                        // Only check solid walls at torso/head level (rows 0-6); row 7 is walking floor
                        int bottomRow = Math.min(6, (hitbox.y + hitbox.height - 2) / tileSize);
                        boolean blocked = false;
                        for (int r = topRow; r <= bottomRow; r++) {
                              int t = gp.tileM.getTileType(checkCol, r);
                              if (t == 1) { // High solid walls/ceilings block movement
                                    blocked = true;
                                    break;
                              }
                        }
                        if (blocked) {
                              dx = 0;
                              x = checkCol * tileSize - width + 10;
                        }
                  } else if (dx < 0) {
                        int nextLeftX = hitbox.x + dx;
                        int checkCol = nextLeftX / tileSize;
                        int topRow = hitbox.y / tileSize;
                        int bottomRow = Math.min(6, (hitbox.y + hitbox.height - 2) / tileSize);
                        boolean blocked = false;
                        for (int r = topRow; r <= bottomRow; r++) {
                              int t = gp.tileM.getTileType(checkCol, r);
                              if (t == 1) {
                                    blocked = true;
                                    break;
                              }
                        }
                        if (blocked) {
                              dx = 0;
                              x = (checkCol + 1) * tileSize - 10;
                        }
                  }
            }

            x += dx;

            // Clamp horizontal position to screen boundaries
            if (x < 0) {
                  x = 0;
            }
            if (x > gp.screenWidth - width) {
                  x = gp.screenWidth - width;
            }

            hitbox.x = x + 10;

            // 2. Drop through floating platform (Tile 2) when pressing Down (S key)
            if (keyH.downPress && !isJumping && gp.tileM != null) {
                  int currentFeetY = y + height;
                  int footRow = currentFeetY / gp.tileM.tileSize;
                  int col1 = (hitbox.x + 4) / gp.tileM.tileSize;
                  int col2 = (hitbox.x + hitbox.width - 4) / gp.tileM.tileSize;
                  int t1 = gp.tileM.getTileType(col1, footRow);
                  int t2 = gp.tileM.getTileType(col2, footRow);
                  if (t1 == 2 || t2 == 2) {
                        y += 8;
                        ySpeed = 3;
                        isJumping = true;
                        dropThroughCooldown = 15;
                  }
            }

            // 3. Jump initiation (allowed only when grounded)
            if (keyH.upPress && !isJumping) {
                  ySpeed = jumpStrength;
                  isJumping = true;
            }

            // 4. Shoot bullet fireball from gun barrel (Spacebar)
            if (keyH.shoot && shotCooldown <= 0) {
                  int bulletW = 30;
                  int bulletH = 20;
                  int bulletY = y + 26; // Aligned with the center of the player's gun barrel
                  int bulletX;
                  int bulletDir;

                  if ("left".equals(direction)) {
                        bulletX = x - bulletW + 4;
                        bulletDir = -1;
                  } else {
                        bulletX = x + width - 4;
                        bulletDir = 1;
                  }

                  if (gp != null && gp.bullets != null) {
                        gp.bullets.add(new Bullet(bulletX, bulletY, bulletDir));
                  }
                  shotCooldown = 15; // Cooldown of 15 frames (~4 shots/sec)
            }

            // 4. Apply gravity
            ySpeed += gravity;
            if (ySpeed > maxFallSpeed) {
                  ySpeed = maxFallSpeed;
            }

            if (keyH.downPress && isJumping && ySpeed > 0) {
                  ySpeed += 1; // Fast fall
            }

            // 5. Vertical landing & standing on map tiles (aligned to exact visual floor heights)
            if (gp.tileM != null) {
                  int tileSize = gp.tileM.tileSize;

                  if (ySpeed >= 0) { // Falling or standing on surface
                        int currentFeetY = y + height;
                        int nextFeetY = currentFeetY + ySpeed;
                        int col1 = Math.max(0, (hitbox.x + 4) / tileSize);
                        int col2 = Math.min(gp.tileM.maxScreenCol - 1, (hitbox.x + hitbox.width - 4) / tileSize);

                        boolean landed = false;
                        int startRow = Math.max(0, (currentFeetY - 12) / tileSize);
                        int endRow = Math.min(gp.tileM.maxScreenRow - 1, (nextFeetY + 8) / tileSize);
                        for (int r = startRow; r <= endRow; r++) {
                              if (r >= 0 && r < gp.tileM.maxScreenRow) {
                                    for (int c = col1; c <= col2; c++) {
                                          int t = gp.tileM.getTileType(c, r);
                                          // Solid ground (1), wooden crate (4), or platform (2 when not dropping through)
                                          if (t == 1 || t == 4 || (t == 2 && dropThroughCooldown <= 0)) {
                                                int platSurfaceY = gp.tileM.getSurfaceY(c, r);
                                                // Check landing and smooth step-up/step-down
                                                if (currentFeetY <= platSurfaceY + 8 && nextFeetY >= platSurfaceY - 4) {
                                                      y = platSurfaceY - height;
                                                      ySpeed = 0;
                                                      isJumping = false;
                                                      landed = true;
                                                      break;
                                                }
                                          }
                                    }
                                    if (landed) break;
                              }
                        }

                        if (!landed) {
                              y += ySpeed;
                              isJumping = true;
                        }
                  } else { // Jumping upward
                        int headY = y + 8 + ySpeed;
                        int headRow = headY / tileSize;
                        int col1 = Math.max(0, (hitbox.x + 4) / tileSize);
                        int col2 = Math.min(gp.tileM.maxScreenCol - 1, (hitbox.x + hitbox.width - 4) / tileSize);
                        boolean hitCeiling = false;

                        if (headRow >= 0 && headRow < gp.tileM.maxScreenRow) {
                              for (int c = col1; c <= col2; c++) {
                                    int t = gp.tileM.getTileType(c, headRow);
                                    if (t == 1) { // Only solid ceiling blocks upward jump
                                          y = (headRow + 1) * tileSize - 8;
                                          ySpeed = 0;
                                          hitCeiling = true;
                                          break;
                                    }
                              }
                        }

                        if (!hitCeiling) {
                              y += ySpeed;
                        }
                  }
            } else {
                  y += ySpeed;
            }

            // Synchronize collision hitbox
            hitbox.x = x + 10;
            hitbox.y = y + 8;

            // 6. Hazard & pit check (Tile 3 = Acid pool)
            if (gp.tileM != null) {
                  int currentFeetY = y + height;
                  int footRow = currentFeetY / gp.tileM.tileSize;
                  int footCol = (hitbox.x + hitbox.width / 2) / gp.tileM.tileSize;
                  if (footRow >= 0 && footRow < gp.tileM.maxScreenRow && footCol >= 0 && footCol < gp.tileM.maxScreenCol) {
                        if (gp.tileM.getTileType(footCol, footRow) == 3) {
                              respawn();
                        }
                  }
            }

            if (y > gp.screenHeight + 10) {
                  respawn();
            }

            // 7. Animation frame timing
            spriteCounter++;
            if (spriteCounter > 6) {
                  spriteCounter = 0;
                  spriteNum++;
            }
      }

      // Render player sprite or fallback rectangle onto the screen
      @Override
      public void draw(Graphics g) {
            // Flash/blink character when invincible after taking damage
            if (invincible && (invincibleCounter % 8 < 4)) {
                  return;
            }

            BufferedImage image = null;

            // Select sprite based on state
            if (isJumping) {
                  int jumpIndex = Math.min(Math.max((ySpeed + 14) / 6, 0), 4);
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
                  g.drawString("Player", x + 10, y - 4);
            }
      }
}
