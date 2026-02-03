package com.neonjumper.service;

import com.neonjumper.core.GameLoop;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import org.springframework.stereotype.Service;

@Service
public class UIService {

    private final GameLoop gameLoop;
    private final LevelService levelService;
    private final SaveService saveService;
    private StackPane root;
    private StackPane uiContainer;
    private VBox menuBox;
    private VBox pauseBox;
    private VBox charSelectBox;
    private StackPane levelSelectPane;
    private StackPane hudPane;
    private StackPane tutorialPane;
    private Label hudLevelLabel;
    private Canvas menuBackground;
    private boolean isPaused = false;
    private boolean tutorialShown = false;
    private AnimationTimer menuAnim;

    public UIService(GameLoop gameLoop, LevelService levelService, SaveService saveService) {
        this.gameLoop = gameLoop;
        this.levelService = levelService;
        this.saveService = saveService;
    }

    public void init(StackPane root) {
        this.root = root;
        this.uiContainer = new StackPane();
        this.uiContainer.setPickOnBounds(false); // Let clicks pass through if not on a button
        this.root.getChildren().add(uiContainer);

        this.menuBackground = new Canvas(1280, 720);
        this.root.getChildren().add(0, menuBackground);
        initMenuAnimation();

        this.menuBox = createMainMenu();
        this.pauseBox = createPauseMenu();
        this.charSelectBox = createCharacterSelect();
        this.levelSelectPane = createLevelSelect();
        this.hudPane = createHUD();
        this.tutorialPane = createTutorialOverlay();
        
        // Keyboard Listener for Pause
        root.getScene().addEventHandler(javafx.scene.input.KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                togglePause();
            }
        });
        
        levelService.setLevelChangeListener(this::showLevelTransition);
        
        showMenu();
    }

    private StackPane createHUD() {
        StackPane pane = new StackPane();
        pane.setPadding(new javafx.geometry.Insets(20));
        pane.setAlignment(Pos.TOP_LEFT);
        pane.setPickOnBounds(false);

        Label hearts = new Label();
        hearts.setTextFill(Color.RED);
        hearts.setFont(Font.font("Arial", 40));
        
        // Update hearts in a loop or binding
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(100), e -> {
                if (levelService.getPlayer() != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < levelService.getPlayer().getLives(); i++) sb.append("❤ ");
                    hearts.setText(sb.toString());
                }
            })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        pane.getChildren().add(hearts);

        hudLevelLabel = new Label("LEVEL 1");
        hudLevelLabel.setTextFill(Color.CYAN);
        hudLevelLabel.setFont(Font.font("Arial", 24));
        StackPane.setAlignment(hudLevelLabel, Pos.TOP_CENTER);
        pane.getChildren().add(hudLevelLabel);

        return pane;
    }

    private void initMenuAnimation() {
        GraphicsContext gc = menuBackground.getGraphicsContext2D();
        menuAnim = new AnimationTimer() {
            double time = 0;
            @Override
            public void handle(long now) {
                time += 0.016;
                gc.setFill(Color.web("#000511")); // Deep space blue
                gc.fillRect(0, 0, 1280, 720);
                
                // Draw a pulsing grid in blue
                double pulse = 0.5 + 0.5 * Math.sin(time * 2);
                gc.setStroke(Color.web("#003366", pulse * 0.5));
                gc.setLineWidth(1);
                
                double gridSize = 80;
                double scroll = (time * 20) % gridSize;
                
                for (double x = -gridSize; x < 1280 + gridSize; x += gridSize) {
                    gc.strokeLine(x + scroll, 0, x + scroll, 720);
                }
                for (double y = -gridSize; y < 720 + gridSize; y += gridSize) {
                    gc.strokeLine(0, y + scroll, 1280, y + scroll);
                }
                
                // Draw some floating "circuit" lines
                gc.setStroke(Color.web("#00aaff", pulse));
                gc.setLineWidth(2);
                for (int i = 0; i < 5; i++) {
                    double y = (300 + i * 100 + time * 50) % 720;
                    gc.strokeLine(0, y, 1280, y);
                    gc.fillOval( (time * 200 + i * 250) % 1280, y - 2, 4, 4);
                }
            }
        };
    }

    public void showLevelTransition(int levelId) {
        if (hudLevelLabel != null) hudLevelLabel.setText("LEVEL " + levelId);
        
        Label trans = new Label("LEVEL " + levelId);
        trans.setTextFill(Color.CYAN);
        trans.setFont(Font.font("Arial", 80));
        trans.setOpacity(0);
        trans.setScaleX(0.5); trans.setScaleY(0.5);
        uiContainer.getChildren().add(trans);

        Timeline tl = new Timeline(
            new KeyFrame(Duration.ZERO, new javafx.animation.KeyValue(trans.opacityProperty(), 0), new javafx.animation.KeyValue(trans.scaleXProperty(), 0.5), new javafx.animation.KeyValue(trans.scaleYProperty(), 0.5)),
            new KeyFrame(Duration.seconds(0.5), new javafx.animation.KeyValue(trans.opacityProperty(), 1), new javafx.animation.KeyValue(trans.scaleXProperty(), 1.2), new javafx.animation.KeyValue(trans.scaleYProperty(), 1.2)),
            new KeyFrame(Duration.seconds(1.5), new javafx.animation.KeyValue(trans.opacityProperty(), 1), new javafx.animation.KeyValue(trans.scaleXProperty(), 1.0), new javafx.animation.KeyValue(trans.scaleYProperty(), 1.0)),
            new KeyFrame(Duration.seconds(2.0), new javafx.animation.KeyValue(trans.opacityProperty(), 0), new javafx.animation.KeyValue(trans.scaleXProperty(), 2.0), new javafx.animation.KeyValue(trans.scaleYProperty(), 2.0))
        );
        tl.setOnFinished(e -> uiContainer.getChildren().remove(trans));
        tl.play();
    }
    
    private StackPane createTutorialOverlay() {
        StackPane pane = new StackPane();
        pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9);");
        
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-border-color: cyan; -fx-border-width: 4; -fx-padding: 40; -fx-background-color: #000022;");
        box.setMaxWidth(600);
        box.setMaxHeight(400);
        
        Label title = new Label("NEON INTERFACE INITIALIZED");
        title.setTextFill(Color.CYAN);
        title.setFont(Font.font("Arial Black", 32));
        
        Label controls = new Label("CONTROLS:\n[ W / SPACE ] JUMP\n[ A / D ] MOVE\n[ ESC ] SYSTEM PAUSE\n\nOBJECTIVE:\nREACH THE NEON GATE");
        controls.setTextFill(Color.WHITE);
        controls.setFont(Font.font("Consolas", 20));
        controls.setAlignment(Pos.CENTER);
        
        Label hint = new Label(">>> CLICK ANYWHERE TO START MISSION <<<");
        hint.setTextFill(Color.YELLOW);
        hint.setFont(Font.font("Consolas", 16));
        
        box.getChildren().addAll(title, controls, hint);
        pane.getChildren().add(box);
        
        pane.setOnMouseClicked(e -> {
            uiContainer.getChildren().remove(tutorialPane);
            gameLoop.start();
        });
        
        return pane;
    }

    private VBox createCharacterSelect() {
        VBox box = new VBox(30);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(0, 0, 0, 0.95);");
        
        Label title = new Label("SELECT YOUR AVATAR");
        title.setTextFill(Color.CYAN);
        title.setFont(Font.font("Arial Black", 40));
        
        HBox options = new HBox(50);
        options.setAlignment(Pos.CENTER);
        
        VBox cubeOpt = createCharOption("NEON CUBE", "Standard issue agility.", Color.WHITE, com.neonjumper.entity.Player.Skin.CUBE);
        VBox sphereOpt = createCharOption("NEON COMET", "Persistent trail effect.", Color.CYAN, com.neonjumper.entity.Player.Skin.SPHERE);
        
        options.getChildren().addAll(cubeOpt, sphereOpt);
        
        Button btnBack = createButton("BACK TO MENU", this::showMenu);
        
        box.getChildren().addAll(title, options, btnBack);
        return box;
    }
    
    private VBox createCharOption(String name, String desc, Color color, com.neonjumper.entity.Player.Skin skin) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-border-color: grey; -fx-border-width: 2; -fx-padding: 20;");
        
        Label lName = new Label(name);
        lName.setTextFill(color);
        lName.setFont(Font.font("Arial", 24));
        
        Label lDesc = new Label(desc);
        lDesc.setTextFill(Color.LIGHTGREY);
        lDesc.setFont(Font.font("Arial", 14));
        
        Button btnSelect = createButton("SELECT", () -> {
            levelService.setSkin(skin);
            showLevelSelect();
        });
        
        box.getChildren().addAll(lName, lDesc, btnSelect);
        return box;
    }
    
    private StackPane createLevelSelect() {
        StackPane pane = new StackPane();
        pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.95);");
        
        VBox mainBox = new VBox(30);
        mainBox.setAlignment(Pos.CENTER);
        
        Label title = new Label("MISSION SELECT");
        title.setTextFill(Color.CYAN);
        title.setFont(Font.font("Arial Black", 40));
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);
        
        int unlocked = saveService.getUnlockedLevels();
        
        for (int i = 1; i <= 20; i++) {
            final int levelId = i;
            boolean isLocked = i > unlocked;
            
            VBox item = new VBox(5);
            item.setAlignment(Pos.CENTER);
            item.setStyle("-fx-border-color: " + (isLocked ? "grey" : "cyan") + "; -fx-border-width: 2; -fx-padding: 10; -fx-min-width: 100;");
            
            Label lNum = new Label("LEVEL " + i);
            lNum.setTextFill(isLocked ? Color.GREY : Color.WHITE);
            
            int stars = saveService.getStarsForLevel(i);
            Label lStars = new Label("⭐".repeat(stars) + "☆".repeat(3 - stars));
            lStars.setTextFill(Color.YELLOW);
            
            Button btn = new Button(isLocked ? "LOCKED" : "GO");
            btn.setDisable(isLocked);
            btn.setStyle("-fx-background-color: " + (isLocked ? "transparent" : "cyan") + "; -fx-text-fill: black;");
            btn.setOnAction(e -> {
                levelService.loadLevel(levelId);
                showGame();
            });
            
            item.getChildren().addAll(lNum, lStars, btn);
            grid.add(item, (i - 1) % 5, (i - 1) / 5);
        }
        
        Button btnBack = createButton("BACK", this::showMenu);
        
        mainBox.getChildren().addAll(title, grid, btnBack);
        pane.getChildren().add(mainBox);
        return pane;
    }
    
    private VBox createMainMenu() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: transparent;");
        
        Label title = new Label("NEON JUMPER");
        title.setTextFill(Color.web("#00ffff"));
        title.setFont(Font.font("Arial Black", 80));
        title.setEffect(new javafx.scene.effect.Glow(0.8));
        
        Button btnPlay = createButton("INITIATE MISSION", this::showCharacterSelect);
        Button btnQuit = createButton("TERMINATE", () -> System.exit(0));
        
        box.getChildren().addAll(title, btnPlay, btnQuit);
        return box;
    }

    private VBox createPauseMenu() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");
        
        Label txt = new Label("PAUSED");
        txt.setTextFill(Color.WHITE);
        txt.setFont(Font.font("Arial", 40));
        
        Button btnResume = createButton("RESUME", this::togglePause);
        Button btnMenu = createButton("MAIN MENU", this::showMenu);
        
        box.getChildren().addAll(txt, btnResume, btnMenu);
        return box;
    }

    private void togglePause() {
        if (!uiContainer.getChildren().contains(hudPane)) return; // Don't pause in menu

        isPaused = !isPaused;
        if (isPaused) {
            gameLoop.stop();
            uiContainer.getChildren().add(pauseBox);
        } else {
            gameLoop.start();
            uiContainer.getChildren().remove(pauseBox);
        }
    }

    public void showMenu() {
        isPaused = false;
        gameLoop.stop();
        if (menuAnim != null) menuAnim.start();
        if (menuBackground != null) menuBackground.setVisible(true);
        uiContainer.getChildren().clear();
        uiContainer.getChildren().add(menuBox);
    }

    public void showCharacterSelect() {
        uiContainer.getChildren().clear();
        uiContainer.getChildren().add(charSelectBox);
    }
    
    public void showLevelSelect() {
        uiContainer.getChildren().clear();
        levelSelectPane = createLevelSelect();
        uiContainer.getChildren().add(levelSelectPane);
    }

    public void showGame() {
        uiContainer.getChildren().clear();
        uiContainer.getChildren().add(hudPane);
        if (menuAnim != null) menuAnim.stop();
        if (menuBackground != null) menuBackground.setVisible(false);
        
        if (!tutorialShown) {
            uiContainer.getChildren().add(tutorialPane);
            tutorialShown = true;
            gameLoop.stop(); // Wait for click
        } else {
            showLevelTransition(levelService.getCurrentLevelId());
            gameLoop.start();
        }
    }

    private Button createButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: cyan; -fx-border-width: 2px; -fx-padding: 10 30; -fx-font-size: 20;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: cyan; -fx-text-fill: black; -fx-border-color: cyan; -fx-border-width: 2px; -fx-padding: 10 30; -fx-font-size: 20;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: cyan; -fx-border-width: 2px; -fx-padding: 10 30; -fx-font-size: 20;"));
        btn.setOnAction(e -> action.run());
        return btn;
    }
}
