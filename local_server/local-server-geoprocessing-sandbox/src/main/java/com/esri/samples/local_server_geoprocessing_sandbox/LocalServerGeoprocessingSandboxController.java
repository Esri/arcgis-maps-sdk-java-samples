/*
 * Copyright 2017 Esri.
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
import java.util.concurrent.ExecutionException;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureCollection;
import com.esri.arcgisruntime.data.FeatureCollectionTable;
import com.esri.arcgisruntime.data.FeatureSet;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceGeodatabase;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureCollectionLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Viewpoint;

import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties;
import com.esri.arcgisruntime.mapping.view.SceneView;

import com.esri.arcgisruntime.mapping.view.SketchEditor;
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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.Job;

import com.esri.arcgisruntime.layers.ArcGISTiledLayer;

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
import org.json.JSONObject;


public class LocalServerGeoprocessingSandboxController {

  @FXML
  private Button drawPolyline;
  @FXML
  private Button btnGenerate;
  @FXML
  private Button btnClear;
  @FXML
  private ProgressBar progressBar;
  @FXML
  private SceneView sceneView;

  private Polyline polyline;

  private FeatureCollection featureCollection;
  private GeoprocessingTask gpTask;
  private GraphicsOverlay graphicsOverlay;
  private LocalGeoprocessingService localGPService;

  private static LocalServer server;

  /**
   * Called after FXML loads. Sets up scene and map and configures property bindings.
   */
  public void initialize() {

    try {
      // authentication with an API key or named user is required to access basemaps and other location services
      String yourAPIKey = System.getProperty("apiKey");
      ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);

      // create a map with the light gray basemap style
      ArcGISScene scene = new ArcGISScene(BasemapStyle.ARCGIS_LIGHT_GRAY);

      // set the map to the map view
      sceneView.setArcGISScene(scene);
      sceneView.setViewpointAsync(new Viewpoint(55.60, -5.28, 100000));

      featureCollection = new FeatureCollection();
      var featureCollectionLayer = new FeatureCollectionLayer(featureCollection);

      sceneView.getArcGISScene().getOperationalLayers().add(featureCollectionLayer);


      // create a graphics overlay for the sketch polyline 
      graphicsOverlay = new GraphicsOverlay();
      // thin green line for polylines
      SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF64c113, 4);
      SimpleRenderer polylineRenderer = new SimpleRenderer(lineSymbol);
      graphicsOverlay.setRenderer(polylineRenderer);

      // add the graphics overlay to the map view
      sceneView.getGraphicsOverlays().add(graphicsOverlay);

      // check that local server install path can be accessed
      System.out.println(LocalServer.INSTANCE.getInstallPath());

      if (LocalServer.INSTANCE.checkInstallValid()) {
        progressBar.setVisible(true);
        server = LocalServer.INSTANCE;
        System.out.println("Temp data path " + server.getTempDataPath());
        // start the local server
        server.addStatusChangedListener(status -> {
          if (server.getStatus() == LocalServerStatus.STARTED) {
            try {
//              String gpServiceURL = new File(System.getProperty("data.dir"), "
//              ./samples-data/local_server/interpolate" +
//                ".gpkx").getAbsolutePath();
              String gpServiceURL = new File(System.getProperty("data.dir"), "./samples-data/local_server" +
                "/create_elevation_profile_model.gpkx").getAbsolutePath();

              // need map server result to add contour lines to map
              localGPService =
                new LocalGeoprocessingService(gpServiceURL, ServiceType.ASYNCHRONOUS_SUBMIT_WITH_MAP_SERVER_RESULT);
            } catch (Exception e) {
              e.printStackTrace();
            }

            localGPService.addStatusChangedListener(s -> {
              // create geoprocessing task once local geoprocessing service is started 
              if (s.getNewStatus() == LocalServerStatus.STARTED) {
                // add `/NameOfScript/Model` to use contour geoprocessing tool 
                gpTask = new GeoprocessingTask(localGPService.getUrl() + "/CreateElevationProfileModel");
//                gpTask = new GeoprocessingTask(localGPService.getUrl() + "/Model");

                System.out.println(localGPService.getUrl());
                btnClear.disableProperty().bind(btnGenerate.disabledProperty().not());
                btnGenerate.setDisable(false);
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

    } catch (Exception e) {
      // on any exception, print the stack trace
      e.printStackTrace();
    }
  }

  /**
   * Creates a Map Image Layer that displays contour lines on the map using the interval the that is set.
   */
  @FXML
  protected void handleDrawElevationProfile() {

    // tracking progress of creating contour map 
    progressBar.setVisible(true);
    // create parameter using interval set

    GeoprocessingParameters gpParameters = new GeoprocessingParameters(
      GeoprocessingParameters.ExecutionType.ASYNCHRONOUS_SUBMIT);

    final Map<String, GeoprocessingParameter> inputs = gpParameters.getInputs();
    System.out.println("Values " + inputs.values());
//    double interval = Double.parseDouble(txtInterval.getText());
    var inputRasterPath = ("C:\\Users\\rach9955\\Downloads\\arran-lidar-data\\arran-lidar-data\\MergedArranRasters" +
      ".tif");

//**??**??**////**??**??**////**??**??**////**??**??**////**??**??**////**??**??**//

    graphicsOverlay.getGraphics().clear();
    GraphicsOverlay anewtempGraphicsOverlay = new GraphicsOverlay();
    sceneView.getGraphicsOverlays().add(anewtempGraphicsOverlay);
    SimpleLineSymbol newsimpleMarkerSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH,
      ColorUtil.colorToArgb(Color.DARKORANGE), 5);
    SimpleRenderer newrenderer = new SimpleRenderer(newsimpleMarkerSymbol);
    anewtempGraphicsOverlay.setRenderer(newrenderer);
    anewtempGraphicsOverlay.getGraphics().add(new Graphic(polyline));

    // create required viewshed fields
    List<Field> polylineField = new ArrayList<>();

    polylineField.add(Field.createString("Shape_Length", "Length of shape", 20));

//    Field geometryField = Field.createString("geometry", "geometry", 50); // pass in json
//    Field attributesField = Field.createString("attributes", "attributes", 50); // don't think these are really 
//    // needed so can be blank
//
//    subFields.add(geometryField);
//    subFields.add(attributesField);

//    Field featuresField = Field.createString("features", "features", 1000000000);
//    List<Field> featureField = new ArrayList<>();
//    featureField.add(featuresField);

//    Map<String, Object> attributes = new HashMap<>();
//    Map<String, Object> featuresAttributes = new HashMap<>();
//    featuresAttributes.put(subFields.get(0).getName(), polyline.toJson()); // geometry
//    featuresAttributes.put(subFields.get(1).getName(), ""); // attributes
//
//    attributes.put(featureField.get(0).getName(), featuresAttributes); //features

//    JSONObject json = new JSONObject(attributes);
//    System.out.println("Json :" + json);

    // debug from here
    // try adding feature collection table as feature layer to scene view again and see it re-projected to see if 
    // reprojected line is going on ok
    // try Geometry.fromJson on the json response from the query of working input parameters from browser query 

    FeatureCollectionTable featureCollectionTable = new FeatureCollectionTable(polylineField, GeometryType.POLYLINE,
      SpatialReference.create(27700));


    SimpleLineSymbol tableStyle = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH,
      ColorUtil.colorToArgb(Color.TURQUOISE), 5);
    SimpleRenderer tableRenderer = new SimpleRenderer(tableStyle);
    featureCollectionTable.setRenderer(tableRenderer);

    featureCollection.getTables().add(featureCollectionTable);

    System.out.println(featureCollectionTable.getLoadStatus());

    Map<String, Object> attributes = new HashMap<>();
    attributes.put(polylineField.get(0).getName(), "19840");

    var testFeature = featureCollectionTable.createFeature(attributes, polyline);
//        testFeature.setGeometry(polyline); // correct projection

    featureCollectionTable.addFeatureAsync(testFeature);
    
//    System.out.println(featureCollection.getTables().size());
//
//    System.out.println("Added feature");
//    
//    System.out.println("Feature Collection as json: " + featureCollection.toJson());
//    System.out.println("Test feature in json: " + testFeature.getGeometry().toJson());
//
//    System.out.println("Test feature geometry as json: " + testFeature.getGeometry().toJson());
//    System.out.println("Attribute: " + attributes);
    
    
    ////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////
    
    inputs.put("Input_Polyline", new GeoprocessingFeatures(featureCollectionTable));


//    Feature addedFeature = featureCollectionTable.createFeature(attributes, polyline);
//    var testFeature = featureCollectionTable.createFeature();
//    var projectedPolyline = GeometryEngine.project(polyline, SpatialReference.create(27700)); // projects polyline 
//    to BNG


    // input raster data
    // name of input parameter, input type (geoprocessing raster)
    inputs.put("Input_Raster", new GeoprocessingString(inputRasterPath)); // just the string, GeoprocessingString

    // input line of section path
    // name of input parameter, json url
//    inputs.put("Input_Polyline", new GeoprocessingFeatures("C:\\Users\\rach9955\\Documents\\ArcGIS\\Projects" +
//      "\\ArranCrossSection\\Profile_FeaturesToJSON.json"));


    // adds contour lines to map
    GeoprocessingJob gpJob = gpTask.createJob(gpParameters);

    gpJob.addJobDoneListener(() -> {

      System.out.println("GP Job status: " + gpJob.getStatus());
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
            // try reprojecting back to wgs84
            sceneView.setViewpoint(new Viewpoint(featureLayer.getFullExtent()));

          });

          btnGenerate.setDisable(true);

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

    if (sceneView.getArcGISScene().getOperationalLayers().size() > 1) {
      sceneView.getArcGISScene().getOperationalLayers().remove(1);
      btnGenerate.setDisable(false);
    }
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
  private Polyline startPolylineSketchEditor() {

    GraphicsOverlay tempGraphicsOverlay = new GraphicsOverlay();
    sceneView.getGraphicsOverlays().add(tempGraphicsOverlay);
    SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE,
      ColorUtil.colorToArgb(Color.AQUA), 5);
    SimpleRenderer renderer = new SimpleRenderer(simpleMarkerSymbol);
    tempGraphicsOverlay.setRenderer(renderer);

    // British National Grid
    PointCollection pointCollection = new PointCollection(SpatialReference.create(27700));

    sceneView.setOnMouseClicked(event -> {
      if (event.isStillSincePress() && event.getButton() == MouseButton.PRIMARY) {
        // get the clicked location
        Point2D point2D = new Point2D(event.getX(), event.getY());
        ListenableFuture<Point> pointFuture = sceneView.screenToLocationAsync(point2D);
        pointFuture.addDoneListener(() -> {
          try {
            Point point = pointFuture.get();
            Point projectedPoint = (Point) GeometryEngine.project(point, SpatialReference.create(27700)); // british 
            // national grid
            pointCollection.add(projectedPoint);
            tempGraphicsOverlay.getGraphics().add(new Graphic(projectedPoint));


          } catch (Exception e) {
            e.printStackTrace();
          }
        });
      } else if (event.getButton() == MouseButton.SECONDARY) {

        tempGraphicsOverlay.getGraphics().clear();
        polyline = new Polyline(pointCollection);
        Graphic graphic = new Graphic(polyline);
        graphicsOverlay.getGraphics().add(graphic);
        new Alert(AlertType.INFORMATION, "Polyline sketched").show();
      }
    });

    return polyline;
  }

  private void createPolylineTable(FeatureCollection featureCollection) {

    // defines the schema for the geometry's attribute
    List<Field> polylineFields = new ArrayList<>();
    polylineFields.add(Field.createString("Boundary", "Boundary Name", 50));

    // a feature collection table that creates polyline geometry
    FeatureCollectionTable polylineTable = new FeatureCollectionTable(polylineFields, GeometryType.POLYLINE, WGS84);

    // set a default symbol for features in the collection table
    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, 0xFF00FF00, 3);
    SimpleRenderer renderer = new SimpleRenderer(lineSymbol);
    polylineTable.setRenderer(renderer);

    // add feature collection table to feature collection
    featureCollection.getTables().add(polylineTable);

    // create feature using the collection table by passing an attribute and geometry
    Map<String, Object> attributes = new HashMap<>();
    attributes.put(polylineFields.get(0).getName(), "AManAPlanACanalPanama");
    PolylineBuilder builder = new PolylineBuilder(WGS84);
    builder.addPoint(new Point(-79.497238, 8.849289, WGS84));
    builder.addPoint(new Point(-80.035568, 9.432302, WGS84));
    Feature addedFeature = polylineTable.createFeature(attributes, builder.toGeometry());

    // add feature to collection table
    polylineTable.addFeatureAsync(addedFeature);
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
