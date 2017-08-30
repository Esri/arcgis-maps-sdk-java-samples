package com.esri.samples.raster.raster_function;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
import com.esri.arcgisruntime.raster.Raster;
import com.esri.arcgisruntime.raster.RasterFunction;
import com.esri.arcgisruntime.raster.RasterFunctionArguments;

public class RasterFunctionSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);

      // set title, size, and add scene to stage
      stage.setTitle("Raster Function Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a map with dark canvas vector basemap
      ArcGISMap map = new ArcGISMap(Basemap.createDarkGrayCanvasVector());

      // add the map to a map view
      mapView = new MapView();
      mapView.setMap(map);

      // create an image service raster from an online raster service
      ImageServiceRaster imageServiceRaster = new ImageServiceRaster("http://sampleserver6.arcgisonline" +
          ".com/arcgis/rest/services/NLCDLandCover2001/ImageServer");
      imageServiceRaster.loadAsync();
      imageServiceRaster.addDoneLoadingListener(() -> {

          if (imageServiceRaster.getLoadStatus() == LoadStatus.LOADED) {
            // create raster function from local json file
            File jsonFile = new File("./samples-data/raster/hillshade_simplified.json");
            try (Scanner scanner = new Scanner(jsonFile)) {
              // read in the complete file as a string
              String json = scanner.useDelimiter("\\A").next();
              RasterFunction rasterFunction = RasterFunction.fromJson(json);
              RasterFunctionArguments arguments = rasterFunction.getArguments();
              // apply the raster function
              arguments.setRaster(arguments.getRasterNames().get(0), imageServiceRaster);
              // create a new raster from the function definition
              Raster raster = new Raster(rasterFunction);
              // create raster layer and add to map as operational layer
              RasterLayer hillshadeLayer = new RasterLayer(raster);
              // add the hillshade raster layer to the map
              map.getOperationalLayers().add(hillshadeLayer);
              hillshadeLayer.addDoneLoadingListener(() -> {
                if (hillshadeLayer.getLoadStatus() == LoadStatus.LOADED) {
                  // set viewpoint on the raster
                  mapView.setViewpointGeometryAsync(hillshadeLayer.getFullExtent(), 150);
                } else {
                  Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load the hillshade raster layer");
                  alert.show();
                }
              });
            } catch (FileNotFoundException e) {
              Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to locate raster function json");
              alert.show();
            }
          } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load image service raster");
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
