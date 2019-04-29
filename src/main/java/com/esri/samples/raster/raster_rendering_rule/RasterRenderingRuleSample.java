package com.esri.samples.raster.raster_rendering_rule;

import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.raster.ImageServiceRaster;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class RasterRenderingRuleSample extends Application {

    private MapView mapView;

    @Override
    public void start(Stage stage) {
        try {
            // create stack pane and application scene
            StackPane stackPane = new StackPane();
            Scene scene = new Scene(stackPane);

            // set title, size, and add scene to stage
            stage.setTitle("Raster Rendering Rule Sample");
            stage.setWidth(800);
            stage.setHeight(700);
            stage.setScene(scene);
            stage.show();

            // create a Streets BaseMap
            ArcGISMap map = new ArcGISMap(Basemap.createStreets());

            // add the map to a new map view
            mapView = new MapView();
            mapView.setMap(map);

            // create an Image Service Raster as a raster layer and add to map
            final ImageServiceRaster imageServiceRaster = new ImageServiceRaster("http://sampleserver6.arcgisonline" +
                    ".com/arcgis/rest/services/NLCDLandCover2001/ImageServer");
            final RasterLayer imageRasterLayer = new RasterLayer(imageServiceRaster);
            map.getOperationalLayers().add(imageRasterLayer);

//            imageServiceRaster.loadAsync();
//            imageServiceRaster.addDoneLoadingListener(() -> {
//            });
//
//
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
    public void stop() {

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
