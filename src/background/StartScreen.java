package background;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import main.GamePanel;

/**
 * StartScreen class handles the title and start screen of the game.
 * Stored in package background (demonstrating OOP Composition with Background).
 * 
 * Features:
 * - Solid black background (Color.BLACK)
 * - Title and game description
 * - Swing JButton to start the game
 * - Uses standard java.awt.Graphics (NO Graphics2D).
 */
public class StartScreen {

    private GamePanel gp;
    private JButton startButton;

    private int btnX;
    private int btnY;
    private int btnW = 260;
    private int btnH = 54;

    public StartScreen(GamePanel gp) {
        this.gp = gp;
        initStartButton();
    }

    /**
     * Initialize Swing JButton for starting the game
     */
    private void initStartButton() {
        if (gp == null) return;

        btnW = 260;
        btnH = 54;
        btnX = (gp.screenWidth - btnW) / 2;
        btnY = (gp.screenHeight / 2) + 15;

        startButton = new JButton("START GAME");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 22));
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(new Color(34, 139, 34)); // Forest green
        startButton.setFocusPainted(false);
        startButton.setFocusable(false); // Do not steal keyboard controls
        startButton.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 2)); // Gold border
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.setBounds(btnX, btnY, btnW, btnH);
        startButton.setVisible(true);

        // Click event listener
        startButton.addActionListener(e -> {
            startGame();
        });

        // Add button to GamePanel with absolute positioning
        gp.setLayout(null);
        gp.add(startButton);

        // MouseListener on GamePanel as a secondary trigger for button clicks
        gp.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (gp != null && !gp.gameStarted) {
                    int mx = e.getX();
                    int my = e.getY();
                    if (mx >= btnX && mx <= btnX + btnW && my >= btnY && my <= btnY + btnH) {
                        startGame();
                    }
                }
            }
        });
    }

    /**
     * Start the game: transitions state, hides start button, and returns focus to GamePanel
     */
    public void startGame() {
        hideStartButton();
        if (gp != null) {
            gp.startGame();
            gp.requestFocusInWindow();
        }
    }

    public void showStartButton() {
        if (startButton != null && !startButton.isVisible()) {
            startButton.setVisible(true);
            startButton.repaint();
        }
    }

    public void hideStartButton() {
        if (startButton != null && startButton.isVisible()) {
            startButton.setVisible(false);
        }
    }

    /**
     * Render the Start Screen with solid black background, title, and start button.
     * Uses standard java.awt.Graphics (NO Graphics2D).
     */
    public void draw(Graphics g) {
        if (gp == null) return;

        showStartButton();

        // 1. Background สีดำ (Solid black background)
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // 2. Title: SHOOTDOWN
        Font titleFont = new Font("SansSerif", Font.BOLD, 64);
        g.setFont(titleFont);
        FontMetrics fmTitle = g.getFontMetrics();
        String title = "SHOOTDOWN";
        int titleX = (gp.screenWidth - fmTitle.stringWidth(title)) / 2;
        int titleY = (gp.screenHeight / 2) - 130;

        // Title drop shadow & vibrant text
        g.setColor(new Color(180, 0, 0));
        g.drawString(title, titleX + 4, titleY + 4);
        g.setColor(new Color(255, 60, 60));
        g.drawString(title, titleX, titleY);

        

        //  Fallback visual Start Button (renders in offscreen captures & underneath JButton)
        btnX = (gp.screenWidth - btnW) / 2;
        btnY = (gp.screenHeight / 2) + 15;
        if (startButton != null) {
            startButton.setBounds(btnX, btnY, btnW, btnH);
        }

        g.setColor(new Color(34, 139, 34)); // Forest green
        g.fillRoundRect(btnX, btnY, btnW, btnH, 14, 14);
        g.setColor(new Color(255, 215, 0)); // Gold border
        g.drawRoundRect(btnX, btnY, btnW, btnH, 14, 14);
        g.drawRoundRect(btnX + 1, btnY + 1, btnW - 2, btnH - 2, 12, 12);

        Font btnFont = new Font("SansSerif", Font.BOLD, 22);
        g.setFont(btnFont);
        FontMetrics fmBtn = g.getFontMetrics();
        String btnText = "START GAME";
        int btnTextX = btnX + (btnW - fmBtn.stringWidth(btnText)) / 2;
        int btnTextY = btnY + ((btnH - fmBtn.getHeight()) / 2) + fmBtn.getAscent();
        g.setColor(Color.WHITE);
        g.drawString(btnText, btnTextX, btnTextY);  
    }

    public JButton getStartButton() {
        return startButton;
    }
}

