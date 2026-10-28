package main;

import entity.Player;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {

      // SCREEN SETTINGS
      public int screenWidth = 1280;  // Panel width in pixels (16:9 aspect ratio)
      public int screenHeight = 720;  // Panel height in pixels
      private BufferedImage bgImage;  // Background image asset

      // SYSTEM & CONTROLS
      final int FPS = 60;             // Target frames per second
      public Thread gameThread;       // Game loop thread
      public Keyhandler keyH = new Keyhandler();

      // GAME ENTITIES
      public Player player;

      public GamePanel() {
            // Configure panel settings
            this.setPreferredSize(new Dimension(screenWidth, screenHeight));
            this.setDoubleBuffered(true); // Eliminate screen flickering
            this.setFocusable(true);      // Allow receiving keyboard input
            this.addKeyListener(keyH);    // Register key listener for controls

            // Load background image
            loadBackgroundImage();

            // Instantiate player entity
            player = new Player(this, keyH);
      }

      // Load background image with multi-path resolution
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
      }

      // Built-in Swing method for rendering graphics on this panel
      @Override 
      public void paintComponent(Graphics g) {
            super.paintComponent(g);

            // 1. Draw background image
            if (bgImage != null) {
                  g.drawImage(bgImage, 0, 0, screenWidth, screenHeight, null);
            } else {
                  // Fallback dark background
                  g.setColor(new Color(25, 25, 30));
                  g.fillRect(0, 0, screenWidth, screenHeight);
            }

            // 2. Draw player
            if (player != null) {
                  player.draw(g);
            }
      }
}
