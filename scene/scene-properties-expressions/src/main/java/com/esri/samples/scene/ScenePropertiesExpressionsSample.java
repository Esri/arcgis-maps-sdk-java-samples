package com.esri.samples.scene;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScenePropertiesExpressionsSample extends Application {

  private static ScenePropertiesExpressionsController controller;

  @Override
  public void start(Stage stage) throws IOException {
    // set up the scene
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/scene_properties_expressions.fxml"));
    Parent root = loader.load();
    controller = loader.getController();
    Scene scene = new Scene(root);

    // set up the stage
    stage.setTitle("Scene Properties Expressions Sample");
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
    controller.terminate();
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