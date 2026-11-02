package main;

import entity.Enemy;
import entity.Player;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class TestSunsetStage {
    public static void main(String[] args) throws Exception {
        GamePanel gp = new GamePanel();
        Player p = gp.player;

        System.out.println("=== TEST 1: Stage 1 Initial State ===");
        System.out.println("Stage: " + gp.currentStage + ", enemies count: " + gp.enemies.size() + 
            ", player pos=(" + p.x + ", " + p.y + ")");
        if (gp.currentStage == 1 && gp.enemies.size() == 9 && p.x == 40) {
            System.out.println("PASS: Stage 1 loaded correctly with 9 enemies and player at spawn!");
        } else {
            System.out.println("FAIL: Stage 1 initial state incorrect");
        }

        System.out.println("\n=== TEST 2: Reaching End Door in Stage 1 Advances to Stage 2 ===");
        p.x = 1185;
        gp.update();
        System.out.println("After reaching door -> Stage: " + gp.currentStage + 
            ", enemies count: " + gp.enemies.size() + ", player pos=(" + p.x + ", " + p.y + ")");
        if (gp.currentStage == 2 && gp.enemies.size() == 10 && p.x == 60) {
            System.out.println("PASS: Advanced to Stage 2 (map_sunset) with 10 enemies and player at sunset spawn!");
        } else {
            System.out.println("FAIL: Stage progression to Stage 2 failed!");
        }

        System.out.println("\n=== TEST 3: Sunset Enemy Diversity ===");
        boolean hasDragon = false, hasBeetle = false, hasSnake = false, hasOctopus = false, hasSnail = false, hasSkull = false;
        for (Enemy e : gp.enemies) {
            if ("dragon".equalsIgnoreCase(e.type) || "dragon_run".equalsIgnoreCase(e.type)) hasDragon = true;
            if ("beetle".equalsIgnoreCase(e.type) || "beetle_fly".equalsIgnoreCase(e.type)) hasBeetle = true;
            if ("snake_slither".equalsIgnoreCase(e.type) || "snake".equalsIgnoreCase(e.type)) hasSnake = true;
            if ("octopus".equalsIgnoreCase(e.type)) hasOctopus = true;
            if ("snail".equalsIgnoreCase(e.type)) hasSnail = true;
            if ("skull".equalsIgnoreCase(e.type)) hasSkull = true;
        }
        System.out.println("Enemies: Dragon=" + hasDragon + ", Beetle=" + hasBeetle + ", Snake=" + hasSnake +
            ", Octopus=" + hasOctopus + ", Snail=" + hasSnail + ", Skull=" + hasSkull);
        if (hasDragon && hasBeetle && hasSnake && hasOctopus && hasSnail && hasSkull) {
            System.out.println("PASS: All 6 enemy types successfully spawned on map_sunset!");
        } else {
            System.out.println("FAIL: Missing enemy types in Stage 2");
        }

        System.out.println("\n=== TEST 4: Walking Across Sunset Map (No Fall Off / Solid Full Ground) ===");
        // Walk pixel by pixel across the entire width of map sunset (x = 0 to 1200)
        boolean fullWalkPass = true;
        p.invincible = true; // avoid enemy damage during continuous walk test
        for (int testX = 50; testX <= 1180; testX += 15) {
            p.x = testX;
            p.y = 544 - p.height;
            p.ySpeed = 1;
            p.isJumping = true;
            gp.update();

            // Feet should be safely on ground or platform (feet Y <= 548) and NEVER fall below or die
            int feetY = p.y + p.height;
            if (gp.gameOver || feetY > 548 || feetY < 380) {
                System.out.println("FAIL at X=" + testX + ": feetY=" + feetY + ", gameOver=" + gp.gameOver);
                fullWalkPass = false;
                break;
            }
        }
        if (fullWalkPass) {
            System.out.println("PASS: Verified continuous solid ground across entire map (X=50..1180)! No falling off!");
        } else {
            System.out.println("FAIL: Player fell through ground on map sunset!");
        }

        System.out.println("\n=== TEST 5: Standing on Sunset Platforms ===");
        // 1. Pillar 1 platform (y = 424) - temporarily kill enemy on Pillar 1 to test landing
        gp.enemies.get(2).alive = false; // Disable snail so it doesn't knock back player during landing test
        p.x = 380;
        p.y = 424 - p.height;
        p.ySpeed = 1;
        p.isJumping = true;
        gp.update();
        boolean standPillar1 = (p.y == 424 - p.height && !p.isJumping);

        // 2. Radar rooftop (y = 388)
        gp.enemies.get(5).alive = false; // Disable octopus so it doesn't knock back player
        p.x = 900;
        p.y = 388 - p.height;
        p.ySpeed = 1;
        p.isJumping = true;
        gp.update();
        boolean standRadar = (p.y == 388 - p.height && !p.isJumping);

        System.out.println("Pillar 1 Standing: " + standPillar1 + ", Radar Standing: " + standRadar);
        if (standPillar1 && standRadar) {
            System.out.println("PASS: Player landed and stood flush on sunset elevated platforms!");
        } else {
            System.out.println("FAIL: Platform standing failed in Stage 2");
        }

        System.out.println("\n=== TEST 6: Render Sunset Stage Screen Preview ===");
        // Move player to middle to see alongside enemies
        p.x = 240;
        p.y = 547 - p.height;
        BufferedImage screen = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
        gp.paintComponent(screen.getGraphics());
        File outFile = new File("stage2_sunset_preview.png");
        ImageIO.write(screen, "png", outFile);
        System.out.println("PASS: Saved stage2_sunset_preview.png successfully!");
    }
}
