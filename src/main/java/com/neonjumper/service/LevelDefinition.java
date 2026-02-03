package com.neonjumper.service;

import java.util.List;

public class LevelDefinition {
    public int id;
    public StartPosition start;
    public List<PlatformDef> platforms;
    public List<EntityDef> spikes;
    public EntityDef goal;

    public static class StartPosition {
        public double x, y;
    }

    public static class PlatformDef {
        public double x, y, w, h;
    }
    
    public static class EntityDef {
        public double x, y;
    }
}
