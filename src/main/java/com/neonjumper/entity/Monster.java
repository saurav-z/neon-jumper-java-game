package com.neonjumper.entity;

import com.neonjumper.common.Vector2D;

public class Monster extends GameObject {
    
    private double patrolSpeed = 150;
    
    public Monster(Vector2D position) {
        super(position, 60, 60, Type.MONSTER); // Larger than normal enemy
        this.assetName = "monster";
        this.velocity = new Vector2D(patrolSpeed, 0);
    }

    @Override
    public void update(double deltaTime) {
        // Patrol logic
        if (Math.abs(velocity.x()) < 1.0) {
            patrolSpeed = -patrolSpeed;
        }
        velocity = new Vector2D(patrolSpeed, velocity.y());
        
        // Maybe monsters jump occasionally?
        if (Math.random() < 0.01 && velocity.y() == 0) {
            velocity = new Vector2D(velocity.x(), -600);
        }
    }
}
