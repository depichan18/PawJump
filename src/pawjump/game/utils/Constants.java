package pawjump.game.utils;

import java.awt.event.KeyEvent;

public class Constants {
    // Screen Dimensions (GamePanel size)
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;

    // Player Constants
    public static final int PLAYER_START_X = 200;
    public static final int PLAYER_START_Y = 360; // Initial Y before gravity
    public static final int PLAYER_WIDTH = 50;
    public static final int PLAYER_HEIGHT = 50;
    public static final int PLAYER_MAX_HEALTH = 4;
    public static final int PLAYER_JUMP_VELOCITY = -30; // Negative for upward movement
    public static final int PLAYER_GRAVITY = 2;
    public static final double PLAYER_SPRITE_SCALE_NORMAL = 3.0;
    public static final double PLAYER_SPRITE_SCALE_MEAT_EFFECT = 4.2;


    // Game Mechanics
    public static final int GROUND_Y = 600; // Platform top Y, player stands on this
    public static final int BASE_SPEED = 12; // Base speed for obstacles/items
    public static final double SPEED_INCREMENT_PER_LEVEL = 0.2;
    public static final int METERS_PER_LEVEL = 250;
    public static final int OBSTACLE_SPAWN_GAP = 170; // Gap after an obstacle goes off-screen

    // Item Constants
    public static final int ITEM_WIDTH = 48;
    public static final int ITEM_HEIGHT = 48;
    public static final int FISH_NEEDED_PER_LEVEL = 15;
    public static final int MEAT_EFFECT_DURATION_MS = 3000;
    public static final int SUPLEMEN_SPAWN_OFFSET = 125; // meters into phase
    public static final int MEAT_SPAWN_OFFSET_1 = 50;    // meters into phase
    public static final int MEAT_SPAWN_OFFSET_2 = 200;   // meters into phase


    // Obstacle Constants
    public static final int OBSTACLE_WIDTH = 80;
    public static final int OBSTACLE_HEIGHT = 80;
    public static final int OBSTACLE_FLOAT_MIN_SCORE = 250;
    public static final int OBSTACLE_MOVING_MIN_SCORE = 500;
    public static final double OBSTACLE_FLOAT_CHANCE = 0.3;
    public static final double OBSTACLE_MOVING_CHANCE = 0.2;
    public static final int OBSTACLE_FLOAT_GAP_FROM_GROUND = 180;
    public static final int OBSTACLE_MOVE_SPEED = 4;
    public static final int OBSTACLE_MOVE_MIN_Y_OFFSET = -350; // Relative to groundY
    public static final int OBSTACLE_MOVE_MAX_Y_OFFSET = -30;  // Relative to groundY


    // Animation Delays (in game ticks, approx 16ms per tick)
    public static final int WALK_ANIM_DELAY = 3;
    public static final int HURT_ANIM_DELAY = 6;
    public static final int HURT_ANIM_DURATION_TICKS = 18;
    public static final int ATTACK_ANIM_DELAY = 4;
    public static final int ATTACK_ANIM_DURATION_TICKS = 16;
    public static final int DEATH_ANIM_DELAY = 8;
    public static final int DEATH_ANIM_DURATION_TICKS = 32; // 4 frames * 8 ticks
    public static final int BIRD_ANIM_DELAY = 3;


    // File Paths
    public static final String HIGHSCORE_FILE_PATH = "/resources/highscore.txt";
    public static final String IMAGE_PATH_PREFIX = "/resources/images/";
    public static final String AUDIO_PATH_PREFIX = "/resources/audio/";

    public static final String DAY_BG_IMG = IMAGE_PATH_PREFIX + "day.png";
    public static final String NIGHT_BG_IMG = IMAGE_PATH_PREFIX + "night.png";
    public static final String PLATFORM_IMG = IMAGE_PATH_PREFIX + "platform.png";
    public static final String HEALTH_IMG = IMAGE_PATH_PREFIX + "health.png";
    public static final String SHINE_IMG = IMAGE_PATH_PREFIX + "shine.png";

    public static final String PLAYER_WALK_SHEET = IMAGE_PATH_PREFIX + "Walk.png";
    public static final String PLAYER_HURT_SHEET = IMAGE_PATH_PREFIX + "Hurt.png";
    public static final String PLAYER_ATTACK_SHEET = IMAGE_PATH_PREFIX + "Attack.png";
    public static final String PLAYER_DEATH_SHEET = IMAGE_PATH_PREFIX + "Death.png";

    public static final String OBSTACLE_BIRD_SHEET = IMAGE_PATH_PREFIX + "bird.png";
    public static final String OBSTACLE_BUSH_IMG = IMAGE_PATH_PREFIX + "bush.png";
    public static final String OBSTACLE_GROUND_IMG = IMAGE_PATH_PREFIX + "ground.png"; // For moving obstacle

    public static final String ITEM_FISH_IMG = IMAGE_PATH_PREFIX + "fish.png";
    public static final String ITEM_MEAT_IMG = IMAGE_PATH_PREFIX + "meat.png";
    public static final String ITEM_SUPLEMEN_IMG = IMAGE_PATH_PREFIX + "suplemen.png";

    // Audio Files
    public static final String AUDIO_BG = AUDIO_PATH_PREFIX + "bg.wav";
    public static final String AUDIO_FISH = AUDIO_PATH_PREFIX + "fish.wav";
    public static final String AUDIO_GAMEOVER = AUDIO_PATH_PREFIX + "gameover.wav";
    public static final String AUDIO_HURT = AUDIO_PATH_PREFIX + "hurt.wav";
    public static final String AUDIO_MEAT = AUDIO_PATH_PREFIX + "meat.wav";
    public static final String AUDIO_SUPLEMEN = AUDIO_PATH_PREFIX + "suplemen.wav";

    // Chasing Character
    public static final String CHASING_CHARACTER_SHEET = IMAGE_PATH_PREFIX + "dog.png";
    public static final int CHASING_CHARACTER_WIDTH = 50;
    public static final int CHASING_CHARACTER_HEIGHT = 50;
    public static final int CHASING_DISTANCE_METERS = 100; // Distance in meters before disappearing
    public static final int CHASING_TARGET_DISTANCE = 70; // Distance to maintain behind player
    public static final double CHASING_ACCELERATION = 0.8; // Acceleration when appearing
    public static final double CHASING_DECELERATION = 0.6; // Deceleration when disappearing
    public static final int CHASING_ANIM_DELAY = 4; // Animation delay for running frames

    // UI
    public static final int LEVEL_UP_MSG_DURATION_MS = 2000;

    // Supplement Effect
    public static final int SUPLEMEN_SPOTLIGHT_RADIUS = 200; // Radius of light around player
    public static final float SUPLEMEN_OVERLAY_ALPHA = 0.45f; // Darkness level (0.0 = transparent, 1.0 = opaque)

    // Input
    public static final int JUMP_KEY = KeyEvent.VK_UP;
    public static final int FALL_KEY = KeyEvent.VK_DOWN;

    // Ticks
    public static final int GAME_TICK_MS = 16; // For Timer
}
