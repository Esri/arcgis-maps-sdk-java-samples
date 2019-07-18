/*
 * Copyright 2019 Esri.
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

package com.esri.samples.map_reference_scale;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class MapReferenceScaleController {

  @FXML
  private MapView mapView;
  @FXML
  private Label scaleLabel;
  @FXML
  private ComboBox<Double> scaleComboBox;
  @FXML
  private VBox layerVBox;
  @FXML
  private VBox scaleVBox;
  @FXML
  private ProgressIndicator progressIndicator;

  private ArcGISMap map;

  @FXML
  private void initialize() {

    // access a web map as a portal item
    Portal portal = new Portal("http://runtime.maps.arcgis.com");
    PortalItem portalItem = new PortalItem(portal, "3953413f3bd34e53a42bf70f2937a408");

    // create a map with the portal item
    map = new ArcGISMap(portalItem);

    // set the map to the map view
    mapView.setMap(map);
    map.setReferenceScale(250000);

    scaleComboBox.setConverter(new StringConverter<>() {
      @Override
      public String toString(Double value) {
        return "1:" + Math.round(value);
      }

      @Override
      public Double fromString(String string) {
        // not required
        return null;
      }
    });

    // create a label to display current scale of the map
    mapView.addMapScaleChangedListener(event ->
      scaleLabel.setText("Current Map Scale 1:" + Math.round(mapView.getMapScale()))
    );

    map.addDoneLoadingListener(() -> {
      if (map.getLoadStatus() == LoadStatus.LOADED) {

        // remove progress indicator when the map has loaded
        progressIndicator.setVisible(false);

        // create a check box for each feature layer in the map
        for (Layer layer : map.getOperationalLayers()) {
          if (layer instanceof FeatureLayer) {
            FeatureLayer featureLayer = (FeatureLayer) layer;
            CheckBox checkBox = new CheckBox(featureLayer.getName());
            checkBox.setSelected(true);
            layerVBox.getChildren().add(checkBox);
            // make the feature layer honor the reference scale if the check box is selected
            checkBox.setOnAction(event -> featureLayer.setScaleSymbols(checkBox.isSelected()));
          }
        }
        scaleVBox.setVisible(true);
        layerVBox.setVisible(true);

      } else {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Map Failed to Load!");
        alert.show();
      }
    });
  }

  /**
   * Set the map's reference scale to the scale selected in the combo box.
   */
  @FXML
  private void handleComboBoxSelection() {

    map.setReferenceScale(scaleComboBox.getSelectionModel().getSelectedItem());
  }

  /**
   * Takes the reference scale from the combobox, and sets it as the map's reference scale.
   */
  @FXML
  private void handleScaleButtonClicked() {

    // get the center of the current viewpoint extent
    Point centerPoint = mapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();
    // get the map's current reference scale
    double currentReferenceScale = mapView.getMap().getReferenceScale();
    // set a viewpoint with the scale at the map's reference scale
    Viewpoint newViewPoint = new Viewpoint(centerPoint, currentReferenceScale);
    // set new view point
    mapView.setViewpointAsync(newViewPoint);
  }

  /**
   * Stops the animation and disposes of application resources.
   */
  void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }
}
