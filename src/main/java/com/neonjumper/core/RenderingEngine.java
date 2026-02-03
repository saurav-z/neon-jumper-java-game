package com.neonjumper.core;

import com.neonjumper.common.Rect;
import com.neonjumper.common.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
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
            if (entity instanceof com.neonjumper.entity.Player player) {
                if (player.isInvincible() && (System.currentTimeMillis() / 100) % 2 == 0) {
                    continue; // Skip rendering for flicker
                }
            }

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
    }
}
