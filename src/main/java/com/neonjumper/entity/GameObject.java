package com.neonjumper.entity;

import com.neonjumper.common.Rect;
import com.neonjumper.common.Vector2D;
import com.neonjumper.core.RenderingEngine;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class GameObject implements RenderingEngine.Renderable {
    public enum Type { PLAYER, WALL, SPIKE, GOAL, ENEMY }
    
    protected Vector2D position;
    protected Vector2D velocity;
    protected double width;
    protected double height;
    protected String assetName;
    protected boolean isStatic;
    protected Type type;

    public GameObject(Vector2D position, double width, double height, Type type) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.type = type;
        this.velocity = new Vector2D(0, 0);
        this.isStatic = (type == Type.WALL || type == Type.SPIKE || type == Type.GOAL);
    }

    public abstract void update(double deltaTime);

    @Override
    public Rect getBounds() {
        return new Rect(position.x(), position.y(), width, height);
    }
    
    @Override
    public String getAssetName() {
        return assetName;
    }
}
