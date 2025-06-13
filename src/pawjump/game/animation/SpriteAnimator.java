package pawjump.game.animation;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class SpriteAnimator {
    private Image[] frames;
    private int currentFrameIndex;
    private int frameDelay; // Number of game ticks to wait before advancing frame
    private int frameTickCounter;
    private boolean playing;
    private boolean loop;

    public SpriteAnimator(String spriteSheetPath, int numFrames, int frameWidth, int frameHeight, int frameDelay, boolean loop) {
        this.frames = new Image[numFrames];
        sliceSpriteSheet(spriteSheetPath, numFrames, frameWidth, frameHeight);
        this.frameDelay = frameDelay;
        this.loop = loop;
        this.currentFrameIndex = 0;
        this.frameTickCounter = 0;
        this.playing = false; // Start paused, call play() to begin
    }
    
    public SpriteAnimator(Image[] frames, int frameDelay, boolean loop) {
        this.frames = frames;
        this.frameDelay = frameDelay;
        this.loop = loop;
        this.currentFrameIndex = 0;
        this.frameTickCounter = 0;
        this.playing = false;
    }


    private void sliceSpriteSheet(String spriteSheetPath, int numFrames, int frameWidth, int frameHeight) {

        Image sheetImage = pawjump.game.utils.AssetLoader.loadImage(spriteSheetPath);

        if (sheetImage.getWidth(null) <= 0 || sheetImage.getHeight(null) <= 0 || frameWidth <= 0 || frameHeight <= 0) {
            System.err.println("Error loading or invalid dimensions for spritesheet: " + spriteSheetPath);
            // Fill with placeholder or throw exception
            for (int i = 0; i < numFrames; i++) {
                frames[i] = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB); // Minimal placeholder
            }
            return;
        }
        
        // Calculate actual number of frames based on image width, if frameWidth provided
        // For simplicity, we assume numFrames provided is correct for a horizontal strip.
        // If frameWidth is 0, it means the image is a single frame or frames are pre-sliced.
        if (frameWidth == 0 && numFrames == 1) { // Single frame image
             frames[0] = sheetImage;
             return;
        }


        for (int i = 0; i < numFrames; i++) {
            BufferedImage frame = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = frame.createGraphics();
            // Assuming horizontal sprite sheet
            g.drawImage(sheetImage, 0, 0, frameWidth, frameHeight,
                        i * frameWidth, 0, (i + 1) * frameWidth, frameHeight, null);
            g.dispose();
            frames[i] = frame;
        }
    }

    public void update() {
        if (!playing || frames == null || frames.length == 0) {
            return;
        }

        frameTickCounter++;
        if (frameTickCounter >= frameDelay) {
            frameTickCounter = 0;
            currentFrameIndex++;
            if (currentFrameIndex >= frames.length) {
                if (loop) {
                    currentFrameIndex = 0;
                } else {
                    currentFrameIndex = frames.length - 1; // Stay on last frame
                    playing = false; // Stop animation if not looping
                }
            }
        }
    }

    public Image getCurrentFrame() {
        if (frames == null || frames.length == 0 || currentFrameIndex < 0 || currentFrameIndex >= frames.length) {
             // Return a placeholder or handle error
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
        return frames[currentFrameIndex];
    }

    public void play() {
        this.playing = true;
        this.currentFrameIndex = 0; // Reset to start
        this.frameTickCounter = 0;
    }
    
    public void playFromStart() {
        this.playing = true;
        this.currentFrameIndex = 0;
        this.frameTickCounter = 0;
    }

    public void stop() {
        this.playing = false;
    }

    public void reset() {
        this.currentFrameIndex = 0;
        this.frameTickCounter = 0;
        this.playing = false;
    }
    
    public boolean isPlaying() {
        return playing;
    }

    public int getCurrentFrameIndex() {
        return currentFrameIndex;
    }
    
    public int getTotalFrames() {
        return frames.length;
    }
    
    public void setFrames(Image[] newFrames) {
        this.frames = newFrames;
        reset();
    }
}
