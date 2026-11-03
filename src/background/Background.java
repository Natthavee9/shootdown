package background;

import java.awt.Graphics;
import main.GamePanel;

/**
 * Background class acts as the composite presentation manager in package background.
 * Demonstrates OOP Composition:
 * - Background HAS-A Graphic (environment/map rendering)
 * - Background HAS-A GUI (HUD hearts & Game Over screen overlay)
 * - Background HAS-A StartScreen (Title screen with black background & start button)
 */
public class Background {

    private GamePanel gp;

    // OOP COMPOSITION: Background HAS-A Graphic
    private Graphic graphic;

    // OOP COMPOSITION: Background HAS-A GUI
    private GUI gui;

    // OOP COMPOSITION: Background HAS-A StartScreen
    private StartScreen startScreen;

    public Background(GamePanel gp) {
        this.gp = gp;
        // Instantiating composed objects (Composition lifecycle management)
        this.gui = new GUI(gp);
        this.graphic = new Graphic(gp, this.gui);
        this.startScreen = new StartScreen(gp);
    }

    /**
     * Render the Start Screen (Title & Start Game Button with black background)
     */
    public void drawStartScreen(Graphics g) {
        if (startScreen != null) {
            startScreen.draw(g);
        }
    }

    /**
     * Switch background stage graphics (Stage 1 = Sewer Lab, Stage 2 = Sunset Outpost)
     */
    public void loadStage(int stage) {
        if (graphic != null) {
            graphic.loadStage(stage);
        }
    }

    /**
     * Render the background environment graphics
     */
    public void drawBackground(Graphics g) {
        if (graphic != null) {
            graphic.draw(g);
        }
    }

    /**
     * Render the GUI overlay elements (player health hearts & game over screen)
     */
    public void drawGUI(Graphics g) {
        if (gui != null) {
            gui.draw(g);
        }
    }

    // --- COMPOSITION GETTERS & SETTERS ---

    public Graphic getGraphic() {
        return graphic;
    }

    public void setGraphic(Graphic graphic) {
        this.graphic = graphic;
    }

    public GUI getGui() {
        return gui;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public javax.swing.JButton getRestartButton() {
        return (gui != null) ? gui.getRestartButton() : null;
    }

    public javax.swing.JButton getNextStageButton() {
        return (gui != null) ? gui.getNextStageButton() : null;
    }

    public StartScreen getStartScreen() {
        return startScreen;
    }

    public void setStartScreen(StartScreen startScreen) {
        this.startScreen = startScreen;
    }

    public javax.swing.JButton getStartButton() {
        return (startScreen != null) ? startScreen.getStartButton() : null;
    }
}

