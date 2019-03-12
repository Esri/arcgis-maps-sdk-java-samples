package com.esri.samples.scene.view_content_beneath_terrain_surface;

import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.view.SceneView;
import javafx.application.Application;
import javafx.scene.Scene;
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

    ArcGISScene scene = new ArcGISScene("https://www.arcgis.com/home/item.html?id=91a4fafd747a47c7bab7797066cb9272");
    sceneView = new SceneView();
    sceneView.setArcGISScene(scene);

    System.out.println(scene.getBaseSurface().getName());

    scene.getBaseSurface().setOpacity((float)0.1);

    System.out.println(scene.getBaseSurface().getNavigationConstraint());

    stackPane.getChildren().add(sceneView);



  }
}
