package com.esri.samples.ogc.wmts_layer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.layers.WmtsLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.ogc.wmts.WmtsService;
import com.esri.arcgisruntime.ogc.wmts.WmtsServiceInfo;

public class WmtsLayerSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("WMTS Layer Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map and add it to the map view
      ArcGISMap map = new ArcGISMap();
      mapView = new MapView();
      mapView.setMap(map);

      // create a WMTS service from a URL
      String serviceURL = "http://sampleserver6.arcgisonline.com/arcgis/rest/services/WorldTimeZones/MapServer/WMTS";
      WmtsService wmtsService = new WmtsService(serviceURL);
      wmtsService.addDoneLoadingListener(() -> {
        if (wmtsService.getLoadStatus() == LoadStatus.LOADED) {
          WmtsServiceInfo wmtsServiceInfo = wmtsService.getServiceInfo();
          // get the first layer's ID
          List<WmtsLayerInfo> layerInfos = wmtsServiceInfo.getLayerInfos();
          // create the WMTS layer with the LayerInfo
          WmtsLayer wmtsLayer = new WmtsLayer(layerInfos.get(0));
          map.setBasemap(new Basemap(wmtsLayer));
        } else {
          Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load WMTS layer");
          alert.show();
        }
      });
      wmtsService.loadAsync();

      // add the map view to stack pane
      stackPane.getChildren().addAll(mapView);
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
