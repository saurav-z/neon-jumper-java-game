package com.neonjumper.core;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

import org.springframework.stereotype.Service;

@Service
public class GameLoop extends AnimationTimer {

    private GraphicsContext gc;
    private long lastTime = 0;
    
    private final com.neonjumper.core.PhysicsEngine physics;
    private final com.neonjumper.core.RenderingEngine renderer;
    private final com.neonjumper.service.LevelService levelService;
    private final com.neonjumper.core.Camera camera;
    private final com.neonjumper.service.ParticleService particleService;

    public GameLoop(com.neonjumper.core.PhysicsEngine physics, 
                   com.neonjumper.core.RenderingEngine renderer,
                   com.neonjumper.service.LevelService levelService,
                   com.neonjumper.core.Camera camera,
                   com.neonjumper.service.ParticleService particleService) {
        this.physics = physics;
        this.renderer = renderer;
        this.levelService = levelService;
        this.camera = camera;
        this.particleService = particleService;
    }

    public void setGraphicsContext(GraphicsContext gc) {
        this.gc = gc;
    }
    
    @Override
    public void start() {
        super.start();
    }

    /**
     * @deprecated Use setGraphicsContext() and start() instead.
     */
    public void start(GraphicsContext gc) {
        this.gc = gc;
        super.start();
    }

    @Override
    public void handle(long now) {
        if (lastTime == 0) {
            lastTime = now;
            return;
        }

        double deltaTime = (now - lastTime) / 1_000_000_000.0; // Seconds
        lastTime = now;
        
        // Cap deltaTime to avoid spiral of death
        if (deltaTime > 0.05) deltaTime = 0.05;

        update(deltaTime);
        render();
    }

    private void update(double deltaTime) {
        // Update all entities
        for (com.neonjumper.entity.GameObject obj : levelService.getObjects()) {
            obj.update(deltaTime);
        }
        
        // Run Physics
        physics.update(levelService.getObjects(), deltaTime);
        
        // Update Camera and Particles
        camera.update(deltaTime);
        particleService.update(deltaTime);

        if (levelService.getPlayer() != null) {
            camera.follow(levelService.getPlayer().getPosition(), 0.1);
        }
        
        // Handle Game Events (Damage, Goals, Pitfalls)
        levelService.checkLevelEvents();
    }

    private void render() {
        if (gc == null) return;
        
        renderer.render(gc, levelService.getObjects());
        particleService.render(gc, camera.getPosition());
        
        // Handle player flickering when invincible handled in RenderingEngine now for better control
        if (levelService.getPlayer() != null && levelService.getPlayer().isInvincible()) {
            if ((System.currentTimeMillis() / 100) % 2 == 0) {
              // Note: This is an overlay hack, better to handle in Renderer
              // But for simplicity, we just won't render the player in some frames
            }
        }
    }
}
