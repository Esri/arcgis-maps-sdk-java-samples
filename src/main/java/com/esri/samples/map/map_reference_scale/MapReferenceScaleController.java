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
import javafx.scene.layout.VBox;

public class MapReferenceScaleController {

  @FXML
  private MapView mapView;
  @FXML
  private Label scaleLabel;
  @FXML
  private Label loadingLabel;
  @FXML
  private ComboBox<String> scaleComboBox;
  @FXML
  private VBox vBox;

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
    scaleLabel.setText("LOADING...");
    loadingLabel.setText("LOADING...");

    // set up the combobox to have 1:50k, 100k, 250k and 500k reference scales
    scaleComboBox.getItems().addAll("1:50000", "1:100000", "1:250000", "1:500000");
    scaleComboBox.getSelectionModel().select(2);

    // create a label to display current scale of the map
    mapView.addMapScaleChangedListener(event -> {
      Long mapScale = Math.round(mapView.getMapScale());
      String mapScaleString = mapScale.toString();
      scaleLabel.setText("Current Map Scale 1:" + mapScaleString);
    });

    map.addDoneLoadingListener(() -> {
      setScaleSymbolsOnSelectedLayers();
      loadingLabel.setText("Apply Reference Scale");
    });
  }

  /**
   * Sets the scale symbols (i.e. if the layer will honor the reference scale or not)
   * for each layer within the map
   */
  private void setScaleSymbolsOnSelectedLayers(){

    LayerList operationalLayers = map.getOperationalLayers();

    for (Layer layer : operationalLayers) {
      // create a checkbox per operational layer name
      CheckBox checkBox = new CheckBox(layer.getName());
      checkBox.setSelected(true);
      // add the checkboxes to the VBox
      vBox.getChildren().add(checkBox);

      FeatureLayer featureLayer = (FeatureLayer) layer;
      // set if the feature layer will honor the reference scale
      checkBox.setOnAction(event -> {
        featureLayer.setScaleSymbols(checkBox.isSelected());
      });
    }
  }

  /**
   * Takes the reference scale from the combobox, and sets it as the map's reference scale.
   */
  @FXML
  private void handleScaleButtonClicked() {

      // get string value from the combobox, convert to double
      String selectedString = scaleComboBox.getSelectionModel().getSelectedItem();
      String[] splitString = selectedString.split(":");
      String stringMapRefScale = splitString[1];
      double mapRefScale = Double.valueOf(stringMapRefScale);

      // set the reference scale to that selected from the combo box
      map.setReferenceScale(Math.round(mapRefScale));
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
