package com.esri.samples.na.find_service_area_for_facility;

import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaTask;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FindServiceAreaForFacilitySample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Find Route Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create a control panel
    VBox controlPanelVBox = new VBox();

    // create buttons
    Button findServiceAreasButton = new Button("Find Service Areas");
    findServiceAreasButton.setDisable(true);
    Button resetButton = new Button("Reset");
    resetButton.setDisable(true);

    // add the buttons to the control panel
    controlPanelVBox.getChildren().addAll(findServiceAreasButton, resetButton);

    // create a progress indicator
    ProgressIndicator progressIndicator = new ProgressIndicator();
    progressIndicator.setVisible(false);

    // create a ArcGISMap with a streets basemap
    ArcGISMap map = new ArcGISMap(Basemap.createStreets());

    // set the ArcGISMap to be displayed in the view
    mapView = new MapView();
    mapView.setMap(map);

    // create graphics overlays for facilities and service areas
    GraphicsOverlay facilitiesGraphicsOverlay = new GraphicsOverlay();
    GraphicsOverlay serviceAreasGraphicsOverlay = new GraphicsOverlay();

    // add the graphics overlays to the map view
    mapView.getGraphicsOverlays().addAll(Arrays.asList(facilitiesGraphicsOverlay, serviceAreasGraphicsOverlay));

    // create a symbol to mark service area outline
    SimpleLineSymbol serviceAreaOutlineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF000000, 3.0f);
    // create a fill symbols to fill the service area outline with a color
    List<SimpleFillSymbol> fillSymbols = new ArrayList<>();
    fillSymbols.add(new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x66FF0000, serviceAreaOutlineSymbol));
    fillSymbols.add(new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x66FFA500, serviceAreaOutlineSymbol));

    // create an icon to display the facilites to the map view
    PictureMarkerSymbol facilitySymbol = new PictureMarkerSymbol("http://static.arcgis.com/images/Symbols/SafetyHealth/Hospital.png");
    facilitySymbol.setHeight(30);
    facilitySymbol.setWidth(30);

    // create a service area task from URL
    ServiceAreaTask serviceAreaTask = new ServiceAreaTask("https://sampleserver6.arcgisonline.com/arcgis/rest/services/NetworkAnalysis/SanDiego/NAServer/ServiceArea");
    serviceAreaTask.loadAsync();

    // create a feature table of facilities using a FeatureServer
    FeatureTable facilitiesFeatureTable = new ServiceFeatureTable("https://services2.arcgis.com/ZQgQTuoyBrtmoGdP/ArcGIS/rest/services/San_Diego_Facilities/FeatureServer/0");
    // create a feature layer from the table, apply facilities icon
    FeatureLayer facilitiesFeatureLayer = new FeatureLayer(facilitiesFeatureTable);
    facilitiesFeatureLayer.setRenderer(new SimpleRenderer(facilitySymbol));
    // add the feature layer to the map
    map.getOperationalLayers().add(facilitiesFeatureLayer);

    // wait for the facilities feature table to load
    facilitiesFeatureLayer.addDoneLoadingListener(()->{
      if (facilitiesFeatureLayer.getLoadStatus() == LoadStatus.LOADED){

        // zoom to the extent of the feature layer
        mapView.setViewpointGeometryAsync(facilitiesFeatureLayer.getFullExtent(), 90);

        // enable the 'find service areas' button
        findServiceAreasButton.setDisable(false);

        // resolve 'find service areas' button click
        findServiceAreasButton.setOnAction(event -> {


        });
      }
      // resolve 'reset button click'
      resetButton.setOnAction(event -> {
        facilitiesGraphicsOverlay.getGraphics().clear();
        serviceAreasGraphicsOverlay.getGraphics().clear();
      });

    });

    // add the map view, control panel and progress indicator to stack pane
    stackPane.getChildren().addAll(mapView, controlPanelVBox, progressIndicator);
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
