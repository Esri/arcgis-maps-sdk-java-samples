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

    StackPane stackPane = new StackPane();
    Scene fxScene = new Scene(stackPane);

    stage.setTitle("View Content Beneath Terrain Surface Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(fxScene);
    stage.show();

    // create a scene from a web scene portal item and set it to the scene view
    Portal portal = new Portal("http://www.arcgis.com/");
    PortalItem webScene = new PortalItem(portal, "91a4fafd747a47c7bab7797066cb9272");

    ArcGISScene scene = new ArcGISScene(webScene);
    sceneView = new SceneView();
    sceneView.setArcGISScene(scene);

    ProgressIndicator progressIndicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);

    scene.addDoneLoadingListener(() -> {

      progressIndicator.setVisible(false);
      scene.getBaseSurface().setNavigationConstraint(NavigationConstraint.NONE);
      scene.getBaseSurface().setOpacity((float)0.1);

    });


    stackPane.getChildren().addAll(sceneView, progressIndicator);



  }
}
