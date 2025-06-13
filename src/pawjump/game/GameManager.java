package pawjump.game;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import pawjump.game.entities.BirdObstacle;
import pawjump.game.entities.BushObstacle;
import pawjump.game.entities.ChasingCharacter;
import pawjump.game.entities.MovingGroundObstacle;
import pawjump.game.entities.Obstacle;
import pawjump.game.entities.Player;
import pawjump.game.entities.items.Fish;
import pawjump.game.entities.items.Item;
import pawjump.game.entities.items.Meat;
import pawjump.game.entities.items.Suplemen;
import pawjump.game.utils.AudioManager;
import pawjump.game.utils.Constants;

public class GameManager {
    private Player player;
    private List<Obstacle> obstacles;
    private List<Item> items;
    private ChasingCharacter chasingCharacter;
    private AudioManager audioManager;

    private int score;
    private int currentLevel;
    private double speedMultiplier;
    private boolean isDay;
    private int highScore;
    private boolean isRunning;
    private boolean isGameOver;

    private double accumulatedDistance; // For precise score calculation

    // Level up and item tracking
    private boolean levelUpMessageActive;
    private long levelUpMessageStartTime;
    private int fishCollectedThisLevel;
    private boolean levelUpRequiredThisPhase; // If player must collect fish to pass
    private boolean levelUpFailedThisPhase;

    // Item spawning state (per phase/level)
    private int fishPhase; // Tracks 250m phases for fish spawning
    private int meatPhase;
    private int meatAppearCountThisPhase;
    private int suplemenPhase;
    private int suplemenAppearCountThisPhase;
    
    private GamePanel gamePanelRef; // Reference to GamePanel to request focus or other panel actions

    public GameManager(GamePanel gamePanelRef) {
        this.gamePanelRef = gamePanelRef;
        this.player = new Player(Constants.PLAYER_START_X, Constants.PLAYER_START_Y);
        this.obstacles = new ArrayList<>();
        this.items = new ArrayList<>();
        this.chasingCharacter = new ChasingCharacter();
        this.audioManager = AudioManager.getInstance();
        loadHighScore();
        initializeNewGame();
    }

    private void initializeNewGame() {
        this.player.resetState();
        this.obstacles.clear();
        this.items.clear();
        
        // Reset chasing character
        if (this.chasingCharacter != null) {
            this.chasingCharacter.reset();
        }

        this.score = 0;
        this.currentLevel = 1;
        this.speedMultiplier = 1.0;
        this.isDay = true;
        this.isRunning = true;
        this.isGameOver = false;
        this.accumulatedDistance = 0.0;

        this.levelUpMessageActive = false;
        this.fishCollectedThisLevel = 0;
        this.levelUpRequiredThisPhase = false; // Becomes true at end of a 250m phase
        this.levelUpFailedThisPhase = false;

        this.fishPhase = 0;
        this.meatPhase = 0;
        this.meatAppearCountThisPhase = 0;
        this.suplemenPhase = 0;
        this.suplemenAppearCountThisPhase = 0;

        initFirstObstacle();
        // Initial items will be spawned by update based on score 0
        
        // Start background music
        audioManager.startBackgroundMusic();
    }

    public void restartGame() {
        initializeNewGame();
        if (gamePanelRef != null) {
             gamePanelRef.resetBackgroundAndPlatform(); // Tell panel to reset its visuals
             gamePanelRef.restartGameTimer(); // Restart the game timer
             gamePanelRef.requestFocusForGame();
        }
    }

    private void loadHighScore() {
        try (InputStream is = pawjump.game.utils.AssetLoader.getResourceAsStream(Constants.HIGHSCORE_FILE_PATH);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            this.highScore = Integer.parseInt(br.readLine());
        } catch (Exception e) {
            this.highScore = 0;
        }
    }

    // Note: Writing to a resource inside a JAR is not possible. For a real game, save to user home or appdata.
    private void saveHighScore() {
        try {
            java.nio.file.Path userScore = java.nio.file.Paths.get(System.getProperty("user.home"), ".pawjump_highscore.txt");
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(java.nio.file.Files.newOutputStream(userScore)))) {
                bw.write(String.valueOf(this.highScore));
            }
        } catch (Exception ignored) {
            System.err.println("Could not save high score: " + ignored.getMessage());
        }
    }

    public void update() {
        if (!isRunning) {
            if (isGameOver && player.isDying() && player.isDeathAnimationDone()) {
                 // Death animation finished, game over screen should be shown by GamePanel
            } else if (player.isDying()){
                player.update(); // Continue death animation
            }
            return;
        }

        // Update score and level
        double distanceThisFrame = (Constants.BASE_SPEED / 50.0) * 0.5; // Original calculation for score increment
        accumulatedDistance += distanceThisFrame;
        score = (int) Math.floor(accumulatedDistance);

        int newLevel = (int) (accumulatedDistance / Constants.METERS_PER_LEVEL) + 1;
        if (newLevel > currentLevel) {
            currentLevel = newLevel;
            levelUpMessageActive = true;
            levelUpMessageStartTime = System.currentTimeMillis();
            // Reset per-level item counts if necessary, or handle phase transitions
        }
        speedMultiplier = 1.0 + Constants.SPEED_INCREMENT_PER_LEVEL * (currentLevel - 1);
        
        // Apply meat effect speed reduction (25% slower)
        if (player.isMeatEffectActive()) {
            speedMultiplier *= 0.75; // Reduce speed by 25%
        }
        
        isDay = ((score / Constants.METERS_PER_LEVEL) % 2 == 0);


        // Phase management for items
        // Fish phase (every 250m a new phase starts, checking for fish collection)
        int currentFishPhase = score / Constants.METERS_PER_LEVEL;
        if (currentFishPhase > fishPhase) { // Crossed into a new 250m phase
            levelUpRequiredThisPhase = true; // Now player must have enough fish
            if (fishCollectedThisLevel < Constants.FISH_NEEDED_PER_LEVEL) {
                levelUpFailedThisPhase = true;
                gameOver("Failed to collect enough fish!");
            } else {
                levelUpRequiredThisPhase = false; // Requirement met
                fishCollectedThisLevel = 0; // Reset for next phase/level
            }
            fishPhase = currentFishPhase;
        }
        
        // Meat phase
        int currentMeatPhase = score / Constants.METERS_PER_LEVEL;
        if (currentMeatPhase > meatPhase) {
            meatPhase = currentMeatPhase;
            meatAppearCountThisPhase = 0;
        }

        // Suplemen phase
        int currentSuplemenPhase = score / Constants.METERS_PER_LEVEL;
        if (currentSuplemenPhase > suplemenPhase) {
            suplemenPhase = currentSuplemenPhase;
            suplemenAppearCountThisPhase = 0;
            player.setSuplemenEffectActive(false); // Suplemen effect wears off at phase end
            player.setSuplemenEffectUsed(false);
        }


        // Update player
        player.update();
        if (player.isDying() && !isGameOver) { // Player might have started dying due to health loss
            gameOver("Player health depleted.");
        }

        // Update chasing character
        if (chasingCharacter != null) {
            chasingCharacter.update();
            chasingCharacter.updateChasing(player, Constants.BASE_SPEED * speedMultiplier, score);
        }

        // Spawn entities
        spawnObstaclesIfNeeded();
        spawnItemsIfNeeded();

        // Update obstacles
        Iterator<Obstacle> obsIterator = obstacles.iterator();
        while (obsIterator.hasNext()) {
            Obstacle obs = obsIterator.next();
            obs.move(Constants.BASE_SPEED, speedMultiplier);
            obs.updateSpecific(); // For moving obstacles
            if (obs.isOffScreen()) {
                obsIterator.remove();
            }
        }

        // Update items
        Iterator<Item> itemIterator = items.iterator();
        while (itemIterator.hasNext()) {
            Item item = itemIterator.next();
            item.move(Constants.BASE_SPEED, speedMultiplier);
            // item.update(); // If items have own update logic beyond moving
            if (!item.isActive()) { // Off-screen or collected
                itemIterator.remove();
            }
        }

        // Check collisions
        checkCollisions();

        // Update level up message visibility
        if (levelUpMessageActive && System.currentTimeMillis() - levelUpMessageStartTime > Constants.LEVEL_UP_MSG_DURATION_MS) {
            levelUpMessageActive = false;
        }
    }

    private void initFirstObstacle() {
        obstacles.add(createObstacle(Constants.SCREEN_WIDTH));
    }

    private Obstacle createObstacle(double xPos) {
        if (levelUpRequiredThisPhase) return null; // Don't spawn new obstacles during fish collection check period

        double r = Math.random();
        boolean allowFloating = score >= Constants.OBSTACLE_FLOAT_MIN_SCORE;
        boolean allowMoving = score >= Constants.OBSTACLE_MOVING_MIN_SCORE;
        double floatChance = allowFloating ? Constants.OBSTACLE_FLOAT_CHANCE : 0.0;
        double movingChance = allowMoving ? Constants.OBSTACLE_MOVING_CHANCE : 0.0;

        int obsY;
        if (r < floatChance) {
            obsY = Constants.GROUND_Y - Constants.OBSTACLE_HEIGHT - Constants.OBSTACLE_FLOAT_GAP_FROM_GROUND;
            return new BirdObstacle(xPos, obsY);
        } else if (r < floatChance + movingChance) {
            int minY = Constants.GROUND_Y + Constants.OBSTACLE_MOVE_MIN_Y_OFFSET - Constants.OBSTACLE_HEIGHT;
            int maxY = Constants.GROUND_Y + Constants.OBSTACLE_MOVE_MAX_Y_OFFSET - Constants.OBSTACLE_HEIGHT;
            if (minY < 0) minY = 0;
            if (maxY < minY) maxY = minY + 1; // Ensure range
            obsY = maxY - (maxY - minY) / 2; // Start in middle
            int initialMoveDir = Math.random() < 0.5 ? 1 : -1;
            // Clamp so the obstacle doesn't go below the platform
            if (obsY > Constants.GROUND_Y - Constants.OBSTACLE_HEIGHT) obsY = Constants.GROUND_Y - Constants.OBSTACLE_HEIGHT;
            return new MovingGroundObstacle(xPos, obsY, minY, maxY, initialMoveDir);
        } else {
            obsY = Constants.GROUND_Y - Constants.OBSTACLE_HEIGHT;
            return new BushObstacle(xPos, obsY);
        }
    }
    
    private void spawnObstaclesIfNeeded() {
        // Only allow one obstacle at a time, like the reference
        if (obstacles.isEmpty() || (obstacles.get(obstacles.size() - 1).getX() < Constants.SCREEN_WIDTH - Constants.OBSTACLE_SPAWN_GAP - Constants.OBSTACLE_WIDTH)) {
            // Remove all obstacles that are off-screen (should already be handled in update, but ensure only one remains)
            obstacles.removeIf(Obstacle::isOffScreen);
            if (obstacles.size() < 1) {
                Obstacle newObs = createObstacle(Constants.SCREEN_WIDTH);
                if (newObs != null) obstacles.add(newObs);
            }
        }
    }
    
    private boolean isItemYValid(int candidateY, int candidateH, List<Item> currentItems, int minDist) {
        for (Item item : currentItems) {
            if (item.isActive()) {
                 // Simple vertical separation check (original was more complex, adapting)
                Rectangle newItemRect = new Rectangle(0, candidateY, 1, candidateH);
                Rectangle existingItemRect = new Rectangle(0, (int)item.getY(), 1, item.getHeight());
                if (newItemRect.y < existingItemRect.y + existingItemRect.height + minDist &&
                    newItemRect.y + newItemRect.height + minDist > existingItemRect.y) {
                    return false; // Overlap or too close
                }
            }
        }
        // Also check against player jump height for accessibility (simplified)
        // int jumpHeight = 225;
        // int marginAbovePlatform = 60;
        // if(candidateY < Constants.GROUND_Y - candidateH - jumpHeight || candidateY > Constants.GROUND_Y - candidateH - marginAbovePlatform) return false;

        return true;
    }


    private void spawnItemsIfNeeded() {
        // Simplified spawning logic; original was tied to score thresholds within phases.
        // Fish (1 per phase, if not already active and phase allows)
        int phaseStart = fishPhase * Constants.METERS_PER_LEVEL;
        if (score >= phaseStart && score < phaseStart + Constants.METERS_PER_LEVEL) {
            boolean fishAlreadyActive = items.stream().anyMatch(item -> item instanceof Fish && item.isActive());
            if (!fishAlreadyActive && !levelUpRequiredThisPhase) { // Don't spawn new fish if checking for current phase completion
                int y = Constants.GROUND_Y - Constants.ITEM_HEIGHT - 50 - (int)(Math.random() * 150); // Random Y
                if (isItemYValid(y, Constants.ITEM_HEIGHT, items, 20)) {
                     items.add(new Fish(Constants.SCREEN_WIDTH + 50, y));
                }
            }
        }


        // Meat (2 per phase at specific score points if not already active)
        phaseStart = meatPhase * Constants.METERS_PER_LEVEL;
        int[] meatSpawnScores = { phaseStart + Constants.MEAT_SPAWN_OFFSET_1, phaseStart + Constants.MEAT_SPAWN_OFFSET_2 };
        boolean meatAlreadyActive = items.stream().anyMatch(item -> item instanceof Meat && item.isActive());

        if (!meatAlreadyActive && meatAppearCountThisPhase < 2) {
            if (meatAppearCountThisPhase == 0 && score >= meatSpawnScores[0] && score < meatSpawnScores[1]) {
                 int y = Constants.GROUND_Y - Constants.ITEM_HEIGHT - 50 - (int)(Math.random() * 150);
                 if (isItemYValid(y, Constants.ITEM_HEIGHT, items, 20)) {
                    items.add(new Meat(Constants.SCREEN_WIDTH + 100, y));
                    meatAppearCountThisPhase++;
                 }
            } else if (meatAppearCountThisPhase == 1 && score >= meatSpawnScores[1] && score < (meatPhase + 1) * Constants.METERS_PER_LEVEL) {
                 int y = Constants.GROUND_Y - Constants.ITEM_HEIGHT - 50 - (int)(Math.random() * 150);
                 if (isItemYValid(y, Constants.ITEM_HEIGHT, items, 20)) {
                    items.add(new Meat(Constants.SCREEN_WIDTH + 100, y));
                    meatAppearCountThisPhase++;
                 }
            }
        }

        // Suplemen (1 per phase at specific score point if not already active)
        phaseStart = suplemenPhase * Constants.METERS_PER_LEVEL;
        int suplemenSpawnScore = phaseStart + Constants.SUPLEMEN_SPAWN_OFFSET;
        boolean suplemenAlreadyActive = items.stream().anyMatch(item -> item instanceof Suplemen && item.isActive());

        if (!suplemenAlreadyActive && suplemenAppearCountThisPhase < 1 && score >= suplemenSpawnScore && score < (suplemenPhase + 1) * Constants.METERS_PER_LEVEL) {
            int y = Constants.GROUND_Y - Constants.ITEM_HEIGHT - 50 - (int)(Math.random() * 150);
            if (isItemYValid(y, Constants.ITEM_HEIGHT, items, 20)) {
                items.add(new Suplemen(Constants.SCREEN_WIDTH + 150, y));
                suplemenAppearCountThisPhase++;
            }
        }
    }


    private void checkCollisions() {
        // Player vs Obstacles
        Iterator<Obstacle> obsIterator = obstacles.iterator();
        while (obsIterator.hasNext()) {
            Obstacle obs = obsIterator.next();
            if (player.collidesWith(obs)) {
                if (player.isImmuneFromSuplemen()) {
                    player.consumeSuplemenShield();
                    // Remove the obstacle that was hit by the immune player
                    obsIterator.remove();
                    // Spawn a new obstacle immediately to maintain gameplay flow
                    Obstacle newObs = createObstacle(Constants.SCREEN_WIDTH + Constants.OBSTACLE_SPAWN_GAP);
                    if (newObs != null) {
                        obstacles.add(newObs);
                    }
                } else {
                    player.takeDamage();
                    audioManager.playHurtSound(); // Play hurt sound
                    // Trigger chasing character when player takes damage
                    if (chasingCharacter != null && !chasingCharacter.isActive()) {
                        chasingCharacter.startChasing(player, score);
                    }
                }
                break; // Process one collision per frame to avoid multiple damage from same group
            }
        }

        // Player vs Items
        Iterator<Item> itemIterator = items.iterator();
        while (itemIterator.hasNext()) {
            Item item = itemIterator.next();
            if (item.isActive() && player.collidesWith(item)) {
                item.applyEffect(player, this); // Item applies its effect
                // Item is marked as collected within applyEffect, loop will remove it if !isActive
            }
        }
    }

    public void collectFish() {
        this.fishCollectedThisLevel++;
    }
    
    public void itemCollected(Item item) {
        // Could be used to track specific item states if needed beyond player effects
        // e.g., if Meat/Suplemen had limited spawns that GameManager managed more directly.
    }


    private void gameOver(String reason) {
        System.out.println("Game Over: " + reason);
        this.isRunning = false;
        this.isGameOver = true;
        audioManager.playGameOverSound(); // Play game over sound and stop background music
        if (this.score > this.highScore) {
            this.highScore = this.score;
            saveHighScore();
        }
        if (!player.isDying()) { // If game over was not due to health reaching 0 (e.g. fish fail)
             player.startDying(); // Start death animation
        }
    }

    // Getters for GamePanel, HUD, etc.
    public Player getPlayer() { return player; }
    public List<Obstacle> getObstacles() { return obstacles; }
    public List<Item> getItems() { return items; }
    public ChasingCharacter getChasingCharacter() { return chasingCharacter; }
    public int getScore() { return score; }
    public int getCurrentLevel() { return currentLevel; }
    public double getSpeedMultiplier() { return speedMultiplier; }
    public boolean isDay() { return isDay; }
    public int getHighScore() { return highScore; }
    public boolean isRunning() { return isRunning; }
    public boolean isGameOver() { return isGameOver; }
    public boolean isLevelUpMessageActive() { return levelUpMessageActive; }
    public int getFishCollectedThisLevel() { return fishCollectedThisLevel; }

}
