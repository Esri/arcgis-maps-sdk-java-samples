package com.esri.samples.localserver.local_server_dynamic_workspace_raster;

import java.io.File;
import java.util.Collections;
import java.util.List;

import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISMapImageSublayer;
import com.esri.arcgisruntime.layers.RasterSublayerSource;
import com.esri.arcgisruntime.localserver.DynamicWorkspace;
import com.esri.arcgisruntime.localserver.LocalMapService;
import com.esri.arcgisruntime.localserver.LocalServer;
import com.esri.arcgisruntime.localserver.LocalServerStatus;
import com.esri.arcgisruntime.localserver.RasterWorkspace;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class LocalServerDynamicWorkspaceRasterSample extends Application {

  private MapView mapView;
  private static final LocalServer server = LocalServer.INSTANCE;

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // create stack pane and application scene
      StackPane stackPane = new StackPane();
      Scene scene = new Scene(stackPane);
      scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // set title, size, and add scene to stage
      stage.setTitle("Local Server Dynamic Workspace Raster Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(scene);
      stage.show();

      // create a control panel
      VBox vBoxControl = new VBox(6);
      vBoxControl.setMaxSize(240, 185);
      vBoxControl.getStyleClass().add("panel-region");

      // create the descriptive label
      Label description = new Label("This application shows how to create a dynamic workspace connection " +
              "to a raster folder and display raster data in a map. Click the " +
              "button to select a local raster and add it to the map.");
      description.setWrapText(true);

      // create a file chooser to select a raster
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Open Resource File");
      fileChooser.getExtensionFilters().addAll(
          new ExtensionFilter("Image Files", "*.tif"));
      fileChooser.setInitialDirectory(new File("./samples-data/raster/"));

      // create button to open file chooser
      Button addButton = new Button("Choose Raster");
      addButton.setMaxWidth(Double.MAX_VALUE);
      addButton.setDisable(true);
      addButton.setOnAction(e -> {
       File rasterFile = fileChooser.showOpenDialog(stage);
       if(rasterFile != null){
         startLocalService(rasterFile);
       }
      });

      // add button to the control panel
      vBoxControl.getChildren().addAll(description, addButton);

      // create a map and add it to a map view
      ArcGISMap map = new ArcGISMap(Basemap.createTopographic());
      mapView = new MapView();
      mapView.setMap(map);

      // start the local server
      server.startAsync().addDoneListener(() -> {
        if (server.getStatus() == LocalServerStatus.STARTED) {
          addButton.setDisable(false);
        }
      });

      // add the map view and control panel to stack pane
      stackPane.getChildren().addAll(mapView, vBoxControl);
      StackPane.setAlignment(vBoxControl, Pos.TOP_LEFT);
      StackPane.setMargin(vBoxControl, new Insets(10, 0, 0, 10));

    } catch (Exception e) {
      // on any error, display the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Start a LocalMapService with the Raster File.
   *
   * @param rasterFile a raster file
   */
  private void startLocalService(File rasterFile) {
    // start a service from a blank map package
    String mapServiceURL = "./samples-data/local_server/mpk_blank.mpk";
    LocalMapService localMapService = new LocalMapService(mapServiceURL);

    // create a raster workspace
    String fileName = rasterFile.getName();
    String path = rasterFile.getParent();
    RasterWorkspace rasterWorkspace = new RasterWorkspace("raster_wkspc", path);
    RasterSublayerSource source = new RasterSublayerSource(rasterWorkspace.getId(), fileName);
    ArcGISMapImageSublayer imageSublayer = new ArcGISMapImageSublayer(101, source);
    List<DynamicWorkspace> dynamicWorkspaces = Collections.singletonList(rasterWorkspace);
    localMapService.setDynamicWorkspaces(dynamicWorkspaces);

    // start the local map service
    localMapService.startAsync();
    localMapService.addStatusChangedListener(event -> {
      if (event.getNewStatus() == LocalServerStatus.STARTED) {
        // create a map image layer with the URL of the local map service
        ArcGISMapImageLayer imageLayer = new ArcGISMapImageLayer(localMapService.getUrl());

        // add the sublayer to the image layer
        imageLayer.getSublayers().add(imageSublayer);
        imageLayer.loadAsync();

        // add the image layer to the map as an operational layer
        mapView.getMap().getOperationalLayers().add(imageLayer);
        mapView.setViewpoint(new Viewpoint(imageLayer.getFullExtent()));
      }
    });
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
