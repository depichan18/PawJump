package pawjump.game.entities.items;

import java.awt.Graphics2D;
import java.awt.Image;
import pawjump.game.GameManager; // To interact with game state if needed by item effect
import pawjump.game.entities.Entity;
import pawjump.game.entities.Player;

public abstract class Item extends Entity {
    protected Image image;
    protected boolean isActive; // If the item is currently on screen and collectible
    protected boolean collected;

    public Item(double x, double y, int width, int height, String imagePath) {
        super(x, y, width, height);
        if (imagePath != null && !imagePath.isEmpty()) {
            this.image = pawjump.game.utils.AssetLoader.loadImage(imagePath);
        }
        this.isActive = true; // Assumed active when created
        this.collected = false;
    }

    public void move(double speed, double speedMultiplier) {
        this.x -= (speed * speedMultiplier);
        if (this.x + this.width < 0) {
            this.isActive = false; // Mark as inactive if it moves off-screen
        }
    }
    
    @Override
    public void update() {
        // Basic items might not need complex update logic beyond movement
        // Movement is handled by GameManager calling move()
    }

    @Override
    public void draw(Graphics2D g2d, double spriteScale) { // spriteScale typically not used for items
        if (isActive && image != null && !collected) {
            g2d.drawImage(image, (int) x, (int) y, width, height, null);
        }
    }

    public boolean isActive() {
        return isActive && !collected;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    public void setCollected(boolean collected) {
        this.collected = collected;
        if (collected) {
            this.isActive = false; // Cannot be active if collected
        }
    }

    public boolean isCollected() {
        return collected;
    }

    // Abstract method for the item's specific effect
    public abstract void applyEffect(Player player, GameManager gameManager);
}
