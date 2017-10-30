package com.esri.samples.analysis.viewshed_location;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewshedLocationSample extends Application {

  private static ViewshedLocationController controller;

  @Override
  public void start(Stage stage) throws IOException {
    // set up the scene
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/viewshed_location.fxml"));
    Parent root = loader.load();
    controller = loader.getController();
    Scene scene = new Scene(root);

    // set up the stage
    stage.setTitle("Viewshed Location Sample");
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

