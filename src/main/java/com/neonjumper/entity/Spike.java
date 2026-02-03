package com.neonjumper.entity;

import com.neonjumper.common.Vector2D;

public class Spike extends GameObject {
    public Spike(Vector2D position) {
        super(position, 40, 40, Type.SPIKE);
        this.assetName = "spike";
    }

    @Override
    public void update(double deltaTime) {
        // Warning: Collision logic for hazards is currently in PhysicsEngine or needs to be here.
        // For simplicity, we can let PhysicsEngine detect it or checking in LevelService?
        // Actually, PhysicsEngine handles checking collisions. 
        // We should add a Type or Tag to GameObject to know IF it kills.
    }
}
