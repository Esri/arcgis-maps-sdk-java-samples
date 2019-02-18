package com.esri.samples.map.map_reference_scale;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class MapReferenceScaleController {

  @FXML private MapView mapView;
  @FXML private Label scaleLabel;

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

    // create a label to display current scale of the map
    mapView.addMapScaleChangedListener(event -> {
      Long mapScale = Math.round(mapView.getMapScale());
      String mapScaleString = mapScale.toString();
      scaleLabel.setText("Current map scale 1:" + mapScaleString);
    });

    mapView.addMapScaleChangedListener(event -> {
      System.out.println("1:" + Math.round(mapView.getMapScale()));
    });



  }

  @FXML
  private void updateLabel(){




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
