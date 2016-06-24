package com.esri.samples.scene;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MissionReplay extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // setup the scene
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MissionView.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/SamplesTheme.css").toExternalForm());

        stage.setTitle("Display a scene");
        stage.setWidth(800);
        stage.setHeight(700);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Stops and releases all resources used in application.
     */
    @Override
    public void stop() {

        /*if (sceneView != null) {
            sceneView.dispose();
        }*/
    }

    /**
     * Opens and runs application.
     *
     * @param args arguments passed to this application
     */
    public static void main(String[] args) {

        Application.launch(args);
    }
}
