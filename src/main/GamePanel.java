package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {

      // SCREEN SETTINGS
      int screenWidth = 1280;  // Panel width in pixels (16:9 aspect ratio)
      int screenHeight = 720;  // Panel height in pixels
      BufferedImage bgImage;   // Background image asset

      public GamePanel() {
            // Configure panel settings
            this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // Set canvas dimensions
            this.setDoubleBuffered(true); // Enable double buffering to eliminate screen flickering
            this.setFocusable(true);      // Allow panel to receive keyboard focus and input
            
            // Load background image from resources
            loadBackgroundImage();
      }

      private void loadBackgroundImage() {
            try {
                  File file = new File("res/background/map_sewer_lab.png");
                  // Fallback for different working directories
                  if (!file.exists()) {
                        file = new File("../../res/background/map_sewer_lab.png");
                  }
                  if (file.exists()) {
                        bgImage = ImageIO.read(file);
                  } else {
                        System.out.println("Warning: Cannot find background image at: " + file.getAbsolutePath());
                  }
            } catch (IOException e) {
                  e.printStackTrace();
            }
      }

      // Built-in Swing method for rendering graphics on this panel
      @Override 
      public void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Draw background image scaled to match the panel size
            if (bgImage != null) {
                  g.drawImage(bgImage, 0, 0, screenWidth, screenHeight, null);
            }
      }

      // Game Loop: handles continuous game updates and rendering at a target frame rate
      @Override 
      public void run() {
            // Game loop logic will be implemented here
      }
      
}
