package pawjump.game.entities.items;

import pawjump.game.entities.Player;
import pawjump.game.GameManager;
import pawjump.game.utils.AudioManager;
import pawjump.game.utils.Constants;

public class Meat extends Item {
    public Meat(double x, double y) {
        super(x, y, Constants.ITEM_WIDTH, Constants.ITEM_HEIGHT, Constants.ITEM_MEAT_IMG);
    }

    @Override
    public void applyEffect(Player player, GameManager gameManager) {
        if (!isCollected()) {
            AudioManager.getInstance().playMeatSound(); // Play meat collection sound
            player.activateMeatEffect();
            // GameManager might track meat phase if needed for respawn logic
            gameManager.itemCollected(this);
            setCollected(true);
        }
    }
}
