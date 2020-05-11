/*
 * Copyright 2018 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.esri.samples.list_transformations_by_suitability;

import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.geometry.DatumTransformation;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.TransformationCatalog;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

public class ListTransformationsBySuitabilitySample extends Application {

  private ArcGISMap map; // keeps loadable in scope to avoid garbage collection
  private MapView mapView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("List Transformations by Suitability Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map with light gray canvas basemap and add it to the map view
      map = new ArcGISMap(Basemap.createLightGrayCanvas());
      mapView = new MapView();
      mapView.setMap(map);

      // create a graphics overlay to show the original graphic and the the transformed graphic
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create a blue square graphic located in the Greenwich observatory courtyard in London, UK, the location of the
      // Greenwich prime meridian. This will be projected using the selected transformation.
      Point originalPoint = new Point(538985.355, 177329.516, SpatialReference.create(27700));
      Graphic originalGraphic = new Graphic(originalPoint, new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, 0xFF0000FF,
        10));
      graphicsOverlay.getGraphics().add(originalGraphic);

      // create red cross graphic for transformed point
      Graphic transformedGraphic = new Graphic();
      transformedGraphic.setSymbol(new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, 0xFFFF0000, 10));
      transformedGraphic.setVisible(false);
      graphicsOverlay.getGraphics().add(transformedGraphic);

      // zoom to the location of the original graphic
      mapView.setViewpointCenterAsync(originalPoint, 5000);

      // create a list of transformations
      ListView<DatumTransformation> transformationsListView = new ListView<>();

      // show the transformation name in the list
      transformationsListView.setCellFactory(list -> new ListCell<DatumTransformation>() {

        @Override
        protected void updateItem(DatumTransformation transformation, boolean bln) {

          super.updateItem(transformation, bln);
          if (transformation != null) {
            setText(transformation.getName());
          }
        }

      });

      // if the checkbox is not selected, transformations should be ordered by suitability for the whole
      // spatial reference. If checked, then transformations will be ordered by suitability for the map extent.
      CheckBox suitabilityCheckBox = new CheckBox("Order by extent suitability");
      suitabilityCheckBox.setOnAction(e -> {
        transformationsListView.getItems().clear();
        List<DatumTransformation> transformations;
        if (suitabilityCheckBox.isSelected()) {
          transformations = TransformationCatalog.getTransformationsBySuitability(
            originalGraphic.getGeometry().getSpatialReference(), map.getSpatialReference(), mapView.getVisibleArea().getExtent());
        } else {
          transformations = TransformationCatalog.getTransformationsBySuitability(
            originalGraphic.getGeometry().getSpatialReference(), map.getSpatialReference());
        }
        transformationsListView.getItems().addAll(transformations);
      });

      // trigger the event to load the initial transformations list when the map is loaded
      map.addDoneLoadingListener(() -> suitabilityCheckBox.fireEvent(new ActionEvent()));

      // create a button that when clicked, shows a new graphic with the selected transformation applied
      Button transformButton = new Button("Transform");
      transformButton.setOnAction(e -> {
        DatumTransformation transformation = transformationsListView.getSelectionModel().getSelectedItem();
        if (transformation != null) {
          Point projectedPoint = (Point) GeometryEngine.project(originalGraphic.getGeometry(), mapView.getSpatialReference(),
            transformation);
          transformedGraphic.setVisible(true);
          transformedGraphic.setGeometry(projectedPoint);
        }
      });

      // add the controls to the view
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
          Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(300, 500);
      controlsVBox.getStyleClass().add("panel-region");
      controlsVBox.getChildren().addAll(suitabilityCheckBox, transformationsListView, transformButton);

      // add the map view to stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
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
