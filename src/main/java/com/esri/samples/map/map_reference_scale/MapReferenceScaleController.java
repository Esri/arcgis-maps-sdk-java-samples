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
import javafx.scene.control.Button;
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
  private Button setMapToRefScaleButton;
  @FXML
  private CheckBox layerInCheckBox;
  @FXML
  private VBox vBox;

  private ArcGISMap map;
  private FeatureLayer featureLayer;

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

    // create a label to display current scale of the map
    mapView.addMapScaleChangedListener(event -> {
      Long mapScale = Math.round(mapView.getMapScale());
      String mapScaleString = mapScale.toString();
      scaleLabel.setText("Current Map Scale 1:" + mapScaleString);

    });

    // set up the combobox to have 1:50k, 100k, 250k and 500k reference scales
    scaleComboBox.getItems().addAll("1:50000", "1:100000", "1:250000", "1:500000");
    scaleComboBox.getSelectionModel().select(2);

    setMapToRefScaleButton.setOnAction(event -> {

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

    });


    map.addDoneLoadingListener(() -> {
      populateCheckBoxList();
      setScaleSymbolsForMapLayers(layerInCheckBox);
      loadingLabel.setText("Apply Reference Scale");
    });

  }

  // populate a list of checkboxes based on layers in the map
  private void populateCheckBoxList(){

    LayerList operationalLayers = map.getOperationalLayers();

    for (Layer layer : operationalLayers) {
      layerInCheckBox = new CheckBox(layer.getName());
      layerInCheckBox.setSelected(true);
      vBox.getChildren().add(layerInCheckBox);
    }
  }

  @FXML
  private void setScaleSymbolsForMapLayers(CheckBox checkBox) {

    LayerList operationalLayers = map.getOperationalLayers();


    for (int i = 0; i < operationalLayers.size(); i++) {
      featureLayer = (FeatureLayer) operationalLayers.get(i);
      System.out.println("Get the feature layers name: " + featureLayer.getName());

      checkBox.setOnAction(e -> {
        System.out.println("Checked item " + featureLayer.getName());
        featureLayer.setScaleSymbols(checkBox.isSelected());

      });

      }

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
