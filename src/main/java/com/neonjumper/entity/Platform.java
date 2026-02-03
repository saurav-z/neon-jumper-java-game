package com.neonjumper.entity;

import com.neonjumper.common.Vector2D;

public class Platform extends GameObject {
    public Platform(Vector2D position, double width, double height) {
        super(position, width, height, Type.WALL);
        this.assetName = "wall";
    }

    @Override
    public void update(double deltaTime) {
        // Static
    }
}
