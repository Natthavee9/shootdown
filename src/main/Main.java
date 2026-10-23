package main;
import javax.swing.*;

public class Main {
      public static void main(String[] args) {
            // 1. Create the game window
            JFrame window = new JFrame();
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit application when the close button is clicked
            window.setResizable(false); // Lock window size to prevent resolution distortion
            window.setTitle("Shootdown"); // Set window title

            // 2. Add GamePanel (game logic and graphics) to the window
            GamePanel gamePanel = new GamePanel();
            window.add(gamePanel);

            // 3. Configure window size and display settings
            window.pack();
            window.setLocationRelativeTo(null); // Center the window on the screen
            window.setVisible(true); // Make the window visible

            
      }
}