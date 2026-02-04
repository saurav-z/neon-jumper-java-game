package com.neonjumper.entity;

import com.neonjumper.common.Vector2D;

public class MovingPlatform extends GameObject {
    
    private final Vector2D startPos;
    private final Vector2D endPos;
    private final double speed;
    private double progress = 0;
    private boolean movingToEnd = true;

    public MovingPlatform(Vector2D startPos, double w, double h, Vector2D endPos, double speed) {
        super(startPos, w, h, Type.WALL); // It's still a WALL type for physics collision
        this.startPos = startPos;
        this.endPos = endPos;
        this.speed = speed;
        this.isStatic = true; // Still "static" for the physics engine's simplified collision
        this.assetName = "wall";
    }

    private double deltaX = 0;
    private double deltaY = 0;

    @Override
    public void update(double deltaTime) {
        double distance = startPos.distance(endPos);
        if (distance < 1) return;

        double prevX = position.x();
        double prevY = position.y();

        double moveStep = (speed * deltaTime) / distance;
        
        if (movingToEnd) {
            progress += moveStep;
            if (progress >= 1.0) {
                progress = 1.0;
                movingToEnd = false;
            }
        } else {
            progress -= moveStep;
            if (progress <= 0) {
                progress = 0;
                movingToEnd = true;
            }
        }

        double newX = startPos.x() + (endPos.x() - startPos.x()) * progress;
        double newY = startPos.y() + (endPos.y() - startPos.y()) * progress;
        
        this.position = new Vector2D(newX, newY);
        this.deltaX = newX - prevX;
        this.deltaY = newY - prevY;
    }

    public double getDeltaX() { return deltaX; }
    public double getDeltaY() { return deltaY; }
}
