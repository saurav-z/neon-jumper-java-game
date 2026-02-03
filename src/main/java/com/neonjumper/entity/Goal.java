package com.neonjumper.entity;

import com.neonjumper.common.Vector2D;

public class Goal extends GameObject {
    public Goal(Vector2D position) {
        super(position, 50, 80, Type.GOAL); // Tall portal shape
        this.assetName = "goal";
    }

    @Override
    public void update(double deltaTime) {
        // Animation effects could go here
    }
}
