package pawjump.game.utils;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private static AudioManager instance;
    private Map<String, Clip> soundClips;
    private Clip backgroundMusic;
    private boolean audioEnabled = true;
    
    private AudioManager() {
        soundClips = new HashMap<>();
        loadAllAudio();
    }
    
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
      private void loadAllAudio() {
        System.out.println("Starting audio initialization...");
        try {
            // Load sound effects
            loadSound("fish", Constants.AUDIO_FISH);
            loadSound("hurt", Constants.AUDIO_HURT);
            loadSound("meat", Constants.AUDIO_MEAT);
            loadSound("suplemen", Constants.AUDIO_SUPLEMEN);
            loadSound("gameover", Constants.AUDIO_GAMEOVER);
            
            // Load background music
            loadBackgroundMusic(Constants.AUDIO_BG);
            
            System.out.println("Audio initialization complete. Loaded " + soundClips.size() + " sound clips.");
            if (backgroundMusic != null) {
                System.out.println("Background music loaded successfully.");
            }
            
        } catch (Exception e) {
            System.err.println("Failed to load audio files: " + e.getMessage());
            e.printStackTrace();
            audioEnabled = false;
        }
    }    private void loadSound(String name, String filePath) {
        System.out.println("Loading sound: " + name + " from " + filePath);
        try {
            // Try multiple ways to load the resource for better EXE compatibility
            InputStream audioStream = null;
            
            // Method 1: Use AssetLoader (current method)
            audioStream = AssetLoader.getResourceAsStream(filePath);
            
            // Method 2: Try direct class loader access if AssetLoader fails
            if (audioStream == null) {
                audioStream = AudioManager.class.getResourceAsStream(filePath);
            }
            
            // Method 3: Try system class loader if still null
            if (audioStream == null) {
                audioStream = ClassLoader.getSystemResourceAsStream(filePath.substring(1)); // Remove leading slash
            }
            
            if (audioStream == null) {
                System.err.println("Audio file not found: " + filePath);
                return;
            }
            
            System.out.println("Successfully found audio stream for: " + name);
            
            // Buffer the audio data to avoid mark/reset issues with JAR resources
            byte[] audioData = audioStream.readAllBytes();
            audioStream.close();
            
            // Create a new BufferedInputStream from the buffered data
            java.io.ByteArrayInputStream bufferedStream = new java.io.ByteArrayInputStream(audioData);
            
            // Create AudioInputStream and Clip
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedStream);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            
            // Set higher volume for hurt and fish sounds
            if (name.equals("hurt") || name.equals("fish")) {
                setVolume(clip, 1.0f); // Full volume for hurt and fish
            } else {
                setVolume(clip, 0.8f); // Default volume for other sounds
            }
            
            soundClips.put(name, clip);
            System.out.println("Successfully loaded audio clip: " + name);
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Failed to load sound: " + filePath + " - " + e.getMessage());
            e.printStackTrace();
        }
    }      private void loadBackgroundMusic(String filePath) {
        System.out.println("Loading background music from: " + filePath);
        try {
            // Try multiple ways to load the resource for better EXE compatibility
            InputStream audioStream = null;
            
            // Method 1: Use AssetLoader (current method)
            audioStream = AssetLoader.getResourceAsStream(filePath);
            
            // Method 2: Try direct class loader access if AssetLoader fails
            if (audioStream == null) {
                audioStream = AudioManager.class.getResourceAsStream(filePath);
            }
            
            // Method 3: Try system class loader if still null
            if (audioStream == null) {
                audioStream = ClassLoader.getSystemResourceAsStream(filePath.substring(1)); // Remove leading slash
            }
            
            if (audioStream == null) {
                System.err.println("Background music file not found: " + filePath);
                return;
            }
            
            System.out.println("Successfully found background music stream");
            
            // Buffer the audio data to avoid mark/reset issues with JAR resources (same as sound effects)
            byte[] audioData = audioStream.readAllBytes();
            audioStream.close();
            
            // Create a new BufferedInputStream from the buffered data
            java.io.ByteArrayInputStream bufferedStream = new java.io.ByteArrayInputStream(audioData);
            
            // Create AudioInputStream and Clip
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedStream);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInputStream);
            
            // Set volume to 50%
            setVolume(backgroundMusic, 0.5f);
            
            System.out.println("Background music loaded successfully");
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Failed to load background music: " + filePath + " - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setVolume(Clip clip, float volume) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(Math.max(dB, gainControl.getMinimum()));
        }
    }    public void playSound(String soundName) {
        if (!audioEnabled) {
            System.out.println("Audio disabled, cannot play sound: " + soundName);
            return;
        }
        
        Clip clip = soundClips.get(soundName);
        if (clip != null) {
            System.out.println("Playing sound: " + soundName);
            // Ensure the clip is fully stopped before restarting
            if (clip.isRunning()) {
                clip.stop();
            }
            // Wait a tiny bit for the clip to fully stop
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            clip.setFramePosition(0);
            clip.start();
        } else {
            System.err.println("Sound clip not found: " + soundName + ". Available clips: " + soundClips.keySet());
        }
    }
      public void startBackgroundMusic() {
        if (!audioEnabled) {
            System.out.println("Audio disabled, cannot start background music");
            return;
        }
        
        if (backgroundMusic == null) {
            System.err.println("Background music not loaded");
            return;
        }
        
        if (!backgroundMusic.isRunning()) {
            System.out.println("Starting background music");
            backgroundMusic.setFramePosition(0);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            System.out.println("Background music already running");
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
        if (!audioEnabled || backgroundMusic == null) return;
        
        if (!backgroundMusic.isRunning()) {
            backgroundMusic.start();
        }
    }
    
    public void stopAllSounds() {
        stopBackgroundMusic();
        for (Clip clip : soundClips.values()) {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
        }
    }
    
    public void setAudioEnabled(boolean enabled) {
        this.audioEnabled = enabled;
        if (!enabled) {
            stopAllSounds();
        }
    }
    
    public boolean isAudioEnabled() {
        return audioEnabled;
    }
    
    // Convenience methods for game events
    public void playFishSound() { playSound("fish"); }
    public void playHurtSound() { playSound("hurt"); }
    public void playMeatSound() { playSound("meat"); }
    public void playSuplemenSound() { playSound("suplemen"); }
    public void playGameOverSound() { 
        stopBackgroundMusic();
        playSound("gameover"); 
    }
}
