package pawjump;

import javax.swing.SwingUtilities;
import pawjump.game.GamePanel; // Import if MenuWindow directly instantiates GamePanel from another package (not the case here)
import pawjump.MenuWindow;     // Import MenuWindow

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuWindow menu = new MenuWindow();
            menu.setVisible(true);
        });
    }
}
