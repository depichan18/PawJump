package pawjump.game.entities;

import java.awt.Graphics2D;
import java.awt.Image;

public abstract class Obstacle extends Entity {
    protected Image image; // For simple, non-animated obstacles
    protected boolean isOffScreen = false;

    public Obstacle(double x, double y, int width, int height, String imagePath) {
        super(x, y, width, height);
        if (imagePath != null && !imagePath.isEmpty()) {
            this.image = pawjump.game.utils.AssetLoader.loadImage(imagePath);
        }
    }

    public void move(double speed, double speedMultiplier) {
        this.x -= (speed * speedMultiplier);
        if (this.x + this.width < 0) {
            isOffScreen = true;
        }
    }

    @Override
    public void update() {
        // Basic obstacles might not need complex update logic beyond movement
        // Movement is handled by GameManager or GamePanel calling move()
    }
    
    // Specific update for moving obstacles etc.
    public abstract void updateSpecific();


    @Override
    public void draw(Graphics2D g2d, double spriteScale) { // spriteScale might not be used for obstacles
        if (animator != null) {
            animator.update(); // Update animator if entity has one
            Image frame = animator.getCurrentFrame();
            int drawW = width, drawH = height;
            int drawX = (int)x, drawY = (int)y;

            if (this instanceof BirdObstacle) { // Example of type-specific drawing
                drawW = (int)(width * 1.5);
                drawH = (int)(height * 1.5);
                drawX = (int)x + (width - drawW) / 2;
                drawY = (int)y + (height - drawH) / 2;
            }
            g2d.drawImage(frame, drawX, drawY, drawW, drawH, null);
        } else if (image != null) {
            // Always draw at (x, y) where y = GROUND_Y - height for ground obstacles
            g2d.drawImage(image, (int) x, (int) y, width, height, null);
        }
        // else, draw bounds for debugging?
        // g2d.setColor(java.awt.Color.RED);
        // g2d.drawRect((int)x, (int)y, width, height);
    }
    
    public boolean isOffScreen() {
        return isOffScreen;
    }
}
