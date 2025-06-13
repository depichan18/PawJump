package pawjump;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.*;
import pawjump.game.GamePanel;

public class MenuWindow extends JFrame {
    public MenuWindow() {
        setTitle("Paw Jump!");
        setSize(520, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(30, 30, 60));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(null);

        final ImageIcon logoIcon = new ImageIcon(pawjump.game.utils.AssetLoader.class.getResource("/resources/images/pawjump.png"));
        final int logoW = logoIcon.getIconWidth();
        final int logoH = logoIcon.getIconHeight();
        final int maxLogoW = 440;
        final int maxLogoH = 160;
        final int[] drawSize = new int[2];
        drawSize[0] = logoW;
        drawSize[1] = logoH;
        if (logoW > maxLogoW || logoH > maxLogoH) {
            double scaleW = maxLogoW / (double)logoW;
            double scaleH = maxLogoH / (double)logoH;
            double scale = Math.min(scaleW, scaleH);
            drawSize[0] = (int)(logoW * scale);
            drawSize[1] = (int)(logoH * scale);
        }
        final int drawW = drawSize[0];
        final int drawH = drawSize[1];
        JLabel logoLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.drawImage(logoIcon.getImage(), 0, 0, drawW, drawH, this);
                g2.dispose();
            }
        };
        logoLabel.setBounds((520 - drawW) / 2, 60, drawW, drawH);
        panel.add(logoLabel);

        JButton playBtn = new JButton("Play");
        playBtn.setFont(new Font("Arial", Font.BOLD, 32));
        playBtn.setBackground(new Color(60, 180, 255));
        playBtn.setForeground(Color.WHITE);
        playBtn.setFocusPainted(false);
        playBtn.setBounds(160, 260, 200, 70);
        playBtn.addActionListener(e -> {
            JFrame gameFrame = new JFrame("Paw Jump!");
            gameFrame.setSize(1280, 720);
            gameFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
            gameFrame.setResizable(false);
            GamePanel panelGame = new GamePanel(); // GamePanel is now in pawjump.game package
            gameFrame.add(panelGame);
            gameFrame.setLocationRelativeTo(null);
            this.dispose();
            gameFrame.setVisible(true);
            panelGame.requestFocusForGame(); // Changed to a new method in GamePanel
        });
        panel.add(playBtn);

        int highScoreValue = 0;
        try (InputStream is = pawjump.game.utils.AssetLoader.getResourceAsStream(pawjump.game.utils.Constants.HIGHSCORE_FILE_PATH);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            highScoreValue = Integer.parseInt(br.readLine());
        } catch (Exception e) {
            highScoreValue = 0;
        }
        JLabel highScoreLabel = new JLabel("High Score: " + highScoreValue + " m", SwingConstants.CENTER);
        highScoreLabel.setFont(new Font("Arial", Font.BOLD, 26));
        highScoreLabel.setForeground(new Color(255, 215, 0));
        highScoreLabel.setBounds(0, 350, 520, 40);
        panel.add(highScoreLabel);

        setContentPane(panel);
    }
}
