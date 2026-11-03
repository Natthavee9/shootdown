package main;

import background.Background;
import entity.Bullet;
import entity.Enemy;
import entity.GreenFireball;
import entity.Player;
import entity.SkeletonBoss;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable {

      // SCREEN SETTINGS
      public int screenWidth = 1280;  // Panel width in pixels (16:9 aspect ratio)
      public int screenHeight = 720;  // Panel height in pixels

      // BACKGROUND & GUI SYSTEM (OOP COMPOSITION)
      public Background background;
      public int currentStage = 1; // 1 = Sewer Lab, 2 = Sunset Military Outpost
      public boolean gameStarted = false; // Start screen is active initially

      // SYSTEM & CONTROLS
      final int FPS = 60;             // Target frames per second
      public Thread gameThread;       // Game loop thread
      public Keyhandler keyH = new Keyhandler();
      public boolean gameOver = false; // Game over / defeat state flag ("YOU LOSE")
      public boolean gameWon = false;  // Victory state flag ("YOU WIN")

      // GAME MAP & ENTITIES
      public TileManager tileM;
      public Player player;
      public ArrayList<Enemy> enemies = new ArrayList<>();
      public ArrayList<Bullet> bullets = new ArrayList<>();
      public ArrayList<GreenFireball> bossProjectiles = new ArrayList<>();

      public GamePanel() {
            // Configure panel settings
            this.setPreferredSize(new Dimension(screenWidth, screenHeight));
            this.setDoubleBuffered(true); // Eliminate screen flickering
            this.setFocusable(true);      // Allow receiving keyboard input
            this.addKeyListener(keyH);    // Register key listener for controls

            // Initialize Background system (demonstrating OOP Composition: Background HAS-A Graphic and GUI)
            background = new Background(this);

            // Initialize map layout manager for physics and platform collision
            tileM = new TileManager(this);

            // Instantiate player entity
            player = new Player(this, keyH);

            // Populate enemies onto map platforms
            setupEnemies();
      }

      // Load specific stage (1 = Sewer Lab, 2 = Sunset Military Outpost)
      public synchronized void loadStage(int stage) {
            this.currentStage = stage;
            this.gameOver = false;
            this.gameWon = false;
            this.enemies.clear(); // Clear enemies immediately to prevent race condition with isStage2Cleared()
            this.bullets.clear();
            this.bossProjectiles.clear();

            // 1. Switch background graphics (via Composition)
            if (background != null) {
                  background.loadStage(stage);
                  if (background.getGui() != null) {
                        background.getGui().hideRestartButton();
                        background.getGui().hideNextStageButton();
                  }
                  if (background.getStartScreen() != null && gameStarted) {
                        background.getStartScreen().hideStartButton();
                  }
            }

            // 2. Switch platform collision map
            if (tileM != null) {
                  tileM.loadMap(stage);
            }

            // 3. Spawn stage-specific enemies
            setupEnemies();

            // 4. Respawn player at stage spawn point
            if (player != null) {
                  player.respawn();
            }

            if (keyH != null) {
                  keyH.resetAll();
            }
      }

      // Initialize and place enemies on the map
      public void setupEnemies() {
            enemies.clear();

            if (currentStage == 2) {
                  // === STAGE 2: SUNSET MILITARY OUTPOST ===
                  // 1. Snake slithering on the Left Watchtower Ground (surface y = 547)
                  enemies.add(new Enemy(this, "snake_slither", 180, 511, 130, 300));

                  // 2. Octopus guarding the Watchtower Balcony (surface y = 412)
                  enemies.add(new Enemy(this, "octopus", 80, 362, 50, 130));

                  // 3. Snail crawling on Pillar 1 Platform (surface y = 424)
                  enemies.add(new Enemy(this, "snail", 360, 386, 325, 435));

                  // 4. Dragon running on the Mid Floor Walkway (surface y = 544)
                  enemies.add(new Enemy(this, "dragon", 460, 492, 400, 620));

                  // 5. Snail crawling on Pillar 2 Platform (surface y = 446)
                  enemies.add(new Enemy(this, "snail", 680, 408, 630, 775));

                  // 6. Octopus patrolling the Radar Rooftop (surface y = 388)
                  enemies.add(new Enemy(this, "octopus", 880, 338, 830, 975));

                  // 7. Snake slithering across the Bunker Terrace (surface y = 506)
                  enemies.add(new Enemy(this, "snake_slither", 850, 470, 795, 1030));

                  // 8. Beetle flying in the west sky over the outpost
                  enemies.add(new Enemy(this, "beetle", 320, 220, 220, 450));

                  // 9. Skull floating in the clouds near the radar dish
                  enemies.add(new Enemy(this, "skull", 720, 240, 620, 900));

                  // 10. SKELETON BOSS guarding the EXIT bunker door (surface y = 542, feet at 542 -> y = 542 - 100 = 442)
                  enemies.add(new SkeletonBoss(this, 1110, 442, 1050, 1200));
            } else {
                  // === STAGE 1: SEWER LAB ===
                  // 1. Snail crawling on Platform 1 (surface is at y = 398)
                  enemies.add(new Enemy(this, "snail", 160, 360, 130, 230));

                  // 2. Octopus guarding the Turret Platform (surface is at y = 324)
                  enemies.add(new Enemy(this, "octopus", 350, 274, 320, 400));

                  // 3. Octopus patrolling the Pipe Catwalk (surface is at y = 374)
                  enemies.add(new Enemy(this, "octopus", 440, 324, 390, 520));

                  // 4. Skull floating above the toxic acid pit (cols 3-5, y = 210)
                  enemies.add(new Enemy(this, "skull", 220, 210, 180, 310));

                  // 5. Snake slithering across Lower Bridge walkway over acid (surface is at y = 478)
                  enemies.add(new Enemy(this, "snake_slither", 400, 447, 330, 560));

                  // 6. Octopus patrolling the Top Ladder Catwalk (surface is at y = 320)
                  enemies.add(new Enemy(this, "octopus", 830, 270, 775, 895));

                  // 7. Snail crawling on the Right Sewer Floor (surface is at y = 478)
                  enemies.add(new Enemy(this, "snail", 760, 440, 730, 880));

                  // 8. Snake slithering on the Right Floor near crates (surface is at y = 478)
                  enemies.add(new Enemy(this, "snake_slither", 950, 447, 900, 1080));

                  // 9. Skull hovering near Laboratory Bunker Door (cols 16-18, y = 230)
                  enemies.add(new Enemy(this, "skull", 1120, 230, 1060, 1200));
            }
      }

      // Start the game from the Start Screen
      public void startGame() {
            this.gameStarted = true;
            if (background != null && background.getStartScreen() != null) {
                  background.getStartScreen().hideStartButton();
            }
            this.requestFocusInWindow();
      }

      // Reset all game elements to restart the game
      public synchronized void restartGame() {
            this.gameStarted = true;
            this.gameOver = false;
            int targetStage = gameWon ? 1 : currentStage;
            this.gameWon = false;
            loadStage(targetStage);
            this.requestFocusInWindow();
      }

      /**
       * Count the number of currently active (alive) enemies in the stage
       */
      public int getAliveEnemyCount() {
            int count = 0;
            for (Enemy e : enemies) {
                  if (e != null && e.alive) {
                        count++;
                  }
            }
            return count;
      }

      /**
       * Check if all enemies in Stage 1 have been defeated
       * Triggers victory condition and shows the "GO TO STAGE 2" button
       */
      public boolean isStage1Cleared() {
            return gameStarted && !gameOver && currentStage == 1 && !enemies.isEmpty() && getAliveEnemyCount() == 0;
      }

      /**
       * Check if all enemies in Stage 2 have been defeated (all 10 enemies including Skeleton Boss)
       * Triggers Stage 2 victory ("YOU WIN")
       */
      public boolean isStage2Cleared() {
            return gameStarted && !gameOver && currentStage == 2 && !enemies.isEmpty() && getAliveEnemyCount() == 0;
      }

      /**
       * Check if the Stage 2 Skeleton Boss has been defeated
       */
      public boolean isBossDefeated() {
            if (currentStage != 2) return false;
            for (Enemy e : enemies) {
                  if (e instanceof SkeletonBoss) {
                        SkeletonBoss sb = (SkeletonBoss) e;
                        if (sb.hp <= 0 && !sb.alive) {
                              return true;
                        }
                  }
            }
            return false;
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
      public synchronized void update() {
            // When game is not started, stay on Start Screen
            if (!gameStarted) {
                  return;
            }

            // Hotkeys for stage switching (Press [ 1 ] for Stage 1, [ 2 ] for Stage 2, [ N ] for Next Stage)
            if (keyH.stage1Key && currentStage != 1) {
                  loadStage(1);
                  return;
            }
            if (keyH.stage2Key && currentStage != 2) {
                  loadStage(2);
                  return;
            }
            if (keyH.nextStageKey) {
                  keyH.nextStageKey = false;
                  loadStage(currentStage == 1 ? 2 : 1);
                  return;
            }

            // Check victory condition in Stage 2: ALL enemies defeated ("กำจัด enemy หมด ทุกตัว ถึงจะชนะ")
            if (currentStage == 2 && !gameWon && isStage2Cleared()) {
                  gameWon = true;
            }

            // When game is over ("YOU LOSE"), won ("YOU WIN"), or Stage 1 cleared, pause entity updates
            if (gameOver || gameWon || isStage1Cleared()) {
                  return;
            }

            if (player != null) {
                  player.update();

                  // Stage progression: reaching the laboratory bunker door on Stage 1 advances to Stage 2!
                  if (currentStage == 1 && player.x >= 1180) {
                        loadStage(2);
                        return;
                  }
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
                                                if (e instanceof SkeletonBoss) {
                                                      SkeletonBoss sb = (SkeletonBoss) e;
                                                      sb.takeDamage(1);
                                                } else {
                                                      e.hp--;
                                                      if (e.hp <= 0) {
                                                            e.alive = false;
                                                      }
                                                }

                                                // Victory only when all enemies in Stage 2 are defeated ("กำจัด enemy หมด ทุกตัว ถึงจะชนะ")
                                                if (currentStage == 2 && isStage2Cleared()) {
                                                      gameWon = true;
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

            // Update Boss Projectiles (Green Fireballs launched by Skeleton Boss)
            for (int i = bossProjectiles.size() - 1; i >= 0; i--) {
                  GreenFireball gf = bossProjectiles.get(i);
                  if (gf != null && gf.active) {
                        gf.update();

                        // Check collision with player
                        if (player != null && player.hitbox != null && gf.hitbox != null && gf.active) {
                              if (gf.hitbox.intersects(player.hitbox)) {
                                    gf.active = false;
                                    if (!player.invincible) {
                                          player.life--;
                                          player.invincible = true;
                                          player.ySpeed = -6; // Hurt hop
                                          player.isJumping = true;

                                          // Knockback player away from fireball
                                          if (player.x < gf.x) {
                                                player.x -= 15;
                                          } else {
                                                player.x += 15;
                                          }
                                          if (player.x < 0) player.x = 0;
                                          if (player.x > screenWidth - player.width) player.x = screenWidth - player.width;

                                          if (player.life <= 0) {
                                                player.die();
                                          }
                                    }
                              }
                        }
                  }

                  if (gf == null || !gf.active) {
                        bossProjectiles.remove(i);
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

                                          // If all hearts lost, player dies and triggers game over
                                          if (player.life <= 0) {
                                                player.die();
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

            // 0. If game not started, render Start Screen with black background & start button
            if (!gameStarted) {
                  if (background != null) {
                        background.drawStartScreen(g);
                  }
                  return;
            }

            // 1. Render map background graphic (via Background -> Graphic Composition)
            if (background != null) {
                  background.drawBackground(g);
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

            // 3b. Render all active green fireball projectiles from Skeleton Boss
            for (GreenFireball gf : bossProjectiles) {
                  if (gf != null && gf.active) {
                        gf.draw(g);
                  }
            }

            // 4. Render player on the map
            if (player != null) {
                  player.draw(g);
            }

            // 5. Render GUI overlay (Hearts HUD & Game Over screen via Background -> GUI Composition)
            if (background != null) {
                  background.drawGUI(g);
            }
      }

      // Getter for background image asset (delegated to Graphic)
      public BufferedImage getBgImage() {
            return (background != null && background.getGraphic() != null) ? background.getGraphic().getBgImage() : null;
      }
}




     