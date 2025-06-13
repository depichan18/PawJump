package pawjump.game.utils;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.ImageIcon;

public class AssetLoader {
    /**
     * Loads an image from the resources folder using the class loader.
     */
    public static Image loadImage(String path) {
        try {
            return new ImageIcon(AssetLoader.class.getResource(path)).getImage();
        } catch (Exception e) {
            System.err.println("Could not load image: " + path);
            return null;
        }
    }

    /**
     * Loads a text file from the resources folder as a String.
     */
    public static String loadTextResource(String path) {
        try (InputStream is = AssetLoader.class.getResourceAsStream(path);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString().trim();
        } catch (IOException | NullPointerException e) {
            System.err.println("Could not load text resource: " + path);
            return null;
        }
    }

    /**
     * Loads a text file from the resources folder as an InputStream (for reading/writing highscore).
     */
    public static InputStream getResourceAsStream(String path) {
        return AssetLoader.class.getResourceAsStream(path);
    }
}
