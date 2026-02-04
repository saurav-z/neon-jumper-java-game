package com.neonjumper.entity;

import com.neonjumper.common.Vector2D;

public class Bird extends GameObject {
    
    private double speed;
    
    public Bird(Vector2D position, boolean fromLeft) {
        super(position, 30, 20, Type.BIRD);
        this.assetName = "bird";
        this.isStatic = false;
        this.speed = fromLeft ? 300 : -300;
        this.velocity = new Vector2D(speed, 0);
    }

    @Override
    public void update(double deltaTime) {
        // Birds fly in a sine wave pattern
        velocity = new Vector2D(speed, Math.cos(System.currentTimeMillis() * 0.005) * 100); 
    }
    
    // Override gravity for birds
    public boolean isAffectedByGravity() {
        return false;
    }
}
