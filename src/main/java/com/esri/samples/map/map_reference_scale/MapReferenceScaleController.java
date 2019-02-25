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

package com.esri.samples.map.map_reference_scale;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;

import javafx.fxml.FXML;
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
  private Label loadingLabel;
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
    mapView.addMapScaleChangedListener(event -> {
      scaleLabel.setText("Current Map Scale 1:" + Math.round(mapView.getMapScale()));
    });

    map.addDoneLoadingListener(() -> {

      progressIndicator.setVisible(false);

      LayerList operationalLayers = map.getOperationalLayers();

      for (Layer layer : operationalLayers) {
        // create a checkbox per operational layer name
        CheckBox checkBox = new CheckBox(layer.getName());
        checkBox.setSelected(true);
        // add the checkboxes to the VBox
        layerVBox.getChildren().add(checkBox);

        if (layer instanceof FeatureLayer) {

          FeatureLayer featureLayer = (FeatureLayer) layer;
          // set if the feature layer will honor the reference scale
          checkBox.setOnAction(event -> {
            featureLayer.setScaleSymbols(checkBox.isSelected());
          });
        }
      }

      scaleVBox.setVisible(true);
      layerVBox.setVisible(true);

      loadingLabel.setText("Apply Reference Scale");
    });
  }

  /**
   * Takes the reference scale from the combobox, and sets it as the map's reference scale.
   */
  @FXML
  private void handleScaleButtonClicked() {

    // set the reference scale to that selected from the combo box
    map.setReferenceScale(scaleComboBox.getSelectionModel().getSelectedItem());
    // get the center point of the current view
    Point centerPoint = mapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();
    // get the current reference scale of the map
    double currentReferenceScale = mapView.getMap().getReferenceScale();
    // set a new view point passing in the center point and reference scale
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
