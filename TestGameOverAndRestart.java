package main;

import entity.Player;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class TestGameOverAndRestart {
    public static void main(String[] args) throws Exception {
        GamePanel gp = new GamePanel();
        Player p = gp.player;

        System.out.println("=== TEST 1: Walking into Acid Pit 1 (Col 4) ===");
        p.x = 265;
        p.y = 398 - p.height;
        p.ySpeed = 1;
        p.isJumping = true;

        for (int i = 0; i < 30; i++) {
            gp.update();
            if (gp.gameOver) break;
        }

        System.out.println("gameOver: " + gp.gameOver + ", player life: " + p.life);
        if (gp.gameOver && p.life == 0) {
            System.out.println("PASS: Player died immediately upon falling into acid pit 1!");
        } else {
            System.out.println("FAIL: gameOver=" + gp.gameOver + ", life=" + p.life);
        }

        System.out.println("\n=== TEST 2: In GameOver state, movement is paused ===");
        int savedX = p.x;
        int savedY = p.y;
        gp.keyH.rightPress = true;
        gp.update();
        gp.keyH.rightPress = false;
        if (p.x == savedX && p.y == savedY && gp.gameOver) {
            System.out.println("PASS: Player cannot move while in GameOver state!");
        } else {
            System.out.println("FAIL: Player moved during GameOver!");
        }

        System.out.println("\n=== TEST 3: Keyboard inputs (R, ENTER, SPACE) DO NOT restart the game ===");
        gp.keyH.restart = true;
        gp.keyH.enterPress = true;
        gp.keyH.shoot = true;
        gp.update();
        gp.keyH.restart = false;
        gp.keyH.enterPress = false;
        gp.keyH.shoot = false;

        if (gp.gameOver && p.life == 0) {
            System.out.println("PASS: Keyboard input ignored during GameOver. Game still in GameOver state!");
        } else {
            System.out.println("FAIL: Keyboard input restarted the game!");
        }

        System.out.println("\n=== TEST 4: Clicking GUI Restart Button restarts the game ===");
        javax.swing.JButton btn = gp.background.getRestartButton();
        if (btn != null) {
            System.out.println("Button found with text: '" + btn.getText() + "'");
            btn.doClick(); // Click the button
            boolean btnRestartOk = (!gp.gameOver && p.life == p.maxLife && p.x == 40 && !btn.isVisible());
            if (btnRestartOk) {
                System.out.println("PASS: Restart button clicked successfully and reset the game!");
            } else {
                System.out.println("FAIL: Restart button did not reset state properly.");
            }
        } else {
            System.out.println("FAIL: Restart button is null!");
        }

        System.out.println("\n=== TEST 5: Walking off Lower Bridge into Col 10 ===");
        p.x = 665;
        p.y = 478 - p.height;
        p.ySpeed = 1;
        p.isJumping = true;
        for (int i = 0; i < 30; i++) {
            gp.update();
            if (gp.gameOver) break;
        }
        if (gp.gameOver && p.life == 0) {
            System.out.println("PASS: Player died immediately in Acid Pit 2!");
        } else {
            System.out.println("FAIL: Acid Pit 2 gameOver=" + gp.gameOver + ", life=" + p.life);
        }

        System.out.println("\n=== TEST 6: Render Game Over Screen Overlay with Button ===");
        BufferedImage screen = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
        gp.paintComponent(screen.getGraphics());
        File outFile = new File("game_over_preview.png");
        ImageIO.write(screen, "png", outFile);
        System.out.println("PASS: Saved game_over_preview.png successfully!");
    }
}

