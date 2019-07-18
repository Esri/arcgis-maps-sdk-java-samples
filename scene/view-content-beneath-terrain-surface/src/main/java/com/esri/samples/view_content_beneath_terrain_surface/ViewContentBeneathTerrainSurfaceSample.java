package com.esri.samples.view_content_beneath_terrain_surface;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.NavigationConstraint;
import com.esri.arcgisruntime.mapping.view.SceneView;

public class ViewContentBeneathTerrainSurfaceSample extends Application {

  private SceneView sceneView;

  @Override
  public void start(Stage stage) throws Exception {

    // create stack pane and JavaFX app scene
    StackPane stackPane = new StackPane();
    Scene fxScene = new Scene(stackPane);

    // set title, size, and add JavaFX scene to stage
    stage.setTitle("View Content Beneath Terrain Surface Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(fxScene);
    stage.show();

    // create a scene from a web scene Url and set it to the scene view
    ArcGISScene scene = new ArcGISScene("https://www.arcgis.com/home/item.html?id=91a4fafd747a47c7bab7797066cb9272");
    sceneView = new SceneView();
    sceneView.setArcGISScene(scene);

    // add a progress indicator to show the scene is loading
    ProgressIndicator progressIndicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);

    // once the scene has loaded, set the navigation constraint and opacity of the base surface
    scene.addDoneLoadingListener(() -> {
      // ensure the navigation constraint is set to NONE to view content beneath the terrain surface
      scene.getBaseSurface().setNavigationConstraint(NavigationConstraint.NONE);
      // set opacity to view content beneath the base surface
      scene.getBaseSurface().setOpacity(0.4f);
    });

    // hide the progress indicator once the sceneview has completed drawing
    sceneView.addDrawStatusChangedListener(event -> progressIndicator.setVisible(false));

    // add the scene view and progress indicator to the stack pane
    stackPane.getChildren().addAll(sceneView, progressIndicator);
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    if (sceneView != null) {
      sceneView.dispose();
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