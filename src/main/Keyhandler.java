package main;

import java.awt.event.*;

// Keyhandler processes keyboard inputs for character movement, shooting, and restarting
public class Keyhandler implements KeyListener {

      // Flags tracking whether control keys are currently being pressed
      public boolean upPress, downPress, leftPress, rightPress;
      public boolean shoot;
      public boolean restart;
      public boolean enterPress;
      public boolean stage1Key, stage2Key, nextStageKey;

      // Invoked when a key is typed (pressed and released); not used for game movement
      @Override
      public void keyTyped(KeyEvent e) {
            // Unused, but required by the KeyListener interface
      }

      // Invoked when a key is pressed down
      @Override 
      public void keyPressed(KeyEvent e) {
            int button = e.getKeyCode(); // Get the key code of the pressed key
            
            // Movement controls (WASD and Arrow keys)
            if (button == KeyEvent.VK_W || button == KeyEvent.VK_UP) {
                  upPress = true;
            }
            if (button == KeyEvent.VK_D || button == KeyEvent.VK_RIGHT) {
                  rightPress = true;
            }
            if (button == KeyEvent.VK_A || button == KeyEvent.VK_LEFT) {
                  leftPress = true;
            }
            if (button == KeyEvent.VK_S || button == KeyEvent.VK_DOWN) {
                  downPress = true;
            }

            // Shooting action
            if (button == KeyEvent.VK_SPACE) {
                  shoot = true;
            }

            // Game Over / Restart controls
            if (button == KeyEvent.VK_R) {
                  restart = true;
            }
            if (button == KeyEvent.VK_ENTER) {
                  enterPress = true;
            }

            // Stage selection controls
            if (button == KeyEvent.VK_1) {
                  stage1Key = true;
            }
            if (button == KeyEvent.VK_2) {
                  stage2Key = true;
            }
            if (button == KeyEvent.VK_N) {
                  nextStageKey = true;
            }
      }

      // Invoked when a key is released; resets flags to stop continuous action
      @Override 
      public void keyReleased(KeyEvent e) {
            int button = e.getKeyCode(); // Get the key code of the released key

            // Reset movement flags
            if (button == KeyEvent.VK_W || button == KeyEvent.VK_UP) {
                  upPress = false;
            }
            if (button == KeyEvent.VK_D || button == KeyEvent.VK_RIGHT) {
                  rightPress = false;
            }
            if (button == KeyEvent.VK_A || button == KeyEvent.VK_LEFT) {
                  leftPress = false;
            }
            if (button == KeyEvent.VK_S || button == KeyEvent.VK_DOWN) {
                  downPress = false;
            }

            // Reset shooting flag
            if (button == KeyEvent.VK_SPACE) {
                  shoot = false;
            }

            // Reset restart flags
            if (button == KeyEvent.VK_R) {
                  restart = false;
            }
            if (button == KeyEvent.VK_ENTER) {
                  enterPress = false;
            }

            // Reset stage selection flags
            if (button == KeyEvent.VK_1) {
                  stage1Key = false;
            }
            if (button == KeyEvent.VK_2) {
                  stage2Key = false;
            }
            if (button == KeyEvent.VK_N) {
                  nextStageKey = false;
            }
      }

      // Reset all key inputs
      public void resetAll() {
            upPress = false;
            downPress = false;
            leftPress = false;
            rightPress = false;
            shoot = false;
            restart = false;
            enterPress = false;
            stage1Key = false;
            stage2Key = false;
            nextStageKey = false;
      }
}
