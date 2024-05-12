package com.ta.client;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HelloApplication extends Application {
    private static final int TABLE_SIZE = 10;
    private static final int CELL_SIZE = 40;

    @Override
    public void start(Stage primaryStage) {

        ResponseEntity<Map> response;

        User user = new User();
        user.setUsername("newuser1");
        user.setPassword("12345678");
        user.setEmail("user1@example.com");

        UserServise userServise = new UserServise();

        userServise.createUser(user);

        response = userServise.signIn(user);

        userServise.create(response);

        TerrainData[] terrainData = userServise.choose();

        GridPane root = new GridPane();
        Scene scene = new Scene(root);
        primaryStage.setFullScreen(false);

        Timeline updateTerrainDataTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            TerrainData[] td = userServise.choose();
            FightRequest fightRequest = userServise.fight();
            System.out.println(fightRequest.getEnemyHp()+" "+ fightRequest.getEnemyId());
            drawTable(root, td, userServise.move(9999, 9999));
        }));
        updateTerrainDataTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTerrainDataTimeline.play();

        scene.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                double mouseX = event.getX();
                double mouseY = event.getY();
                int clickedColumn = (int) (mouseX / CELL_SIZE);
                int clickedRow = (int) (mouseY / CELL_SIZE);
                System.out.println("Clicked on square: Row " + clickedRow + ", Column " + clickedColumn);
                Map<String, Integer> charCoords = userServise.move(clickedRow, clickedColumn);

                drawTable(root, terrainData, charCoords);

                TextInputDialog textInputDialog = new TextInputDialog();
                textInputDialog.setTitle("Text Input Dialog");
                for (var x:terrainData){
                    if (charCoords.get("x").equals(x.getXcoord()) && charCoords.get("y").equals(x.getYcoord())){
                        textInputDialog.setHeaderText(x.getXcoord()+" "+x.getYcoord()+" "+x.getEnemies().toString());
                    }
                }
                //textInputDialog.show();
                Optional<String> result = textInputDialog.showAndWait();
                result.ifPresent(input -> {
                    System.out.println("User's input: " + input);
                    userServise.selectTarget(Integer.valueOf(input));
                });

            }
        });



        Map<String, Integer> startCoords = new HashMap<>();
        startCoords.put("x", 0);
        startCoords.put("y", 0);
        // Create the initial table
        drawTable(root, terrainData, startCoords);

        primaryStage.setTitle("Clickable Grid");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void drawTable(GridPane root, TerrainData[] terrainData, Map<String, Integer> charCoords) {
        root.getChildren().clear(); // Clear existing content
        // Create the table
        for (TerrainData data : terrainData) {
            int row = data.getXcoord();
            int col = data.getYcoord();

            Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);

            if (row == charCoords.get("x") && col == charCoords.get("y")) {
                cell.setFill(Color.YELLOW);
            } else if ("Grass".equals(data.getTerrainType())) {
                if (data.getEnemies().isEmpty()) {
                    cell.setFill(Color.GREEN);
                } else {
                    cell.setFill(Color.RED);
                }
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

