package main;

import entity.Bullet;
import entity.Enemy;
import entity.Player;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable {

      // SCREEN SETTINGS
      public int screenWidth = 1280;  // Panel width in pixels (16:9 aspect ratio)
      public int screenHeight = 720;  // Panel height in pixels
      private BufferedImage bgImage;  // Map background image asset

      // SYSTEM & CONTROLS
      final int FPS = 60;             // Target frames per second
      public Thread gameThread;       // Game loop thread
      public Keyhandler keyH = new Keyhandler();

      // GAME MAP & ENTITIES
      public TileManager tileM;
      public Player player;
      public ArrayList<Enemy> enemies = new ArrayList<>();
      public ArrayList<Bullet> bullets = new ArrayList<>();

      // HUD ASSETS
      private BufferedImage heartFull;
      private BufferedImage heartEmpty;

      public GamePanel() {
            // Configure panel settings
            this.setPreferredSize(new Dimension(screenWidth, screenHeight));
            this.setDoubleBuffered(true); // Eliminate screen flickering
            this.setFocusable(true);      // Allow receiving keyboard input
            this.addKeyListener(keyH);    // Register key listener for controls

            // Load map background and HUD images
            loadBackgroundImage();
            loadHeartImages();

            // Initialize map layout manager for physics and platform collision
            tileM = new TileManager(this);

            // Instantiate player entity
            player = new Player(this, keyH);

            // Populate enemies onto map platforms
            setupEnemies();
      }

      // Initialize and place enemies on the map
      public void setupEnemies() {
            enemies.clear();

            // 1. Snail crawling on Platform 1 (surface is at y = 398, touching caution floor directly)
            enemies.add(new Enemy(this, "snail", 160, 360, 130, 230));

            // 2. Octopus guarding the Turret Platform (surface is at y = 324)
            enemies.add(new Enemy(this, "octopus", 350, 274, 320, 400));

            // 3. Octopus patrolling the Pipe Catwalk (surface is at y = 374, touching caution walkway directly)
            enemies.add(new Enemy(this, "octopus", 440, 324, 390, 520));

            // 4. Skull floating above the toxic acid pit (cols 3-5, y = 210)
            enemies.add(new Enemy(this, "skull", 220, 210, 180, 310));

            // 5. Snake slithering across the Lower Bridge walkway over the acid (surface is at y = 478, touching planks directly)
            enemies.add(new Enemy(this, "snake_slither", 400, 447, 330, 560));

            // 6. Octopus patrolling the Top Ladder Catwalk (surface is at y = 320)
            enemies.add(new Enemy(this, "octopus", 830, 270, 775, 895));

            // 7. Snail crawling on the Right Sewer Floor (surface is at y = 478, touching stone floor directly)
            enemies.add(new Enemy(this, "snail", 760, 440, 730, 880));

            // 8. Snake slithering on the Right Floor near crates (surface is at y = 478, touching stone floor directly)
            enemies.add(new Enemy(this, "snake_slither", 950, 447, 900, 1080));

            // 9. Skull hovering near the Laboratory Bunker Door (cols 16-18, y = 230)
            enemies.add(new Enemy(this, "skull", 1120, 230, 1060, 1200));
      }

      // Load background map image with multi-path resolution
      private void loadBackgroundImage() {
            try {
                  InputStream stream = getClass().getResourceAsStream("/background/map_sewer_lab.png");
                  if (stream != null) {
                        bgImage = ImageIO.read(stream);
                        return;
                  }

                  File[] candidates = {
                        new File("res/background/map_sewer_lab.png"),
                        new File("shootdown/res/background/map_sewer_lab.png"),
                        new File("../../res/background/map_sewer_lab.png")
                  };
                  for (File f : candidates) {
                        if (f.exists()) {
                              bgImage = ImageIO.read(f);
                              return;
                        }
                  }
                  System.out.println("Warning: Cannot find background image at res/background/map_sewer_lab.png");
            } catch (IOException e) {
                  e.printStackTrace();
            }
      }

      // Start the game loop thread
      public void startGameThread() {
            gameThread = new Thread(this);
            gameThread.start();
      }

      // Game Loop: updates game state and renders at 60 FPS safely
      @Override 
      public void run() {
            double drawInterval = 1000000000.0 / FPS; // 0.01666 seconds per frame
            double delta = 0;
            long lastTime = System.nanoTime();
            long currentTime;

            while (gameThread != null) {
                  try {
                        currentTime = System.nanoTime();
                        delta += (currentTime - lastTime) / drawInterval;
                        lastTime = currentTime;

                        if (delta >= 1) {
                              update();
                              repaint();
                              delta--;
                        }

                        // Prevent 100% CPU thread starvation and allow Swing EDT to render smoothly
                        Thread.sleep(1);
                  } catch (Exception e) {
                        e.printStackTrace();
                  }
            }
      }

      // Update game entities and game logic
      public void update() {
            if (player != null) {
                  player.update();
            }

            // Update all projectiles (Bullets)
            for (int i = bullets.size() - 1; i >= 0; i--) {
                  Bullet b = bullets.get(i);
                  if (b != null && b.active) {
                        b.update();

                        // Bullet collision with active enemies
                        if (b.active) {
                              for (Enemy e : enemies) {
                                    if (e != null && e.alive && e.hitbox != null && b.hitbox != null) {
                                          if (b.hitbox.intersects(e.hitbox)) {
                                                b.active = false;
                                                e.hp--;
                                                if (e.hp <= 0) {
                                                      e.alive = false;
                                                }
                                                break;
                                          }
                                    }
                              }
                        }

                        // Bullet collision with solid terrain (only true ground impact or high solid walls)
                        if (b.active && tileM != null) {
                              int centerCol = (b.x + b.width / 2) / tileM.tileSize;
                              int centerRow = (b.y + b.height / 2) / tileM.tileSize;
                              if (centerCol >= 0 && centerCol < tileM.maxScreenCol && centerRow >= 0 && centerRow < tileM.maxScreenRow) {
                                    int t = tileM.getTileType(centerCol, centerRow);
                                    if (t == 1) {
                                          int surfY = tileM.getSurfaceY(centerCol, centerRow);
                                          // Only destroy projectile if its bottom physically impacts the ground surface
                                          if ((b.y + b.height) >= surfY) {
                                                b.active = false;
                                          }
                                    }
                              }
                        }
                  }

                  // Remove inactive bullets
                  if (b == null || !b.active) {
                        bullets.remove(i);
                  }
            }

            // Update all active enemies
            for (int i = 0; i < enemies.size(); i++) {
                  Enemy e = enemies.get(i);
                  if (e != null && e.alive) {
                        e.update();

                        // Collision check between player and enemy (damage, knockback & invincibility)
                        if (player != null && player.hitbox != null && e.hitbox != null) {
                              if (player.hitbox.intersects(e.hitbox)) {
                                    if (!player.invincible) {
                                          player.life--;
                                          player.invincible = true;

                                          // Knockback player away from enemy
                                          if (player.x < e.x) {
                                                player.x -= 20;
                                          } else {
                                                player.x += 20;
                                          }
                                          if (player.x < 0) player.x = 0;
                                          if (player.x > screenWidth - player.width) player.x = screenWidth - player.width;

                                          player.ySpeed = -6;
                                          player.isJumping = true;
                                          player.hitbox.x = player.x + 10;
                                          player.hitbox.y = player.y + 8;

                                          // If all hearts lost, respawn player
                                          if (player.life <= 0) {
                                                player.respawn();
                                          }
                                    }
                              }
                        }
                  }
            }
      }

      // Built-in Swing method for rendering graphics on this panel
      @Override 
      public void paintComponent(Graphics g) {
            super.paintComponent(g);

            // 1. Render map background image
            if (bgImage != null) {
                  g.drawImage(bgImage, 0, 0, screenWidth, screenHeight, null);
            } else {
                  // Fallback dark background
                  g.setColor(new Color(25, 25, 30));
                  g.fillRect(0, 0, screenWidth, screenHeight);
            }

            // 2. Render all enemies on the map
            for (Enemy e : enemies) {
                  if (e != null && e.alive) {
                        e.draw(g);
                  }
            }

            // 3. Render all active bullet projectiles
            for (Bullet b : bullets) {
                  if (b != null && b.active) {
                        b.draw(g);
                  }
            }

            // 4. Render player on the map
            if (player != null) {
                  player.draw(g);
            }

            // 5. Render Health HUD (Hearts on the top-left)
            drawPlayerLife(g);
      }

      // Draw health hearts on the top-left of the screen
      private void drawPlayerLife(Graphics g) {
            if (player == null) return;

            int startX = 20;
            int startY = 20;
            int heartSize = 32;
            int gap = 8;

            for (int i = 0; i < player.maxLife; i++) {
                  BufferedImage img = (i < player.life) ? heartFull : heartEmpty;
                  if (img != null) {
                        g.drawImage(img, startX + i * (heartSize + gap), startY, heartSize, heartSize, null);
                  } else {
                        // Fallback shape if heart image not loaded
                        g.setColor((i < player.life) ? Color.RED : Color.GRAY);
                        g.fillRoundRect(startX + i * (heartSize + gap), startY, heartSize, heartSize, 6, 6);
                  }
            }
      }

      // Load health heart sprites
      private void loadHeartImages() {
            try {
                  BufferedImage fullRaw = loadImage("res/heart/heart_full.png");
                  BufferedImage emptyRaw = loadImage("res/heart/heart_empty.png");

                  // Crop the actual heart graphic (13x13) to eliminate empty transparent margins
                  if (fullRaw != null && fullRaw.getWidth() >= 22 && fullRaw.getHeight() >= 20) {
                        heartFull = fullRaw.getSubimage(9, 7, 13, 13);
                  } else {
                        heartFull = fullRaw;
                  }

                  if (emptyRaw != null && emptyRaw.getWidth() >= 22 && emptyRaw.getHeight() >= 20) {
                        heartEmpty = emptyRaw.getSubimage(9, 7, 13, 13);
                  } else {
                        heartEmpty = emptyRaw;
                  }
            } catch (Exception e) {
                  System.out.println("Warning: Cannot load heart images");
            }
      }

      private BufferedImage loadImage(String path) {
            try {
                  InputStream stream = getClass().getResourceAsStream("/" + path.replaceFirst("^res/", ""));
                  if (stream != null) return ImageIO.read(stream);

                  File[] candidates = {
                        new File(path),
                        new File("shootdown/" + path),
                        new File("../../" + path)
                  };
                  for (File f : candidates) {
                        if (f.exists()) return ImageIO.read(f);
                  }
            } catch (Exception e) {}
            return null;
      }
}
