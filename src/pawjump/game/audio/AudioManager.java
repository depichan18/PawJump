package pawjump.game.audio;

import javax.sound.sampled.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import pawjump.game.utils.Constants;

public class AudioManager {
    private static AudioManager instance;
    private Map<String, Clip> soundEffects;
    private Clip backgroundMusic;
    private boolean soundEnabled = true;
    
    private AudioManager() {
        soundEffects = new HashMap<>();
        loadSounds();
    }
    
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    private void loadSounds() {
        try {
            // Load sound effects
            loadSound("fish", Constants.AUDIO_FISH);
            loadSound("hurt", Constants.AUDIO_HURT);
            loadSound("meat", Constants.AUDIO_MEAT);
            loadSound("suplemen", Constants.AUDIO_SUPLEMEN);
            loadSound("gameover", Constants.AUDIO_GAMEOVER);
            
            // Load background music
            loadBackgroundMusic(Constants.AUDIO_BG);
            
        } catch (Exception e) {
            System.err.println("Error loading audio files: " + e.getMessage());
            soundEnabled = false;
        }
    }
    
    private void loadSound(String name, String path) {
        try {
            InputStream audioStream = pawjump.game.utils.AssetLoader.getResourceAsStream(path);
            if (audioStream == null) {
                System.err.println("Could not find audio file: " + path);
                return;
            }
            
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioStream);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            soundEffects.put(name, clip);
            
        } catch (Exception e) {
            System.err.println("Error loading sound " + name + ": " + e.getMessage());
        }
    }
    
    private void loadBackgroundMusic(String path) {
        try {
            InputStream audioStream = pawjump.game.utils.AssetLoader.getResourceAsStream(path);
            if (audioStream == null) {
                System.err.println("Could not find background music file: " + path);
                return;
            }
            
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioStream);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInputStream);
            
            // Set volume to 50%
            FloatControl volumeControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            float volume = 0.5f; // 50% volume
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            volumeControl.setValue(dB);
            
        } catch (Exception e) {
            System.err.println("Error loading background music: " + e.getMessage());
        }
    }
    
    public void playSound(String soundName) {
        if (!soundEnabled) return;
        
        Clip clip = soundEffects.get(soundName);
        if (clip != null) {
            // Stop the clip if it's already playing
            if (clip.isRunning()) {
                clip.stop();
            }
            // Reset to beginning and play
            clip.setFramePosition(0);
            clip.start();
        }
    }
    
    public void startBackgroundMusic() {
        if (!soundEnabled || backgroundMusic == null) return;
        
        if (!backgroundMusic.isRunning()) {
            backgroundMusic.setFramePosition(0);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusic.start();
        }
    }
    
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }
    
    public void pauseBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }
    
    public void resumeBackgroundMusic() {
        startBackgroundMusic();
    }
    
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopBackgroundMusic();
        }
    }
    
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    // Convenience methods for specific game events
    public void playFishSound() {
        playSound("fish");
    }
    
    public void playHurtSound() {
        playSound("hurt");
    }
    
    public void playMeatSound() {
        playSound("meat");
    }
    
    public void playSuplemenSound() {
        playSound("suplemen");
    }
    
    public void playGameOverSound() {
        playSound("gameover");
    }
    
    public void cleanup() {
        stopBackgroundMusic();
        for (Clip clip : soundEffects.values()) {
            if (clip != null) {
                clip.close();
            }
        }
        if (backgroundMusic != null) {
            backgroundMusic.close();
        }
    }
}
