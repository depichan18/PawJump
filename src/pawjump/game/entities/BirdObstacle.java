package pawjump.game.entities;

import pawjump.game.animation.SpriteAnimator;
import pawjump.game.utils.Constants;

public class BirdObstacle extends Obstacle {
    public BirdObstacle(double x, double y) {
        super(x, y, Constants.OBSTACLE_WIDTH, Constants.OBSTACLE_HEIGHT, null); // No single image path
        // Bird specific animation - use AssetLoader for consistent image loading
        java.awt.Image birdSheet = pawjump.game.utils.AssetLoader.loadImage(Constants.OBSTACLE_BIRD_SHEET);
        int frameWidth = birdSheet.getWidth(null) / 6;
        int frameHeight = birdSheet.getHeight(null);
        
        this.animator = new SpriteAnimator(Constants.OBSTACLE_BIRD_SHEET, 6,
                                           frameWidth, frameHeight,
                                           Constants.BIRD_ANIM_DELAY, true);
        this.animator.play();
    }
    
    @Override
    public void updateSpecific() {
        if (this.animator != null) {
            this.animator.update();
        }
        // Floating obstacles don't move vertically on their own in the original code,
        // their Y is set at spawn time.
    }
    // Drawing is handled by Obstacle.draw with a check for BirdObstacle instance for scaling
}
