package pawjump.game.entities;

import pawjump.game.utils.Constants;
import java.awt.Graphics2D;

public class MovingGroundObstacle extends Obstacle {
    private int moveDir;
    private int moveSpeed;
    private int minY, maxY;

    public MovingGroundObstacle(double x, double y, int minY, int maxY, int initialMoveDir) {
        super(x, y, Constants.OBSTACLE_WIDTH, Constants.OBSTACLE_HEIGHT, Constants.OBSTACLE_GROUND_IMG);
        this.moveSpeed = Constants.OBSTACLE_MOVE_SPEED;
        this.minY = minY;
        this.maxY = maxY;
        this.moveDir = initialMoveDir;
    }

    @Override
    public void updateSpecific() {
        // Vertical movement
        this.y += moveDir * moveSpeed;
        if (this.y <= minY) {
            this.y = minY;
            moveDir = 1; // Move down
        } else if (this.y >= maxY) {
            this.y = maxY;
            moveDir = -1; // Move up
        }
    }
}
