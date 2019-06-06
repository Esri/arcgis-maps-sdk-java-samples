package com.esri.samples.na.find_service_areas_for_multiple_facilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DrawStatus;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedEvent;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaPolygon;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaPolygonDetail;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaResult;
import com.esri.arcgisruntime.tasks.networkanalysis.ServiceAreaTask;

public class FindServiceAreasForMultipleFacilitiesSample extends Application {

  private MapView mapView;

  @Override
  public void start(Stage stage) {

    // create stack pane and application scene
    StackPane stackPane = new StackPane();
    Scene scene = new Scene(stackPane);
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

    // set title, size, and add scene to stage
    stage.setTitle("Find Service Area for Facilities Sample");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

    // create control panel
    VBox controlsVBox = new VBox(6);
    controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0,0,0,0.3)"), CornerRadii.EMPTY,
            Insets.EMPTY)));
    controlsVBox.setPadding(new Insets(10.0));
    controlsVBox.setMaxSize(150, 50);

    // create buttons
    Button findServiceAreasButton = new Button("Find Service Areas");
    findServiceAreasButton.setMaxWidth(150);
    findServiceAreasButton.setDisable(true);

    // create a progress indicator
    ProgressIndicator progressIndicator = new ProgressIndicator();
    progressIndicator.setVisible(false);

    // create an ArcGISMap with a streets basemap
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

    // create an icon used to display the facilities
    PictureMarkerSymbol facilitySymbol = new PictureMarkerSymbol("http://static.arcgis.com/images/Symbols/SafetyHealth/Hospital.png");
    facilitySymbol.setHeight(30);
    facilitySymbol.setWidth(30);

    // create a service area task from URL
    ServiceAreaTask serviceAreaTask = new ServiceAreaTask("https://sampleserver6.arcgisonline.com/arcgis/rest/services/NetworkAnalysis/SanDiego/NAServer/ServiceArea");
    serviceAreaTask.loadAsync();

    // create a feature table of facilities using a FeatureServer
    ArcGISFeatureTable facilitiesArcGISFeatureTable = new ServiceFeatureTable("https://services2.arcgis.com/ZQgQTuoyBrtmoGdP/ArcGIS/rest/services/San_Diego_Facilities/FeatureServer/0");
    // create a feature layer from the table, set renderer with the facilities icon
    FeatureLayer facilitiesFeatureLayer = new FeatureLayer(facilitiesArcGISFeatureTable);
    facilitiesFeatureLayer.setRenderer(new SimpleRenderer(facilitySymbol));
    // add the feature layer to the map
    map.getOperationalLayers().add(facilitiesFeatureLayer);

    // wait for the facilities feature layer to load
    facilitiesFeatureLayer.addDoneLoadingListener(() -> {
      if (facilitiesFeatureLayer.getLoadStatus() == LoadStatus.LOADED) {

        // zoom to the extent of the feature layer
        mapView.setViewpointGeometryAsync(facilitiesFeatureLayer.getFullExtent(), 90);

        // wait for the view to zoom to enable the ui
        mapView.addDrawStatusChangedListener(new DrawStatusChangedListener() {
          @Override
          public void drawStatusChanged(DrawStatusChangedEvent drawStatusChangedEvent) {
            if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.COMPLETED) {
              // enable the 'find service areas' button
              findServiceAreasButton.setDisable(false);
              mapView.removeDrawStatusChangedListener(this);
            }
          }
        });

        // resolve 'find service areas' button click
        findServiceAreasButton.setOnAction(event -> {

          // disable the 'find service areas' button
          findServiceAreasButton.setDisable(true);

          // show the progress indicator
          progressIndicator.setVisible(true);

          // create default service area task parameters
          ListenableFuture<ServiceAreaParameters> serviceAreaTaskParametersFuture = serviceAreaTask.createDefaultParametersAsync();
          serviceAreaTaskParametersFuture.addDoneListener(() -> {
            try {
              ServiceAreaParameters serviceAreaParameters = serviceAreaTaskParametersFuture.get();
              // set the task parameters to have the task result return polygons
              serviceAreaParameters.setPolygonDetail(ServiceAreaPolygonDetail.HIGH);
              serviceAreaParameters.setReturnPolygons(true);
              // change the service area to 3 minutes (default service area is 5 minutes)
              serviceAreaParameters.getDefaultImpedanceCutoffs().set(0, 3.0);

              // create query parameters used to select all facilities from the feature table
              QueryParameters queryParameters = new QueryParameters();
              queryParameters.setWhereClause("1=1");

              // add all facilities to the service area parameters
              serviceAreaParameters.setFacilities(facilitiesArcGISFeatureTable, queryParameters);

              // find the service areas around the facilities using the parameters
              ListenableFuture<ServiceAreaResult> serviceAreaResultFuture = serviceAreaTask.solveServiceAreaAsync(serviceAreaParameters);
              serviceAreaResultFuture.addDoneListener(() -> {
                try {
                  // get the task results
                  ServiceAreaResult serviceAreaResult = serviceAreaResultFuture.get();

                  // display all the service areas that were found to the map view
                  List<Graphic> serviceAreaGraphics = serviceAreasGraphicsOverlay.getGraphics();

                  // iterate through all the facilities to get the service area polygons
                  for (int i = 0; i < serviceAreaResult.getFacilities().size(); i++) {
                    List<ServiceAreaPolygon> serviceAreaPolygonList = serviceAreaResult.getResultPolygons(i);

                    // we may have more than one resulting service area, so create a graphics from each available polygon
                    for (int j = 0; j < serviceAreaPolygonList.size(); j++) {
                      // create and show a graphics for the service area
                      serviceAreaGraphics.add(new Graphic(serviceAreaPolygonList.get(j).getGeometry(), fillSymbols.get(j % 2)));
                    }
                  }

                } catch (ExecutionException | InterruptedException e) {
                  new Alert(Alert.AlertType.ERROR, "Error solving the service area task").show();
                }
                // hide the progress indicator after the task is complete
                progressIndicator.setVisible(false);
              });

            } catch (ExecutionException | InterruptedException e) {
              e.printStackTrace();
            }
          });
        });
      }
    });

    // add the map view, control panel and progress indicator to stack pane
    stackPane.getChildren().addAll(mapView, findServiceAreasButton, progressIndicator);
    StackPane.setAlignment(findServiceAreasButton, Pos.TOP_LEFT);
    StackPane.setMargin(findServiceAreasButton, new Insets(10, 0, 0, 10));
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
