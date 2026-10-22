package main;
import javax.swing.*;

public class Main {
      public  void main(String[] args) {
            // 1. Create the game window
            JFrame window = new JFrame();
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit application when the close button is clicked
            window.setResizable(false); // Lock window size to prevent resolution distortion
            window.setTitle("Shootdown"); // Set window title

            // 2. Add GamePanel (game logic and graphics) to the window
            GamePanel gamePanel = new GamePanel();
            window.add(gamePanel);

            // 3. Configure window size and display settings
            window.setSize(1280, 720); // Set window resolution to 1280 x 720 pixels (16:9)
            window.setLocationRelativeTo(null); // Center the window on the screen
            window.setVisible(true); // Make the window visible

            
      }
}