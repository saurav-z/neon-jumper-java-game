package com.neonjumper.service;

import com.neonjumper.common.Vector2D;
import com.neonjumper.entity.GameObject;
import com.neonjumper.entity.Platform;
import com.neonjumper.entity.Player;
import com.neonjumper.core.Camera;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LevelService {

    @Getter
    private List<GameObject> objects = new ArrayList<>();
    
    @Getter
    private int currentLevelId = 1;

    @Getter
    private Player player;
    
    private java.util.function.Consumer<Integer> levelChangeListener;
    
    private final InputManager inputManager;
    private final Camera camera;
    private final ParticleService particleService;
    private final SaveService saveService;
    private final com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    
    private int deathsThisLevel = 0;
    private com.neonjumper.entity.Player.Skin selectedSkin = com.neonjumper.entity.Player.Skin.CUBE;

    public LevelService(InputManager inputManager, Camera camera, ParticleService particleService, SaveService saveService) {
        this.inputManager = inputManager;
        this.camera = camera;
        this.particleService = particleService;
        this.saveService = saveService;
    }

    public void setLevelChangeListener(java.util.function.Consumer<Integer> listener) {
        this.levelChangeListener = listener;
    }
    
    public void startNewGame() {
        currentLevelId = 1;
        deathsThisLevel = 0;
        loadLevel(currentLevelId);
        if (player != null) player.resetLives();
    }
    
    public void loadLevel(int levelId) {
        this.currentLevelId = levelId;
        objects.clear();
        try {
            java.io.InputStream is = getClass().getResourceAsStream("/levels/level" + levelId + ".json");
            if (is == null) {
                System.err.println("Level " + levelId + " not found. Returning to menu or restart.");
                return;
            }
// ... [rest of the loading logic is fine, just ensure it uses def]
            
            LevelDefinition def = mapper.readValue(is, LevelDefinition.class);
            
            player = new Player(new Vector2D(def.start.x, def.start.y), inputManager, particleService);
            player.setSkin(selectedSkin);
            objects.add(player);
            
            if (def.platforms != null) {
                for (LevelDefinition.PlatformDef p : def.platforms) {
                    objects.add(new Platform(new Vector2D(p.x, p.y), p.w, p.h));
                }
            }
            
            if (def.spikes != null) {
                for (LevelDefinition.EntityDef s : def.spikes) {
                    objects.add(new com.neonjumper.entity.Spike(new Vector2D(s.x, s.y)));
                }
            }
            
            if (def.goal != null) {
                objects.add(new com.neonjumper.entity.Goal(new Vector2D(def.goal.x, def.goal.y)));
            }
            
            camera.follow(player.getPosition(), 1.0);
            
        } catch (Exception e) {
            e.printStackTrace();
            loadFallbackLevel();
        }
    }
    
    public void checkLevelEvents() {
        if (player == null) return;
        
        // Check Goal
        for (GameObject obj : objects) {
            if (obj.getType() == GameObject.Type.GOAL && player.getBounds().intersects(obj.getBounds())) {
                nextLevel();
                return;
            }
        }
        
        // Check Death
        if (player.getLives() <= 0 || player.getPosition().y() > 1500) {
            resetLevel();
        }
    }

    private void nextLevel() {
        int stars = calculateStars();
        saveService.updateLevelProgress(currentLevelId, stars);
        currentLevelId++;
        deathsThisLevel = 0;
        loadLevel(currentLevelId);
        if (levelChangeListener != null) {
            levelChangeListener.accept(currentLevelId);
        }
    }
    
    private int calculateStars() {
        if (deathsThisLevel == 0) return 3;
        if (deathsThisLevel <= 2) return 2;
        return 1;
    }

    public void setSkin(com.neonjumper.entity.Player.Skin skin) {
        this.selectedSkin = skin;
        if (player != null) player.setSkin(skin);
    }

    public void resetLevel() {
        deathsThisLevel++;
        loadLevel(currentLevelId);
        if (player != null) player.resetLives();
    }
    
    private void loadFallbackLevel() {
        // Floor
        for (int i = 0; i < 20; i++) {
            objects.add(new Platform(new Vector2D(i * 100, 600), 100, 50));
        }
        player = new Player(new Vector2D(100, 500), inputManager, particleService);
        objects.add(player);
    }
}
