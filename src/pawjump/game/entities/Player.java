package pawjump.game.entities;
import java.awt.AlphaComposite; // <--- ADD THIS LINE
import java.awt.Graphics2D;
import java.awt.Image;
import pawjump.game.animation.SpriteAnimator;
import pawjump.game.utils.Constants;

public class Player extends Entity {
    private int health;
    private int maxHealth;
    private double velocityY;
    private boolean jumping;

    private SpriteAnimator walkAnimator;
    private SpriteAnimator hurtAnimator;
    private SpriteAnimator attackAnimator;
    private SpriteAnimator deathAnimator;
    private SpriteAnimator currentAnimator;

    private boolean isHurting;
    private int hurtTicks;
    private boolean isAttacking;
    private int attackTicks;
    private boolean isDying;
    private int deathTicks;
    private boolean deathAnimationDone;

    private boolean meatEffectActive;
    private long meatEffectStartTime;
    private boolean suplemenEffectActive;
    private boolean suplemenEffectUsed;
    
    private Image shineImage;

    public Player(double x, double y) {
        super(x, y, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
        this.maxHealth = Constants.PLAYER_MAX_HEALTH;
        this.health = this.maxHealth;
        this.velocityY = 0;
        this.jumping = false;
        this.shineImage = pawjump.game.utils.AssetLoader.loadImage(Constants.SHINE_IMG);
        initAnimations();
        this.currentAnimator = walkAnimator;
        this.currentAnimator.play();
    }

    private void initAnimations() {
        // Walk Animation
        walkAnimator = new SpriteAnimator(
            Constants.PLAYER_WALK_SHEET, 6,
            pawjump.game.utils.AssetLoader.loadImage(Constants.PLAYER_WALK_SHEET).getWidth(null) / 6,
            pawjump.game.utils.AssetLoader.loadImage(Constants.PLAYER_WALK_SHEET).getHeight(null),
            Constants.WALK_ANIM_DELAY, true);
        // Hurt Animation
        hurtAnimator = new SpriteAnimator(
            Constants.PLAYER_HURT_SHEET, 2,
            pawjump.game.utils.AssetLoader.loadImage(Constants.PLAYER_HURT_SHEET).getWidth(null) / 2,
            pawjump.game.utils.AssetLoader.loadImage(Constants.PLAYER_HURT_SHEET).getHeight(null),
            Constants.HURT_ANIM_DELAY, false); // Non-looping
        // Attack Animation
        attackAnimator = new SpriteAnimator(
            Constants.PLAYER_ATTACK_SHEET, 4,
            pawjump.game.utils.AssetLoader.loadImage(Constants.PLAYER_ATTACK_SHEET).getWidth(null) / 4,
            pawjump.game.utils.AssetLoader.loadImage(Constants.PLAYER_ATTACK_SHEET).getHeight(null),
            Constants.ATTACK_ANIM_DELAY, false); // Non-looping
        // Death Animation
        deathAnimator = new SpriteAnimator(
            Constants.PLAYER_DEATH_SHEET, 4,
            pawjump.game.utils.AssetLoader.loadImage(Constants.PLAYER_DEATH_SHEET).getWidth(null) / 4,
            pawjump.game.utils.AssetLoader.loadImage(Constants.PLAYER_DEATH_SHEET).getHeight(null),
            Constants.DEATH_ANIM_DELAY, false); // Non-looping
    }

    public void jump() {
        if (!jumping && !isDying) {
            this.velocityY = Constants.PLAYER_JUMP_VELOCITY;
            this.jumping = true;
        }
    }

    public void fall() {
        if (jumping && !isDying && velocityY < 0) {
            // Interrupt the jump by setting velocity to a positive value (downward)
            this.velocityY = Math.abs(velocityY) * 0.8; // Convert upward velocity to downward, slightly reduced
        }
    }

    public void takeDamage() {
        if (!isHurting && !isDying && health > 0) {
            this.health--;
            this.isHurting = true;
            this.hurtTicks = 0;
            this.currentAnimator = hurtAnimator;
            this.currentAnimator.playFromStart();
            if (this.health <= 0) {
                startDying();
            }
        }
    }
    
    public boolean isImmuneFromSuplemen() {
        return suplemenEffectActive && !suplemenEffectUsed;
    }

    public void consumeSuplemenShield() {
        if (suplemenEffectActive && !suplemenEffectUsed) {
            suplemenEffectUsed = true;
            // suplemenEffectActive will be turned off by GameManager or Player update
        }
    }


    public void activateMeatEffect() {
        this.meatEffectActive = true;
        this.meatEffectStartTime = System.currentTimeMillis();
        startAttackAnimation();
    }

    public void activateSuplemenEffect() {
        this.suplemenEffectActive = true;
        this.suplemenEffectUsed = false; // Reset used status
        startAttackAnimation();
    }
    
    public void startAttackAnimation() {
        if (!isDying) {
            this.isAttacking = true;
            this.attackTicks = 0;
            this.currentAnimator = attackAnimator;
            this.currentAnimator.playFromStart();
        }
    }

    public void startDying() {
        if (!isDying) {
            this.isDying = true;
            this.deathTicks = 0;
            this.health = 0;
            this.currentAnimator = deathAnimator;
            this.currentAnimator.playFromStart();
            this.deathAnimationDone = false;
        }
    }
    
    public boolean isDeathAnimationDone() {
        return deathAnimationDone;
    }


    @Override
    public void update() {
        // Update effects
        if (meatEffectActive && System.currentTimeMillis() - meatEffectStartTime > Constants.MEAT_EFFECT_DURATION_MS) {
            meatEffectActive = false;
        }
        // Suplemen effect duration is typically one hit or until phase ends (managed by GameManager)


        // Animation state machine & timers
        if (isDying) {
            deathAnimator.update();
            deathTicks++;
            if (deathTicks >= Constants.DEATH_ANIM_DURATION_TICKS) {
                 // Death animation "completes" for logic, actual drawing might hold last frame
                deathAnimationDone = true;
            }
            // Keep player on ground if dying
            y = Constants.GROUND_Y - height;
            velocityY = 0;
            return; // No other updates if dying
        }
        
        if (isAttacking) {
            attackAnimator.update();
            attackTicks++;
            if (attackTicks >= Constants.ATTACK_ANIM_DURATION_TICKS) {
                isAttacking = false;
                // Revert to walk animator if not hurting
                if (!isHurting) {
                    currentAnimator = walkAnimator;
                    if (!currentAnimator.isPlaying()) currentAnimator.play();
                } else { // If was also hurting, revert to hurt
                    currentAnimator = hurtAnimator;
                     if (!currentAnimator.isPlaying()) currentAnimator.playFromStart();
                }
            }
        } else if (isHurting) {
            hurtAnimator.update();
            hurtTicks++;
            if (hurtTicks >= Constants.HURT_ANIM_DURATION_TICKS) {
                isHurting = false;
                // Revert to walk animator
                currentAnimator = walkAnimator;
                if (!currentAnimator.isPlaying()) currentAnimator.play();
            }
        } else { // Not attacking or hurting
            if (currentAnimator != walkAnimator) {
                 currentAnimator = walkAnimator; // Default to walk
            }
            if (y >= Constants.GROUND_Y - height && !jumping) { // On ground and not initiated jump
                if (!walkAnimator.isPlaying()) walkAnimator.play();
                walkAnimator.update();
            } else { // In air
                walkAnimator.stop(); // Or specific jump animation if you have one
            }
        }


        // Gravity and movement
        velocityY += Constants.PLAYER_GRAVITY;
        y += velocityY;

        if (y >= Constants.GROUND_Y - height) {
            y = Constants.GROUND_Y - height;
            velocityY = 0;
            jumping = false;
        }
    }

    @Override
    public void draw(Graphics2D g2d, double spriteScaleOverride) {
        double currentSpriteScale = meatEffectActive ? Constants.PLAYER_SPRITE_SCALE_MEAT_EFFECT : Constants.PLAYER_SPRITE_SCALE_NORMAL;
        
        int spriteW = (int) (width * currentSpriteScale);
        int spriteH = (int) (height * currentSpriteScale);
        int spriteX = (int) (x - (spriteW - width) / 2.0);
        int spriteY = (int) (y + height - spriteH); // Align bottom of sprite with bottom of hitbox

        Image frameToDraw = null;
        if (currentAnimator != null) {
            frameToDraw = currentAnimator.getCurrentFrame();
        }

        // Draw shine effect if active (and not used)
        if (suplemenEffectActive && !suplemenEffectUsed && shineImage != null) {
            // Smaller shine, slightly above player center
            int shineW = (int)(spriteW * 0.45);
            int shineH = (int)(spriteH * 0.45);
            int shineX = spriteX + (spriteW - shineW) / 2;
            int shineY = spriteY + (int)(spriteH * 0.52) - (shineH / 2);
            
            // Save current composite
            java.awt.Composite oldComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
            g2d.drawImage(shineImage, shineX, shineY, shineW, shineH, null);
            // Restore composite
            g2d.setComposite(oldComposite);
        }


        if (frameToDraw != null) {
            g2d.drawImage(frameToDraw, spriteX, spriteY, spriteW, spriteH, null);
        } else { // Fallback
            g2d.setColor(java.awt.Color.CYAN);
            g2d.fillRect((int) x, (int) y, width, height);
        }
    }
    
    public void resetState() {
        this.health = this.maxHealth;
        this.x = Constants.PLAYER_START_X;
        this.y = Constants.PLAYER_START_Y; // It will fall to ground
        this.velocityY = 0;
        this.jumping = false;
        this.isHurting = false;
        this.isAttacking = false;
        this.isDying = false;
        this.deathAnimationDone = false;
        this.meatEffectActive = false;
        this.suplemenEffectActive = false;
        this.suplemenEffectUsed = false;
        this.hurtTicks = 0;
        this.attackTicks = 0;
        this.deathTicks = 0;
        
        walkAnimator.reset();
        hurtAnimator.reset();
        attackAnimator.reset();
        deathAnimator.reset();
        
        this.currentAnimator = walkAnimator;
        this.currentAnimator.play();
    }

    // Getters
    public int getHealth() { return health; }
    public boolean isDying() { return isDying; }
    public boolean isMeatEffectActive() { return meatEffectActive; }
    public boolean isSuplemenEffectActive() { return suplemenEffectActive; }
    public boolean isSuplemenEffectUsed() { return suplemenEffectUsed; }
    public void setSuplemenEffectActive(boolean active) { this.suplemenEffectActive = active; }
    public void setSuplemenEffectUsed(boolean used) { this.suplemenEffectUsed = used; }
}
