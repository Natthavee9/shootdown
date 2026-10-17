package main;
import  java.awt.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {

      // SCREEN SETTINGS
      int screenWidth = 1280;  // Panel width in pixels (16:9 aspect ratio)
      int screenHeight = 720;  // Panel height in pixels
      Image bgImage;           // Background image asset

      public GamePanel() {
            // Configure panel settings
            this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // Set canvas dimensions
            this.setDoubleBuffered(true); // Enable double buffering to eliminate screen flickering
            this.setFocusable(true);      // Allow panel to receive keyboard focus and input
            
            // Load background image from resources
            bgImage = new ImageIcon("res/background/map_sewer_lab.png").getImage();
      }

      // Built-in Swing method for rendering graphics on this panel
      @Override 
      public void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Draw background image scaled to match the panel size
            if (bgImage != null) {
                  g.drawImage(bgImage, 0, 0, screenWidth, screenHeight, this);
            }
      }

      // Game Loop: handles continuous game updates and rendering at a target frame rate
      @Override 
      public void run() {
            // Game loop logic will be implemented here
      }
      
}
