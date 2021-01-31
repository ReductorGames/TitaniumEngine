package com.reductorgames.titaniumengine.particles;

import com.reductorgames.titaniumengine.entities.Camera;
import com.reductorgames.titaniumengine.renderEngine.Loader;
import org.lwjgl.util.vector.Matrix4f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParticleMaster {

    private static List<Particle> particles = new ArrayList<Particle>();
    private static ParticleRenderer renderer;

    public static void init(Loader loader, Matrix4f projectionMatrix) {
        renderer = new ParticleRenderer(loader, projectionMatrix);
    }

    public static void update() {
        Iterator<Particle> iterator = particles.iterator();
        while(iterator.hasNext()) {
            Particle p = iterator.next();
            boolean stillAlive = p.update();
            if(!stillAlive) {
                iterator.remove();
            }
        }
    }

    public static void renderParticles(Camera camera) {
        renderer.render(particles, camera);
    }

    public static void cleanUp() {
        renderer.cleanUp();
    }

    public static void addParticle(Particle particle) {
        particles.add(particle);
    }
}