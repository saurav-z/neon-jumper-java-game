package com.neonjumper.core;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AssetManager {
    
    private final Map<String, Image> cache = new HashMap<>();

    public Image getAsset(String name) {
        if (!cache.containsKey(name)) {
            cache.put(name, generatePlaceholder(name));
        }
        return cache.get(name);
    }
    
    // Procedural "Neon" asset generation
    private Image generatePlaceholder(String name) {
        int w = 50;
        int h = 50;
        WritableImage image = new WritableImage(w, h);
        PixelWriter writer = image.getPixelWriter();
        
        Color color = switch (name) {
            case "player" -> Color.CYAN;
            case "wall" -> Color.MAGENTA;
            case "spike" -> Color.RED;
            case "enemy" -> Color.ORANGE;
            case "goal" -> Color.GREEN;
            case "goal_activated" -> Color.GOLD;
            default -> Color.WHITE;
        };
        
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (x == 0 || y == 0 || x == w - 1 || y == h - 1) {
                    writer.setColor(x, y, color);
                } else {
                    writer.setColor(x, y, color.deriveColor(0, 1, 1, 0.3));
                }
            }
        }
        return image;
    }
}
