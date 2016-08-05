/*
 * Copyright 2015 Esri. Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.esri.samples.scene;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.esri.arcgisruntime.geometry.*;
import com.esri.arcgisruntime.mapping.*;
import com.esri.arcgisruntime.mapping.view.*;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties.SurfacePlacement;
import com.esri.arcgisruntime.symbology.ModelSceneSymbol;
import com.esri.arcgisruntime.symbology.Renderer.SceneProperties;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

public class MissionReplaySample extends Application {

  private Slider zoomSlider;

  // gives better understanding of what each value means
  private static final int STARTING_POINT = 0;
  private static final int X_VALUE = 0;
  private static final int Y_VALUE = 1;
  private static final int Z_VALUE = 2;
  private static final int HEADING = 0;
  private static final int PITCH = 1;
  private static final int ROLL = 2;
  private static final int CONTROL_WIDTH = 200;
  private static final int CONTROL_HEIGHT = 50;

  // for creating a 3D and 2D views
  private StackPane view3D;
  private StackPane view2D;
  // for playing animation for plane
  private Timeline timer;
  // tracks the progress of the plane along its route
  private Slider progressSlider;
  // controls play and stop animation of plane
  private Button startButton;
  private Button stopButton;
  // setting for camera control
  private VBox cameraControlBox;

  // plane settings at each point along route
  private ArrayList<List<Double>> planeSettings;
  // points along the route the plane takes
  private ArrayList<Point> routePoints;

  // distance camera is behind plane
  private SimpleDoubleProperty cameraZoom = new SimpleDoubleProperty(1000);
  // angle between the camera and plane
  private SimpleDoubleProperty cameraAngle = new SimpleDoubleProperty(70);
  // how close the 2D view is to the surface
  private int scale2D = 100000;
  // counter for the location of the plane
  private int nextPoint;
  // time it take for the plane to get from one point to the next (milliseconds)
  private int timerDelay = 20;
  // if the camera should follow the plane
  private boolean followCamera = true;
  // zoom for 2D view
  private int zoomFactor2D = 5;

  // plane settings to be displayed to user
  private Text altitudeText = new Text();
  private Text headingText = new Text();
  private Text pitchText = new Text();
  private Text rollText = new Text();

  // user's view for 3D
  private Camera camera;
  // holds all graphics for 3D space (scene)
  private GraphicsOverlay view3DOverlay;
  // holds all graphics for 2D space (map)
  private GraphicsOverlay view2DOverlay;
  // store location and symbol of plane in 3D and 2D space
  private Graphic plane3DGraphic;
  private Graphic plane2DGraphic;
  // represent a plane for a 3D and 2D graphic
  private ModelSceneSymbol plane3DMarker;
  // private SimpleMarkerSceneSymbol plane3DMarker;
  private SimpleMarkerSymbol plane2DMarker;

  // displays a 3D scene to user
  private SceneView sceneView;
  // displays a 2D map to user
  private MapView mapView;

  // stores location for plane to fly, default GrandCanyon
  private String missionLocation = "GrandCanyon";
  // adds elevation to the surface of the scene
  private static final String ELEVATED_LAYER =
      "http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer";

  @Override
  public void start(Stage stage) throws Exception {

    try {
      // setting up application window
      view3D = new StackPane();
      Scene fxScene = new Scene(view3D);
      fxScene.getStylesheets().add(getClass().getResource("/SamplesTheme.css").toExternalForm());
      stage.setTitle("3D Mission Replay Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(fxScene);
      stage.show();

      create3DView();
      create2DView();
      createTimer();
      setScene();
      setupHUD();
    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Creates a SceneView, 3D view, using an ArcGISScene that has an elevated
   * layer and adds a GraphicsOverlay with surface placement set to absolute.
   */
  private void create3DView() {

    ArcGISScene scene = new ArcGISScene();
    scene.setBasemap(Basemap.createImagery());
    sceneView = new SceneView();
    sceneView.setArcGISScene(scene);
    // add view to application window
    view3D.getChildren().addAll(sceneView);

    // adds elevation to scene
    Surface surface = new Surface();
    surface.getElevationSources().add(new ArcGISTiledElevationSource(ELEVATED_LAYER));
    scene.setBaseSurface(surface);

    // holds graphics for our 3D view
    view3DOverlay = new GraphicsOverlay();
    sceneView.getGraphicsOverlays().add(view3DOverlay);
    // graphics now have altitude relative to ground level using graphic's Z
    // value
    view3DOverlay.getSceneProperties().setSurfacePlacement(SurfacePlacement.ABSOLUTE);
  }

  /**
   * Creates a MapView, 2D view, using a ArcGISMap and GraphicsOevrlay.
   */
  private void create2DView() {

    // create 2D view with a map
    ArcGISMap map = new ArcGISMap(Basemap.createImagery());
    mapView = new MapView();
    mapView.setMap(map);
    mapView.setRotate(90.0);
    // add view to application window
    view2D = new StackPane(mapView);
    view2D.setMaxSize(200, 200);
    view2D.setMinSize(200, 200);

    // holds graphics for our 2D view
    view2DOverlay = new GraphicsOverlay();
    mapView.getGraphicsOverlays().add(view2DOverlay);
  }

  /**
   * Sets up everything needed to start the plane along it's path.
   * <p>
   * Stores location and settings for the plane, resets any animation or
   * graphics, and creates graphics for displaying plane and route.
   */
  private void setScene() {

    // store all points along the route selected
    storeRouteSettings();

    // resetting animation and graphics
    if (timer != null) {
      timer.stop();
    }

    if (view3DOverlay != null) {
      view3DOverlay.getGraphics().clear();
      plane3DGraphic = null;
    }

    if (view2DOverlay != null) {
      view2DOverlay.getGraphics().clear();
      plane2DGraphic = null;
    }

    if (startButton != null) {
      startButton.setDisable(false);
      stopButton.setDisable(true);
    }

    createGraphics();

    // create camera at initial plane location
    moveView(STARTING_POINT);
    // updates text in bottom right corner
    updateDisplayedSettings();
  }

  /**
   * Creates a 3D plane and a 2D plane with a route.
   */
  private void createGraphics() {

    File modelFile = new File(getClass().getResource("/SkyCrane/SkyCrane.lwo").getPath());
    List<Double> settings = planeSettings.get(STARTING_POINT);
    if (modelFile.exists()) {
      plane3DMarker = new ModelSceneSymbol(modelFile.getAbsolutePath(), 0.03);
      plane3DMarker.setHeading(-180);
      plane3DMarker.addDoneLoadingListener(() -> {
        plane3DGraphic = new Graphic(routePoints.get(STARTING_POINT), plane3DMarker);
        Map<String, Object> planeAttributes = plane3DGraphic.getAttributes();
        planeAttributes.put("HEADING", settings.get(HEADING));
        planeAttributes.put("PITCH", settings.get(PITCH));
        planeAttributes.put("ROLL", settings.get(ROLL));

        SimpleRenderer renderer3D = new SimpleRenderer();
        SceneProperties renderProperties = renderer3D.getSceneProperties();
        renderProperties.setHeadingExpression("HEADING");
        renderProperties.setPitchExpression("PITCH");
        renderProperties.setRollExpression("ROLL");

        // add 3D plane to 3D view
        view3DOverlay.setRenderer(renderer3D);
        view3DOverlay.getGraphics().add(plane3DGraphic);
      });
      plane3DMarker.loadAsync();
    }

    // creating 2D plane
    SimpleRenderer renderer2D = new SimpleRenderer();
    view2DOverlay.setRenderer(renderer2D);
    plane2DMarker = new SimpleMarkerSymbol(Style.TRIANGLE, 0xff0000ff, 10);
    plane2DGraphic = new Graphic(routePoints.get(STARTING_POINT), plane2DMarker);

    // set 2D plane in direction of route
    plane2DGraphic.getAttributes().put("ANGLE", settings.get(HEADING).floatValue());
    renderer2D.setRotationExpression("[ANGLE]");

    // creating route for 2D view
    int width = 2;
    int red = 0xFFFF0000;
    SimpleLineSymbol routeLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, red, width);
    PointCollection points = new PointCollection(SpatialReferences.getWgs84());
    // creates a line that is connected at every point
    for (Point point : routePoints) {
      points.add(point);
    }

    Polyline routeLine = new Polyline(points);
    Graphic routeGraphic = new Graphic(routeLine, routeLineSymbol);
    // add 2D plane and route to 2D view
    view2DOverlay.getGraphics().addAll(Arrays.asList(routeGraphic, plane2DGraphic));

  }

  /**
   * Moves 2D and 3D view to follow plane if the toggle option is set to true.
   * <p>
   * 3D view hovers behind 3D plane using the camera distance and camera angle
   * settings. 3D view mimics heading of plane.
   * <p>
   * 2D view centers on 2D plane looking down.
   * 
   * @param point location to move camera
   */
  private void moveView(int point) {

    if (followCamera) {
      // moving 3D view
      camera = new Camera(routePoints.get(point), cameraZoom.get(), ((double) plane3DGraphic.getAttributes().get(
          "HEADING")), cameraAngle.get(), 0);

      sceneView.setViewpointCamera(camera);

      // moving 2D View
      mapView.setViewpoint(new Viewpoint(routePoints.get(point), scale2D));
    }
  }

  /**
   * Creates the animation, camera, and mini-map controls along with displaying
   * plane settings.
   */
  private void setupHUD() {

    Pane planeDetails = createPlaneDetails();
    Pane cameraControl = createCameraControl();
    Pane animationControl = createAnimationControl();
    Pane miniMapControl = createMiniMapControl();

    view3D.getChildren().addAll(animationControl, cameraControl, planeDetails, miniMapControl);
    StackPane.setAlignment(cameraControl, Pos.TOP_RIGHT);
    StackPane.setAlignment(animationControl, Pos.TOP_LEFT);
    StackPane.setAlignment(planeDetails, Pos.BOTTOM_RIGHT);
    StackPane.setAlignment(miniMapControl, Pos.BOTTOM_LEFT);
  }

  /**
   * Creates a display for showing plane's altitude, heading, pitch, and roll
   * information.
   * 
   * @return pane that displays all of the planes settings
   */
  private Pane createPlaneDetails() {

    Label detailsLabel = new Label("Current Position");
    detailsLabel.getStyleClass().add("panel-label");

    HBox altitudeBox = createLabelAndText("Altitude: ", altitudeText);
    HBox headingBox = createLabelAndText("Heading: ", headingText);
    HBox pitchBox = createLabelAndText("Pitch: ", pitchText);
    HBox rollBox = createLabelAndText("Roll: ", rollText);

    VBox planeDetails = new VBox(detailsLabel, altitudeBox, headingBox, pitchBox, rollBox);
    planeDetails.getStyleClass().add("panel-region");
    planeDetails.setMaxSize(CONTROL_WIDTH, CONTROL_HEIGHT);
    planeDetails.setAlignment(Pos.CENTER);

    return planeDetails;
  }

  /**
   * Creates the controls for camera zoom, angle, and speed.
   * 
   * @return pane control for camera settings
   */
  private Pane createCameraControl() {

    Label zoomLabel = new Label("Camera Zoom");
    zoomLabel.getStyleClass().add("panel-label");
    zoomSlider = createSlider(500, 3000, cameraZoom.get());
    // zoomSlider.valueProperty().bindBidirectional(cameraZoom);
    // connecting mouse scroll to zoom slider
    sceneView.setOnScroll(e -> {
      double scrollAmount = e.getDeltaY();
      if (scrollAmount < 0 && cameraZoom.get() <= 5000) {
        cameraZoom.set(cameraZoom.get() + 200);
      } else if (scrollAmount > 0 && cameraZoom.get() >= 500) {
        cameraZoom.set(cameraZoom.get() - 200);
      }
    });

    Label angleLabel = new Label("Camera Angle");
    angleLabel.getStyleClass().add("panel-label");
    Slider angleSlider = createSlider(0, 90, cameraAngle.get());
    angleSlider.valueProperty().bindBidirectional(cameraAngle);

    Label speedLabel = new Label("Flight Speed");
    speedLabel.getStyleClass().add("panel-label");
    Slider speedSlider = createSlider(0.25, 5, timer.getRate());
    speedSlider.valueProperty().bindBidirectional(timer.rateProperty());

    cameraControlBox = new VBox();
    cameraControlBox.setDisable(true);
    cameraControlBox.getChildren().addAll(zoomLabel, zoomSlider, angleLabel, angleSlider, speedLabel, speedSlider);
    cameraControlBox.getStyleClass().add("panel-region");
    cameraControlBox.setMaxSize(CONTROL_WIDTH, CONTROL_HEIGHT);

    return cameraControlBox;
  }

  /**
   * Creates the controls for selecting a mission, tracking mission progress,
   * start/stop mission buttons, and button to toggle following the plane.
   * 
   * @return pane that hold all the animation controls
   */
  private Pane createAnimationControl() {

    // creates selecting mission control
    Label missionLabel = new Label("Select a Mission");
    missionLabel.getStyleClass().add("panel-label");
    ObservableList<String> missionList = FXCollections.observableArrayList();
    missionList.add("GrandCanyon");
    missionList.add("Hawaii");
    missionList.add("Pyrenees");
    missionList.add("Snowdon");
    ComboBox<String> missionSelect = new ComboBox<>(missionList);
    missionSelect.getSelectionModel().select(0);
    missionSelect.valueProperty().addListener((obs, old_val, new_val) -> {
      missionLocation = new_val;
      setScene();
    });

    // displays mission progress to user
    Label progressLabel = new Label("Mission Progress");
    progressLabel.getStyleClass().add("panel-label");
    progressSlider = createSlider(0, routePoints.size(), nextPoint);
    progressSlider.setDisable(true);

    // creating start and stop control
    startButton = new Button("Start");
    stopButton = new Button("Stop");
    stopButton.setDisable(true);
    startButton.setMinWidth(75);
    stopButton.setMinWidth(75);
    HBox animationBox = new HBox(startButton, stopButton);
    animationBox.setAlignment(Pos.CENTER);

    startButton.setOnAction(e -> {
      startButton.setDisable(true);
      stopButton.setDisable(false);
      cameraControlBox.setDisable(false);
      cameraZoom.bindBidirectional(zoomSlider.valueProperty());

      timer.play();
    });
    stopButton.setOnAction(e -> {
      startButton.setDisable(false);
      stopButton.setDisable(true);
      cameraControlBox.setDisable(true);
      cameraZoom.unbindBidirectional(zoomSlider.valueProperty());

      timer.stop();
    });

    // button to follow or don't follow plane
    Button toggleButton = new Button("Toggle Follow Plane");
    toggleButton.setOnAction(e -> {
      if (followCamera) {
        followCamera = false;
        cameraControlBox.setDisable(true);
        cameraZoom.unbindBidirectional(zoomSlider.valueProperty());
      } else {
        followCamera = true;
        if (startButton.isDisabled()) {
          cameraControlBox.setDisable(false);
          cameraZoom.bindBidirectional(zoomSlider.valueProperty());
        }
      }
    });
    toggleButton.setMinWidth(150);

    VBox animationControl = new VBox(missionLabel, missionSelect, progressLabel, progressSlider, animationBox,
        toggleButton);
    animationControl.setSpacing(5);
    animationControl.setAlignment(Pos.CENTER);
    animationControl.getStyleClass().add("panel-region");
    animationControl.setMaxSize(CONTROL_WIDTH, CONTROL_HEIGHT);

    return animationControl;
  }

  /**
   * Creates plus and minus buttons for mini-map to zoom in/out.
   * 
   * @return pane with buttons attached
   */
  private Pane createMiniMapControl() {

    Button plusButton = new Button("+");
    plusButton.setMinWidth(30);
    plusButton.setOnAction(e -> {
      if (scale2D >= 10000) {
        scale2D /= zoomFactor2D;
        mapView.setViewpointScaleAsync(scale2D);
      }
    });

    Button minusButton = new Button("-");
    minusButton.setMinWidth(30);
    minusButton.setOnAction(e -> {
      if (scale2D <= 500000) {
        scale2D *= zoomFactor2D;
        mapView.setViewpointScaleAsync(scale2D);
      }
    });

    VBox zoomBox = new VBox(plusButton, minusButton);
    view2D.getChildren().add(zoomBox);
    return view2D;
  }

  /**
   * Moves plane from one location to the next while moving camera with it. Also
   * sets the plane's heading, pitch, and roll at each point.
   * <p>
   * If last point is reached then plane starts back at the beginning.
   * <p>
   * Updates the settings in the bottom right as well.
   */
  private void movePlane() {

    // stores what point we are on
    if (nextPoint == (routePoints.size() - 1)) {
      nextPoint = 0;
    } else {
      nextPoint++;
    }

    // set plane to next location
    plane3DGraphic.setGeometry(routePoints.get(nextPoint));
    plane2DGraphic.setGeometry(routePoints.get(nextPoint));

    Map<String, Object> planeAttributes = plane3DGraphic.getAttributes();
    List<Double> settings = planeSettings.get(nextPoint);
    planeAttributes.replace("HEADING", settings.get(HEADING));
    planeAttributes.replace("PITCH", settings.get(PITCH));
    planeAttributes.replace("ROLL", settings.get(ROLL));

    plane2DGraphic.getAttributes().replace("ANGLE", settings.get(HEADING).floatValue());

    moveView(nextPoint);
    updateDisplayedSettings();
  }

  /**
   * Updates the displayed settings in the bottom right corner and progress bar
   * in top left corner.
   */
  private void updateDisplayedSettings() {

    DecimalFormat df = new DecimalFormat("0.0");
    altitudeText.setText("" + df.format(routePoints.get(nextPoint).getZ()));
    headingText.setText("" + df.format(((double) plane3DGraphic.getAttributes().get("HEADING"))));
    pitchText.setText("" + df.format(((double) plane3DGraphic.getAttributes().get("PITCH"))));
    rollText.setText("" + df.format(((double) plane3DGraphic.getAttributes().get("ROLL"))));

    if (progressSlider != null) {
      progressSlider.setValue(nextPoint);
    }
  }

  /**
   * Creates a timer for the plane animation.
   */
  private void createTimer() {

    timer = new Timeline(new KeyFrame(Duration.millis(timerDelay), ae -> {
      movePlane();
    }));
    timer.setCycleCount(Animation.INDEFINITE);
  }

  /**
   * Creates a slider with a minimum and maximum slide amount as well as an
   * initial.
   * <p>
   * Show tick marks is enabled, showing ten major tick marks and one minor tick
   * marks between each major one.
   * 
   * @param min minimum value of the slider
   * @param max maximum value of the slider
   * @param value initial value to set to the slider
   * @return slider that was created
   */
  private Slider createSlider(double min, double max, double value) {

    Slider newSlider = new Slider(min, max, value);
    newSlider.setShowTickMarks(true);
    newSlider.setMajorTickUnit((max / 10));
    newSlider.setMinorTickCount(1);

    return newSlider;
  }

  /**
   * Creates a label that displays text next to it.
   * 
   * @param labelText text the label is going to display
   * @param text text that will be displayed next to the label
   * @return container for label and text
   */
  private HBox createLabelAndText(String labelText, Text text) {

    HBox newBox = new HBox();
    Label newLabel = new Label(labelText);
    newLabel.setMinWidth(70);
    newLabel.getStyleClass().add("panel-label");
    text.setStyle("-fx-stroke: white;");
    newBox.getChildren().addAll(newLabel, text);

    return newBox;
  }

  /**
   * Reads information from a csv file that describes a point (x,y,z), heading,
   * pitch, and roll.
   * <p>
   * The point is stored in a list of route locations. Heading, pitch, and roll
   * are stored in a list of settings for the plane at each point.
   * 
   */
  private void storeRouteSettings() {

    // reads csv file
    BufferedReader buffer = null;
    // holds a line from the csv file
    String bufferLine;
    // stores where the csv file can be located
    String filePath = getClass().getResource("/" + missionLocation + ".csv").getPath();
    File file = new File(filePath);

    try {
      buffer = new BufferedReader(new FileReader(file));
      planeSettings = new ArrayList<>();
      routePoints = new ArrayList<>();
      nextPoint = 0;
      double x, y, z;
      SpatialReference wgs84 = SpatialReferences.getWgs84();

      // loops till their is no more line to be read
      while ((bufferLine = buffer.readLine()) != null) {
        // separates each property in the line
        List<String> lineArray = Arrays.asList(bufferLine.split(","));
        if (lineArray.size() == 6) {
          // stores first 3 properties as a point
          x = Double.parseDouble(lineArray.get(X_VALUE));
          y = Double.parseDouble(lineArray.get(Y_VALUE));
          z = Double.parseDouble(lineArray.get(Z_VALUE));
          routePoints.add(new Point(x, y, z, wgs84));

          // stores the last 3 properties as settings
          List<Double> settings = new ArrayList<>();
          settings.add(Double.parseDouble(lineArray.get(HEADING + 3)));
          settings.add(Double.parseDouble(lineArray.get(PITCH + 3)));
          settings.add(Double.parseDouble(lineArray.get(ROLL + 3)));
          planeSettings.add(settings);
        } else {
          System.out.println("Bad line input file: " + bufferLine);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (buffer != null) {
          buffer.close();
        }
      } catch (IOException io) {
        io.printStackTrace();
      }
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {

    if (timer != null) {
      timer.stop();
    }

    if (sceneView != null) {
      sceneView.dispose();
    }
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
