package com.neonjumper.ui;

import com.neonjumper.core.GameLoop;
import com.neonjumper.service.InputManager;
import com.neonjumper.service.UIService;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StageInitializer implements ApplicationListener<JavaFxApplication.StageReadyEvent> {

    private final GameLoop gameLoop;
    private final InputManager inputManager;
    private final UIService uiService;
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    public StageInitializer(GameLoop gameLoop, InputManager inputManager, UIService uiService) {
        this.gameLoop = gameLoop;
        this.inputManager = inputManager;
        this.uiService = uiService;
    }

    @Override
    public void onApplicationEvent(JavaFxApplication.StageReadyEvent event) {
        Stage stage = event.getStage();
        
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D(); 
        gameLoop.setGraphicsContext(gc); // Need to add this setter to GameLoop or pass it differently
        
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);
        
        // Setup Input
        scene.setOnKeyPressed(inputManager::handleKeyPressed);
        scene.setOnKeyReleased(inputManager::handleKeyReleased);
        
        stage.setScene(scene);
        stage.setTitle("Neon Jumper");
        stage.setResizable(false);
        stage.show();
        
        // Init UI and Game
        uiService.init(root);
    }
}
