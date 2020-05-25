/* Copyright 2019 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.esri.samples.raster_rendering_rule;

import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import com.esri.arcgisruntime.arcgisservices.RenderingRuleInfo;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.raster.ImageServiceRaster;
import com.esri.arcgisruntime.raster.RenderingRule;

public class RasterRenderingRuleSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {
    try {

      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Raster Rendering Rule Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a control panel
      VBox controlsVBox = new VBox(8);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.6)"), CornerRadii.EMPTY, Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(260, 160);
      controlsVBox.getStyleClass().add("panel-region");

      // create progress indicator
      ProgressIndicator progressIndicator = new ProgressIndicator();
      progressIndicator.setVisible(false);

      // create drop down menu of Rendering Rules
      ComboBox<RenderingRuleInfo> renderingRuleInfoComboBox = new ComboBox<>();
      renderingRuleInfoComboBox.setMaxWidth(260.0);
      renderingRuleInfoComboBox.setConverter(new StringConverter<>() {
        @Override
        public String toString(RenderingRuleInfo renderingRuleInfo) {

          return renderingRuleInfo != null ? renderingRuleInfo.getName() : "";
        }

        @Override
        public RenderingRuleInfo fromString(String string) {
          return null;
        }
      });

      // create a label for the Rendering Rule info
      Label renderingRuleInfoLabel = new Label("");
      renderingRuleInfoLabel.setWrapText(true);

      // add the ComboBox and Label to the controlsVBox
      controlsVBox.getChildren().addAll(renderingRuleInfoComboBox, renderingRuleInfoLabel);

      // create a Streets BaseMap
      ArcGISMap map = new ArcGISMap(Basemap.createStreets());

      // add the map to a new map view
      mapView = new MapView();
      mapView.setMap(map);

      // create an Image Service Raster as a raster layer and add to map
      final String ImageServiceRasterUri = "https://sampleserver6.arcgisonline.com/arcgis/rest/services/CharlotteLAS/ImageServer";
      ImageServiceRaster imageServiceRaster = new ImageServiceRaster(ImageServiceRasterUri);
      RasterLayer imageRasterLayer = new RasterLayer(imageServiceRaster);
      map.getOperationalLayers().add(imageRasterLayer);

      // show alert if layer fails to load
      imageRasterLayer.addDoneLoadingListener(() -> {
        if (imageRasterLayer.getLoadStatus() == LoadStatus.LOADED) {
          // zoom to extent of the raster
          mapView.setViewpointGeometryAsync(imageServiceRaster.getServiceInfo().getFullExtent());

          // get the predefined rendering rules
          List<RenderingRuleInfo> renderingRuleInfos = imageServiceRaster.getServiceInfo().getRenderingRuleInfos();

          // populate the drop down menu with the rendering rule names
          renderingRuleInfoComboBox.getItems().addAll(renderingRuleInfos);

          // listen to selection in the drop-down menu
          renderingRuleInfoComboBox.getSelectionModel().selectedItemProperty().addListener(o -> {

            // get the requested rendering rule info from the list
            RenderingRuleInfo selectedRenderingRuleInfo = renderingRuleInfoComboBox.getSelectionModel().getSelectedItem();

            // change the label text to the rendering rule info description
            String renderingRuleInfoDescription = selectedRenderingRuleInfo.getDescription();
            renderingRuleInfoLabel.setText("Rule Description: \n" + renderingRuleInfoDescription);

            // clear previous raster layer from the map's operational layers
            map.getOperationalLayers().clear();

            // create a rendering rule object using the rendering rule info
            RenderingRule renderingRule = new RenderingRule(selectedRenderingRuleInfo);

            // create a new image service raster
            ImageServiceRaster appliedImageServiceRaster = new ImageServiceRaster(ImageServiceRasterUri);

            // show progress indicator while rule is loading
            appliedImageServiceRaster.addLoadStatusChangedListener((e) -> {
              if (e.getNewLoadStatus() == LoadStatus.LOADING) {
                progressIndicator.setVisible(true);
              } else {
                progressIndicator.setVisible(false);
              }
            });

            // apply the rendering rule
            appliedImageServiceRaster.setRenderingRule(renderingRule);
            RasterLayer rasterLayer = new RasterLayer(appliedImageServiceRaster);
            map.getOperationalLayers().add(rasterLayer);

          });

          // automatically select the first rendering rule
          renderingRuleInfoComboBox.getSelectionModel().selectFirst();
        } else {
          new Alert(Alert.AlertType.ERROR, "Error loading Image Raster Layer.").show();
        }
      });

      // add the map view to the stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox, progressIndicator);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));
      StackPane.setAlignment(progressIndicator, Pos.BOTTOM_CENTER);
      StackPane.setMargin(progressIndicator, new Insets(10, 0, 50, 0));
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
