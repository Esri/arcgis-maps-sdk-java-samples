package com.esri.samples.scene.view_content_beneath_terrain_surface;

import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.NavigationConstraint;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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

    // when the scene has loaded, set navigation constraint and opacity to see below the surface
    scene.addDoneLoadingListener(() -> {
      progressIndicator.setVisible(false);
      // ensure the navigation constraint is set to NONE
      scene.getBaseSurface().setNavigationConstraint(NavigationConstraint.NONE);
      // set opacity to view content beneath the base surface
      scene.getBaseSurface().setOpacity((float)0.5);
    });

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

