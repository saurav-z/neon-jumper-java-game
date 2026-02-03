package com.neonjumper.service;

import com.neonjumper.common.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Service
public class ParticleService {

    private static class Particle {
        Vector2D pos;
        Vector2D vel;
        double life;
        double maxLife;
        Color color;
        double size;

        Particle(Vector2D pos, Vector2D vel, double life, Color color, double size) {
            this.pos = pos;
            this.vel = vel;
            this.life = life;
            this.maxLife = life;
            this.color = color;
            this.size = size;
        }

        void update(double dt) {
            pos = pos.add(vel.multiply(dt));
            life -= dt;
        }

        void render(GraphicsContext gc, Vector2D cameraPos) {
            double opacity = life / maxLife;
            gc.setGlobalAlpha(opacity);
            gc.setFill(color);
            gc.fillOval(pos.x() - cameraPos.x(), pos.y() - cameraPos.y(), size, size);
            gc.setGlobalAlpha(1.0);
        }
    }

    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();

    public void spawnExplosion(Vector2D pos, Color color, int count) {
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 50 + random.nextDouble() * 150;
            Vector2D vel = new Vector2D(Math.cos(angle) * speed, Math.sin(angle) * speed);
            particles.add(new Particle(pos, vel, 0.5 + random.nextDouble() * 0.5, color, 2 + random.nextDouble() * 4));
        }
    }

    public void spawnDust(Vector2D pos, Color color, int count) {
        for (int i = 0; i < count; i++) {
            Vector2D vel = new Vector2D((random.nextDouble() - 0.5) * 50, -random.nextDouble() * 30);
            particles.add(new Particle(pos, vel, 0.3 + random.nextDouble() * 0.3, color, 1 + random.nextDouble() * 2));
        }
    }

    public void update(double dt) {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.update(dt);
            if (p.life <= 0) it.remove();
        }
    }

    public void render(GraphicsContext gc, Vector2D cameraPos) {
        for (Particle p : particles) {
            p.render(gc, cameraPos);
        }
    }
}
