package pawjump.game.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import pawjump.game.GameManager; // To get scores and tell it to restart/exit

public class GameOverScreen {
    private JDialog gameOverDialog;
    private GameManager gameManager; // To communicate restart/exit actions

    public GameOverScreen(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void show(JFrame parentFrame) {
        if (gameOverDialog != null && gameOverDialog.isVisible()) {
            return;
        }

        gameOverDialog = new JDialog(parentFrame, "Game Over", true);
        gameOverDialog.setSize(420, 300);
        gameOverDialog.setLocationRelativeTo(parentFrame);
        gameOverDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Dispose only
        gameOverDialog.setResizable(false);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(30, 30, 60));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(null);

        JLabel title = new JLabel("GAME OVER", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setForeground(new Color(220, 40, 40));
        title.setBounds(0, 20, 420, 50);
        panel.add(title);

        JLabel scoreLabel = new JLabel("Score: " + gameManager.getScore() + " m", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 28));
        scoreLabel.setForeground(new Color(255, 215, 0));
        scoreLabel.setBounds(0, 70, 420, 40);
        panel.add(scoreLabel);

        JLabel levelLabel = new JLabel("Level: " + gameManager.getCurrentLevel(), SwingConstants.CENTER);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 24));
        levelLabel.setForeground(new Color(120, 255, 255));
        levelLabel.setBounds(0, 110, 420, 32);
        panel.add(levelLabel);

        JLabel highScoreLabelVal = new JLabel("High Score: " + gameManager.getHighScore() + " m", SwingConstants.CENTER);
        highScoreLabelVal.setFont(new Font("Arial", Font.BOLD, 22));
        highScoreLabelVal.setForeground(new Color(100, 255, 100));
        highScoreLabelVal.setBounds(0, 150, 420, 30);
        panel.add(highScoreLabelVal);

        JButton retryBtn = new JButton("Retry");
        retryBtn.setFont(new Font("Arial", Font.BOLD, 22));
        retryBtn.setBackground(new Color(60, 180, 255));
        retryBtn.setForeground(Color.WHITE);
        retryBtn.setFocusPainted(false);
        retryBtn.setBounds(60, 200, 120, 50); // Adjusted Y
        retryBtn.addActionListener(e -> {
            gameOverDialog.dispose();
            gameManager.restartGame();
        });
        panel.add(retryBtn);

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 22));
        closeBtn.setBackground(new Color(220, 40, 40));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBounds(240, 200, 120, 50); // Adjusted Y
        closeBtn.addActionListener(e -> {
            // gameOverDialog.dispose(); // Dialog will dispose anyway
            System.exit(0); // Close the application
        });
        panel.add(closeBtn);

        gameOverDialog.setContentPane(panel);
        gameOverDialog.setVisible(true);
    }
    
    public boolean isVisible() {
        return gameOverDialog != null && gameOverDialog.isVisible();
    }
}
