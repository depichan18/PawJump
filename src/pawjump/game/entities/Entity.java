package pawjump.game.entities;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import pawjump.game.animation.SpriteAnimator;

public abstract class Entity {
    protected double x, y;
    protected int width, height;
    protected SpriteAnimator animator; // Can be null if not animated or uses single image
    protected Image staticImage; // For non-animated entities

    public Entity(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void update(); // Basic update, might need parameters like speedMultiplier
    public abstract void draw(Graphics2D g2d, double spriteScale); // spriteScale for effects

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    // Getters and Setters
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public boolean collidesWith(Entity other) {
        return getBounds().intersects(other.getBounds());
    }
}
