/*
 * Copyright 2021 Esri.
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

package com.esri.samples.display_dimensions;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.layers.DimensionLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.io.File;
import java.util.Objects;

public class DisplayDimensionsSample extends Application {

  private DimensionLayer dimensionLayer;
  private MapView mapView;
  private MobileMapPackage mobileMapPackage; // keep loadable in scope to avoid garbage collection

  @Override
  public void start(Stage stage) {
    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/display_dimensions/style.css")).toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Display Dimensions Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // set up display label for dimension layer name, and check boxes for controlling visibility and definition expression
      Label dimensionLayerName = new Label();
      CheckBox visibilityCheckBox = new CheckBox("Dimension Layer visibility");
      visibilityCheckBox.setSelected(true);
      CheckBox defExpressionCheckBox = new CheckBox("Definition Expression:" + "\n" + "Dimensions >= 450m");
      defExpressionCheckBox.setWrapText(true);

      // add the label and checkboxes to a JavaFX VBox
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.5)"), CornerRadii.EMPTY,
        Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(220, 120);
      controlsVBox.getStyleClass().add("panel-region");
      controlsVBox.getChildren().addAll(dimensionLayerName, visibilityCheckBox, defExpressionCheckBox);
      controlsVBox.setDisable(true);

      // create a map view
      mapView = new MapView();

      // create and load a mobile map package
      final String mmpkPath = new File(System.getProperty("data.dir"), "./samples-data/mmpk/EdinburghPylonsDimensions.mmpk").getAbsolutePath();
      mobileMapPackage = new MobileMapPackage(mmpkPath);

      mobileMapPackage.addDoneLoadingListener(() -> {
        // check the mmpk has loaded successfully and that it contains a map
        if (mobileMapPackage.getLoadStatus() == LoadStatus.LOADED && !mobileMapPackage.getMaps().isEmpty()) {
          // add the map from the mobile map package to the map view, and set a min scale to maintain dimension readability
          mapView.setMap(mobileMapPackage.getMaps().get(0));
          mapView.getMap().setMinScale(35000);

          // find the dimension layer within the map
          for (Layer layer : mapView.getMap().getOperationalLayers()) {
            if (layer instanceof DimensionLayer) {
              dimensionLayer = (DimensionLayer) layer;
              // set the label to the name of the dimension layer
              dimensionLayerName.setText(dimensionLayer.getName());
              // enable the vbox for dimension layer controls
              controlsVBox.setDisable(false);
              visibilityCheckBox.setSelected(dimensionLayer.isVisible());
            }
          }
        } else {
          new Alert(Alert.AlertType.ERROR, "Failed to load the mobile map package").show();
        }
      });
      mobileMapPackage.loadAsync();

      // set a definition expression to show dimension lengths of greater than or equal to 450m when the checkbox is selected,
      // or to reset the definition expression to show all dimension lengths when unselected
      defExpressionCheckBox.setOnAction(e -> {
        String defExpression = defExpressionCheckBox.isSelected() ? "DIMLENGTH >= 450" : "";
        dimensionLayer.setDefinitionExpression(defExpression);
      });

      // set the visibility of the dimension layer
      visibilityCheckBox.setOnAction(e -> dimensionLayer.setVisible(visibilityCheckBox.isSelected()));

      // add the map view to stack pane
      stackPane.getChildren().addAll(mapView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_LEFT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));
    } catch(Exception e){
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
