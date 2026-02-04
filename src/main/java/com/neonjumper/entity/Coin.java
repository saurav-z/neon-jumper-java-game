package com.neonjumper.entity;

import com.neonjumper.common.Vector2D;

public class Coin extends GameObject {
    
    public Coin(Vector2D position) {
        super(position, 25, 25, Type.COIN);
        this.assetName = "coin";
        this.isStatic = true;
    }

    @Override
    public void update(double deltaTime) {
        // Coins could rotate or bob up and down visually in RenderingEngine
    }
}
