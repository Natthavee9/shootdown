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

/**
 * GUI class handles all graphical user interface rendering,
 * including player health hearts (HUD), Game Over dialog,
 * and the Restart Button.
 * 
 * Uses standard java.awt.Graphics (NO Graphics2D).
 */
public class GUI {

    private GamePanel gp;
    private BufferedImage heartFull;
    private BufferedImage heartEmpty;

    // Swing GUI Restart Button
    private JButton restartButton;
    private int btnX;
    private int btnY;
    private int btnW = 240;
    private int btnH = 46;

    public GUI(GamePanel gp) {
        this.gp = gp;
        loadHeartImages();
        initRestartButton();
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
                if (gp != null && gp.gameOver) {
                    int mx = e.getX();
                    int my = e.getY();
                    if (mx >= btnX && mx <= btnX + btnW && my >= btnY && my <= btnY + btnH) {
                        restartGame();
                    }
                }
            }
        });
    }

    /**
     * Trigger game restart and return keyboard focus to GamePanel
     */
    private void restartGame() {
        hideRestartButton();
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
     * Draw all GUI components: HUD (hearts), stage indicator, and overlays (Game Over dialog)
     */
    public void draw(Graphics g) {
        drawPlayerLife(g);
        drawStageBadge(g);

        if (gp != null && gp.gameOver) {
            drawGameOverScreen(g);
        } else {
            hideRestartButton();
        }
    }

    /**
     * Draw current stage badge at top-center of the screen
     */
    public void drawStageBadge(Graphics g) {
        if (gp == null) return;

        String stageText = (gp.currentStage == 2) ? "STAGE 2 : SUNSET OUTPOST" : "STAGE 1 : SEWER LAB";
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

        for (int i = 0; i < gp.player.maxLife; i++) {
            BufferedImage img = (i < gp.player.life) ? heartFull : heartEmpty;
            if (img != null) {
                g.drawImage(img, startX + i * (heartSize + gap), startY, heartSize, heartSize, null);
            } else {
                // Fallback shape if heart image not loaded
                g.setColor((i < gp.player.life) ? Color.RED : Color.GRAY);
                g.fillRoundRect(startX + i * (heartSize + gap), startY, heartSize, heartSize, 6, 6);
            }
        }
    }

    /**
     * Draw Game Over dialog box and restart prompt.
     * Uses standard java.awt.Graphics (NO Graphics2D).
     */
    public void drawGameOverScreen(Graphics g) {
        if (gp == null) return;

        // Show the Swing JButton
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

        // Title: GAME OVER
        Font titleFont = new Font("SansSerif", Font.BOLD, 46);
        g.setFont(titleFont);
        FontMetrics fmTitle = g.getFontMetrics();
        String title = "GAME OVER";
        int titleX = boxX + (boxW - fmTitle.stringWidth(title)) / 2;
        int titleY = boxY + 65;

        // Shadow & Title text
        g.setColor(new Color(40, 0, 0));
        g.drawString(title, titleX + 2, titleY + 2);
        g.setColor(new Color(255, 60, 60));
        g.drawString(title, titleX, titleY);

        // Reason / Subtitle
        Font subFont = new Font("SansSerif", Font.PLAIN, 18);
        g.setFont(subFont);
        FontMetrics fmSub = g.getFontMetrics();
        String sub = (gp.currentStage == 2) ? "MISSION FAILED IN SUNSET OUTPOST!" : "YOU FELL INTO TOXIC ACID!";
        int subX = boxX + (boxW - fmSub.stringWidth(sub)) / 2;
        g.setColor(new Color(210, 210, 210));
        g.drawString(sub, subX, boxY + 108);

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

        // Secondary prompt: Button-only restart instruction
        Font promptFont = new Font("SansSerif", Font.PLAIN, 14);
        g.setFont(promptFont);
        FontMetrics fmPrompt = g.getFontMetrics();
        String prompt = "CLICK BUTTON ABOVE TO RESTART";
        int promptX = boxX + (boxW - fmPrompt.stringWidth(prompt)) / 2;
        g.setColor(new Color(175, 175, 175));
        g.drawString(prompt, promptX, boxY + 225);
    }

    /**
     * Load health heart sprites
     */
    private void loadHeartImages() {
        try {
            BufferedImage fullRaw = loadImage("res/heart/heart_full.png");
            BufferedImage emptyRaw = loadImage("res/heart/heart_empty.png");

            // Crop the actual heart graphic (13x13) to eliminate empty transparent margins
            if (fullRaw != null && fullRaw.getWidth() >= 22 && fullRaw.getHeight() >= 20) {
                heartFull = fullRaw.getSubimage(9, 7, 13, 13);
            } else {
                heartFull = fullRaw;
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
