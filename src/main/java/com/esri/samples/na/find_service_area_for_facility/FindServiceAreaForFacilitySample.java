package com.esri.samples.na.find_service_area_for_facility;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaFacility;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaPolygon;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaPolygonDetail;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaResult;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaTask;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
    GraphicsOverlay serviceAreasGraphicsOverlay = new GraphicsOverlay();
    GraphicsOverlay facilitiesGraphicsOverlay = new GraphicsOverlay();

    // add the graphics overlays to the map view
    mapView.getGraphicsOverlays().addAll(Arrays.asList(serviceAreasGraphicsOverlay, facilitiesGraphicsOverlay));

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

    // create a list to hold all facilities
    ArrayList<ServiceAreaFacility> serviceAreaFacilitiesArrayList = new ArrayList<>();

    // wait for the facilities feature table to load
    facilitiesFeatureLayer.addDoneLoadingListener(()->{
      if (facilitiesFeatureLayer.getLoadStatus() == LoadStatus.LOADED){

        // zoom to the extent of the feature layer
        mapView.setViewpointGeometryAsync(facilitiesFeatureLayer.getFullExtent(), 90);

        // enable the 'find service areas' button
        findServiceAreasButton.setDisable(false);

        // resolve 'find service areas' button click
        findServiceAreasButton.setOnAction(event -> {

          // show the progress indicator
          progressIndicator.setVisible(true);

          // create default service area task parameters
          ListenableFuture<ServiceAreaParameters> serviceAreaTaskParametersFuture = serviceAreaTask.createDefaultParametersAsync();
          serviceAreaTaskParametersFuture.addDoneListener(() -> {
            try {
              ServiceAreaParameters serviceAreaParameters = serviceAreaTaskParametersFuture.get();
              serviceAreaParameters.setPolygonDetail(ServiceAreaPolygonDetail.HIGH);
              serviceAreaParameters.setReturnPolygons(true);
              // add another service area of 2 minutes (default service area is 5 minutes)
              serviceAreaParameters.getDefaultImpedanceCutoffs().addAll(Collections.singletonList(2.0));

              // create query parameters to select all features
              QueryParameters queryParameters = new QueryParameters();
              queryParameters.setWhereClause("1=1");

              // create a list of the facilities from the feature table
              ListenableFuture<FeatureQueryResult> featureQueryResultFuture = facilitiesFeatureTable.queryFeaturesAsync(queryParameters);
              featureQueryResultFuture.addDoneListener(() -> {
                try {
                  FeatureQueryResult featureQueryResult = featureQueryResultFuture.get();

                  // add the found facilities to the list
                  for (Feature facilityFeature : featureQueryResult){
                    serviceAreaFacilitiesArrayList.add(new ServiceAreaFacility(facilityFeature.getGeometry().getExtent().getCenter()));
                  }

                  // add the facilities to the service area parameters
                  serviceAreaParameters.setFacilities(serviceAreaFacilitiesArrayList);

                  // find the service areas around the facilities using the parameters
                  ListenableFuture<ServiceAreaResult> serviceAreaResultFuture = serviceAreaTask.solveServiceAreaAsync(serviceAreaParameters);
                  serviceAreaResultFuture.addDoneListener(()->{
                    try {
                      // display all the service areas that were found to the map view
                      List<Graphic> serviceAreaGraphics = serviceAreasGraphicsOverlay.getGraphics();
                      ServiceAreaResult serviceAreaResult = serviceAreaResultFuture.get();
                      for (int i = 0 ; i < serviceAreaFacilitiesArrayList.size(); i++) {
                        List<ServiceAreaPolygon> serviceAreaPolygonList = serviceAreaResult.getResultPolygons(i);

                        // we may have more than one resulting service area, so create a graphics from each available polygon
                        for (int j = 0; j < serviceAreaPolygonList.size(); j ++){
                          serviceAreaGraphics.add(new Graphic(serviceAreaPolygonList.get(j).getGeometry(), fillSymbols.get(j%2)));
                        }
                      }
                      // enable the reset button
                      resetButton.setDisable(false);

                    } catch (ExecutionException | InterruptedException e) {
                      if (e.getMessage().contains("Unable to complete operation")) {
                        new Alert(Alert.AlertType.ERROR, "Facility not within San Diego area!").show();
                      } else {
                        e.printStackTrace();
                      }
                    }
                    // hide the progress indicator after the task is complete
                    progressIndicator.setVisible(false);
                  });
                } catch (ExecutionException | InterruptedException e) {
                  e.printStackTrace();
                }
              });

            } catch (ExecutionException | InterruptedException e) {
              e.printStackTrace();
            }
          });

        });
      }
      // resolve 'reset button click'
      resetButton.setOnAction(event -> {
        // clear the graphics overlays
        facilitiesGraphicsOverlay.getGraphics().clear();
        serviceAreasGraphicsOverlay.getGraphics().clear();

        // toggle the buttons
        findServiceAreasButton.setDisable(false);
        findServiceAreasButton.setDisable(true);
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
