package main;

import javax.swing.*;

public class Main {
      public static void main(String[] args) {
            // 1. Create the game window
            JFrame window = new JFrame();
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);
            window.setTitle("Shootdown");

            // 2. Add GamePanel (game canvas & logic)
            GamePanel gamePanel = new GamePanel();
            window.add(gamePanel);

            // 3. Configure window display
            window.pack();
            window.setLocationRelativeTo(null); // Center on screen
            window.setVisible(true);

            // 4. Start the 60 FPS game loop thread
            gamePanel.startGameThread();
      }
}
