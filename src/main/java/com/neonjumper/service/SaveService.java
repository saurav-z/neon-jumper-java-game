package com.neonjumper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class SaveService {

    private static final String SAVE_FILE = "save_data.json";
    private final ObjectMapper mapper = new ObjectMapper();
    private SaveData data = new SaveData();

    public static class SaveData {
        public int unlockedLevels = 1;
        public int totalStars = 0;
        public Map<Integer, Integer> levelStars = new HashMap<>();
    }

    public SaveService() {
        load();
    }

    public void save() {
        try {
            mapper.writeValue(new File(SAVE_FILE), data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            File file = new File(SAVE_FILE);
            if (file.exists()) {
                data = mapper.readValue(file, SaveData.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getUnlockedLevels() {
        return data.unlockedLevels;
    }

    public int getStarsForLevel(int levelId) {
        return data.levelStars.getOrDefault(levelId, 0);
    }

    public int getTotalStars() {
        return data.totalStars;
    }

    public void updateLevelProgress(int levelId, int stars) {
        int oldStars = data.levelStars.getOrDefault(levelId, 0);
        if (stars > oldStars) {
            data.levelStars.put(levelId, stars);
            data.totalStars += (stars - oldStars);
        }
        
        if (levelId == data.unlockedLevels && levelId < 20) {
            data.unlockedLevels++;
        }
        save();
    }
}
