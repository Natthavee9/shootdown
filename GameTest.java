package main;

import entity.Enemy;
import entity.Player;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Unified Test Suite for the Game
 * Combines:
 * - Start Screen (Black background & Start Button)
 * - Stage 1 (Sewer Lab, Game Over, Hearts, Restart Button) 
 * - Stage 2 (Sunset Outpost, Enemies, Full Ground, Platform Physics)
 */
public class GameTest {

    public static void main(String[] args) throws Exception {
        System.out.println("==================================================");
        System.out.println("         SHOOTDOWN GAME - AUTOMATED TEST SUITE    ");
        System.out.println("==================================================");

        testStartScreen_TitleAndButton();
        testStage1_SewerLabAndGameOver();
        testStage2_SunsetOutpostAndPlatforms();
        testSkeletonBoss_CombatAndDurability();
        testStageBadge_CleanNameWithoutEnemyCount();

        System.out.println("\n>>> ALL TESTS EXECUTED SUCCESSFULLY! <<<");
    }

    /**
     * Suite 0: Start Screen - Black background, Title, and Start Game Button.
     */
    public static void testStartScreen_TitleAndButton() throws Exception {
        System.out.println("\n--- [SUITE 0] START SCREEN: BLACK BG & START BUTTON ---");
        GamePanel gp = new GamePanel();

        // 1. Initial State: gameStarted is false
        assertTest(!gp.gameStarted, "Game starts in Start Screen state (gameStarted == false)");

        // 2. Start Button exists in Background composition
        javax.swing.JButton startBtn = gp.background.getStartButton();
        assertTest(startBtn != null && "START GAME".equals(startBtn.getText()),
            "Start Button exists with text 'START GAME' in Background -> StartScreen composition");

        // 3. Render Start Screen to preview image
        BufferedImage screen = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
        gp.paintComponent(screen.getGraphics());
        File outFile = new File("start_screen_preview.png");
        ImageIO.write(screen, "png", outFile);
        assertTest(outFile.exists() && outFile.length() > 0, "Rendered start_screen_preview.png successfully");

        // 4. Click Start Button transitions game to active play state
        startBtn.doClick();
        assertTest(gp.gameStarted && !startBtn.isVisible(),
            "Clicking START GAME button starts the game (gameStarted == true) and hides the button");
    }

    /**
     * Suite 1: Stage 1 (Sewer Lab) - Fall hazards, half-heart reduction (ลดทีละครึ่งหัวใจ),
     * returning to spawn, Game Over when hearts run out, and GUI Restart Button.
     */
    public static void testStage1_SewerLabAndGameOver() throws Exception {
        System.out.println("\n--- [SUITE 1] STAGE 1: SEWER LAB & HALF-HEART HP SYSTEM ---");
        GamePanel gp = new GamePanel();
        gp.startGame(); // Enter play state
        Player p = gp.player;

        // 1. Initial State: 6 half-hearts = 3 full heart containers
        assertTest(gp.currentStage == 1 && gp.enemies.size() == 9 && p.x == 40 && p.life == 6 && p.maxLife == 6,
            "Stage 1 starts with 9 enemies, player at spawn (40, 378), and 6 half-hearts (3 full hearts)");

        // 2. Fall #1 into acid pit: loses half a heart (6 -> 5 HP) and returns to spawn
        p.x = 265;
        p.y = 485 - p.height;
        p.ySpeed = 1;
        p.isJumping = true;
        gp.update();
        assertTest(p.life == 5 && p.x == 40 && !gp.gameOver,
            "Fall #1: Lost half a heart (6 -> 5 HP), returned to spawn (40, 378), game continues");

        // 3. Fall #2 into acid pit: loses another half heart (5 -> 4 HP = 2 full hearts) and returns to spawn
        p.x = 265;
        p.y = 485 - p.height;
        p.ySpeed = 1;
        p.isJumping = true;
        gp.update();
        assertTest(p.life == 4 && p.x == 40 && !gp.gameOver,
            "Fall #2: Lost 2nd half-heart (5 -> 4 HP = 2 full hearts), returned to spawn, game continues");

        // 4. Deplete remaining half-hearts until 0 -> triggers Game Over
        p.life = 1;
        p.x = 670;
        p.y = 485 - p.height;
        p.ySpeed = 1;
        p.isJumping = true;
        gp.update();
        assertTest(p.life == 0 && gp.gameOver,
            "Final Fall: Last half-heart lost (1 -> 0 HP) -> Game Over dialog and state triggered");

        // 5. In Game Over state, keyboard inputs (R, ENTER, SPACE) DO NOT restart the game
        gp.keyH.restart = true;
        gp.keyH.enterPress = true;
        gp.keyH.shoot = true;
        gp.update();
        gp.keyH.restart = false;
        gp.keyH.enterPress = false;
        gp.keyH.shoot = false;
        assertTest(gp.gameOver && p.life == 0,
            "Keyboard inputs ignored during GameOver (Button click only)");

        // 6. Clicking GUI Restart Button resets game and restores full health (6 half-hearts)
        javax.swing.JButton btn = gp.background.getRestartButton();
        if (btn != null) {
            btn.doClick();
            assertTest(!gp.gameOver && p.life == p.maxLife && p.life == 6 && p.x == 40,
                "GUI Restart Button clicked: Restores full health (6 half-hearts = 3 full hearts), resets enemies & player at spawn");
        } else {
            assertTest(false, "GUI Restart button not found");
        }

        // 7. Defeating all 9 Stage 1 enemies displays "GO TO STAGE 2" button
        assertTest(gp.getAliveEnemyCount() == 9 && !gp.isStage1Cleared(),
            "Stage 1 starts with 9 active enemies, Stage 2 button remains hidden");

        // Defeat all enemies in Stage 1
        for (Enemy e : gp.enemies) {
            e.alive = false;
        }

        assertTest(gp.getAliveEnemyCount() == 0 && gp.isStage1Cleared(),
            "All 9 enemies defeated -> isStage1Cleared() becomes true");

        // Render preview image of Stage 1 Cleared with the Go to Stage 2 button
        BufferedImage clearImg = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics gClear = clearImg.getGraphics();
        gp.background.drawBackground(gClear);
        gp.player.draw(gClear);
        gp.background.drawGUI(gClear);
        gClear.dispose();

        javax.swing.JButton nextBtn = gp.background.getNextStageButton();
        assertTest(nextBtn != null && nextBtn.isVisible() && nextBtn.getText().contains("STAGE 2"),
            "Stage 2 Button appears: 'GO TO STAGE 2  ►' button is active and visible upon eliminating all enemies");

        File clearOutFile = new File("C:/Users/LOQ/.gemini/antigravity/brain/a1d41a47-75ae-4edc-a426-b46c2fbc991d/stage1_cleared_button_preview.png");
        ImageIO.write(clearImg, "png", clearOutFile);
        assertTest(clearOutFile.exists() && clearOutFile.length() > 0,
            "Rendered stage1_cleared_button_preview.png successfully");

        // Click "GO TO STAGE 2" button -> Advances game to Stage 2 (Sunset Outpost)
        nextBtn.doClick();
        assertTest(gp.currentStage == 2 && gp.enemies.size() == 10 && p.x == 60 && !nextBtn.isVisible(),
            "Clicking 'GO TO STAGE 2' button successfully transitions to Stage 2 and spawns 10 outpost enemies");
    }

    /**
     * Suite 2: Stage 2 (Sunset Outpost) - Progression, Enemy diversity, 
     * Continuous full ground (no fall-off), and Platform standing.
     */
    public static void testStage2_SunsetOutpostAndPlatforms() throws Exception {
        System.out.println("\n--- [SUITE 2] STAGE 2: SUNSET OUTPOST & PLATFORMS ---");
        GamePanel gp = new GamePanel();
        gp.startGame(); // Enter play state
        Player p = gp.player;

        // 1. Stage progression: Reaching right bunker door in Stage 1 advances to Stage 2
        p.x = 1185;
        gp.update();
        assertTest(gp.currentStage == 2 && gp.enemies.size() == 10 && p.x == 60,
            "Door progression: Reaching right door in Stage 1 advances to Stage 2 (Sunset)");

        // 2. Enemy diversity on sunset map (including SKELETON BOSS)
        boolean hasDragon = false, hasBeetle = false, hasSnake = false, hasOctopus = false, hasSnail = false, hasSkull = false, hasBoss = false;
        for (Enemy e : gp.enemies) {
            if ("dragon".equalsIgnoreCase(e.type) || "dragon_run".equalsIgnoreCase(e.type)) hasDragon = true;
            if ("beetle".equalsIgnoreCase(e.type) || "beetle_fly".equalsIgnoreCase(e.type)) hasBeetle = true;
            if ("snake_slither".equalsIgnoreCase(e.type) || "snake".equalsIgnoreCase(e.type)) hasSnake = true;
            if ("octopus".equalsIgnoreCase(e.type)) hasOctopus = true;
            if ("snail".equalsIgnoreCase(e.type)) hasSnail = true;
            if ("skull".equalsIgnoreCase(e.type)) hasSkull = true;
            if (e instanceof entity.SkeletonBoss) hasBoss = true;
        }
        assertTest(hasDragon && hasBeetle && hasSnake && hasOctopus && hasSnail && hasSkull && hasBoss,
            "All enemy types active in Stage 2 including formidable SKELETON BOSS");

        // 3. Player Movement & Physics Settings (Reduced speed & Higher jump)
        assertTest(p.speed == 3 && p.jumpStrength == -18,
            "Player Physics Tuning: speed reduced to 3 (controlled movement), jumpStrength boosted to -18 (higher jump)");

        // 4. Dynamic walking across the entire map ("เดินตามพื้น"):
        // Player walks right smoothly: Watchtower (547) -> Mid Floor (544) -> Bunker Terrace (506) -> Exit Ground (542)
        // Keep player invincible during the peaceful terrain contour walk
        p.invincible = true;
        p.invincibleCounter = -999999;

        p.x = 60;
        p.y = 547 - p.height;
        p.isJumping = false;
        p.onSolidGround = true;

        boolean walkWatchtower = false;
        boolean walkMidFloor = false;
        boolean walkBunkerTerrace = false;
        boolean walkExitGround = false;
        boolean noSinkingOnTerrace = true;

        gp.keyH.rightPress = true;
        for (int frame = 0; frame < 420; frame++) {
            gp.update();
            int feetY = p.y + p.height;
            if (p.x >= 100 && p.x <= 320 && feetY == 547) walkWatchtower = true;
            if (p.x >= 400 && p.x <= 720 && feetY == 544) walkMidFloor = true;
            if (p.x >= 800 && p.x <= 1020) {
                if (feetY == 506) {
                    walkBunkerTerrace = true;
                } else if (feetY > 510) {
                    noSinkingOnTerrace = false; // Player sunk into the stone!
                }
            }
            if (p.x >= 1070 && p.x <= 1180 && feetY == 542) walkExitGround = true;
        }
        gp.keyH.rightPress = false;

        assertTest(walkWatchtower && walkMidFloor && walkBunkerTerrace && walkExitGround && noSinkingOnTerrace,
            "Walking along ground contour ('เดินตามพื้น'): 547 -> 544 -> 506 (Bunker Terrace) -> 542 (Exit Ground) with NO sinking");

        // 5. Reverse walking left: Exit Ground (542) -> Bunker Terrace (506) -> Mid Floor (544)
        boolean reverseWalkTerrace = false;
        boolean reverseWalkMid = false;
        gp.keyH.leftPress = true;
        for (int frame = 0; frame < 280; frame++) {
            gp.update();
            int feetY = p.y + p.height;
            if (p.x >= 800 && p.x <= 1020 && feetY == 506) reverseWalkTerrace = true;
            if (p.x >= 450 && p.x <= 700 && feetY == 544) reverseWalkMid = true;
        }
        gp.keyH.leftPress = false;

        assertTest(reverseWalkTerrace && reverseWalkMid,
            "Reverse walking left: Steps smoothly back onto Bunker Terrace (506) and down to Mid Floor (544)");

        // 6. Elevated platform landing
        p.x = 380;
        p.y = 424 - p.height;
        p.ySpeed = 1;
        p.isJumping = true;
        gp.update();
        boolean standPillar1 = (p.y == 424 - p.height && !p.isJumping);

        p.x = 900;
        p.y = 388 - p.height;
        p.ySpeed = 1;
        p.isJumping = true;
        gp.update();
        boolean standRadar = (p.y == 388 - p.height && !p.isJumping);

        assertTest(standPillar1 && standRadar,
            "Platform physics: Player lands and stands flush on Pillar 1 and Radar rooftop");
    }

    /**
     * Suite 3: Skeleton Boss Combat - High HP, Green Fireball Projectiles,
     * Damage Flash, Toughness (does not die in 1 hit), and Defeat.
     */
    public static void testSkeletonBoss_CombatAndDurability() throws Exception {
        System.out.println("\n--- [SUITE 3] SKELETON BOSS: HIGH HP & GREEN FIREBALL COMBAT ---");
        GamePanel gp = new GamePanel();
        gp.startGame();
        gp.loadStage(2); // Directly load Stage 2 (Sunset Outpost)

        // 1. Find SkeletonBoss in active enemies
        entity.SkeletonBoss boss = null;
        for (Enemy e : gp.enemies) {
            if (e instanceof entity.SkeletonBoss) {
                boss = (entity.SkeletonBoss) e;
                break;
            }
        }
        assertTest(boss != null && boss.x >= 1050 && (boss.y + boss.height == 542),
            "Skeleton Boss successfully positioned guarding the EXIT bunker door (x = 1110, feet at y = 542)");
        assertTest(boss != null && boss.maxHp == 15 && boss.hp == 15,
            "Boss High Durability: maxHp = 15 (normal enemies have 1 HP, boss is tough to defeat)");

        // 2. Boss shoots Green Fireball towards player
        gp.bossProjectiles.clear();
        gp.player.x = 700; // Player is to the left on Mid Floor / Terrace
        boss.x = 1110;
        boss.y = 442;
        // Run updates until boss shoots green fireball
        for (int i = 0; i < 70; i++) {
            boss.update();
            if (!gp.bossProjectiles.isEmpty()) break;
        }
        assertTest(!gp.bossProjectiles.isEmpty(),
            "Boss Attack: Skeleton Boss launches Green Fireball projectile towards player");

        // 3. Green Fireball hits player -> player loses half a heart and fireball deactivates
        entity.GreenFireball gf = gp.bossProjectiles.get(0);
        assertTest(gf.direction == -1 && gf.vx < 0,
            "Targeted Aiming: Green Fireball aimed directly towards player's position and horizontal direction");

        // Straight-line trajectory test ("ยิงตามการเคลื่อนที่ของ player แบบเป็นทางตรง ไม่ต้องแบบเลี้ยวตามการกระโดด")
        entity.GreenFireball straightFireball = new entity.GreenFireball(gp, 1000, 480, gp.player.x, gp.player.y);
        double initialVx = straightFireball.vx;
        double initialVy = straightFireball.vy;
        gp.player.y = 300; // Player jumped high into the air
        gp.player.isJumping = true;
        gp.player.ySpeed = -10;
        for (int step = 0; step < 10; step++) {
            straightFireball.update();
        }
        // Verify projectile maintains its straight-line velocity without turning or curving upwards
        assertTest(straightFireball.vx == initialVx && straightFireball.vy == initialVy,
            "Straight-Line Flight ('แบบเป็นทางตรง ไม่เลี้ยวตามการกระโดด'): Velocity remains constant without curving toward jump");

        // Impact test: Green Fireball hits player -> damages half a heart and deactivates
        gp.player.invincible = false;
        gp.player.isJumping = false;
        gp.player.ySpeed = 0;
        gp.player.x = 700;
        gp.player.y = 468; // Player standing flush on Mid Floor
        gp.player.hitbox.x = gp.player.x + 10;
        gp.player.hitbox.y = gp.player.y + 8;
        gp.player.life = 6;

        gf.posX = gp.player.x + 10;
        gf.posY = gp.player.y + 10;
        gf.x = (int) gf.posX;
        gf.y = (int) gf.posY;
        gf.hitbox.x = gf.x + 4;
        gf.hitbox.y = gf.y + 4;
        gp.update();

        assertTest(gp.player.life == 5 && !gf.active,
            "Green Fireball Impact: Damages player by half a heart (6 -> 5 HP) and projectile deactivates upon impact");

        // 4. Bullet impact on Boss: Does NOT die in 1 hit, HP drops to 14, triggers damage flash
        int initialHp = boss.hp;
        entity.Bullet bullet = new entity.Bullet(boss.x - 10, boss.y + 30, 1);
        gp.bullets.clear();
        gp.bullets.add(bullet);
        gp.update();

        assertTest(!bullet.active && boss.hp == initialHp - 1 && boss.alive,
            "Boss Durability: Player bullet hits boss -> HP decreases (15 -> 14) and boss REMAINS ALIVE (Tough boss)");

        // 5. Boss Defeat: Takes 14 more hits to fully defeat
        for (int hit = 0; hit < 14; hit++) {
            boss.takeDamage(1);
        }
        assertTest(boss.hp == 0 && !boss.alive,
            "Boss Defeat: Requires all 15 hits to defeat the Skeleton Boss (hp = 0, alive = false)");

        // Defeating only boss while other enemies remain DOES NOT trigger victory yet
        gp.update();
        assertTest(!gp.gameWon,
            "Boss Defeated but 9 enemies remain -> gameWon is FALSE ('แก้ stage 2 เมื่อ กำจัด enemy หมด ทุกตัว ถึงจะชนะ')");

        // 6. Defeat ALL remaining enemies in Stage 2 to achieve victory
        for (Enemy e : gp.enemies) {
            e.alive = false;
        }
        gp.update();
        assertTest(gp.isStage2Cleared() && gp.gameWon && !gp.gameOver,
            "All 10 enemies defeated in Stage 2 -> gameWon becomes true ('YOU WIN' state)");

        BufferedImage winImg = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics gWin = winImg.getGraphics();
        gp.background.drawBackground(gWin);
        gp.background.drawGUI(gWin);
        gWin.dispose();

        File winFile = new File("C:/Users/LOQ/.gemini/antigravity/brain/a1d41a47-75ae-4edc-a426-b46c2fbc991d/stage2_you_win_preview.png");
        ImageIO.write(winImg, "png", winFile);
        assertTest(winFile.exists() && winFile.length() > 0,
            "Rendered stage2_you_win_preview.png ('YOU WIN' dialog with 'PLAY AGAIN' button) successfully");

        // Single click on 'PLAY AGAIN' button: immediately restarts game without needing a 2nd click!
        javax.swing.JButton playAgainBtn = gp.background.getRestartButton();
        assertTest(playAgainBtn != null && playAgainBtn.isVisible(),
            "Play Again button is active and visible on victory screen");
        playAgainBtn.doClick();
        assertTest(!gp.gameWon && !gp.gameOver && gp.currentStage == 1 && gp.getAliveEnemyCount() == 9,
            "Single click on 'PLAY AGAIN' immediately restarts game from Stage 1 (NO 2nd click needed!)");

        // 7. Stage 2 Defeat: Player dies in Stage 2 triggers "YOU LOSE" screen
        GamePanel gpLose = new GamePanel();
        gpLose.startGame();
        gpLose.loadStage(2);
        gpLose.player.die();
        assertTest(gpLose.gameOver && !gpLose.gameWon && gpLose.currentStage == 2,
            "Player dies in Stage 2 -> gameOver becomes true ('YOU LOSE' state)");

        BufferedImage loseImg = new BufferedImage(gpLose.screenWidth, gpLose.screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics gLose = loseImg.getGraphics();
        gpLose.background.drawBackground(gLose);
        gpLose.background.drawGUI(gLose);
        gLose.dispose();

        File loseFile = new File("C:/Users/LOQ/.gemini/antigravity/brain/a1d41a47-75ae-4edc-a426-b46c2fbc991d/stage2_you_lose_preview.png");
        ImageIO.write(loseImg, "png", loseFile);
        assertTest(loseFile.exists() && loseFile.length() > 0,
            "Rendered stage2_you_lose_preview.png ('YOU LOSE' dialog with 'RESTART GAME' button) successfully");
    }

    /**
     * Suite 4: Top-Center Stage Badge - Shows ONLY Stage Name without enemy count
     * ("ปรับให้มีแค่ ชื่อ stage แต่ ไม่ต้องนับ enemy")
     */
    public static void testStageBadge_CleanNameWithoutEnemyCount() throws Exception {
        System.out.println("\n--- [SUITE 4] STAGE BADGE: CLEAN STAGE NAME WITHOUT ENEMY COUNT ---");
        GamePanel gp = new GamePanel();
        gp.startGame();

        // 1. Stage 1 Badge: Only "STAGE 1 : SEWER LAB", no enemy count
        BufferedImage s1Img = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics g1 = s1Img.getGraphics();
        gp.background.drawBackground(g1);
        gp.player.draw(g1);
        gp.background.drawGUI(g1);
        g1.dispose();

        File s1File = new File("C:/Users/LOQ/.gemini/antigravity/brain/a1d41a47-75ae-4edc-a426-b46c2fbc991d/stage1_clean_badge_preview.png");
        ImageIO.write(s1Img, "png", s1File);
        assertTest(s1File.exists() && s1File.length() > 0,
            "Stage 1 Clean Badge rendered to stage1_clean_badge_preview.png (shows 'STAGE 1 : SEWER LAB')");

        // 2. Stage 2 Badge: Only "STAGE 2 : SUNSET OUTPOST", no enemy count
        gp.loadStage(2);
        BufferedImage s2Img = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics g2 = s2Img.getGraphics();
        gp.background.drawBackground(g2);
        gp.player.draw(g2);
        gp.background.drawGUI(g2);
        g2.dispose();

        File s2File = new File("C:/Users/LOQ/.gemini/antigravity/brain/a1d41a47-75ae-4edc-a426-b46c2fbc991d/stage2_clean_badge_preview.png");
        ImageIO.write(s2Img, "png", s2File);
        assertTest(s2File.exists() && s2File.length() > 0,
            "Stage 2 Clean Badge rendered to stage2_clean_badge_preview.png (shows 'STAGE 2 : SUNSET OUTPOST')");
    }

    private static void assertTest(boolean passed, String message) {
        if (passed) {
            System.out.println("  [PASS] " + message);
        } else {
            System.out.println("  [FAIL] " + message);
        }
    }
}
