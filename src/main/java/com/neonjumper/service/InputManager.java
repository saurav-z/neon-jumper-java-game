package com.neonjumper.service;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class InputManager {

    private final Set<KeyCode> activeKeys = new HashSet<>();

    public void handleKeyPressed(KeyEvent event) {
        activeKeys.add(event.getCode());
    }

    public void handleKeyReleased(KeyEvent event) {
        activeKeys.remove(event.getCode());
    }

    public boolean isKeyPressed(KeyCode key) {
        return activeKeys.contains(key);
    }
}
