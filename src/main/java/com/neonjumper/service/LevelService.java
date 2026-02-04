package com.neonjumper.service;

import com.neonjumper.common.Vector2D;
import com.neonjumper.entity.GameObject;
import com.neonjumper.entity.Platform;
import com.neonjumper.entity.Player;
import com.neonjumper.entity.Enemy;
import com.neonjumper.entity.Monster;
import com.neonjumper.entity.Bird;
import com.neonjumper.entity.Spike;
import com.neonjumper.entity.Goal;
import com.neonjumper.entity.Coin;
import com.neonjumper.entity.MovingPlatform;
import com.neonjumper.core.Camera;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
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
    private long lastHazardTime = 0;

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
            if (def == null) throw new Exception("LevelDefinition is null");
            System.out.println("DEBUG: Successfully parsed Level: " + def.name);
            
            player = new Player(new Vector2D(def.start.x, def.start.y), inputManager, particleService);
            player.setSkin(selectedSkin);
            objects.add(player);
            
            if (def.platforms != null) {
                System.out.println("DEBUG: Loading " + def.platforms.size() + " platforms.");
                for (LevelDefinition.PlatformDef p : def.platforms) {
                    objects.add(new Platform(new Vector2D(p.x, p.y), p.w, p.h));
                }
            }
            
            if (def.spikes != null) {
                System.out.println("DEBUG: Loading " + def.spikes.size() + " spikes.");
                for (LevelDefinition.EntityDef s : def.spikes) {
                    objects.add(new Spike(new Vector2D(s.x, s.y)));
                }
            }
            
            if (def.goal != null) {
                objects.add(new Goal(new Vector2D(def.goal.x, def.goal.y)));
            }

            if (def.enemies != null) {
                System.out.println("DEBUG: Loading " + def.enemies.size() + " enemies.");
                for (LevelDefinition.EntityDef e : def.enemies) {
                    objects.add(new Enemy(new Vector2D(e.x, e.y)));
                }
            }

            if (def.monsters != null) {
                System.out.println("DEBUG: Loading " + def.monsters.size() + " monsters.");
                for (LevelDefinition.EntityDef m : def.monsters) {
                    objects.add(new Monster(new Vector2D(m.x, m.y)));
                }
            }
            
            if (def.coins != null) {
                System.out.println("DEBUG: Loading " + def.coins.size() + " coins.");
                for (LevelDefinition.EntityDef c : def.coins) {
                    objects.add(new Coin(new Vector2D(c.x, c.y)));
                }
            }

            if (def.movingPlatforms != null) {
                System.out.println("DEBUG: Loading " + def.movingPlatforms.size() + " moving platforms.");
                for (LevelDefinition.MovingPlatformDef mp : def.movingPlatforms) {
                    objects.add(new MovingPlatform(
                        new Vector2D(mp.x, mp.y), mp.w, mp.h, 
                        new Vector2D(mp.targetX, mp.targetY), mp.speed));
                }
            }
            
            System.out.println("DEBUG: Level " + levelId + " fully loaded with " + objects.size() + " objects.");
            camera.follow(player.getPosition(), 1.0);
            
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: Failed to load level " + levelId + ". Reason: " + e.getMessage());
            e.printStackTrace();
            loadFallbackLevel();
        }
    }
    
    public void checkLevelEvents() {
        if (player == null) return;
        
        checkRandomHazards();

        // Cleanup offscreen objects (like birds)
        Iterator<GameObject> it = objects.iterator();
        while (it.hasNext()) {
            GameObject obj = it.next();
            if (obj.getType() == GameObject.Type.BIRD) {
                if (obj.getPosition().x() < -2000 || obj.getPosition().x() > 5000) {
                    it.remove();
                }
            }
        }

        // Check Goal & Coins
        for (int i = 0; i < objects.size(); i++) {
            GameObject obj = objects.get(i);
            if (obj.getType() == GameObject.Type.GOAL && player.getBounds().intersects(obj.getBounds())) {
                nextLevel();
                return;
            }
            if (obj.getType() == GameObject.Type.COIN && player.getBounds().intersects(obj.getBounds())) {
                player.addScore(10);
                particleService.spawnExplosion(obj.getPosition(), javafx.scene.paint.Color.GOLD, 5);
                objects.remove(i);
                i--;
            }
        }
        
        // Check Death
        if (player.getLives() <= 0 || player.getPosition().y() > 1500) {
            resetLevel();
        }
    }

    private void checkRandomHazards() {
        long now = System.currentTimeMillis();
        if (now - lastHazardTime > 5000) { // Every 5 seconds
            if (Math.random() < 0.3) { // 30% chance
                spawnBird();
                lastHazardTime = now;
            }
        }
    }

    private void spawnBird() {
        boolean fromLeft = Math.random() > 0.5;
        double startX = fromLeft ? player.getPosition().x() - 800 : player.getPosition().x() + 800;
        double startY = player.getPosition().y() - 100 - Math.random() * 200;
        objects.add(new Bird(new Vector2D(startX, startY), fromLeft));
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
        player.setSkin(selectedSkin); // FIX: Set skin in fallback!
        objects.add(player);
        
        // Add a goal so the level isn't "endless"
        objects.add(new Goal(new Vector2D(1800, 550)));
    }
}
