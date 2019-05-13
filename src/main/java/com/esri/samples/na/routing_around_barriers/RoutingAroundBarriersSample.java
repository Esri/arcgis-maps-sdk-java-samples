package com.esri.samples.na.routing_around_barriers;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class RoutingAroundBarriersSample extends Application {

  MapView mapView;

  @Override
  public void start(Stage stage) {

    try{
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // set title, size and add scene to stage
      stage.setTitle("Routing Around Barriers Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a control panel
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY, Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(400,300);
      controlsVBox.getStyleClass().add("panel-region");




      // create an ArcGISMap with a streets basemap
      ArcGISMap map = new ArcGISMap(Basemap.createStreets());

      // set the ArcGISMap to be displayed in this view
      mapView = new MapView();
      mapView.setMap(map);

      // add the map view and control panel to the stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10,0,0,10));

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

//    if (mapView != null) {
//      mapView.dispose();
//    }
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


