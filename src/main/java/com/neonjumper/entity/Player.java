package com.neonjumper.entity;

import com.neonjumper.common.Vector2D;
import com.neonjumper.service.InputManager;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

public class Player extends GameObject {

    private final InputManager input;
    private final com.neonjumper.service.ParticleService particleService;
    private static final double MOVE_SPEED = 400;
    private static final double JUMP_FORCE = -850;
    
    @Getter
    private int lives = 3;
    private double invincibilityTimer = 0;
    private boolean onGround = false; 
    
    @Getter
    private double nitroFuel = 1.0; // 0.0 to 1.0
    private static final double NITRO_CONSUMPTION = 0.4; // per second
    private static final double NITRO_RECOVERY = 0.15; // per second
    private boolean isBoosting = false;

    @Getter
    private int score = 0;

    public enum Skin { CUBE, SPHERE }
    
    @Getter @Setter
    private Skin skin = Skin.CUBE;

    public Player(Vector2D position, InputManager input, com.neonjumper.service.ParticleService particleService) {
        super(position, 40, 40, Type.PLAYER);
        this.input = input;
        this.particleService = particleService;
        this.assetName = "player";
    }

    @Override
    public void update(double deltaTime) {
        if (invincibilityTimer > 0) {
            invincibilityTimer -= deltaTime;
        }

        // Nitro Boost
        isBoosting = input.isKeyPressed(KeyCode.SHIFT) && nitroFuel > 0.1;
        double currentSpeed = MOVE_SPEED;
        
        if (isBoosting) {
            currentSpeed *= 2.0;
            nitroFuel -= NITRO_CONSUMPTION * deltaTime;
            if (nitroFuel < 0) {
                nitroFuel = 0;
                isBoosting = false;
            }
            // Blue nitro particles
            particleService.spawnDust(position.add(new Vector2D(width/2, height/2)), Color.BLUE, 2);
        } else {
            nitroFuel += NITRO_RECOVERY * deltaTime;
            if (nitroFuel > 1.0) nitroFuel = 1.0;
        }

        // Horizontal Movement
        double dx = 0;
        if (input.isKeyPressed(KeyCode.LEFT) || input.isKeyPressed(KeyCode.A)) {
            dx -= currentSpeed;
        }
        if (input.isKeyPressed(KeyCode.RIGHT) || input.isKeyPressed(KeyCode.D)) {
            dx += currentSpeed;
        }
        
        this.velocity = new Vector2D(dx, this.velocity.y());
        
        // Jump
        if ((input.isKeyPressed(KeyCode.SPACE) || input.isKeyPressed(KeyCode.W)) && onGround) {
             this.velocity = new Vector2D(this.velocity.x(), JUMP_FORCE);
             onGround = false;
             particleService.spawnDust(position.add(new Vector2D(width/2, height)), javafx.scene.paint.Color.LIGHTGRAY, 10);
        }

        // Comet trail for Sphere
        if (skin == Skin.SPHERE && velocity.length() > 10) {
            particleService.spawnDust(position.add(new Vector2D(width/2, height/2)), Color.CYAN, 1);
        }
    }
    
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void takeDamage() {
        if (invincibilityTimer > 0) return;
        lives--;
        invincibilityTimer = 2.0; // 2 seconds of invincibility
    }

    public boolean isInvincible() {
        return invincibilityTimer > 0;
    }
    
    public void resetLives() {
        this.lives = 3;
    }

    public void addScore(int points) {
        this.score += points;
    }
}
