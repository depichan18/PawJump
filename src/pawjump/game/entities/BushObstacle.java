package pawjump.game.entities;

import pawjump.game.utils.Constants;
import java.awt.Graphics2D;

public class BushObstacle extends Obstacle {
    public BushObstacle(double x, double y) {
        super(x, y, Constants.OBSTACLE_WIDTH, Constants.OBSTACLE_HEIGHT, Constants.OBSTACLE_BUSH_IMG);
    }

    @Override
    public void updateSpecific() {
        // Static, no specific update needed beyond what Obstacle.move() does
    }
}
