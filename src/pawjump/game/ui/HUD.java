package pawjump.game.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import pawjump.game.GameManager;
import pawjump.game.entities.Player;
import pawjump.game.utils.Constants;

public class HUD {
    private Image healthImage;

    public HUD() {
        this.healthImage = pawjump.game.utils.AssetLoader.loadImage(Constants.HEALTH_IMG);
    }

    public void draw(Graphics2D g2d, GameManager gameManager, Player player) {
        drawScoreAndLevelInfo(g2d, gameManager);
        drawHealthBar(g2d, player);
        drawLevelUpMessage(g2d, gameManager);
    }

    private void drawScoreAndLevelInfo(Graphics2D g2d, GameManager gameManager) {
        int scoreBoxX = 20;
        int scoreBoxY = 20;
        int scoreBoxW = 340;
        int scoreBoxH = 170; // Adjusted for more info

        // Save original composite
        java.awt.Composite oldComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f));
        g2d.setColor(new Color(20, 20, 30));
        g2d.fillRoundRect(scoreBoxX, scoreBoxY, scoreBoxW, scoreBoxH, 28, 28);
        g2d.setComposite(oldComposite); // Restore

        int textX = scoreBoxX + 18;
        int textY = scoreBoxY + 34;

        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Score: " + gameManager.getScore() + " m", textX, textY);

        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(new Color(255, 215, 0));
        g2d.drawString("Level: " + gameManager.getCurrentLevel(), textX, textY + 30);

        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        g2d.setColor(new Color(120, 255, 120));
        String speedText = String.format("Speed: %.1fx", gameManager.getSpeedMultiplier());
        g2d.drawString(speedText, textX, textY + 60 + 28); // Adjusted Y

        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        g2d.setColor(new Color(80, 200, 255));
        String fishText = String.format("Fish Collected: %d/%d", gameManager.getFishCollectedThisLevel(), Constants.FISH_NEEDED_PER_LEVEL);
        g2d.drawString(fishText, textX, textY + 88 + 28); // Adjusted Y
    }

    private void drawHealthBar(Graphics2D g2d, Player player) {
        int barWidth = Constants.PLAYER_MAX_HEALTH * 48;
        int barHeight = 60; 
        int rightMargin = 50;
        int healthBoxPadding = 30;
        int groupWidth = Math.max(barWidth, 180); 
        int groupX = Constants.SCREEN_WIDTH - groupWidth - rightMargin;
        int groupY = healthBoxPadding;

        // Save original composite
        java.awt.Composite oldComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        g2d.setColor(new Color(30, 30, 40));
        g2d.fillRoundRect(groupX - 16, groupY - 12, groupWidth + 32, barHeight + 44, 28, 28);
        g2d.setComposite(oldComposite); // Restore

        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.setColor(Color.WHITE);
        String healthLabel = "Remaining Health";
        FontMetrics fm = g2d.getFontMetrics();
        int labelWidth = fm.stringWidth(healthLabel);
        int labelX = groupX + (groupWidth - labelWidth) / 2;
        int labelY = groupY + 18;
        g2d.drawString(healthLabel, labelX, labelY);

        int heartsStartX = groupX + (groupWidth - barWidth) / 2;
        int heartsY = labelY + 8;
        for (int i = 0; i < Constants.PLAYER_MAX_HEALTH; i++) {
            int heartX = heartsStartX + i * 48;
            int heartYPos = heartsY;
            
            oldComposite = g2d.getComposite(); // Save before potential alpha change
            if (i < player.getHealth()) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            } else {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
            }
            if (healthImage != null) {
                 g2d.drawImage(healthImage, heartX, heartYPos, 40, 40, null);
            }
            g2d.setComposite(oldComposite); // Restore
        }
    }

    private void drawLevelUpMessage(Graphics2D g2d, GameManager gameManager) {
        if (gameManager.isLevelUpMessageActive()) {
            String levelUpMsg = "Congratulations! You reached level " + gameManager.getCurrentLevel() + "!";
            g2d.setFont(new Font("Arial", Font.BOLD, 36));
            FontMetrics fmLevelUp = g2d.getFontMetrics();
            int msgWidthLevelUp = fmLevelUp.stringWidth(levelUpMsg);
            int msgHeightLevelUp = fmLevelUp.getHeight();
            int xLevelUp = (Constants.SCREEN_WIDTH - msgWidthLevelUp) / 2;
            int yLevelUp = (Constants.SCREEN_HEIGHT - msgHeightLevelUp) / 2 + fmLevelUp.getAscent();

            int padX = 32, padY = 18;
            int bgXval = xLevelUp - padX;
            int bgYval = yLevelUp - fmLevelUp.getAscent() - padY / 2;
            int bgW = msgWidthLevelUp + padX * 2;
            int bgH = msgHeightLevelUp + padY;

            java.awt.Composite oldComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            g2d.setColor(new Color(30, 30, 30));
            g2d.fillRoundRect(bgXval, bgYval, bgW, bgH, 32, 32);
            g2d.setComposite(oldComposite);

            g2d.setColor(new Color(255, 215, 0));
            g2d.drawString(levelUpMsg, xLevelUp, yLevelUp);
        }
    }
}
