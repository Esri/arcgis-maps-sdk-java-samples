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

package com.esri.samples.geometry.list_transformations_by_suitability;

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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("List Transformations by Suitability Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map with light gray canvas basemap and add it to the map view
      mapView = new MapView();
      ArcGISMap map = new ArcGISMap(Basemap.createLightGrayCanvas());
      mapView.setMap(map);

      // create a graphics overlay to show the original graphic and the the transformed graphic
      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
      mapView.getGraphicsOverlays().add(graphicsOverlay);

      // create a blue square graphic located in the Greenwich observatory courtyard in London, UK, the location of the
      // Greenwich prime meridian. This will be projected using the selected transformation.
      Point point  = new Point(538985.355, 177329.516, SpatialReference.create(27700));
      Graphic pointGraphic = new Graphic(point, new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, 0xFF0000FF,
          10));
      graphicsOverlay.getGraphics().add(pointGraphic);

      // create red cross graphic for transformed point
      Graphic transformedPointGraphic = new Graphic(point, new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS,
          0xFFFF0000, 10));
      transformedPointGraphic.setVisible(false);
      graphicsOverlay.getGraphics().add(transformedPointGraphic);

      // zoom to the initial point
      mapView.setViewpointCenterAsync(point, 5000);

      // create a list of transformations
      ListView<DatumTransformation> transformationsListView = new ListView<>();

      // show the transformation name in the list
      transformationsListView.setCellFactory(list -> new ListCell<>() {

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
          transformations = TransformationCatalog.getTransformationsBySuitability(pointGraphic.getGeometry()
                  .getSpatialReference(), map.getSpatialReference(), mapView.getVisibleArea().getExtent());
        } else {
          transformations = TransformationCatalog.getTransformationsBySuitability(pointGraphic.getGeometry()
              .getSpatialReference(), map.getSpatialReference());
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
          Point projectedPoint = (Point) GeometryEngine.project(pointGraphic.getGeometry(), mapView
                  .getSpatialReference(), transformation);
          transformedPointGraphic.setVisible(true);
          transformedPointGraphic.setGeometry(projectedPoint);
        }
      });

      // add the controls to the view
      VBox vBox = new VBox(6);
      vBox.setMaxSize(300, 500);
      vBox.getStyleClass().add("panel-region");
      vBox.getChildren().addAll(suitabilityCheckBox, transformationsListView, transformButton);

      // add the map view to stack pane
      stackPane.getChildren().addAll(mapView, vBox);
      StackPane.setAlignment(vBox, Pos.TOP_LEFT);
      StackPane.setMargin(vBox, new Insets(10, 0, 0, 10));
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() throws Exception {

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
