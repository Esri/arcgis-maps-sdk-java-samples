package com.esri.samples.edit_with_branch_versioning;

import javafx.scene.control.Alert;

import javafx.fxml.FXML;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.data.ServiceGeodatabase;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.security.UserCredential;

public class EditWithBranchVersioningController {

  @FXML private MapView mapView;
  private ServiceGeodatabase serviceGeodatabase;
  private ServiceFeatureTable serviceFeatureTable;
  private FeatureLayer featureLayer;

  public void initialize(){

    try {

      // create a map with the imagery basemap
      ArcGISMap map = new ArcGISMap(Basemap.createStreetsVector());

      // create a map view and set its map
      mapView.setMap(map);

      UserCredential userCredential = new UserCredential("editor01", "editor01.password");

      // create and load a ServiceGeodatabase
      serviceGeodatabase = new ServiceGeodatabase("https://sampleserver7.arcgisonline.com/arcgis/rest/services/DamageAssessment/FeatureServer");
      serviceGeodatabase.setCredential(userCredential);
      serviceGeodatabase.loadAsync();
      serviceGeodatabase.addDoneLoadingListener(() -> {

        if (serviceGeodatabase.getLoadStatus() == LoadStatus.LOADED) {

          // when the service geodatabase loads, create a service feature table
          if (serviceGeodatabase.getTable(0) != null) {
            serviceFeatureTable = serviceGeodatabase.getTable(0);
            // create a feature layer from the service feature table and add to the map
            featureLayer = new FeatureLayer(serviceFeatureTable);
            map.getOperationalLayers().add(featureLayer);
            // set the map view to the feature layer full extent
            featureLayer.addDoneLoadingListener(() -> {
              if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
                mapView.setViewpointAsync(new Viewpoint(featureLayer.getFullExtent()));
              }
            });
          }
        } else {
          new Alert(Alert.AlertType.ERROR, "Service geodatabase failed to load.").show();
        }
      });

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Disposes application resources.
   */
  void terminate() {
    if (mapView != null) {
      mapView.dispose();
    }
  }
}
