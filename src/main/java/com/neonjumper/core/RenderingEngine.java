package com.neonjumper.core;

import com.neonjumper.common.Rect;
import com.neonjumper.common.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.effect.Glow;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RenderingEngine {

    private final Camera camera;
    private final AssetManager assetManager;

    public RenderingEngine(Camera camera, AssetManager assetManager) {
        this.camera = camera;
        this.assetManager = assetManager;
    }

    public void render(GraphicsContext gc, List<? extends Renderable> entities) {
        Vector2D camPos = camera.getPosition();
        
        // Background - Parallax could go here
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.setFill(Color.web("#1a1a2e")); 
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        
        // Grid lines for "Synthwave" feel
        drawBackgroundGrid(gc, camPos);

        for (Renderable entity : entities) {
            Vector2D pos = entity.getPosition().subtract(camPos);
            Rect bounds = entity.getBounds();
            
            // Draw asset if available, else simple rect
            String assetName = entity.getAssetName();
            if (entity instanceof com.neonjumper.entity.Player player) {
                gc.setFill(player.getSkin() == com.neonjumper.entity.Player.Skin.SPHERE ? Color.CYAN : Color.WHITE);
                if (player.getSkin() == com.neonjumper.entity.Player.Skin.SPHERE) {
                    gc.fillOval(pos.x(), pos.y(), bounds.width(), bounds.height());
                } else {
                    gc.fillRect(pos.x(), pos.y(), bounds.width(), bounds.height());
                }
            } else if (entity.getType() == com.neonjumper.entity.GameObject.Type.BIRD) {
                gc.setFill(Color.YELLOW);
                gc.fillOval(pos.x(), pos.y(), bounds.width(), bounds.height());
                // Simple wings
                double wingY = Math.sin(System.currentTimeMillis() * 0.02) * 10;
                gc.strokeLine(pos.x(), pos.y() + bounds.height()/2, pos.x() - 10, pos.y() + bounds.height()/2 - wingY);
                gc.strokeLine(pos.x() + bounds.width(), pos.y() + bounds.height()/2, pos.x() + bounds.width() + 10, pos.y() + bounds.height()/2 - wingY);
            } else if (entity.getType() == com.neonjumper.entity.GameObject.Type.MONSTER) {
                gc.setFill(Color.DARKRED);
                gc.fillRect(pos.x(), pos.y(), bounds.width(), bounds.height());
                // Eyes
                gc.setFill(Color.YELLOW);
                gc.fillOval(pos.x() + 10, pos.y() + 10, 8, 8);
                gc.fillOval(pos.x() + bounds.width() - 18, pos.y() + 10, 8, 8);
            } else if (entity.getType() == com.neonjumper.entity.GameObject.Type.COIN) {
                double time = System.currentTimeMillis() * 0.005;
                double bob = Math.sin(time * 2) * 5;
                double spin = Math.sin(time * 3); // -1 to 1 for 3D spin effect
                
                gc.save();
                gc.translate(pos.x() + bounds.width()/2, pos.y() + bounds.height()/2 + bob);
                gc.scale(Math.abs(spin), 1.0);
                
                gc.setEffect(new Glow(0.8));
                gc.setFill(Color.GOLD);
                gc.fillOval(-bounds.width()/2, -bounds.height()/2, bounds.width(), bounds.height());
                
                // Rim highlight
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(2);
                gc.strokeOval(-bounds.width()/2, -bounds.height()/2, bounds.width(), bounds.height());
                
                gc.restore();
            } else if (entity.getType() == com.neonjumper.entity.GameObject.Type.MOVING_PLATFORM) {
                gc.save();
                gc.setEffect(new Glow(0.3));
                gc.setFill(Color.web("#ff00ff")); // Distinct pinkish for moving
                gc.fillRect(pos.x(), pos.y(), bounds.width(), bounds.height());
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(1);
                gc.strokeRect(pos.x(), pos.y(), bounds.width(), bounds.height());
                gc.restore();
            } else if (assetName != null) {
                Image img = assetManager.getAsset(assetName);
                gc.drawImage(img, pos.x(), pos.y(), bounds.width(), bounds.height());
            } else {
                gc.setFill(Color.WHITE);
                gc.fillRect(pos.x(), pos.y(), bounds.width(), bounds.height());
            }
        }
    }
    
    private void drawBackgroundGrid(GraphicsContext gc, Vector2D camPos) {
        gc.setStroke(Color.web("#2e2e4a"));
        gc.setLineWidth(2);
        
        double gridSize = 100;
        double offsetX = camPos.x() % gridSize;
        double offsetY = camPos.y() % gridSize;
        
        for (double x = -offsetX; x < gc.getCanvas().getWidth(); x += gridSize) {
            gc.strokeLine(x, 0, x, gc.getCanvas().getHeight());
        }
        for (double y = -offsetY; y < gc.getCanvas().getHeight(); y += gridSize) {
            gc.strokeLine(0, y, gc.getCanvas().getWidth(), y);
        }
    }

    public interface Renderable {
        Vector2D getPosition();
        Rect getBounds();
        String getAssetName();
        com.neonjumper.entity.GameObject.Type getType();
    }
}
