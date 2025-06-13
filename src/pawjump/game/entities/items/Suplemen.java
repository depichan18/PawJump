package pawjump.game.entities.items;

import pawjump.game.entities.Player;
import pawjump.game.GameManager;
import pawjump.game.utils.AudioManager;
import pawjump.game.utils.Constants;

public class Suplemen extends Item {
    public Suplemen(double x, double y) {
        super(x, y, Constants.ITEM_WIDTH, Constants.ITEM_HEIGHT, Constants.ITEM_SUPLEMEN_IMG);
    }

    @Override
    public void applyEffect(Player player, GameManager gameManager) {
        if (!isCollected()) {
            AudioManager.getInstance().playSuplemenSound(); // Play suplemen collection sound
            player.activateSuplemenEffect();
            gameManager.itemCollected(this); // For GameManager to know suplemen was taken this phase
            setCollected(true);
        }
    }
}
