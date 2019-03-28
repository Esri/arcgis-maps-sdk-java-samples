package com.esri.samples.ogc.display_wfs_layer;

import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.internal.jni.CoreWFSFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.ogc.wfs.WfsFeatureTable;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;

public class DisplayWFSLayerSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    // create stack pane and JavaFX app scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);

    // set title, size, and add JavaFX scene to stage
    stage.setTitle("Display a WFS Layer");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create an ArcGISMap with topographic basemap
    ArcGISMap map = new ArcGISMap(Basemap.createTopographic());

    // create a map view and set the map to it
    mapView = new MapView();
    mapView.setMap(map);

    String serviceUrl = "https://dservices2.arcgis.com/ZQgQTuoyBrtmoGdP/arcgis/services/Seattle_Downtown_Features/WFSServer?service=wfs&request=getcapabilities";
    String LayerName = "Seattle_Downtown_Features:Buildings";

    // create a FeatureTable from the WFS service URL and name of the layer
    WfsFeatureTable wfsFeatureTable = new WfsFeatureTable(serviceUrl, LayerName);

    // set the feature request mode to manual - only manual is supported at v100.5
    // In this mode, you must manually populate the table - panning and zooming won't request features automatically.
    wfsFeatureTable.setFeatureRequestMode(ServiceFeatureTable.FeatureRequestMode.MANUAL_CACHE);

    // load the table
    wfsFeatureTable.loadAsync();

    // create a feature layer to visualize the WFS features
    FeatureLayer wfsFeatureLayer = new FeatureLayer(wfsFeatureTable);

    // apply a renderer to the feature layer
    SimpleRenderer renderer = new SimpleRenderer(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 3));
    wfsFeatureLayer.setRenderer(renderer);

    // add the layer to the map's operational layers
    map.getOperationalLayers().add(wfsFeatureLayer);
    System.out.println(map.getOperationalLayers().size());

    // show a progress indicator while the layer loads
    ProgressIndicator progressIndicator = new ProgressIndicator();
    progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
    progressIndicator.setMaxSize(25, 25);

    mapView.addDrawStatusChangedListener(event -> {
      System.out.println(event.getDrawStatus());
      progressIndicator.setVisible(false);



    });

    // use the navigation completed event to populate the table with the features needed for the current extent
    mapView.addViewpointChangedListener(e -> {

      Envelope currentExtent = mapView.getVisibleArea().getExtent();

      // create a query based on the current visible extent
      QueryParameters visibleExtentQuery = new QueryParameters();
      visibleExtentQuery.setGeometry(currentExtent);
      visibleExtentQuery.setSpatialRelationship(QueryParameters.SpatialRelationship.INTERSECTS);

      wfsFeatureTable.populateFromServiceAsync(visibleExtentQuery, false, null);

    });
//
//    mapView.addNavigationChangedListener(navigationChangedEvent -> {
//
//      Envelope currentExtent = mapView.getVisibleArea().getExtent();
//
//      // create a query based on the current visible extent
//      QueryParameters visibleExtentQuery = new QueryParameters();
//      visibleExtentQuery.setGeometry(currentExtent);
//      visibleExtentQuery.setSpatialRelationship(QueryParameters.SpatialRelationship.INTERSECTS);
//
//      wfsFeatureTable.populateFromServiceAsync(visibleExtentQuery, false, null);
//
//    });

    Point topLeft = new Point(-122.341581, 47.617207, SpatialReferences.getWgs84());
    Point bottomRight = new Point(-122.332662, 47.613758, SpatialReferences.getWgs84());

    mapView.setViewpointGeometryAsync(new Envelope(topLeft, bottomRight));


    // add the mapview to the stackpane
    stackPane.getChildren().addAll(mapView, progressIndicator);

  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {
    // release resources when the application closes
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
