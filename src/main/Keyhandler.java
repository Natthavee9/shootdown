package main;

import java.awt.event.*;


// Keyhandler processes keyboard inputs for character movement and game actions
public class Keyhandler implements KeyListener {

      // Flags tracking whether control keys are currently being pressed
      public boolean upPress, downPress, leftPress, rightPress;
      public boolean shoot;

      // Invoked when a key is typed (pressed and released); not used for game movement
      @Override
      public void keyTyped(KeyEvent e) {
            // Unused, but required by the KeyListener interface
      }

      // Invoked when a key is pressed down
      @Override 
      public void keyPressed(KeyEvent e) {
            int button = e.getKeyCode(); // Get the key code of the pressed key
            
            // Movement and action controls
            if (button == KeyEvent.VK_W) { // Jump
                  upPress = true;
            }
            if (button == KeyEvent.VK_D) { // Move right
                  rightPress = true;
            }
            if (button == KeyEvent.VK_A) { // Move left
                  leftPress = true;
            }
            if (button == KeyEvent.VK_S) { // Crouch / Move down
                  downPress = true;
            }

            // Shooting action
            if (button == KeyEvent.VK_SPACE) {
                  shoot = true;
            }
      }

      // Invoked when a key is released; resets flags to stop continuous action
      @Override 
      public void keyReleased(KeyEvent e) {
            int button = e.getKeyCode(); // Get the key code of the released key

            // Reset movement flags
            if (button == KeyEvent.VK_W) {
                  upPress = false;
            }
            if (button == KeyEvent.VK_D) {
                  rightPress = false;
            }
            if (button == KeyEvent.VK_A) {
                  leftPress = false;
            }
            if (button == KeyEvent.VK_S) {
                  downPress = false;
            }

            // Reset shooting flag
            if (button == KeyEvent.VK_SPACE) {
                  shoot = false;
            }
      }
      
}
