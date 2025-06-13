package pawjump.game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import pawjump.game.entities.Obstacle;
import pawjump.game.ui.GameOverScreen;
import pawjump.game.ui.HUD; // For getWindowAncestor
import pawjump.game.utils.Constants; // For casting window ancestor

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private GameManager gameManager;
    private HUD hud;
    private GameOverScreen gameOverScreen;

    private Image dayBgImage;
    private Image nightBgImage;
    private Image platformImage;
    private double bgX; // Background X position for scrolling
    private double platformX; // Platform X position for scrolling

    public GamePanel() {
        setPreferredSize(new java.awt.Dimension(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        this.gameManager = new GameManager(this); // Pass reference to itself
        this.hud = new HUD();
        this.gameOverScreen = new GameOverScreen(gameManager); // Pass GameManager to GameOverScreen

        loadResources();
        resetBackgroundAndPlatform();

        addKeyListener(this);
        timer = new Timer(Constants.GAME_TICK_MS, this);
        timer.start();
    }

    private void loadResources() {
        dayBgImage = pawjump.game.utils.AssetLoader.loadImage(Constants.DAY_BG_IMG);
        nightBgImage = pawjump.game.utils.AssetLoader.loadImage(Constants.NIGHT_BG_IMG);
        platformImage = pawjump.game.utils.AssetLoader.loadImage(Constants.PLATFORM_IMG);
    }
    
    public void resetBackgroundAndPlatform() {
        this.bgX = 0;
        this.platformX = 0;
    }

    public void requestFocusForGame() {
        requestFocusInWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameManager.isRunning()) {
            gameManager.update();
        } else if (gameManager.isGameOver()) {
            // If player death animation is handled internally and GameManager signals when it's truly over for dialog
            if (gameManager.getPlayer().isDeathAnimationDone() && !gameOverScreen.isVisible()) {
                 timer.stop(); // Stop game updates before showing dialog
                 JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                 gameOverScreen.show(parentFrame);
                 // Timer will be restarted by GameManager if "retry" is chosen
            } else if (gameManager.getPlayer().isDying()) {
                gameManager.getPlayer().update(); // Keep updating player for death animation
            }
        }
        repaint();
    }
    
    public void startGameTimer() {
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    // Add method to restart the timer after game restart
    public void restartGameTimer() {
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Draw Background
        Image currentBg = gameManager.isDay() ? dayBgImage : nightBgImage;
        double bgSpeed = Constants.BASE_SPEED * gameManager.getSpeedMultiplier() * 0.025; // Parallax speed
        bgX -= bgSpeed;
        if (bgX <= -Constants.SCREEN_WIDTH) {
            bgX += Constants.SCREEN_WIDTH;
        }
        g2d.drawImage(currentBg, (int) bgX, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, null);
        if (bgX < 0) { // Draw second image for seamless loop
            g2d.drawImage(currentBg, (int) bgX + Constants.SCREEN_WIDTH, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, null);
        }


        // Draw Items (before platform, if they can be behind it, or after player if always in front)
        for (pawjump.game.entities.items.Item item : gameManager.getItems()) {
            item.draw(g2d, 1.0); // Items don't typically scale with player effects
        }

        // Draw Platform (Ground)
        double platSpeed = Constants.BASE_SPEED * gameManager.getSpeedMultiplier();
        platformX -= platSpeed; // Platform moves left
        int platformImgW = platformImage.getWidth(null);
        int platformImgH = platformImage.getHeight(null);
        if (platformImgW > 0 && platformImgH > 0) {
            if (platformX <= -platformImgW) {
                platformX += platformImgW;
            }
            // Draw so the BOTTOM of the platform image aligns with GROUND_Y, but shift image 120px lower
            int platformDrawY = Constants.GROUND_Y - platformImgH + 120;
            for (int px = (int) platformX; px < Constants.SCREEN_WIDTH; px += platformImgW) {
                g2d.drawImage(platformImage, px, platformDrawY, platformImgW, platformImgH, null);
            }
            if (platformX > 0) {
                g2d.drawImage(platformImage, (int) platformX - platformImgW, platformDrawY, platformImgW, platformImgH, null);
            }
        } else { // Fallback solid ground
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(0, Constants.GROUND_Y, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT - Constants.GROUND_Y);
        }


        // Draw Player
        if (gameManager.getPlayer() != null) {
            gameManager.getPlayer().draw(g2d, 1.0); // Player class handles its own scaling
        }

        // Draw Chasing Character (behind obstacles)
        if (gameManager.getChasingCharacter() != null && gameManager.getChasingCharacter().isActive()) {
            gameManager.getChasingCharacter().draw(g2d, 1.0);
        }

        // Draw Obstacles
        for (Obstacle obs : gameManager.getObstacles()) {
            obs.draw(g2d, 1.0);
        }

        // Draw supplement effect overlay (dark surroundings with spotlight)
        drawSuplemenEffect(g2d);

        // Draw HUD
        if (hud != null) {
            hud.draw(g2d, gameManager, gameManager.getPlayer());
        }

        g2d.dispose(); // Dispose of graphics context copy if one was made internally. Usually not needed for `g` from paintComponent.
    }

    private void drawSuplemenEffect(Graphics2D g2d) {
        if (gameManager.getPlayer() != null && gameManager.getPlayer().isSuplemenEffectActive() && !gameManager.getPlayer().isSuplemenEffectUsed()) {
            // Save original composite and clip
            java.awt.Composite oldComposite = g2d.getComposite();
            java.awt.Shape oldClip = g2d.getClip();
            
            // Get player center position
            int playerCenterX = (int) (gameManager.getPlayer().getX() + gameManager.getPlayer().getWidth() / 2);
            int playerCenterY = (int) (gameManager.getPlayer().getY() + gameManager.getPlayer().getHeight() / 2);
            
            // Create a radial gradient for smooth spotlight effect
            java.awt.RadialGradientPaint gradient = new java.awt.RadialGradientPaint(
                playerCenterX, playerCenterY, Constants.SUPLEMEN_SPOTLIGHT_RADIUS,
                new float[]{0.0f, 0.7f, 1.0f},
                new java.awt.Color[]{
                    new java.awt.Color(0, 0, 0, 0),           // Fully transparent at center
                    new java.awt.Color(0, 0, 0, 0),           // Still transparent at 70%
                    new java.awt.Color(0, 0, 0, (int)(255 * Constants.SUPLEMEN_OVERLAY_ALPHA))  // Dark at edges
                }
            );
            
            // Fill the entire screen with the gradient
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
            
            // Also add a general dark overlay outside the gradient radius for consistent darkness
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Constants.SUPLEMEN_OVERLAY_ALPHA));
            g2d.setColor(new java.awt.Color(0, 0, 0));
            
            // Create a circular area to exclude from the dark overlay
            java.awt.geom.Area fullScreen = new java.awt.geom.Area(new java.awt.Rectangle(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
            java.awt.geom.Area spotlight = new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(
                playerCenterX - Constants.SUPLEMEN_SPOTLIGHT_RADIUS,
                playerCenterY - Constants.SUPLEMEN_SPOTLIGHT_RADIUS,
                Constants.SUPLEMEN_SPOTLIGHT_RADIUS * 2,
                Constants.SUPLEMEN_SPOTLIGHT_RADIUS * 2
            ));
            fullScreen.subtract(spotlight);
            
            // Fill everything except the spotlight area
            g2d.fill(fullScreen);
            
            // Restore original composite and clip
            g2d.setComposite(oldComposite);
            g2d.setClip(oldClip);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == Constants.JUMP_KEY && gameManager.isRunning()) {
            gameManager.getPlayer().jump();
        } else if (e.getKeyCode() == Constants.FALL_KEY && gameManager.isRunning()) {
            gameManager.getPlayer().fall();
        }
        // For debugging or development:
        // if (e.getKeyCode() == KeyEvent.VK_R && (gameManager.isGameOver() || !gameManager.isRunning())) {
        //     gameManager.restartGame();
        //     startGameTimer(); // Ensure timer is running
        // }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}
