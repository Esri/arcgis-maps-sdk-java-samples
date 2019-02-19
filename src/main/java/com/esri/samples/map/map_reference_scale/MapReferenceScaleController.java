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
  private ComboBox<Integer> scaleComboBox;
  @FXML
  private Button setMapToRefScaleButton;
  @FXML
  private CheckBox checkBox;
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

    // create a label to display current scale of the map
    mapView.addMapScaleChangedListener(event -> {
      Long mapScale = Math.round(mapView.getMapScale());
      String mapScaleString = mapScale.toString();
      scaleLabel.setText("Current Map Scale 1:" + mapScaleString);

    });

    // set up the combobox to have 1:50k, 100k, 250k and 500k reference scales
    scaleComboBox.getItems().addAll(50000, 100000, 250000, 500000);
    scaleComboBox.getSelectionModel().select(2);

    setMapToRefScaleButton.setOnAction(event -> {

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

    });


    map.addDoneLoadingListener(() -> {
      createNewCheckBox();
      loadingLabel.setText("Apply Reference Scale");


    });

  }

  @FXML
  private void createNewCheckBox() {

    LayerList operationalLayers = map.getOperationalLayers();



    for (Layer layer : operationalLayers) {
      CheckBox layerInCheckBox = new CheckBox(layer.getName());
      layerInCheckBox.setSelected(true);


      for (int i = 0; i < operationalLayers.size(); i++){
        FeatureLayer featureLayer = (FeatureLayer) operationalLayers.get(i);

        layerInCheckBox.setOnAction(e->{
          System.out.println(featureLayer.getName());
          featureLayer.setScaleSymbols(layerInCheckBox.isSelected());
        });

      }

      vBox.getChildren().add(layerInCheckBox);


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
