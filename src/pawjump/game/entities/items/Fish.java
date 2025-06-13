package pawjump.game.entities.items;

import pawjump.game.entities.Player;
import pawjump.game.GameManager;
import pawjump.game.utils.AudioManager;
import pawjump.game.utils.Constants;

public class Fish extends Item {
    public Fish(double x, double y) {
        super(x, y, Constants.ITEM_WIDTH, Constants.ITEM_HEIGHT, Constants.ITEM_FISH_IMG);
    }

    @Override
    public void applyEffect(Player player, GameManager gameManager) {
        if (!isCollected()) {
            AudioManager.getInstance().playFishSound(); // Play fish collection sound
            gameManager.collectFish();
            player.startAttackAnimation(); // Player performs attack animation on collection
            setCollected(true);
        }
    }
}
