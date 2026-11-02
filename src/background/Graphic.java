package background;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import main.GamePanel;

/**
 * Graphic class handles the background environment rendering.
 * Demonstrates OOP Composition by composing a GUI instance:
 * Graphic HAS-A GUI
 */
public class Graphic {

    private GamePanel gp;
    private BufferedImage bgImage;
    private String imagePath = "res/background/map_sewer_lab.png";

    // OOP COMPOSITION: Graphic HAS-A GUI
    private GUI gui;

    public Graphic(GamePanel gp) {
        this.gp = gp;
        this.gui = new GUI(gp); // Instantiate composed GUI component
        loadBackgroundImage();
    }

    public Graphic(GamePanel gp, GUI gui) {
        this.gp = gp;
        this.gui = gui; // Use injected composed GUI component
        loadBackgroundImage();
    }

    /**
     * Switch background stage asset (Stage 1 = Sewer Lab, Stage 2 = Sunset Outpost)
     */
    public void loadStage(int stage) {
        if (stage == 2) {
            imagePath = "res/background/map_sunset.png";
        } else {
            imagePath = "res/background/map_sewer_lab.png";
        }
        loadBackgroundImage();
    }

    /**
     * Load background map image with robust multi-path fallbacks
     */
    public void loadBackgroundImage() {
        try {
            String resPath = "/" + imagePath.replaceFirst("^res/", "");
            InputStream stream = getClass().getResourceAsStream(resPath);
            if (stream != null) {
                bgImage = ImageIO.read(stream);
                return;
            }

            File[] candidates = {
                new File(imagePath),
                new File("shootdown/" + imagePath),
                new File("../../" + imagePath)
            };
            for (File f : candidates) {
                if (f.exists()) {
                    bgImage = ImageIO.read(f);
                    return;
                }
            }
            System.out.println("Warning: Cannot find background image at: " + imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Render the background graphics onto the screen
     */
    public void draw(Graphics g) {
        if (gp == null) return;

        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, gp.screenWidth, gp.screenHeight, null);
        } else {
            // Fallback dark background color
            g.setColor(new Color(25, 25, 30));
            g.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }
    }

    /**
     * Delegate GUI rendering through the composed GUI object
     */
    public void drawGUI(Graphics g) {
        if (gui != null) {
            gui.draw(g);
        }
    }

    // --- COMPOSITION GETTERS & SETTERS ---

    public GUI getGui() {
        return gui;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public BufferedImage getBgImage() {
        return bgImage;
    }

    public void setBgImage(BufferedImage bgImage) {
        this.bgImage = bgImage;
    }
}

