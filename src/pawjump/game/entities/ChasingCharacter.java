package pawjump.game.entities;

import java.awt.Graphics2D;
import java.awt.Image;
import pawjump.game.animation.SpriteAnimator;
import pawjump.game.utils.Constants;

public class ChasingCharacter extends Entity {
    private SpriteAnimator runAnimator;
    private double velocityX;
    private double targetX; // Target position to maintain behind player
    private boolean isAppearing;
    private boolean isDisappearing;
    private boolean isActive;
    private int distanceTraveled; // Distance traveled since appearing (in meters)
    private double startScore; // Score when chasing started
    
    public ChasingCharacter() {
        super(-Constants.CHASING_CHARACTER_WIDTH, Constants.GROUND_Y - Constants.CHASING_CHARACTER_HEIGHT, 
              Constants.CHASING_CHARACTER_WIDTH, Constants.CHASING_CHARACTER_HEIGHT);
        
        initAnimations();
        this.velocityX = 0;
        this.isAppearing = false;
        this.isDisappearing = false;
        this.isActive = false;
        this.distanceTraveled = 0;
        this.startScore = 0;
    }
    
    private void initAnimations() {
        // Load and slice the sprite sheet (6 frames)
        Image chasingSheet = pawjump.game.utils.AssetLoader.loadImage(Constants.CHASING_CHARACTER_SHEET);
        int frameWidth = chasingSheet.getWidth(null) / 6;
        int frameHeight = chasingSheet.getHeight(null);
        
        runAnimator = new SpriteAnimator(Constants.CHASING_CHARACTER_SHEET, 6,
                                        frameWidth, frameHeight,
                                        Constants.CHASING_ANIM_DELAY, true);
    }
    
    public void startChasing(Player player, int currentScore) {
        this.isActive = true;
        this.isAppearing = true;
        this.isDisappearing = false;
        this.x = -width; // Start off-screen to the left
        this.y = Constants.GROUND_Y - height;
        this.velocityX = 0;
        this.distanceTraveled = 0;
        this.startScore = currentScore;
        
        // Calculate target position (70 pixels behind player)
        this.targetX = player.getX() - Constants.CHASING_TARGET_DISTANCE;
        
        runAnimator.play();
    }
    
    public void stopChasing() {
        this.isDisappearing = true;
        this.isAppearing = false;
    }
    
    @Override
    public void update() {
        if (!isActive) return;
        
        runAnimator.update();
        
        if (isAppearing) {
            // Accelerate towards target position
            double distanceToTarget = targetX - x;
            if (Math.abs(distanceToTarget) > 5) {
                velocityX += Constants.CHASING_ACCELERATION;
                if (velocityX > Math.abs(distanceToTarget) * 0.1) {
                    velocityX = Math.abs(distanceToTarget) * 0.1;
                }
            } else {
                // Reached target position, now match player speed
                isAppearing = false;
                velocityX = 0; // Will be set by GameManager to match game speed
            }
        } else if (isDisappearing) {
            // Decelerate and move left off-screen
            velocityX -= Constants.CHASING_DECELERATION;
            if (velocityX < -15) {
                velocityX = -15; // Cap deceleration speed
            }
            
            // Check if completely off-screen
            if (x + width < 0) {
                isActive = false;
                isDisappearing = false;
            }
        }
        
        // Update position
        x += velocityX;
    }
    
    public void updateChasing(Player player, double gameSpeed, int currentScore) {
        if (!isActive || isDisappearing) return;
        
        // Update distance traveled
        distanceTraveled = currentScore - (int)startScore;
        
        // Check if should start disappearing
        if (distanceTraveled >= Constants.CHASING_DISTANCE_METERS) {
            stopChasing();
            return;
        }
        
        if (!isAppearing) {
            // Update target position and match game speed
            targetX = player.getX() - Constants.CHASING_TARGET_DISTANCE;
            
            // Move with game speed but adjust to maintain distance
            velocityX = -gameSpeed; // Base game speed
            
            // Adjust speed to maintain target distance
            double currentDistance = player.getX() - x;
            double targetDistance = Constants.CHASING_TARGET_DISTANCE;
            double distanceDiff = currentDistance - targetDistance;
            
            // Fine-tune velocity to maintain proper distance
            velocityX += distanceDiff * 0.1;
        }
    }
    
    @Override
    public void draw(Graphics2D g2d, double spriteScale) {
        if (!isActive) return;
        
        // Calculate sprite dimensions with scaling
        double currentSpriteScale = 3.0; // Similar to player scale
        int spriteW = (int) (width * currentSpriteScale);
        int spriteH = (int) (height * currentSpriteScale);
        int spriteX = (int) (x - (spriteW - width) / 2.0);
        int spriteY = (int) (y + height - spriteH); // Align bottom of sprite with bottom of hitbox
        
        Image frameToDraw = null;
        if (runAnimator != null) {
            frameToDraw = runAnimator.getCurrentFrame();
        }
        
        if (frameToDraw != null) {
            g2d.drawImage(frameToDraw, spriteX, spriteY, spriteW, spriteH, null);
        } else {
            // Fallback rectangle
            g2d.setColor(java.awt.Color.RED);
            g2d.fillRect((int) x, (int) y, width, height);
        }    }
    
    public void reset() {
        this.isActive = false;
        this.isAppearing = false;
        this.isDisappearing = false;
        this.x = -width; // Reset to off-screen position
        this.y = Constants.GROUND_Y - height;
        this.velocityX = 0;
        this.distanceTraveled = 0;
        this.startScore = 0;
        this.targetX = 0;
        if (runAnimator != null) {
            runAnimator.reset();
        }
    }
    
    // Getters
    public boolean isActive() { return isActive; }
    public boolean isAppearing() { return isAppearing; }
    public boolean isDisappearing() { return isDisappearing; }
    public int getDistanceTraveled() { return distanceTraveled; }
}
