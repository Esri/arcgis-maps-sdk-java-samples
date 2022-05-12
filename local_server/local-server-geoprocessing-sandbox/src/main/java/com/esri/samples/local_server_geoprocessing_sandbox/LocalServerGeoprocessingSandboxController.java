/*
 * Copyright 2022 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.esri.samples.local_server_geoprocessing_sandbox;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureCollection;
import com.esri.arcgisruntime.data.FeatureCollectionTable;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.ServiceGeodatabase;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.FeatureCollectionLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.RasterElevationSource;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.Viewpoint;

import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties;
import com.esri.arcgisruntime.mapping.view.SceneView;

import com.esri.arcgisruntime.raster.HillshadeRenderer;
import com.esri.arcgisruntime.raster.Raster;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingFeatures;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingString;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.Job;

import com.esri.arcgisruntime.localserver.LocalGeoprocessingService;
import com.esri.arcgisruntime.localserver.LocalGeoprocessingService.ServiceType;
import com.esri.arcgisruntime.localserver.LocalServer;
import com.esri.arcgisruntime.localserver.LocalServerStatus;

import com.esri.arcgisruntime.mapping.BasemapStyle;

import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingJob;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingParameter;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingParameters;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingTask;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;


public class LocalServerGeoprocessingSandboxController {

  @FXML
  private Label label;
  @FXML
  private Button drawPolyline;
  @FXML
  private Button generateProfileButton;
  @FXML
  private Button clearResultsButton;
  @FXML
  private ProgressBar progressBar;
  @FXML
  private SceneView sceneView;

  private ArcGISScene scene;
  private Polyline polyline;
  private RasterLayer rasterLayer;
  private FeatureCollection featureCollection;
  private GeoprocessingTask gpTask;
  private GraphicsOverlay graphicsOverlay;
  private LocalGeoprocessingService localGPService;

  private String rasterPath = "./samples-data/local_server/Arran_10m.tif";

  private static LocalServer server;

  /**
   * Called after FXML loads. Sets up scene and map and configures property bindings.
   */
  public void initialize() {

    try {

      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a scene with a topographic basemap style
      scene = new ArcGISScene(BasemapStyle.ARCGIS_HILLSHADE_LIGHT);

      // set the scene to the scene view and viewpoint to the Isle of Arran, Scotland
      sceneView.setViewpointAsync(new Viewpoint(55.60, -5.28, 100000));

      // set up a new feature collection layer from a new feature collection, and add it to the scene's operational 
      // layer list
      featureCollection = new FeatureCollection();
      var featureCollectionLayer = new FeatureCollectionLayer(featureCollection);
      scene.getOperationalLayers().add(featureCollectionLayer);

      // create a graphics overlay for displaying the sketched polyline and add it to the scene view's list of 
      // graphics overlays
      graphicsOverlay = new GraphicsOverlay();
      SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH,
        ColorUtil.colorToArgb(Color.BLACK), 3);
      graphicsOverlay.setRenderer(new SimpleRenderer(lineSymbol));
      sceneView.getGraphicsOverlays().add(graphicsOverlay);
      displayRaster();
      startLocalServer();
      sceneView.setArcGISScene(scene);


    } catch (Exception e) {
      // on any exception, print the stack trace
      e.printStackTrace();
    }
  }

  private void displayRaster() {

    var hillshadeRenderer = new HillshadeRenderer(30, 210, 1);

    scene.addDoneLoadingListener(() -> {
      if (scene.getLoadStatus() == LoadStatus.LOADED) {

        // loop through the GeoTIFFs
//            for (String geoTiffFile : geoTiffFiles) {
        // create a raster from every GeoTIFF
        var raster = new Raster("C:\\Users\\rach9955\\Desktop\\Arran_10m_ProjectRaster.tif");
        // create a raster layer from the raster
        rasterLayer = new RasterLayer(raster);
        // set a hillshade renderer to the raster layer
        rasterLayer.setRasterRenderer(hillshadeRenderer);
        // add the raster layer to the scene's operational layers

        scene.getOperationalLayers().add(rasterLayer);
        rasterLayer.addDoneLoadingListener(() -> {
          if (rasterLayer.getLoadStatus() == LoadStatus.LOADED && rasterLayer.getFullExtent() != null) {
            sceneView.setViewpointAsync(new Viewpoint(rasterLayer.getFullExtent()));
            System.out.println(rasterLayer.getFullExtent());
          }
        });


//            }

      }
    });


  }

  /**
   * Checks that there is a valid install of Local Server, and if so starts the Local Server Instance from the create
   * elevation profile model geoprocessing package
   */
  private void startLocalServer() {

    // check that there is a valid install on user machine
    if (LocalServer.INSTANCE.checkInstallValid()) {
      progressBar.setVisible(true);
      server = LocalServer.INSTANCE;

      // start the local server
      server.addStatusChangedListener(status -> {
        if (server.getStatus() == LocalServerStatus.STARTED) {
          try {
            // get the path to the geoprocessing package (created in ArcGIS Pro) that creates elevation profile from 
            // raster data
            String gpServiceURL = new File(System.getProperty("data.dir"), "./samples-data/local_server" +
              "/create_elevation_profile_model.gpkx").getAbsolutePath();

            // need map server result to display elevation profile on scene
            localGPService =
              new LocalGeoprocessingService(gpServiceURL, ServiceType.ASYNCHRONOUS_SUBMIT_WITH_MAP_SERVER_RESULT);
          } catch (Exception e) {
            e.printStackTrace();
          }

          localGPService.addStatusChangedListener(s -> {
            // create geoprocessing task once local geoprocessing service is started 
            if (s.getNewStatus() == LocalServerStatus.STARTED) {
              // add the name of the model used to create the gpkx in ArcGIS Pro to the Url of the local 
              // geoprocessing task
              // e.g. the model name in this sample's gpkx created in ArcGIS Pro is CreateElevationProfileModel
              gpTask = new GeoprocessingTask(localGPService.getUrl() + "/CreateElevationProfileModel");

              // handle UI behaviour
              label.setText("Draw line of section with button above");
              drawPolyline.setDisable(false);
              progressBar.setVisible(false);
            }
          });
          localGPService.startAsync();

        } else if (server.getStatus() == LocalServerStatus.FAILED) {
          showMessage("Local Geoprocessing Load Error", "Local Geoprocessing Failed to load.");
        }
      });

      server.startAsync();
    } else {
      showMessage("Local Server Load Error", "Local Server install path couldn't be located.");
    }
  }

  /**
   * Generates an elevation profile when the "Generate Elevation Profile" button is clicked. The elevation profile is
   * generated from an input raster (Isle of Arran Lidar data) and a polyline sketched by the user.
   */
  @FXML
  protected void handleGenerateElevationProfile() {

    // tracking progress of generating elevation profile
    progressBar.setVisible(true);
    label.setVisible(false);

    // create geoprocessing parameters and get their inputs
    GeoprocessingParameters gpParameters = new GeoprocessingParameters(
      GeoprocessingParameters.ExecutionType.ASYNCHRONOUS_SUBMIT);
    final Map<String, GeoprocessingParameter> inputs = gpParameters.getInputs();

    // create the feature collection table
    createPolylineTable();

    var inputRasterPath =
      new File(System.getProperty("data.dir"), "C:\\Users\\rach9955\\Desktop\\Arran_10m_ProjectRaster.tif").getAbsolutePath();

    // input polyline path
    // name of input parameter, input type (geoprocessing feature, pointing to polyline)
    inputs.put("Input_Polyline", new GeoprocessingFeatures(featureCollection.getTables().get(0)));

    // input raster data
    // name of input parameter, input type (geoprocessing string pointing to raster file)
    inputs.put("Input_Raster", new GeoprocessingString(inputRasterPath));

    // adds contour lines to map
    GeoprocessingJob gpJob = gpTask.createJob(gpParameters);

    gpJob.addJobDoneListener(() -> {

      if (gpJob.getStatus() == Job.Status.SUCCEEDED) {

        String serviceUrl = localGPService.getUrl();
        String mapServerUrl = serviceUrl.replace("GPServer", "MapServer/jobs/" + gpJob.getServerJobId());

        System.out.println("service url: " + serviceUrl);
        System.out.println("Server Job ID: " + gpJob.getServerJobId());
        System.out.println("Map server url: " + mapServerUrl);

        ServiceGeodatabase serviceGeodatabase = new ServiceGeodatabase(mapServerUrl);
        serviceGeodatabase.addDoneLoadingListener(() -> {

          FeatureTable featureTable = serviceGeodatabase.getTable(0);
          FeatureLayer featureLayer = new FeatureLayer(featureTable);
          featureLayer.loadAsync();
          featureLayer.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.ABSOLUTE);

          featureLayer.addDoneLoadingListener(() -> {

            sceneView.getArcGISScene().getOperationalLayers().add(featureLayer);

          });

          generateProfileButton.setDisable(true);
          clearResultsButton.setDisable(false);

        });
        serviceGeodatabase.loadAsync();

      } else {
        Alert dialog = new Alert(AlertType.ERROR);
        dialog.setHeaderText("Geoprocess Job Fail");
        dialog.setContentText("Error: " + gpJob.getError().getAdditionalMessage());
        System.out.println("Error: " + gpJob.getError().getAdditionalMessage());
        dialog.showAndWait();
      }
      progressBar.setVisible(false);

    });
    gpJob.start();

  }

  /**
   * Removes contour lines from map if any are applied.
   */
  @FXML
  protected void handleClearResults() {

    graphicsOverlay.getGraphics().clear();
    sceneView.getArcGISScene().getOperationalLayers().clear();
    generateProfileButton.setDisable(true);
    drawPolyline.setDisable(false);
    clearResultsButton.setDisable(true);
    featureCollection.getTables().clear();
    label.setVisible(true);
    label.setText("Draw line of section with button above");
  }

  private void showMessage(String title, String message) {

    Platform.runLater(() -> {
      Alert dialog = new Alert(AlertType.INFORMATION);
      dialog.initOwner(sceneView.getScene().getWindow());
      dialog.setHeaderText(title);
      dialog.setContentText(message);
      dialog.showAndWait();

      Platform.exit();
    });
  }

  @FXML
  private Polyline handleDrawPolyline() {
    System.out.println(rasterLayer.getFullExtent());

    label.setText("Right click to save line of section");

    GraphicsOverlay tempGraphicsOverlay = new GraphicsOverlay();
    sceneView.getGraphicsOverlays().add(tempGraphicsOverlay);
    SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE,
      ColorUtil.colorToArgb(Color.BLUE), 5);
    tempGraphicsOverlay.setRenderer(new SimpleRenderer(simpleMarkerSymbol));

    // create a point collection with the British National Grid (BNG) spatial reference
    PointCollection pointCollection = new PointCollection(SpatialReference.create(27700));

    sceneView.setOnMouseClicked(event -> {
      if (event.isStillSincePress() && event.getButton() == MouseButton.PRIMARY) {
        // get the clicked location
        drawPolyline.setDisable(true);
        Point2D point2D = new Point2D(event.getX(), event.getY());
        ListenableFuture<Point> pointFuture = sceneView.screenToLocationAsync(point2D);
        pointFuture.addDoneListener(() -> {
          try {
            Point point = pointFuture.get();
            Point projectedPoint = (Point) GeometryEngine.project(point, SpatialReference.create(27700)); // BNG
            double pointX = projectedPoint.getX();
            double pointY = projectedPoint.getY();

            Point xyPoint = new Point(pointX, pointY);

            if (GeometryEngine.intersects(xyPoint, rasterLayer.getFullExtent())) {
              pointCollection.add(xyPoint);
              tempGraphicsOverlay.getGraphics().add(new Graphic(projectedPoint));
            } else {
              new Alert(AlertType.ERROR, "Clicked point must be within raster layer extent").show();
            }


          } catch (Exception e) {
            e.printStackTrace();
          }
        });
      } else if (event.getButton() == MouseButton.SECONDARY && !tempGraphicsOverlay.getGraphics().isEmpty()) {

        tempGraphicsOverlay.getGraphics().clear();

        // create a polyline from the clicked points on the scene and add it as a graphic to the graphics overlay
        polyline = new Polyline(pointCollection);
        Graphic graphic = new Graphic(polyline);
        graphicsOverlay.getGraphics().add(graphic);
        new Alert(AlertType.INFORMATION, "Polyline sketched").show();
        drawPolyline.setDisable(true);
        generateProfileButton.setDisable(false);
        label.setText("Generate elevation profile along the polyline using the above button");
        pointCollection.clear();


      }
    });

    return polyline;
  }

  @FXML
  private void createPolylineTable() {

    // create name field for polyline
    List<Field> polylineField = new ArrayList<>();
    polylineField.add(Field.createString("Name", "Name of feature", 20));

    // create a feature collection table with BNG spatial reference
    FeatureCollectionTable featureCollectionTable = new FeatureCollectionTable(polylineField, GeometryType.POLYLINE,
      SpatialReference.create(27700));

    // add the feature collection table to the feature collection, and create a feature from it
    featureCollection.getTables().add(featureCollectionTable);
    Map<String, Object> attributes = new HashMap<>();
    attributes.put(polylineField.get(0).getName(), "ElevationSection");
    Feature addedFeature = featureCollectionTable.createFeature(attributes, polyline);

    // add feature to collection table
    featureCollectionTable.addFeatureAsync(addedFeature);
    System.out.println(featureCollection.toJson());

  }

  /**
   * Stops and releases all resources used in application.
   */
  void terminate() {

    if (sceneView != null) {
      sceneView.dispose();
    }
  }
}
