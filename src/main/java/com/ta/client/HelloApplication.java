package com.ta.client;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HelloApplication extends Application {
    private static final int TABLE_SIZE = 10;
    private static final int CELL_SIZE = 40;
    private static final int UPDATE_INTERVAL_SECONDS = 1;
    private static final String SERVER_URL = "http://localhost:8080";

    private UserServise userServise;
    private GridPane root;
    private TerrainData[] terrainData;

    @Override
    public void start(Stage primaryStage) {
        initializeUser();
        setupUI(primaryStage);
        setupTimeline();
    }

    private void initializeUser() {
        User user = new User();
        user.setUsername("player");
        user.setPassword("12345678");
        user.setEmail("player@example.com");

        userServise = new UserServise();
        userServise.createUser(user);
        ResponseEntity<Map> response = userServise.signIn(user);
        userServise.createCharacter(response);
        terrainData = userServise.choose();
    }

    private void setupUI(Stage primaryStage) {
        root = new GridPane();
        Scene scene = new Scene(root);
        primaryStage.setFullScreen(false);

        scene.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                handleMouseClick(event.getX(), event.getY());
            }
        });

        drawTable(terrainData, new HashMap<>(Map.of("x", 0, "y", 0)));

        primaryStage.setTitle("Clickable Grid");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupTimeline() {
        Timeline updateTerrainDataTimeline = new Timeline(new KeyFrame(Duration.seconds(UPDATE_INTERVAL_SECONDS), event -> {
            TerrainData[] newTerrainData = userServise.choose();
            FightRequest fightRequest = userServise.fight();
            System.out.println(fightRequest.getEnemyHp() + " " + fightRequest.getEnemyId());

            Platform.runLater(() -> drawTable(newTerrainData, userServise.move(9999, 9999)));
        }));
        updateTerrainDataTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTerrainDataTimeline.play();
    }

    private void handleMouseClick(double mouseX, double mouseY) {
        int clickedColumn = (int) (mouseX / CELL_SIZE);
        int clickedRow = (int) (mouseY / CELL_SIZE);
        System.out.println("Clicked on square: Row " + clickedRow + ", Column " + clickedColumn);
        Map<String, Integer> charCoords = userServise.move(clickedRow, clickedColumn);

        // Update terrain data after moving the character
        terrainData = userServise.choose();

        // Redraw the table with updated data and character coordinates
        drawTable(terrainData, charCoords);

        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("Text Input Dialog");
        for (TerrainData data : terrainData) {
            if (charCoords.get("x").equals(data.getXcoord()) && charCoords.get("y").equals(data.getYcoord())) {
                textInputDialog.setHeaderText(data.getXcoord() + " " + data.getYcoord() + " " + data.getEnemies().toString());
            }
        }
        Optional<String> result = textInputDialog.showAndWait();
        result.ifPresent(input -> {
            System.out.println("User's input: " + input);
            userServise.selectTarget(Integer.valueOf(input));
        });
    }


    private void drawTable(TerrainData[] terrainData, Map<String, Integer> charCoords) {
        root.getChildren().clear(); // Clear existing content
        // Create the table
        for (TerrainData data : terrainData) {
            int row = data.getXcoord();
            int col = data.getYcoord();

            Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);

            if (row == charCoords.get("x") && col == charCoords.get("y")) {
                cell.setFill(Color.YELLOW);
            } else if ("Grass".equals(data.getTerrainType())) {
                cell.setFill(data.getEnemies().isEmpty() ? Color.GREEN : Color.RED);
            } else {
                cell.setFill(Color.BLACK);
            }

            cell.setStroke(Color.BLACK);
            root.add(cell, col, row);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
