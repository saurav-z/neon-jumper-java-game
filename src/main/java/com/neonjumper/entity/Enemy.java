package com.neonjumper.entity;

import com.neonjumper.common.Vector2D;

public class Enemy extends GameObject {
    
    private double patrolSpeed = 100;
    
    public Enemy(Vector2D position) {
        super(position, 40, 40, Type.ENEMY);
        this.assetName = "enemy"; // Need to ensure AssetManager handles this
        this.velocity = new Vector2D(patrolSpeed, 0);
    }

    @Override
    public void update(double deltaTime) {
        // Simple Patrol Logic
        // Just move. Physics engine handles wall collisions.
        // We need to reverse if velocity becomes 0 (hit wall).
        
        if (Math.abs(velocity.x()) < 1.0) {
            patrolSpeed = -patrolSpeed;
            velocity = new Vector2D(patrolSpeed, velocity.y());
        }
        
        // Keep moving
        velocity = new Vector2D(patrolSpeed, velocity.y());
    }
}
