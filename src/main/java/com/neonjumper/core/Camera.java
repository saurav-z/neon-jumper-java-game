package com.neonjumper.core;

import com.neonjumper.common.Vector2D;
import org.springframework.stereotype.Component;

@Component
public class Camera {
    private Vector2D position = new Vector2D(0, 0);
    private Vector2D offset = new Vector2D(0, 0);
    private double shakeIntensity = 0;
    private double shakeTime = 0;
    private final java.util.Random random = new java.util.Random();
    
    private final double viewportWidth = 1280;
    private final double viewportHeight = 720;

    public void update(double dt) {
        if (shakeTime > 0) {
            shakeTime -= dt;
            double x = (random.nextDouble() - 0.5) * 2 * shakeIntensity;
            double y = (random.nextDouble() - 0.5) * 2 * shakeIntensity;
            offset = new Vector2D(x, y);
        } else {
            offset = new Vector2D(0, 0);
        }
    }

    public void shake(double intensity, double duration) {
        this.shakeIntensity = intensity;
        this.shakeTime = duration;
    }

    public void follow(Vector2D target, double smoothing) {
        // ...
        // Simple Lerp
        double targetX = target.x() - viewportWidth / 2;
        double targetY = target.y() - viewportHeight / 2;
        
        double newX = position.x() + (targetX - position.x()) * smoothing;
        double newY = position.y() + (targetY - position.y()) * smoothing;
        
        this.position = new Vector2D(newX, newY);
    }

    public Vector2D getPosition() {
        return position.add(offset);
    }
}
