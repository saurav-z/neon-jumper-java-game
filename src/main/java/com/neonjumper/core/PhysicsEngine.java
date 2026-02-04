package com.neonjumper.core;

import com.neonjumper.common.Rect;
import com.neonjumper.common.Vector2D;
import com.neonjumper.entity.GameObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhysicsEngine {

    private static final double GRAVITY = 1500; // Pixels per second squared
    private final com.neonjumper.service.ParticleService particleService;
    private final Camera camera;

    public PhysicsEngine(com.neonjumper.service.ParticleService particleService, Camera camera) {
        this.particleService = particleService;
        this.camera = camera;
    }
    public void update(List<GameObject> objects, double deltaTime) {
        // 1. Apply Gravity & Velocity
        for (GameObject obj : objects) {
            if (obj.isStatic()) continue;

            // Apply Gravity
            Vector2D newVel = obj.getVelocity();
            if (obj.isAffectedByGravity()) {
                newVel = newVel.add(new Vector2D(0, GRAVITY * deltaTime));
                obj.setVelocity(newVel);
            }

            // Apply Velocity to Position (X axis first)
            double newX = obj.getPosition().x() + newVel.x() * deltaTime;
            obj.setPosition(new Vector2D(newX, obj.getPosition().y()));
            resolveCollisions(obj, objects, true); // Resolve X

            // Apply Velocity to Position (Y axis)
            double newY = obj.getPosition().y() + newVel.y() * deltaTime;
            obj.setPosition(new Vector2D(obj.getPosition().x(), newY));
            resolveCollisions(obj, objects, false); // Resolve Y
        }
    }

    private void resolveCollisions(GameObject obj, List<GameObject> objects, boolean checkX) {
        Rect myBounds = obj.getBounds();

        for (GameObject other : objects) {
            if (obj == other) continue;
            if (!other.isStatic()) continue; // Only collide with static for now

            if (myBounds.intersects(other.getBounds())) {
                Vector2D vel = obj.getVelocity();
                Vector2D pos = obj.getPosition();
                Rect otherBounds = other.getBounds();

                if (checkX) {
                    if (other.getType() == GameObject.Type.WALL || other.getType() == GameObject.Type.MOVING_PLATFORM) {
                        if (vel.x() > 0) {
                            double correctedX = otherBounds.x() - obj.getWidth();
                            obj.setPosition(new Vector2D(correctedX - 0.01, pos.y()));
                            obj.setVelocity(new Vector2D(0, vel.y()));
                        } else if (vel.x() < 0) {
                            double correctedX = otherBounds.x() + otherBounds.width();
                            obj.setPosition(new Vector2D(correctedX + 0.01, pos.y()));
                            obj.setVelocity(new Vector2D(0, vel.y()));
                        }
                    }
                } else {
                    if (other.getType() == GameObject.Type.WALL || other.getType() == GameObject.Type.MOVING_PLATFORM) {
                        if (vel.y() > 0) {
                            double correctedY = otherBounds.y() - obj.getHeight();
                            obj.setPosition(new Vector2D(pos.x(), correctedY - 0.01));
                            obj.setVelocity(new Vector2D(vel.x(), 0));
                            if (obj instanceof com.neonjumper.entity.Player) {
                                com.neonjumper.entity.Player p = (com.neonjumper.entity.Player) obj;
                                p.setOnGround(true);
                                
                                // Apply movement from platform
                                if (other instanceof com.neonjumper.entity.MovingPlatform) {
                                    com.neonjumper.entity.MovingPlatform mp = (com.neonjumper.entity.MovingPlatform) other;
                                    obj.setPosition(obj.getPosition().add(new Vector2D(mp.getDeltaX(), mp.getDeltaY())));
                                }
                            }
                        } else if (vel.y() < 0) {
                            double correctedY = otherBounds.y() + otherBounds.height();
                            obj.setPosition(new Vector2D(pos.x(), correctedY + 0.01));
                            obj.setVelocity(new Vector2D(vel.x(), 0));
                        }
                    }
                }
                
                // Specific Collision Events for Player
                if (obj instanceof com.neonjumper.entity.Player player) {
                    if ((other.getType() == GameObject.Type.SPIKE || 
                         other.getType() == GameObject.Type.ENEMY || 
                         other.getType() == GameObject.Type.MONSTER || 
                         other.getType() == GameObject.Type.BIRD) && !player.isInvincible()) {
                        player.takeDamage();
                        camera.shake(10, 0.4);
                        particleService.spawnExplosion(player.getPosition(), javafx.scene.paint.Color.RED, 15);
                    } else if (other.getType() == GameObject.Type.GOAL) {
                        obj.setAssetName("goal_activated"); // Visual feedback
                    }
                }

                // Refresh bounds for next check
                myBounds = obj.getBounds();
            }
        }
    }
}
