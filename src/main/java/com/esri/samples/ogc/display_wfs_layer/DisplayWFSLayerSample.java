package com.esri.samples.ogc.display_wfs_layer;

import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.ogc.wfs.WfsFeatureTable;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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

    // create an ArcGISMap with topographic basemap and set it to the map view
    ArcGISMap map = new ArcGISMap(Basemap.createTopographic());
    mapView = new MapView();
    mapView.setMap(map);

    // create an initial extent to load
    Point topLeft = new Point(-122.341581, 47.617207, SpatialReferences.getWgs84());
    Point bottomRight = new Point(-122.336662, 47.613758, SpatialReferences.getWgs84());
    Envelope initialExtent = new Envelope(topLeft, bottomRight);
    mapView.setViewpoint(new Viewpoint(initialExtent));

    String serviceUrl = "https://dservices2.arcgis.com/ZQgQTuoyBrtmoGdP/arcgis/services/Seattle_Downtown_Features/WFSServer?service=wfs&request=getcapabilities";
    String LayerName = "Seattle_Downtown_Features:Buildings";

    // create a FeatureTable from the WFS service URL and name of the layer
    WfsFeatureTable wfsFeatureTable = new WfsFeatureTable(serviceUrl, LayerName);

    // set the feature request mode to manual. The table must be manually populated as panning and zooming won't request features automatically.
    wfsFeatureTable.setFeatureRequestMode(ServiceFeatureTable.FeatureRequestMode.MANUAL_CACHE);

    // create a feature layer to visualize the WFS features
    FeatureLayer wfsFeatureLayer = new FeatureLayer(wfsFeatureTable);

    // apply a renderer to the feature layer
    SimpleRenderer renderer = new SimpleRenderer(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 3));
    wfsFeatureLayer.setRenderer(renderer);

    // add the layer to the map's operational layers
    map.getOperationalLayers().add(wfsFeatureLayer);

    // make an initial call to load the initial extent's data from the WFS, using the WFS spatial reference
    populateFromServer(wfsFeatureTable, (Envelope) GeometryEngine.project(initialExtent, SpatialReference.create(3857)));

    // use the navigation completed event to populate the table with the features needed for the current extent
    mapView.addNavigationChangedListener(navigationChangedEvent -> {
      // once the map view has stopped navigating
      if (!navigationChangedEvent.isNavigating()) {
        populateFromServer(wfsFeatureTable, mapView.getVisibleArea().getExtent());
      }
    });

    // add the mapview to the stackpane
    stackPane.getChildren().addAll(mapView);
  }

  /**
   * Create query parameters using the given extent to populate the WFS table from the service
   * @param wfsTable the WFS feature table to populate
   * @param extent the extent used to define the QueryParameters' geometry
   */
  private void populateFromServer(WfsFeatureTable wfsTable, Envelope extent){

    // create a query based on the current visible extent
    QueryParameters visibleExtentQuery = new QueryParameters();
    visibleExtentQuery.setGeometry(extent);
    visibleExtentQuery.setSpatialRelationship(QueryParameters.SpatialRelationship.INTERSECTS);
    // populate the WFS feature table based on the current extent
    wfsTable.populateFromServiceAsync(visibleExtentQuery, false, null);
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
