package background;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import main.GamePanel;


public class GUI {

    private GamePanel gp;
    private BufferedImage heartFull;
    private BufferedImage heartHalf;
    private BufferedImage heartEmpty;

    // Swing GUI Restart Button
    private JButton restartButton;
    private int btnX;
    private int btnY;
    private int btnW = 240;
    private int btnH = 46;

    // Swing GUI Next Stage Button (Appears when all enemies in Stage 1 are defeated)
    private JButton nextStageButton;
    private int nextBtnX;
    private int nextBtnY;
    private int nextBtnW = 280;
    private int nextBtnH = 50;

    public GUI(GamePanel gp) {
        this.gp = gp;
        loadHeartImages();
        initRestartButton();
        initNextStageButton();
    }

    /**
     * Initialize Swing JButton for transitioning to Stage 2 upon defeating all enemies in Stage 1
     */
    private void initNextStageButton() {
        if (gp == null) return;

        nextBtnW = 280;
        nextBtnH = 50;
        nextBtnX = (gp.screenWidth - nextBtnW) / 2;
        nextBtnY = (gp.screenHeight - 220) / 2 + 115;

        nextStageButton = new JButton("GO TO STAGE 2  ►");
        nextStageButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        nextStageButton.setForeground(Color.WHITE);
        nextStageButton.setBackground(new Color(220, 100, 15)); // Sunset amber/orange
        nextStageButton.setFocusPainted(false);
        nextStageButton.setFocusable(false); // Prevent stealing keyboard controls
        nextStageButton.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 2)); // Gold border
        nextStageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nextStageButton.setBounds(nextBtnX, nextBtnY, nextBtnW, nextBtnH);
        nextStageButton.setVisible(false);

        // Click event listener
        nextStageButton.addActionListener(e -> {
            goToStage2();
        });

        // Add to GamePanel
        gp.setLayout(null);
        gp.add(nextStageButton);
    }

    /**
     * Initialize Swing JButton for restarting the game
     */
    private void initRestartButton() {
        if (gp == null) return;

        btnW = 240;
        btnH = 46;
        btnX = (gp.screenWidth - btnW) / 2;
        btnY = (gp.screenHeight - 260) / 2 + 132;

        restartButton = new JButton("RESTART GAME");
        restartButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        restartButton.setForeground(Color.WHITE);
        restartButton.setBackground(new Color(200, 40, 40));
        restartButton.setFocusPainted(false);
        restartButton.setFocusable(false); // Prevent stealing keyboard controls
        restartButton.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 2));
        restartButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        restartButton.setBounds(btnX, btnY, btnW, btnH);
        restartButton.setVisible(false);

        // Click event listener
        restartButton.addActionListener(e -> {
            restartGame();
        });

        // Use absolute layout on GamePanel to position GUI components
        gp.setLayout(null);
        gp.add(restartButton);

        // MouseListener on GamePanel as a secondary trigger for button clicks
        gp.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (gp != null && (gp.gameOver || gp.gameWon)) {
                    int mx = e.getX();
                    int my = e.getY();
                    if (mx >= btnX && mx <= btnX + btnW && my >= btnY && my <= btnY + btnH) {
                        restartGame();
                    }
                } else if (gp != null && isStage1Cleared()) {
                    int mx = e.getX();
                    int my = e.getY();
                    if (mx >= nextBtnX && mx <= nextBtnX + nextBtnW && my >= nextBtnY && my <= nextBtnY + nextBtnH) {
                        goToStage2();
                    }
                }
            }
        });
    }

    /**
     * Trigger transition to Stage 2 upon clicking button
     */
    public void goToStage2() {
        hideNextStageButton();
        if (gp != null) {
            gp.loadStage(2);
            gp.requestFocusInWindow();
        }
    }

    /**
     * Check if Stage 1 is cleared
     */
    public boolean isStage1Cleared() {
        return gp != null && gp.isStage1Cleared();
    }

    public void showNextStageButton() {
        if (nextStageButton != null && !nextStageButton.isVisible()) {
            nextStageButton.setVisible(true);
            nextStageButton.repaint();
        }
    }

    public void hideNextStageButton() {
        if (nextStageButton != null && nextStageButton.isVisible()) {
            nextStageButton.setVisible(false);
        }
    }

    public JButton getNextStageButton() {
        return nextStageButton;
    }

    /**
     * Trigger game restart and return keyboard focus to GamePanel
     */
    private void restartGame() {
        hideRestartButton();
        hideNextStageButton();
        if (gp != null) {
            gp.restartGame();
            gp.requestFocusInWindow();
        }
    }

    public void showRestartButton() {
        if (restartButton != null && !restartButton.isVisible()) {
            restartButton.setVisible(true);
            restartButton.repaint();
        }
    }

    public void hideRestartButton() {
        if (restartButton != null && restartButton.isVisible()) {
            restartButton.setVisible(false);
        }
    }

    /**
     * Draw all GUI components: HUD (hearts), stage indicator, and overlays (Game Over & Stage Clear dialogs)
     */
    public void draw(Graphics g) {
        if (gp == null || !gp.gameStarted) {
            hideRestartButton();
            hideNextStageButton();
            return;
        }

        drawPlayerLife(g);
        drawStageBadge(g);

        if (gp.gameWon) {
            drawVictoryScreen(g);
            hideNextStageButton();
        } else if (gp.gameOver) {
            drawGameOverScreen(g);
            hideNextStageButton();
        } else {
            hideRestartButton();
            if (isStage1Cleared()) {
                drawStage1ClearedScreen(g);
                showNextStageButton();
            } else {
                hideNextStageButton();
            }
        }
    }

    /**
     * Draw current stage badge at top-center of the screen
     */
    public void drawStageBadge(Graphics g) {
        if (gp == null) return;

        String stageText;
        if (gp.currentStage == 2) {
            stageText = gp.gameWon ? "STAGE 2 CLEAR" : "STAGE 2 : SUNSET OUTPOST";
        } else {
            stageText = isStage1Cleared() ? "STAGE 1 CLEAR" : "STAGE 1 : SEWER LAB";
        }

        Font font = new Font("SansSerif", Font.BOLD, 14);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int textW = fm.stringWidth(stageText);
        int padX = 14;
        int badgeW = textW + padX * 2;
        int badgeH = 26;
        int badgeX = (gp.screenWidth - badgeW) / 2;
        int badgeY = 16;

        // Translucent background badge
        g.setColor(new Color(15, 20, 25, 190));
        g.fillRoundRect(badgeX, badgeY, badgeW, badgeH, 10, 10);
        g.setColor((gp.currentStage == 2) ? new Color(255, 160, 40) : new Color(60, 220, 140));
        g.drawRoundRect(badgeX, badgeY, badgeW, badgeH, 10, 10);

        // Stage text
        g.setColor(Color.WHITE);
        g.drawString(stageText, badgeX + padX, badgeY + 18);
    }

    /**
     * Draw health hearts on top-left of the screen
     */
    public void drawPlayerLife(Graphics g) {
        if (gp == null || gp.player == null) return;

        int startX = 20;
        int startY = 20;
        int heartSize = 32;
        int gap = 8;
        // Each heart container represents 2 half-hearts (e.g. 6 HP = 3 heart containers)
        int heartContainers = gp.player.maxLife / 2;

        for (int i = 0; i < heartContainers; i++) {
            BufferedImage img;
            if (gp.player.life >= (i + 1) * 2) {
                img = heartFull;
            } else if (gp.player.life == (i * 2) + 1) {
                img = heartHalf;
            } else {
                img = heartEmpty;
            }

            int hx = startX + i * (heartSize + gap);
            if (img != null) {
                g.drawImage(img, hx, startY, heartSize, heartSize, null);
            } else {
                // Fallback shape if heart image not loaded
                if (gp.player.life >= (i + 1) * 2) {
                    g.setColor(Color.RED);
                    g.fillRoundRect(hx, startY, heartSize, heartSize, 6, 6);
                } else if (gp.player.life == (i * 2) + 1) {
                    g.setColor(Color.RED);
                    g.fillRoundRect(hx, startY, heartSize / 2, heartSize, 6, 6);
                    g.setColor(Color.GRAY);
                    g.fillRoundRect(hx + heartSize / 2, startY, heartSize / 2, heartSize, 6, 6);
                } else {
                    g.setColor(Color.GRAY);
                    g.fillRoundRect(hx, startY, heartSize, heartSize, 6, 6);
                }
            }
        }
    }

    /**
     * Render Stage 1 Cleared victory dialog with the Go to Stage 2 Button
     */
    public void drawStage1ClearedScreen(Graphics g) {
        if (gp == null) return;

        // Show the Swing JButton
        showNextStageButton();

        // Translucent dark background overlay
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Center dialog panel dimensions
        int boxW = 560;
        int boxH = 240;
        int boxX = (gp.screenWidth - boxW) / 2;
        int boxY = (gp.screenHeight - boxH) / 2;

        // Dialog background box
        g.setColor(new Color(18, 24, 32, 245));
        g.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);

        // Gold border
        g.setColor(new Color(255, 215, 0));
        g.drawRoundRect(boxX, boxY, boxW, boxH, 20, 20);
        g.drawRoundRect(boxX + 1, boxY + 1, boxW - 2, boxH - 2, 18, 18);

        // Title: Stage 1 clear
        Font titleFont = new Font("SansSerif", Font.BOLD, 42);
        g.setFont(titleFont);
        FontMetrics fmTitle = g.getFontMetrics();
        String title = "Stage 1 clear";
        int titleX = boxX + (boxW - fmTitle.stringWidth(title)) / 2;
        int titleY = boxY + 68;

        // Shadow & Title text
        g.setColor(new Color(10, 40, 20));
        g.drawString(title, titleX + 2, titleY + 2);
        g.setColor(new Color(60, 240, 140)); // Vibrant emerald green
        g.drawString(title, titleX, titleY);

        // Button dimensions and placement
        nextBtnX = boxX + (boxW - nextBtnW) / 2;
        nextBtnY = boxY + 122;
        if (nextStageButton != null) {
            nextStageButton.setBounds(nextBtnX, nextBtnY, nextBtnW, nextBtnH);
        }

        // Draw visual button (displays underneath JButton and in offscreen screen captures)
        g.setColor(new Color(220, 100, 15)); // Sunset amber/orange
        g.fillRoundRect(nextBtnX, nextBtnY, nextBtnW, nextBtnH, 12, 12);
        g.setColor(new Color(255, 215, 0)); // Gold border
        g.drawRoundRect(nextBtnX, nextBtnY, nextBtnW, nextBtnH, 12, 12);
        g.drawRoundRect(nextBtnX + 1, nextBtnY + 1, nextBtnW - 2, nextBtnH - 2, 10, 10);

        Font btnFont = new Font("SansSerif", Font.BOLD, 18);
        g.setFont(btnFont);
        FontMetrics fmBtn = g.getFontMetrics();
        String btnText = "GO TO STAGE 2  ►";
        int btnTextX = nextBtnX + (nextBtnW - fmBtn.stringWidth(btnText)) / 2;
        int btnTextY = nextBtnY + ((nextBtnH - fmBtn.getHeight()) / 2) + fmBtn.getAscent();
        g.setColor(Color.WHITE);
        g.drawString(btnText, btnTextX, btnTextY);

       
    }

    public void drawVictoryScreen(Graphics g) {
        if (gp == null) return;

        // Configure and show the Swing JButton for playing again
        if (restartButton != null) {
            restartButton.setText("PLAY AGAIN  ►");
            restartButton.setBackground(new Color(34, 160, 64));
        }
        showRestartButton();

        // Translucent dark background overlay
        g.setColor(new Color(0, 0, 0, 190));
        g.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Center dialog panel dimensions
        int boxW = 580;
        int boxH = 260;
        int boxX = (gp.screenWidth - boxW) / 2;
        int boxY = (gp.screenHeight - boxH) / 2;

        // Dialog background box
        g.setColor(new Color(16, 26, 20, 245));
        g.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);

        // Gold border (drawn with standard Graphics)
        g.setColor(new Color(255, 215, 0));
        g.drawRoundRect(boxX, boxY, boxW, boxH, 20, 20);
        g.drawRoundRect(boxX + 1, boxY + 1, boxW - 2, boxH - 2, 18, 18);

        // Title: YOU WIN
        Font titleFont = new Font("SansSerif", Font.BOLD, 48);
        g.setFont(titleFont);
        FontMetrics fmTitle = g.getFontMetrics();
        String title = "YOU WIN";
        int titleX = boxX + (boxW - fmTitle.stringWidth(title)) / 2;
        int titleY = boxY + 65;

        // Shadow & Title text (radiant gold)
        g.setColor(new Color(10, 40, 15));
        g.drawString(title, titleX + 2, titleY + 2);
        g.setColor(new Color(255, 215, 0)); // Pure gold
        g.drawString(title, titleX, titleY);

        

        // Button dimensions and placement
        btnX = boxX + (boxW - btnW) / 2;
        btnY = boxY + 132;
        if (restartButton != null) {
            restartButton.setBounds(btnX, btnY, btnW, btnH);
        }

        // Draw visual button (displays underneath JButton and in offscreen screen captures)
        g.setColor(new Color(34, 160, 64)); // Emerald green
        g.fillRoundRect(btnX, btnY, btnW, btnH, 12, 12);
        g.setColor(new Color(255, 215, 0)); // Gold border
        g.drawRoundRect(btnX, btnY, btnW, btnH, 12, 12);
        g.drawRoundRect(btnX + 1, btnY + 1, btnW - 2, btnH - 2, 10, 10);

        Font btnFont = new Font("SansSerif", Font.BOLD, 18);
        g.setFont(btnFont);
        FontMetrics fmBtn = g.getFontMetrics();
        String btnText = "PLAY AGAIN";
        int btnTextX = btnX + (btnW - fmBtn.stringWidth(btnText)) / 2;
        int btnTextY = btnY + ((btnH - fmBtn.getHeight()) / 2) + fmBtn.getAscent();
        g.setColor(Color.WHITE);
        g.drawString(btnText, btnTextX, btnTextY);

        
    }

    
    public void drawGameOverScreen(Graphics g) {
        if (gp == null) return;

        // Configure and show the Swing JButton for restarting
        if (restartButton != null) {
            restartButton.setText("RESTART GAME");
            restartButton.setBackground(new Color(200, 40, 40));
        }
        showRestartButton();

        // Translucent dark background overlay
        g.setColor(new Color(0, 0, 0, 190));
        g.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Center dialog panel dimensions
        int boxW = 560;
        int boxH = 260;
        int boxX = (gp.screenWidth - boxW) / 2;
        int boxY = (gp.screenHeight - boxH) / 2;

        // Dialog background box
        g.setColor(new Color(20, 24, 28, 245));
        g.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);

        // Dialog border (drawn with standard Graphics)
        g.setColor(new Color(220, 50, 50));
        g.drawRoundRect(boxX, boxY, boxW, boxH, 20, 20);
        g.drawRoundRect(boxX + 1, boxY + 1, boxW - 2, boxH - 2, 18, 18);

        // Title: YOU LOSE (in Stage 2) or GAME OVER
        Font titleFont = new Font("SansSerif", Font.BOLD, 46);
        g.setFont(titleFont);
        FontMetrics fmTitle = g.getFontMetrics();
        String title = (gp.currentStage == 2) ? "YOU LOSE" : "GAME OVER";
        int titleX = boxX + (boxW - fmTitle.stringWidth(title)) / 2;
        int titleY = boxY + 65;

        // Shadow & Title text
        g.setColor(new Color(40, 0, 0));
        g.drawString(title, titleX + 2, titleY + 2);
        g.setColor(new Color(255, 60, 60));
        g.drawString(title, titleX, titleY);

        

        // Button dimensions and placement
        btnX = boxX + (boxW - btnW) / 2;
        btnY = boxY + 132;
        if (restartButton != null) {
            restartButton.setBounds(btnX, btnY, btnW, btnH);
        }

        // Draw visual button (displays underneath JButton and in offscreen screen captures)
        g.setColor(new Color(200, 40, 40));
        g.fillRoundRect(btnX, btnY, btnW, btnH, 12, 12);
        g.setColor(new Color(255, 215, 0)); // Gold border
        g.drawRoundRect(btnX, btnY, btnW, btnH, 12, 12);
        g.drawRoundRect(btnX + 1, btnY + 1, btnW - 2, btnH - 2, 10, 10);

        Font btnFont = new Font("SansSerif", Font.BOLD, 18);
        g.setFont(btnFont);
        FontMetrics fmBtn = g.getFontMetrics();
        String btnText = "RESTART GAME";
        int btnTextX = btnX + (btnW - fmBtn.stringWidth(btnText)) / 2;
        int btnTextY = btnY + ((btnH - fmBtn.getHeight()) / 2) + fmBtn.getAscent();
        g.setColor(Color.WHITE);
        g.drawString(btnText, btnTextX, btnTextY);

        
    }

    /**
     * Load health heart sprites
     */
    private void loadHeartImages() {
        try {
            BufferedImage fullRaw = loadImage("res/heart/heart_full.png");
            BufferedImage halfRaw = loadImage("res/heart/heart_half.png");
            BufferedImage emptyRaw = loadImage("res/heart/heart_empty.png");

            // Crop the actual heart graphic (13x13) to eliminate empty transparent margins
            if (fullRaw != null && fullRaw.getWidth() >= 22 && fullRaw.getHeight() >= 20) {
                heartFull = fullRaw.getSubimage(9, 7, 13, 13);
            } else {
                heartFull = fullRaw;
            }

            if (halfRaw != null && halfRaw.getWidth() >= 22 && halfRaw.getHeight() >= 20) {
                heartHalf = halfRaw.getSubimage(9, 7, 13, 13);
            } else {
                heartHalf = halfRaw;
            }

            if (emptyRaw != null && emptyRaw.getWidth() >= 22 && emptyRaw.getHeight() >= 20) {
                heartEmpty = emptyRaw.getSubimage(9, 7, 13, 13);
            } else {
                heartEmpty = emptyRaw;
            }
        } catch (Exception e) {
            System.out.println("Warning: Cannot load heart images in GUI");
        }
    }

    private BufferedImage loadImage(String path) {
        try {
            InputStream stream = getClass().getResourceAsStream("/" + path.replaceFirst("^res/", ""));
            if (stream != null) return ImageIO.read(stream);

            File[] candidates = {
                new File(path),
                new File("shootdown/" + path),
                new File("../../" + path)
            };
            for (File f : candidates) {
                if (f.exists()) return ImageIO.read(f);
            }
        } catch (Exception e) {}
        return null;
    }

    public JButton getRestartButton() {
        return restartButton;
    }

    public BufferedImage getHeartFull() {
        return heartFull;
    }

    public BufferedImage getHeartEmpty() {
        return heartEmpty;
    }
}
