package com.esri.samples.ogc.wfs_xml_query;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.ogc.wfs.OgcAxisOrder;
import com.esri.arcgisruntime.ogc.wfs.WfsFeatureTable;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class WfsXmlQuerySample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    // create stack pane and JavaFX app scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);

    // set title, size, and add JavaFX scene to stage
    stage.setTitle("Load WFS with XML Query");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a progress indicator
    ProgressIndicator progressIndicator = new ProgressIndicator();
    progressIndicator.setVisible(true);

    // create an ArcGISMap with topographic basemap and set it to the map view
    ArcGISMap map = new ArcGISMap(Basemap.createNavigationVector());
    mapView = new MapView();
    mapView.setMap(map);

    // define the WFS service URL and layer name
    String serviceUrl = "https://dservices2.arcgis.com/ZQgQTuoyBrtmoGdP/arcgis/services/Seattle_Downtown_Features/WFSServer?service=wfs&request=getcapabilities";
    String LayerName = "Seattle_Downtown_Features:Trees";
    // create a FeatureTable from the WFS service URL and name of the layer
    WfsFeatureTable wfsFeatureTable = new WfsFeatureTable(serviceUrl, LayerName);

    // set the feature request mode and axis order
    wfsFeatureTable.setAxisOrder(OgcAxisOrder.NO_SWAP);
    wfsFeatureTable.setFeatureRequestMode(ServiceFeatureTable.FeatureRequestMode.MANUAL_CACHE);

    // create a feature layer to visualize the WFS features
    FeatureLayer wfsFeatureLayer = new FeatureLayer(wfsFeatureTable);

    // add the layer to the map's operational layers
    map.getOperationalLayers().add(wfsFeatureLayer);

    // create an XML query to retrieve trees of genus Tilia.
    // To learn more about specifying filters in OGC technologies, see https://www.opengeospatial.org/standards/filter.
    String xmlQuery = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<wfs:GetFeature service=\"WFS\" version=\"2.0.0\"\n" +
            "    xmlns:Seattle_Downtown_Features=\"https://dservices2.arcgis.com/ZQgQTuoyBrtmoGdP/arcgis/services/Seattle_Downtown_Features/WFSServer\"\n" +
            "    xmlns:wfs=\"http://www.opengis.net/wfs/2.0\"\n" +
            "    xmlns:fes=\"http://www.opengis.net/fes/2.0\"\n" +
            "    xmlns:gml=\"http://www.opengis.net/gml/3.2\">\n" +
            "    <wfs:Query typeNames=\"Seattle_Downtown_Features:Trees\">\n" +
            "        <fes:Filter>\n" +
            "            <fes:PropertyIsLike wildCard=\"*\" escapeChar=\"\\\">\n" +
            "                <fes:ValueReference>Trees:SCIENTIFIC</fes:ValueReference>\n" +
            "                <fes:Literal>Tilia *</fes:Literal>\n" +
            "            </fes:PropertyIsLike>\n" +
            "        </fes:Filter>\n" +
            "    </wfs:Query>\n" +
            "</wfs:GetFeature>";

    // populate the WFS feature table with XML query
    ListenableFuture<FeatureQueryResult> featureQueryResultListenableFuture = wfsFeatureTable.populateFromServiceAsync(xmlQuery, true);
    featureQueryResultListenableFuture.addDoneListener(() -> {
      // set the viewpoint of the map view to the extent reported by the feature layer
      try {
        mapView.setViewpointGeometryAsync(wfsFeatureLayer.getFullExtent(), 50);
        progressIndicator.setVisible(false);
      } catch (Exception ex) {
        new Alert(Alert.AlertType.ERROR, "Failed to populate table from XML query").show();
      }

    });

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
