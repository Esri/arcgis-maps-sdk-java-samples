package com.esri.samples.raster.raster_layer_url;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.raster.ImageServiceRaster;

public class RasterLayerURLSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Raster Layer URL Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create an image service raster from an online raster service
      ImageServiceRaster imageServiceRaster = new ImageServiceRaster("http://sampleserver6.arcgisonline" +
          ".com/arcgis/rest/services/NLCDLandCover2001/ImageServer");

      // create a raster layer
      RasterLayer rasterLayer = new RasterLayer(imageServiceRaster);

      // create a map with dark canvas vector basemap
      ArcGISMap map = new ArcGISMap(Basemap.createDarkGrayCanvasVector());

      // add the map to a map view
      mapView = new MapView();
      mapView.setMap(map);

      // add the raster layer as an operational layer
      map.getOperationalLayers().add(rasterLayer);

      // set viewpoint on the raster
      rasterLayer.addDoneLoadingListener(() -> {
        if (rasterLayer.getLoadStatus() == LoadStatus.LOADED) {
          mapView.setViewpointGeometryAsync(rasterLayer.getFullExtent(), 150);
        } else {
          Alert alert = new Alert(Alert.AlertType.ERROR, "Raster Layer Failed to Load!");
          alert.show();
        }
      });

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
