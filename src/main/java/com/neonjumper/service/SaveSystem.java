package com.neonjumper.service;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

@Service
public class SaveSystem {

    private final File saveFile = new File("save.properties");
    private final Properties props = new Properties();

    public SaveSystem() {
        load();
    }

    public void save(int level) {
        try {
            props.setProperty("level", String.valueOf(level));
            props.store(new FileOutputStream(saveFile), "Neon Jumper Save Data");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int loadLevel() {
        return Integer.parseInt(props.getProperty("level", "1"));
    }

    private void load() {
        if (saveFile.exists()) {
            try {
                props.load(new FileInputStream(saveFile));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
