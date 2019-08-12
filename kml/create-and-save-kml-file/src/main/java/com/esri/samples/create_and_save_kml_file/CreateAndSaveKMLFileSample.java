package com.esri.samples.create_and_save_kml_file;

import javafx.application.Application;
import javafx.stage.Stage;

import com.esri.arcgisruntime.mapping.view.MapView;

public class CreateAndSaveKMLFileSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    if (mapView != null) {
      mapView.dispose();
    }
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
