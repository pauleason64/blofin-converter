package com.blofinconv;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MyApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create UI components
        TextField textField = new TextField();
        Button button1 = new Button("Button 1");
        Button button2 = new Button("Button 2");
        TableView<String> tableView = new TableView<>();
        TextArea statusTextArea = new TextArea();
        Button saveButton = new Button("Save");

        // Set up layout
        VBox panel1 = new VBox(10, textField, button1, button2);
        VBox panel2 = new VBox(tableView);
        VBox panel3 = new VBox(10, statusTextArea, saveButton);

        // Set panel sizes
        panel1.setPrefWidth(200);
        panel2.setPrefWidth(400);
        panel3.setPrefWidth(200);

        // Create main layout
        HBox root = new HBox(panel1, panel2, panel3);

        // Create scene
        Scene scene = new Scene(root, 800, 400);

        // Set stage properties
        primaryStage.setTitle("My JavaFX App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
